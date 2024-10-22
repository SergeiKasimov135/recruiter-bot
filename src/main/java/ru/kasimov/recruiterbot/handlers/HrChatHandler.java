package ru.kasimov.recruiterbot.handlers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.kasimov.recruiterbot.RecruiterBot;
import ru.kasimov.recruiterbot.constants.MessageConstants;

public class HrChatHandler {

    public void handleHrChat(long chatId, RecruiterBot bot) {
        InlineKeyboardMarkup inlineKeyboardMarkup = bot.createInlineKeyboardMarkup(new String[]{"Вернуться к началу"});
        bot.sendMessageWithKeyboard(chatId, MessageConstants.HR_CHAT_START_MESSAGE, inlineKeyboardMarkup);
    }
}

