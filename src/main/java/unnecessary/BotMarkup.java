package unnecessary;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.LoginUrl;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class BotMarkup {

    public SendMessage activateInline(Message message){
        SendMessage sendMessage = new SendMessage();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("URL");
        button.setCallbackData("URL");
        buttonList.add(button);
        buttons.add(buttonList);
        markup.setKeyboard(buttons);

        sendMessage.setText("please click the button below to go the link url");
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(markup);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        return sendMessage;
    }


}
