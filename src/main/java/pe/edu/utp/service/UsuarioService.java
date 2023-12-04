package pe.edu.utp.service;

import org.springframework.data.domain.Pageable;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    public List<Usuario> findAll(Pageable page);
    public List<Usuario> findByEmail(String email, Pageable page);
    public Usuario findById(int id);
    public Usuario update(Usuario usuario);
    public Usuario save(Usuario usuario);
    public void delete(int id);
    public LoginResponseDto login(LoginRequestDto loginRequestDto);

}
