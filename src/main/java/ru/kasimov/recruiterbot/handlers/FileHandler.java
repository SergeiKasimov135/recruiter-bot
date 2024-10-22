package ru.kasimov.recruiterbot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kasimov.recruiterbot.RecruiterBot;

public class FileHandler {

    public void forwardFileToHr(long userChatId, Document document, RecruiterBot bot) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(bot.getHrChatId()));

        InputFile inputFile = new InputFile();
        inputFile.setMedia(document.getFileId());
        sendDocument.setDocument(inputFile);

        try {
            bot.execute(sendDocument);
            bot.sendMessage(userChatId, "Ваш файл был отправлен HR. Он свяжется с вами в ближайшее время.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            bot.sendMessage(userChatId, "Произошла ошибка при отправке файла HR.");
        }
    }
}

