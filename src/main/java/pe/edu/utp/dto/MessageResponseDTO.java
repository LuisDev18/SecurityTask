package pe.edu.utp.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import pe.edu.utp.entity.Usuario;

@Getter
@Setter
public class MessageResponseDTO {

        private Long messageId;
        private Usuario sender;
        private Usuario recipient;
        private String subject;
        private String body;
        private LocalDateTime sentAt;

}
