package pe.edu.utp.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import pe.edu.utp.entity.Rol;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.repository.UsuarioRepository;

public class UsuarioServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @InjectMocks
  private UsuarioService usuarioService;

  private Usuario usuario;

  @BeforeEach
  void Setup() {
    // Initialize mocks and any required setup here
    usuario =
      Usuario
        .builder()
        .email("luis_18@gmail.com")
        .password("securePassword123")
        .activo(true)
        .rol(Rol.ADMIN)
        .build();
  }
}
