package bot;

import lombok.SneakyThrows;
import model.Client;
import model.Messages;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.Const;
import service.MessageService;
import service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class LoggingTestBot extends TelegramLongPollingBot implements Const {
    UserService userService = new UserService();
    MessageService messageService = new MessageService();

    Markup markup = new Markup();
    Map<String, Stack<String>> map = new HashMap<>();

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.hasMessage() ? update.getMessage() : update.getCallbackQuery().getMessage();
        String chatId = message.getChatId().toString();

        if (update.hasMessage() && update.getMessage().hasText()){
            if (message.getText().equals(START)){

                userService.checkAndSaveUser(message);
                checkTheMap(chatId);
                try {
                    Client byChatId = userService.getByChatId(chatId);
                    if (byChatId.isAdmin()){ //if the user is admin
                        SendMessage sendMessage = markup.showAdminMenu(message);
                        executeMessage(sendMessage);
                    }else if (!byChatId.isAdmin()){ //if the client is the simple user;
                        SendMessage sendMessage = markup.showUserMenu(message);
                        executeMessage(sendMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } //checking the user and adding
            }
            else if (message.getText().equals(SEE_MESSAGES)){
                map.get(chatId).push(SEE_MESSAGES);
            }
            else if (message.getText().equals(SEE_USERS)){
                map.get(chatId).push(SEE_USERS);
            }
            else if (message.getText().equals(WRITE_TO_ADMIN)){
                map.get(chatId).push(WRITE_TO_ADMIN);
            }
            else if (map.get(chatId).peek().equals(SEE_MESSAGES) && isInteger(message.getText())){
                executeMessage(markup.messageNumberChosen(message));
                map.get(chatId).push(WRITE_REPLY + message.getText());
            }
            else if (map.get(chatId).peek().startsWith(WRITE_REPLY) && message.hasText()){
                //find that message and attach the reply;
                String chosenMessageNumber = map.get(chatId).peek().replace(WRITE_REPLY, "");
                Messages byNumber = messageService.getByNumber(Integer.parseInt(chosenMessageNumber));
                byNumber.setReplied(true);
                byNumber.setReplyMessage(message.getText());
                messageService.attachReply(byNumber);
                executeMessage(markup.replyToClient(message, byNumber));
            }
            else if (message.getText().equals(BACK)){
                map.get(chatId).pop();
            }

            executeMap(message);
        }else if (message.hasContact()){
            userService.addUserPhoneNumber(chatId, message.getContact().getPhoneNumber());
        }
    }

    private void executeMap(Message message){
        if (map.get(message.getChatId().toString()).peek().equals(SEE_MESSAGES)){
            SendMessage sendMessage = markup.showUnrepliedMessages(message);
            executeMessage(sendMessage);
        }
        else if (map.get(message.getChatId().toString()).peek().equals(SEE_USERS)) {
            try {
                SendDocument userListExcel = markup.getUserListExcel(message);;
                executeDocument(userListExcel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (map.get(message.getChatId().toString()).peek().equals(WRITE_TO_ADMIN)){
            if (!message.getText().equals(WRITE_TO_ADMIN)){
                messageService.addMessage(message);
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

    private void executeDocument(SendDocument sendDocument){
        try {
            execute(sendDocument);
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

    private void checkTheMap(String chatId){
        Stack<String> stack = new Stack<>();
            map.put(chatId, stack);
    }

    private boolean isInteger(String text){
        try {
            Integer.parseInt(text);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}


//things needed to be added later => 1.admin getting the message history in the form of pdf;
//clients getting the pdf document of their own messages with admin;
//admin writing message to the random client of this bot at his own will f.e i can write a message to mirza via bot;
