package pe.edu.utp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import pe.edu.utp.converter.MessageMapper;
import pe.edu.utp.dto.MessageRequestDTO;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.repository.MessageRepository;
import pe.edu.utp.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  private final MessageRepository messageRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;
  private final UsuarioRepository userRepository;
  private final SimpUserRegistry simpUserRegistry; // Inyecta el SimpUserRegistry

  public void sendMessage(MessageRequestDTO messageRequestDTO) {
    var message = messageMapper.toEntity(messageRequestDTO);
    Optional<Usuario> userRecipientOptional = userRepository.findById(messageRequestDTO.getRecipientUserId());
    pe.edu.utp.entity.Usuario userRecipient = userRecipientOptional.orElseThrow(() -> new RuntimeException("Recipient user not found"));

    log.info("MessageService: Sending message to recipient email: {}", userRecipient.getEmail());

    Optional<pe.edu.utp.entity.Usuario> userSenderOptional = userRepository.findById(messageRequestDTO.getUserSenderId());
    pe.edu.utp.entity.Usuario userSender = userSenderOptional.orElseThrow(() -> new RuntimeException("Sender user not found"));


    message.setRecipient(userRecipient);
    message.setSender(userSender);
    message.setSentAt(LocalDateTime.now());

    // **** Depuración: Imprime los usuarios conectados ****
    log.info("--- START: Current STOMP Users Registered with SimpUserRegistry ---");
    simpUserRegistry.getUsers().forEach(simpUser -> {
      log.info("  User Principal: '{}'", simpUser.getName());
      simpUser.getSessions().forEach(session -> {
        log.info("    - Session ID: '{}', Authenticated Principal: '{}'",
                session.getId());
      });
    });
    log.info("--- END: Current STOMP Users Registered with SimpUserRegistry ---");
    // ****************************************************

    messagingTemplate.convertAndSendToUser(
            userRecipient.getEmail(), // Usa el email para el que se autenticó la sesión del destinatario
            "/queue/message",
            message.getBody()); // Considera enviar un DTO completo para más info en el cliente

    messageRepository.save(message);
  }
}
