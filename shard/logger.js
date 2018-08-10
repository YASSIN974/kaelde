module.exports = {
    log: message => process.send({name: "log", msg: message}),
    info: message => process.send({name: "info", msg: message}),
    debug: message => process.send({name: "debug", msg: message}),
    warn: message => process.send({name: "warn", msg: message}),
    error: message => process.send({name: "error", msg: message}),
};