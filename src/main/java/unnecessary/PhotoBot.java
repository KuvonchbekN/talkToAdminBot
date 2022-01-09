package unnecessary;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.Const;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PhotoBot extends TelegramLongPollingBot implements Const {
    BotMarkup markup = new BotMarkup();

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.hasMessage() ? update.getMessage() : update.getCallbackQuery().getMessage();



        if (update.hasMessage()){
            if (message.getText().equals("/start")){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Welcome to our bot!");
                sendMessage.setChatId(message.getChatId().toString());
                executeMessage(sendMessage);
            }else if (message.getText().equals("/pic")){
                String text = update.getMessage().getText();
                List<PhotoSize> photos = message.getPhoto();
                //this is to know the photo id
                String fId = "AgACAgIAAxkBAAMlYcnCPnXgl3mvOxa-Yk3AAAFztDDwAAIiwzEbzwZISq1XKwABWYRHigEAAwIAA3gAAyME";

                //this is to know the photo width
                int fWidth = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null)).getWidth();

                //to get the photo height
                int fHeight = Objects.requireNonNull(photos.stream().max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null)).getHeight();


                //to get the photo caption
                String photoCaption = "File Id: " + fId + "\n Width: " + Integer.toString(fWidth) + "\n Height: "+ Integer.toString(fHeight);

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setCaption(photoCaption);
                sendPhoto.setChatId(message.getChatId().toString());
                try {
                    InputFile inputFile = new InputFile(fId);
                    sendPhoto.setPhoto(inputFile);
                    executePhoto(sendPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (message.getText().equals("url")){
                executeMessage(markup.activateInline(message));
            }
            else{
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("unknown text");
                sendMessage.setChatId(message.getChatId().toString());
                executeMessage(sendMessage);
            }

        }
    }



    private void executeMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executePhoto(SendPhoto sendPhoto){
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
