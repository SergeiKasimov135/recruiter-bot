package ru.kasimov.recruiterbot.handlers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.kasimov.recruiterbot.RecruiterBot;
import ru.kasimov.recruiterbot.constants.MessageConstants;

public class QuestionHandler {

    public void handleAskQuestion(long chatId, RecruiterBot bot) {
        InlineKeyboardMarkup inlineKeyboardMarkup = bot.createInlineKeyboardMarkup(new String[]{"Вернуться к началу"});
        bot.sendMessageWithKeyboard(chatId, MessageConstants.ASK_QUESTION_START_MESSAGE, inlineKeyboardMarkup);
    }
}