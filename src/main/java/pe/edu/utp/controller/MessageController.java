package pe.edu.utp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import pe.edu.utp.dto.MessageRequestDTO;
import pe.edu.utp.service.MessageService;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageRequestDTO dto) {
        messageService.sendMessage(dto);
    }
}
