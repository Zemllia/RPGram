![RPGram Logo](RPGram.png)

# RPGram

## Dependencies
- [TelegramBots](https://mvnrepository.com/artifact/org.telegram/telegrambots)
- [Crown](https://github.com/F1uctus/crown)

## Building from sources
```
$ git clone https://github.com/Zemllia/RPGram.git
$ cd RPGram
$ mvn compile
```

## Configuration
A configuration file (`config.properties`) example:
```
bot.token=123456789:your-secret-token
bot.name=my_rpgram_bot
proxy.enabled=[true|false]
proxy.host=127.0.0.1
proxy.port=9050
```

## Running the bot
```
$ mvn exec:java -Dexec.args='config.properties'
```
