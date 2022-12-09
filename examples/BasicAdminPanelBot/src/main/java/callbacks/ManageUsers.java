package callbacks;

import commands.RegistrationCommand;
import lombok.SneakyThrows;
import main.MyBot;
import org.carboncock.metagram.annotation.Callback;
import org.carboncock.metagram.annotation.EventHandler;
import org.carboncock.metagram.annotation.Permission;
import org.carboncock.metagram.annotation.types.CallbackFilter;
import org.carboncock.metagram.annotation.types.SendMethod;
import org.carboncock.metagram.listener.Listener;
import org.carboncock.metagram.telegram.data.CallbackData;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EventHandler
public class ManageUsers implements Listener {

    @Callback("viewusers")
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onViewUsers(CallbackData callbackData){
        Update update = callbackData.getUpdate();
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<Long> users = MyBot.users;

        for(int i = 1; i <= users.size(); i++){
            rowInline.add(
                    InlineKeyboardButton.builder()
                            .text("ID " + users.get(i - 1))
                            .callbackData("manageuser=%s".formatted(users.get(i - 1)))
                            .build()
            );
            if(i % 3 == 0){
                rowsInline.add(rowInline);
                rowInline.clear();
            }
        }
        rowsInline.add(rowInline);
        ikm.setKeyboard(rowsInline);
        callbackData.getBotInstance().execute(EditMessageText.builder()
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .text("Manage your users!")
                .replyMarkup(ikm)
                .build());
    }

    @Callback(value = "manageuser={id}", filter = CallbackFilter.CUSTOM_PARAMETER)
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onManageUser(CallbackData callbackData){
        Update update = callbackData.getUpdate();
        InlineKeyboardMarkup ikm = new InlineKeyboardMarkup();
        Long userId = Long.parseLong((String) callbackData.getParameters().get("id"));
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("\uD83D\uDEB7 BAN")
                        .callbackData("banuser=%s".formatted(userId))
                        .build(),
                InlineKeyboardButton.builder()
                        .text("\uD83D\uDD13 UNBAN")
                        .callbackData("unbanuser=%s".formatted(userId))
                        .build()
                )
        );
        rowsInline.add(List.of(
                InlineKeyboardButton.builder()
                        .text("\uD83D\uDEC2 SET ADMIN")
                        .callbackData("setadmin=%s".formatted(userId))
                        .build(),
                InlineKeyboardButton.builder()
                        .text("\uD83D\uDC64 REMOVE ADMIN")
                        .callbackData("removeadmin=%s".formatted(userId))
                        .build()
        ));
        rowsInline.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("\uD83D\uDD19 BACK")
                        .callbackData("viewusers")
                        .build()
        ));
        ikm.setKeyboard(rowsInline);
        callbackData.getBotInstance().execute(
                EditMessageText.builder()
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .text("You are managing the user with id: %s\n\uD83D\uDEC2 is admin: %s".formatted(userId, MyBot.admins.contains(userId)))
                        .replyMarkup(ikm)
                        .build()
        );
    }

    @Callback(value = "banuser={id}", filter = CallbackFilter.CUSTOM_PARAMETER)
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onBanUser(CallbackData callbackData){
        RegistrationCommand.blacklist.add(Long.parseLong((String) callbackData.getParameters().get("id")));
        Update update = callbackData.getUpdate();
        callbackData.getBotInstance().executeAsync(
                AnswerCallbackQuery.builder()
                        .text("\uD83D\uDEB7 User successfully banned!")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
        onViewUsers(callbackData); // call this method will edit again the message to the manage section
    }

    @Callback(value = "unbanuser={id}", filter = CallbackFilter.CUSTOM_PARAMETER)
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onUnbanUser(CallbackData callbackData){
        RegistrationCommand.blacklist.remove(Long.parseLong((String) callbackData.getParameters().get("id")));
        Update update = callbackData.getUpdate();
        callbackData.getBotInstance().executeAsync(
                AnswerCallbackQuery.builder()
                        .text("\uD83D\uDEB7 User successfully unbanned!")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
        onViewUsers(callbackData); // call this method will edit again the message to the manage section
    }

    @Callback(value = "setadmin={id}", filter = CallbackFilter.CUSTOM_PARAMETER)
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onSetAdmin(CallbackData callbackData){
        MyBot.admins.add(Long.parseLong((String) callbackData.getParameters().get("id")));
        Update update = callbackData.getUpdate();
        callbackData.getBotInstance().executeAsync(
                AnswerCallbackQuery.builder()
                        .text("\uD83D\uDEC2 User successfully promoted to admin!")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
        onViewUsers(callbackData); // call this method will edit again the message to the manage section
    }

    @Callback(value = "removeadmin={id}", filter = CallbackFilter.CUSTOM_PARAMETER)
    @Permission(listLocation = MyBot.class, onMissingPermission = "You don't have enough permissions!", send = SendMethod.ANSWER_CALLBACK_QUERY)
    @SneakyThrows
    public void onRemoveAdmin(CallbackData callbackData){
        MyBot.admins.remove(Long.parseLong((String) callbackData.getParameters().get("id")));
        Update update = callbackData.getUpdate();
        callbackData.getBotInstance().executeAsync(
                AnswerCallbackQuery.builder()
                        .text("\uD83D\uDC64 Admin successfully demoted to user!")
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()
        );
        onViewUsers(callbackData); // call this method will edit again the message to the manage section
    }
}
