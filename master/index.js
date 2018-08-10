const fs = require('fs');
const path = require('path');
const logger = require('../common/logger');
const Sharder = require('./sharder');

logger.info(" __                 __           __           __                           ");
logger.info("|  |--.--.--.-----.|  |--.-----.|  |--.-----.|  |_   .--------.-----.-----.");
logger.info("|    <|  |  |  _  ||    <|  _  ||  _  |  _  ||   _|__|        |  _  |  -__|");
logger.info("|__|__|___  |_____||__|__|_____||_____|_____||____|__|__|__|__|_____|_____|");
logger.info("      |_____|                                                              ");

const configPath = path.resolve(process.cwd(), "config.json");

if (!fs.existsSync(configPath)) {
    logger.error("Cannot find configuration file!");
    process.exit(1);
}

var config = require(path.relative(__dirname, configPath));

const sharder = new Sharder(config.token, "/shard/index.js", {
    stats: true,
    debug: true,
    guildsPerShard: "1000",
    name: "Kyoko",
    clientOptions: {
        messageLimit: 150,
        defaultImageFormat: "png"
    }
});
  
sharder.on("stats", stats => {
    console.log(stats);
});

process.on('log', log => {
    console.log(log);
})