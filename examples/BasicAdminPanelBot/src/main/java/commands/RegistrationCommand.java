package commands;

import lombok.SneakyThrows;
import main.MyBot;
import org.carboncock.metagram.annotation.Command;
import org.carboncock.metagram.annotation.EventHandler;
import org.carboncock.metagram.annotation.Permission;
import org.carboncock.metagram.annotation.PermissionHandler;
import org.carboncock.metagram.annotation.types.PermissionType;
import org.carboncock.metagram.annotation.types.SendMethod;
import org.carboncock.metagram.listener.CommandListener;
import org.carboncock.metagram.listener.Permissionable;
import org.carboncock.metagram.telegram.data.CommandData;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command("start") // is triggered only when a user types /start
@Permission(listLocation = RegistrationCommand.class, send = SendMethod.REPLY_MESSAGE, type = PermissionType.UNABLE_TO_DO) // is triggered only when the user is in the blacklist. Block the execution of this annotated method
public class RegistrationCommand implements CommandListener, Permissionable {

    @PermissionHandler
    public static final List<Long> blacklist = new ArrayList<>();

    @SneakyThrows
    @Override
    public void onCommand(CommandData cmd) {
        Update update = cmd.getUpdate();
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<Long> users = MyBot.users; // Normally a database should be used, but since this is a basic example to explain the lib we can avoid
        List<Long> admins = MyBot.admins; // same here

        if(!users.contains(cmd.getSender().getId()))
            users.add(cmd.getSender().getId());

        if(admins.contains(cmd.getSender().getId())){
            rowsInline.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text("\uD83D\uDC41\u200D\uD83D\uDDE8 MANAGE USERS")
                            .callbackData("viewusers")
                            .build()
            ));
        }
        ikm.setKeyboard(rowsInline);
        cmd.getBotInstance().execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text("Welcome to my bot, this is an example on using the [MetaGram](https://github.com/CarbonCock/MetaGram) extension!")
                .replyMarkup(ikm)
                .parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true)
                .build());

    }

    @Override
    public String onPermissionMissing() {
        return "You are banned from the bot!";
    }
}
