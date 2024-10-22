package ru.kasimov.recruiterbot.handlers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.kasimov.recruiterbot.RecruiterBot;
import ru.kasimov.recruiterbot.constants.MessageConstants;

public class VacancyHandler {

    public void handleVacancySearch(long chatId, RecruiterBot bot) {
        InlineKeyboardMarkup inlineKeyboardMarkup = bot.createInlineKeyboardMarkup(
                new String[]{"Разработка", "Маркетинг"},
                new String[]{"Финансы", "Дизайн"},
                new String[]{"Вернуться к началу"}
        );
        bot.sendMessageWithKeyboard(chatId, MessageConstants.FREE_VACANCY_MESSAGE, inlineKeyboardMarkup);
    }

    public void handleVacancyCategory(long chatId, String vacancyCategory, RecruiterBot bot) {
        String vacancyCategoryText = "Вы выбрали категорию: " + vacancyCategory + ". Выберите уровень вакансии:";
        InlineKeyboardMarkup inlineKeyboardMarkup = bot.createInlineKeyboardMarkup(
                new String[]{"Junior", "Middle", "Senior"},
                new String[]{"Вернуться к началу"}
        );
        bot.sendMessageWithKeyboard(chatId, vacancyCategoryText, inlineKeyboardMarkup);
    }

    public void sendVacancies(long chatId, String level, RecruiterBot bot) {
        String vacanciesText = "Вакансии для " + level + " уровня:\n";
        switch (level) {
            case "junior":
                vacanciesText += "Junior at Company X\nJunior at Company Y\n";
                break;
            case "middle":
                vacanciesText += "Middle at Company Z\nMiddle at Company W\n";
                break;
            case "senior":
                vacanciesText += "Senior at Company A\nSenior at Company B\n";
                break;
        }

        vacanciesText += "\nЕсли вас заинтересовала вакансия, отправьте свое резюме нашему HR.";
        InlineKeyboardMarkup inlineKeyboardMarkup = bot.createInlineKeyboardMarkup(new String[]{"Вернуться к началу"});
        bot.sendMessageWithKeyboard(chatId, vacanciesText, inlineKeyboardMarkup);
    }
}
