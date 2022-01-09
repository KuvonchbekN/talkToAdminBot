package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Messages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminService implements Const{
    ObjectMapper objectMapper = new ObjectMapper();



    public List<Messages> getAllUserMessages(){
        List<Messages> messagesList = new ArrayList<>();
        File file = new File(MESSAGES_DATABASE);
        try {
            List<Messages> messages = objectMapper.readValue(file, new TypeReference<List<Messages>>() {});
            for (Messages messages1 : messages){
                if (!messages1.getChatId().equals(ADMIN_CHAT_ID) && !messages1.getText().equals(START)){
                    messagesList.add(messages1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messagesList;
    }




}
