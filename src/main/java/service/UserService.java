package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Client;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.*;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;

public class UserService implements Const {
    ObjectMapper objectMapper = new ObjectMapper();

    public void checkAndSaveUser(Message message) throws IOException {
        if (getByChatId(String.valueOf(message.getChatId())) == null){
            Client client = null;
            if (message.getChatId().toString().equals(ADMIN_CHAT_ID)){
                client = new Client(message.getFrom().getUserName(), message.getChatId().toString(), message.getChat().getFirstName(), "", true,true);
            }else {
                client = new Client(message.getFrom().getUserName(), message.getChatId().toString(), message.getChat().getFirstName(), "", true,false);
            }
            add(client);
        }
    }

    private void add(Client client) throws IOException {
        List<Client> clientList = getUserList();
        if (clientList == null){
            clientList = new ArrayList<>();
        }
        clientList.add(client);
        updateDatabase(clientList);
    }

    public List<Client> getUserList() throws IOException {
        List<Client> clientList = new ArrayList<>();
        try {
            File file = new File(USER_DATABASE);
            if (file.length() != 0){
                List<Client> clients = objectMapper.readValue(file, new TypeReference<List<Client>>() {});
                clients.forEach(client -> {
                    if (client.isActive()){
                        clientList.add(client);
                    }
                });
            }
            return clientList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void updateDatabase(List<Client> clientList){
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USER_DATABASE), clientList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client getByChatId(String chatId) throws IOException {
            for (Client client : getUserList()){
                if (client.getChatId().equals(chatId)){
                    return client;
                }
            }
        return null;
    }

    public boolean isAdmin(String chatId){
        try {
            List<Client> userList = getUserList();
            for (Client client : userList) {
                if (client.getChatId().equals(chatId) && client.isAdmin()) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addUserPhoneNumber(String chatId, String phoneNumber) throws IOException {
        List<Client> userList= getUserList();
        int index = 0;
        for (Client client : userList) {
            if (client.getChatId().equals(chatId)){
                client.setPhoneNumber(phoneNumber);
                userList.set(index, client);
            }
            index++;
        }
        updateDatabase(userList);
    }

}


