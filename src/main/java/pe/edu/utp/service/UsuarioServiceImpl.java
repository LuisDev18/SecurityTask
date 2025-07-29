package pe.edu.utp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.edu.utp.converter.UsuarioConverter;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.dto.UsuarioRequestDto;
import pe.edu.utp.entity.Rol;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.exception.EmailAlreadyException;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.repository.UsuarioRepository;
import pe.edu.utp.security.JwtService;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder encoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UsuarioConverter usuarioConverter;

    @Override
    public List<Usuario> findAll(Pageable page) throws Exception {
        return usuarioRepository.findAll(page).toList();
    }

    @Override
    public Usuario findById(int id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new NoDataFoundException("No existe el usuario con id: %d".formatted(id)));
    }

    @Override
    public Usuario update(UsuarioRequestDto usuario, Integer id) {

        usuarioRepository.findByEmail(usuario.getEmail()).orElseThrow(
                () -> new EmailAlreadyException("El email: %s ya esta registrado".formatted(usuario.getEmail())));

        Usuario registro = usuarioRepository.findById(id).orElseThrow(
                () -> new NoDataFoundException("No existe el registro con el id: %d".formatted(id)));
        registro.setRol(Rol.valueOf(usuario.getRol()));
        registro.setEmail(usuario.getEmail());
        registro.setPassword(encoder.encode(usuario.getPassword()));
        usuarioRepository.save(registro);
        return registro;

    }

    @Override
    public Usuario save(UsuarioRequestDto usuario) {

       var emailRegister = usuarioRepository.findByEmail(usuario.getEmail());
       if(emailRegister.isPresent()){
            throw new EmailAlreadyException("El email: %s ya esta registrado".formatted(usuario.getEmail()));
        }

        var user = new Usuario();
        user.setPassword(encoder.encode(usuario.getPassword()));
        user.setEmail(usuario.getEmail());
        user.setRol(Rol.valueOf(usuario.getRol()));
        var result = usuarioRepository.save(user);
        return result;
    }

    @Override
    public void delete(int id) {
        var userDb = findById(id); 
        usuarioRepository.delete(userDb);
    }


    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            var usuario = usuarioRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();
            var jwtToken = jwtService.generateToken(usuario);
            return new LoginResponseDto(usuarioConverter.fromEntity(usuario), jwtToken);  
    }

}
