package ru.kasimov.recruiterbot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kasimov.recruiterbot.RecruiterBot;

public class FileHandler {

    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    public void forwardFileToHr(long userChatId, Document document, RecruiterBot bot) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(bot.getHrChatId()));

        InputFile inputFile = new InputFile();
        inputFile.setMedia(document.getFileId());
        sendDocument.setDocument(inputFile);

        try {
            bot.execute(sendDocument);

            logger.info("File sent to HR successfully. UserChatId: {}, DocumentId: {}",
                    userChatId, document.getFileId());

            bot.sendMessage(userChatId, "Ваш файл был отправлен HR. Он свяжется с вами в ближайшее время.");
        } catch (TelegramApiException e) {
            logger.error("Error sending file to HR. UserChatId: {}, DocumentId: {}, Error: {}",
                    userChatId, document.getFileId(), e.getMessage());
            bot.sendMessage(userChatId, "Произошла ошибка при отправке файла HR.");
        }
    }
}

