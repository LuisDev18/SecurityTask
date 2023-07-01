package pe.edu.utp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.utp.repository.UsuarioRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final UsuarioRepository repository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username ->
        repository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  // Metodo se utiliza para configurar el proveedor de autenticacion, quien
  // es responsable de definir la logica de autenticacion de los usuarios y verificar sus
  // credenciales.
  // generar un objeto Authentication que representa al usuario
  // autenticado.
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /*Este metodo es usado por spring security para analizar el tipo de autenticacion requerida
  y de delegar al authenticationProvider que pueda manejar el tipo de autenticacion que se esta dando
  jwt,nombre y contraseña, LDAP, etc.
  */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
  }
}
