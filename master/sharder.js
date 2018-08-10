const Master = require('eris-sharder').Master;
const logger = require('../common/logger');
const master = require("cluster");
const numCPUs = require('os').cpus().length;
const EventEmitter = require("events");
const Eris = require("eris");
const cluster = require("../node_modules/eris-sharder/src/sharding/cluster");
const Queue = require("../node_modules/eris-sharder/src/utils/queue");
const pkg = require("../node_modules/eris-sharder/package.json")

class Sharder extends Master {
    constructor(token, mainFile, options) {
        super(token, mainFile, options);
    }

    async launch(test) {
        if (master.isMaster) {
            process.on("uncaughtException", err => {
                logger.error("Cluster Manager", err.stack);
            });

            setTimeout(() => {
                logger.info("General", "Cluster Manager has started!");
                if (test) {
                    this.maxShards = this.shardCount;
                    logger.info("Cluster Manager", `Starting ${this.shardCount} shards in ${this.clusterCount} clusters`);

                    master.setupMaster({
                        silent: true
                    });
                    // Fork workers.
                    this.start(this.clusterCount, 0);
                } else {
                    this.eris.getBotGateway().then(result => {
                        this.calculateShards(result.shards).then(shards => {
                            this.shardCount = shards;
                            this.maxShards = this.shardCount;
                            logger.info("Cluster Manager", `Starting ${this.shardCount} shards in ${this.clusterCount} clusters`);
                            let embed = {
                                title: `Starting ${this.shardCount} shards in ${this.clusterCount} clusters`
                            }
                            this.sendWebhook("cluster", embed);

                            master.setupMaster({
                                silent: true
                            });
                            // Fork workers.
                            this.start(this.clusterCount, 0);
                        });
                    });
                }
            }, 50);
        } else if (master.isWorker) {
            const Cluster = new cluster();
            Cluster.spawn();
        }

        master.on('message', (worker, message, handle) => {
            if (message.name) {
                switch (message.name) {
                    case "log":

                        logger.info(`Cluster ${worker.id}`, `${message.msg}`);
                        break;
                    case "debug":
                        if (this.options.debug) {
                            logger.debug(`Cluster ${worker.id}`, `${message.msg}`);
                        }
                        break;
                    case "info":
                        logger.info(`Cluster ${worker.id}`, `${message.msg}`);
                        break;
                    case "warn":
                        logger.warn(`Cluster ${worker.id}`, `${message.msg}`);
                        break;
                    case "error":
                        logger.error(`Cluster ${worker.id}`, `${message.msg}`);
                        break;
                    case "shardsStarted":
                        this.queue.queue.splice(0, 1);
                        if (this.queue.queue.length > 0) {
                            this.queue.executeQueue();
                        }
                        break;
                    case "cluster":
                        this.sendWebhook("cluster", message.embed);
                        break;
                    case "shard":
                        this.sendWebhook("shard", message.embed);
                        break;
                    case "stats":
                        this.stats.stats.guilds += message.stats.guilds;
                        this.stats.stats.users += message.stats.users;
                        this.stats.stats.voice += message.stats.voice;
                        this.stats.stats.totalRam += message.stats.ram;
                        let ram = message.stats.ram / 1000000;
                        this.stats.stats.exclusiveGuilds += message.stats.exclusiveGuilds;
                        this.stats.stats.largeGuilds += message.stats.largeGuilds;
                        this.stats.stats.clusters.push({
                            cluster: worker.id,
                            shards: message.stats.shards,
                            guilds: message.stats.guilds,
                            ram: ram,
                            voice: message.stats.voice,
                            uptime: message.stats.uptime,
                            exclusiveGuilds: message.stats.exclusiveGuilds,
                            largeGuilds: message.stats.largeGuilds
                        });
                        this.stats.clustersCounted += 1;
                        if (this.stats.clustersCounted === this.clusters.size) {
                            function compare(a, b) {
                                if (a.cluster < b.cluster)
                                    return -1;
                                if (a.cluster > b.cluster)
                                    return 1;
                                return 0;
                            }
                            let clusters = this.stats.stats.clusters.sort(compare);
                            this.emit("stats", {
                                guilds: this.stats.stats.guilds,
                                users: this.stats.stats.users,
                                voice: this.stats.stats.voice,
                                exclusiveGuilds: this.stats.stats.exclusiveGuilds,
                                largeGuilds: this.stats.stats.largeGuilds,
                                totalRam: this.stats.stats.totalRam / 1000000,
                                clusters: clusters
                            });
                        }
                        break;

                    case "fetchUser":
                        this.fetchInfo(1, "fetchUser", message.id);
                        this.callbacks.set(message.id, worker.id);
                        break;
                    case "fetchGuild":
                        this.fetchInfo(1, "fetchGuild", message.id);
                        this.callbacks.set(message.id, worker.id);
                        break;
                    case "fetchChannel":
                        this.fetchInfo(1, "fetchChannel", message.id);
                        this.callbacks.set(message.id, worker.id);
                        break;
                    case "fetchReturn":
                        let callback = this.callbacks.get(message.value.id);
                        let cluster = this.clusters.get(callback);
                        if (cluster) {
                            cluster.worker.send({ name: "fetchReturn", id: message.value.id, value: message.value });
                            this.callbacks.delete(message.value.id);
                        }
                        break;
                    case "broadcast":
                        this.broadcast(1, message.msg);
                        break;
                    case "send":
                        this.sendTo(message.cluster, message.msg)
                        break;
                }
            }
        });

        master.on('disconnect', (worker) => {
            logger.warn("Cluster Manager", `cluster ${worker.id} disconnected. Restarting.`);
        });

        master.on('exit', (worker, code, signal) => {
            this.restartCluster(worker, code, signal);
        });

        this.queue.on("execute", item => {
            let cluster = this.clusters.get(item.item);
            if (cluster) {
                cluster.worker.send(item.value);
            }
        });
    }
}

module.exports = Sharder;