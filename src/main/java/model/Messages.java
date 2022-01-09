package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {
    private String text;
    private String chatId;
    private String firstName;
    private String username;
    private Date messageTime;
    private String messageId;
    private boolean isActive;
    private String replyMessage; //reply by admin;
    private boolean isReplied;
}
