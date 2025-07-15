package pe.edu.utp.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDTO {
    private Integer recipientUserId;
    private Integer userSenderId;
    private String subject;
    private String body;
}
