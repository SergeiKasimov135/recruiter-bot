package ru.kasimov.recruiterbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kasimov.recruiterbot.config.BotConfig;
import ru.kasimov.recruiterbot.handlers.FileHandler;
import ru.kasimov.recruiterbot.handlers.HrChatHandler;
import ru.kasimov.recruiterbot.handlers.QuestionHandler;
import ru.kasimov.recruiterbot.handlers.VacancyHandler;

import java.util.ArrayList;
import java.util.List;

import static ru.kasimov.recruiterbot.constants.MessageConstants.*;

@Component
@AllArgsConstructor
public class RecruiterBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private static final Logger logger = LoggerFactory.getLogger(RecruiterBot.class);

    private final VacancyHandler vacancyHandler = new VacancyHandler();
    private final QuestionHandler questionHandler = new QuestionHandler();
    private final HrChatHandler hrChatHandler = new HrChatHandler();
    private final FileHandler fileHandler = new FileHandler();

    @Getter
    private final long hrChatId = 0; // specify hrChatId

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.equals("/start")) {
                    sendWelcomeMessage(chatId);
                } else {
                    forwardMessageToHr(chatId, messageText);
                }
            } else if (update.getMessage().hasDocument()) {
                fileHandler.forwardFileToHr(chatId, update.getMessage().getDocument(), this);
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "vacancySearch":
                    vacancyHandler.handleVacancySearch(chatId, this);
                    break;
                case "development":
                    vacancyHandler.handleVacancyCategory(chatId, "Разработка", this);
                    break;
                case "junior":
                    vacancyHandler.sendVacancies(chatId, "junior", this);
                    break;
                case "middle":
                    vacancyHandler.sendVacancies(chatId, "middle", this);
                    break;
                case "senior":
                    vacancyHandler.sendVacancies(chatId, "senior", this);
                    break;
                case "marketing":
                    vacancyHandler.handleVacancyCategory(chatId, "Маркетинг", this);
                    break;
                case "finance":
                    vacancyHandler.handleVacancyCategory(chatId, "Финансы", this);
                    break;
                case "design":
                    vacancyHandler.handleVacancyCategory(chatId, "Дизайн", this);
                    break;
                case "askQuestion":
                    questionHandler.handleAskQuestion(chatId, this);
                    break;
                case "hrChat":
                    hrChatHandler.handleHrChat(chatId, this);
                    break;
                case "sendFilesAndLinks":
                    sendMessage(chatId, SEND_FILES_AND_LINKS_START_MESSAGE);
                    break;
                case "returnToStart":
                    sendWelcomeMessage(chatId);
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда");
            }
        }
    }

    private void forwardMessageToHr(long userChatId, String messageText) {
        String forwardedMessage = "Сообщение от пользователя " + userChatId + ": " + messageText;

        sendMessage(hrChatId, forwardedMessage);

        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(new String[]{"Вернуться к началу"});
        sendMessageWithKeyboard(userChatId, "Ваш вопрос был отправлен HR. Он свяжется с вами в ближайшее время.", inlineKeyboardMarkup);
    }

    private void sendWelcomeMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup(
                new String[]{"Поиск Вакансий", "Задать Вопрос"},
                new String[]{"Переписка с HR", "Отправка файлов и ссылок"}
        );

        sendMessageWithKeyboard(chatId, WELCOME_TEXT, inlineKeyboardMarkup);
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup(String[]... buttonTexts) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String[] buttonRow : buttonTexts) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (String buttonText : buttonRow) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonText);
                button.setCallbackData(getCallbackData(buttonText));
                row.add(button);
            }
            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    private String getCallbackData(String buttonText) {
        switch (buttonText) {
            case "Поиск Вакансий":
                return "vacancySearch";
            case "Задать Вопрос":
                return "askQuestion";
            case "Переписка с HR":
                return "hrChat";
            case "Отправка файлов и ссылок":
                return "sendFilesAndLinks";
            case "Разработка":
                return "development";
            case "Маркетинг":
                return "marketing";
            case "Финансы":
                return "finance";
            case "Дизайн":
                return "design";
            case "Junior":
                return "junior";
            case "Middle":
                return "middle";
            case "Senior":
                return "senior";
            case "Вернуться к началу":
                return "returnToStart";
            default:
                return "unknown";
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
            logger.info("Message sent successfully to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Error sending message to chat {}: {}", chatId, e.getMessage());
        }
    }

    public void sendMessageWithKeyboard(long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
            logger.info("Message with keyboard sent successfully to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Error sending message with keyboard to chat {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

}
