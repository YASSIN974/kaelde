const Base = require('eris-sharder').Base;
const logger = require('./logger');

class Kyoko extends Base {
    constructor(bot) {
        super(bot);
        this.bot = bot;
    }

    launch() {
        this.bot.on('ready', () => logger.info("Ready!"))
    }
}

module.exports = Kyoko;