package pe.edu.utp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import pe.edu.utp.entity.Rol;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @InjectMocks private UsuarioService usuarioService;

  private Usuario usuario;

  @BeforeEach
  void Setup() {
    // Initialize mocks and any required setup here
    usuario =
        Usuario.builder()
            .email("luis_18@gmail.com")
            .password("securePassword123")
            .activo(true)
            .rol(Rol.ADMIN)
            .build();
  }

  @Test
  public void CreateUserTest() {
    // Arrange
    when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
    when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

    // Act
    Usuario savedUsuario = usuarioService.save(usuario);

    // Assert
    assertNotNull(savedUsuario);
    assertEquals(usuario.getEmail(), savedUsuario.getEmail());
    assertTrue(savedUsuario.isActivo());
    assertEquals(usuario.getRol(), savedUsuario.getRol());
    verify(usuarioRepository, times(1)).save(any(Usuario.class));
  }
}
