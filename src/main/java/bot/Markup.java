package bot;

import model.Client;
import model.Messages;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ReadingOrder;
import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import service.Const;
import service.MessageService;
import service.UserService;

import java.awt.*;
import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.List;

public class Markup implements Const {
    MessageService messageService = new MessageService();
    UserService userService = new UserService();

    public SendMessage showAdminMenu(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(ADMIN_MENU);
        sendMessage.setReplyMarkup(adminKeyboardMarkup());
        return sendMessage;
    }

    private ReplyKeyboardMarkup adminKeyboardMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> row = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();

        KeyboardButton messageList = new KeyboardButton();
        messageList.setText(SEE_MESSAGES);
        keyboardRow1.add(messageList);

        KeyboardButton activeUserList = new KeyboardButton();
        activeUserList.setText(SEE_USERS);
        keyboardRow1.add(activeUserList);
        keyboardRow2.add(BACK);

        row.add(keyboardRow1);
        row.add(keyboardRow2);

        replyKeyboardMarkup.setKeyboard(row);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("please choose");
        return replyKeyboardMarkup;
    }

    public SendMessage showUserMenu(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(userMenuMarkup());
        sendMessage.setText(USER_MENU);
        sendMessage.setAllowSendingWithoutReply(true);

        return sendMessage;
    }

    private ReplyKeyboardMarkup userMenuMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow.add(WRITE_TO_ADMIN);
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setRequestContact(true);
        keyboardButton.setText(SHARE_CONTACT);
        keyboardRow.add(keyboardButton);
        keyboardRow2.add(BACK);
        rows.add(keyboardRow);
        rows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("you may write a message to the admin");
        return replyKeyboardMarkup;
    }

    public SendMessage showUnrepliedMessages(Message message){
        List<Messages> unrepliedMessageList = messageService.getUnrepliedMessageList();
        StringBuilder sb = new StringBuilder();

        int ind = 1;
        for (Messages messages : unrepliedMessageList){
            sb.append(ind).append(". ").append(messages.getFirstName()).append(" texted you at ").append(messages.getMessageTime().toLocaleString()).append("\n");
            ind++;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(UNREPLIED_MESSAGES + "\n" + sb.toString());

        return sendMessage;
    }

    public SendDocument getUserListExcel(Message message) throws IOException {
        List<Client> userList = userService.getUserList();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(message.getChatId().toString());
        sendDocument.setCaption("here is the list of users");


        FileInputStream fileInputStream = new FileInputStream(USER_EXCEL_PATH);
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert workbook != null;
        HSSFSheet sheet = workbook.getSheet("sheet0");

        HSSFRow row = sheet.getRow(0);

        row.getCell(1).setCellValue("chatIDs");
        row.getCell(2).setCellValue("firstNames");
        row.getCell(3).setCellValue("usernames");
        row.getCell(4).setCellValue("phoneNumbers");


        int index = 1;
        for(Client client: userList) {
            workbook.getSheetAt(0).setColumnWidth(index, 8000);
            HSSFRow row1 = sheet.getRow(index);


            row1.getCell(1).setCellValue(client.getChatId());
            row1.getCell(2).setCellValue(client.getFirstName());
            row1.getCell(3).setCellValue(client.getUsername());
            row1.getCell(4).setCellValue(client.getPhoneNumber());
        }

        FileOutputStream fileOutputStream = new FileOutputStream(USER_EXCEL_PATH);
        workbook.write(fileOutputStream);

        fileInputStream.close();
        fileOutputStream.close();
        workbook.close();

        sendDocument.setDocument(new InputFile(new File(USER_EXCEL_PATH)));

        return sendDocument;
    }



    public SendMessage messageNumberChosen(Message message){
        Messages byNumber = messageService.getByNumber(Integer.parseInt(message.getText()));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(WRITE_REPLY  + "\n" + "Message Text : " + byNumber.getText());
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
    }

    public SendMessage replyToClient(Message message, Messages messages){
        String clientText = messages.getText();
        String adminText = message.getText();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(messages.getChatId());
        sendMessage.setText("Your message : " + clientText + "\n" + " Admin message: "+ adminText);
        return sendMessage;
    }


}

