package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Messages;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageService implements Const{
    ObjectMapper objectMapper = new ObjectMapper();
    UserService userService = new UserService();

    public void addMessage(Message message){
        Messages messageTaken = new Messages(message.getText(), message.getChatId().toString(), message.getChat().getFirstName(),message.getChat().getUserName(), new Date(), message.getMessageId().toString(), true, "", false);

        List<Messages> messageList = getMessageList();

        if (messageList  == null){
            messageList = new ArrayList<>();
        }
        messageList.add(messageTaken);
        updateDatabase(messageList);
    }

    public List<Messages> getUnrepliedMessageList(){
        List<Messages> messageList = getMessageList();
        List<Messages> finalList = new ArrayList<>();

        for (Messages messages : messageList){
            if (!messages.isReplied() && !userService.isAdmin(messages.getChatId())){
                finalList.add(messages);
            }
        }
        return finalList;
    }

    public List<Messages> getMessageList(){
        List<Messages> messages = new ArrayList<>();
        File file = new File(MESSAGES_DATABASE);
        try {
            messages = objectMapper.readValue(file, new TypeReference<List<Messages>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void updateDatabase(List<Messages> messages){
        File file = new File(MESSAGES_DATABASE);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Messages getByNumber(int chosenMessage){
        int index= 1;
        for (Messages messages : getUnrepliedMessageList()){
            if (chosenMessage == index){
                return messages;
            }
            index++;
        }
        return null;
    }

    public void attachReply(Messages messages){
        List<Messages> messageList = getMessageList();
        int index= 0;
        for (Messages messages1 : messageList){
            if (messages.getMessageId().equals(messages1.getMessageId())){
                messageList.set(index, messages);
                updateDatabase(messageList);
            }
            index++;
        }
    }
}
