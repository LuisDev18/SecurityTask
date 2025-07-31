package pe.edu.utp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pe.edu.utp.entity.Usuario;
import pe.edu.utp.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // TODO Auto-generated method stub
    Usuario usuario = usuarioRepository
      .findByEmail(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)
      );

    return org.springframework.security.core.userdetails.User
      .builder()
      .username(usuario.getEmail())
      .password(usuario.getPassword())
      .roles(usuario.getRol().name())
      .build();
  }
}
