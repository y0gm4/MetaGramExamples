package main;

import commands.RegistrationCommand;
import lombok.SneakyThrows;
import org.carboncock.metagram.annotation.PermissionHandler;
import org.carboncock.metagram.telegram.api.MetaGramApi;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class MyBot {

    private static final String TOKEN = "bot token";
    private static final String USERNAME = "bot username";

    @PermissionHandler
    public static final List<Long> admins = new ArrayList<>();

    public static final List<Long> users = new ArrayList<>();


    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class); // TelegramBots api rubenlaugs
        MetaGramApi bot = new MetaGramApi(); // MetaGram api CarbonCock
        bot.setBotToken(TOKEN);
        bot.setBotUsername(USERNAME);
        api.registerBot(bot); // TelegramBots api
        // Registration of multiple classes that handle events.
        bot.registerEvents("commands");
        bot.registerEvents("callbacks");
    }
}


