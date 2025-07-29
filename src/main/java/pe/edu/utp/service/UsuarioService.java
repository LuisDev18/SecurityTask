package pe.edu.utp.service;

import org.springframework.data.domain.Pageable;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.dto.UsuarioRequestDto;
import pe.edu.utp.entity.Usuario;

import java.util.List;

public interface UsuarioService {
    public List<Usuario> findAll(Pageable page) throws Exception;
    public Usuario findById(int id);
    public Usuario update(UsuarioRequestDto usuario, Integer id);
    public Usuario save(UsuarioRequestDto usuario);
    public void delete(int id);
    public LoginResponseDto login(LoginRequestDto loginRequestDto);

}
