package pe.edu.utp.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.exception.GeneralServiceException;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.exception.ValidateServiceException;
import pe.edu.utp.converter.UsuarioConverter;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.repository.UsuarioRepository;
import pe.edu.utp.security.JwtService;
import pe.edu.utp.validator.UsuarioValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService{

    private static final Logger logger= LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder encoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UsuarioConverter usuarioConverter;



    @Override
    public List<Usuario> findAll(Pageable page) {
      try {
          return usuarioRepository.findAll(page).toList();
      }catch (ValidateServiceException | NoDataFoundException e){
          logger.info(e.getMessage());
          throw e;
      }catch (Exception e){
          logger.error(e.getMessage());
          throw new GeneralServiceException(e.getMessage());
      }
    }

    @Override
    public List<Usuario> findByEmail(String email, Pageable page) {
       try {
           return usuarioRepository.findByEmailContaining(email, page);
       }catch (ValidateServiceException | NoDataFoundException e){
           logger.info(e.getMessage());
           throw e;
       }catch(Exception e){
           logger.error(e.getMessage());
           throw new GeneralServiceException(e.getMessage());
       }
    }

    @Override
    public Usuario findById(int id) {
        try {
            return usuarioRepository.findById(id).orElseThrow(
                    () -> new NoDataFoundException("No existe el usuario con id" + id)
            );
        }catch (ValidateServiceException | NoDataFoundException e){
            logger.info(e.getMessage());
            throw e;
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new GeneralServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario update(Usuario usuario) {

        try{
            UsuarioValidator.save(usuario);
            Usuario registroDB=  usuarioRepository.findByEmail(usuario.getEmail()).orElseThrow(
                    ()->new NoDataFoundException("No existe el usuario para el email ingresado")
            );
          if(registroDB!=null && !Objects.equals(registroDB.getId(), usuario.getId())){
             throw new ValidateServiceException("Ya existe un registro con el email " +usuario.getEmail());
          }
          Usuario registro= usuarioRepository.findById(usuario.getId()).orElseThrow(
                  ()-> new NoDataFoundException("No existe el registro con el id: "+usuario.getId())
          );
          registro.setRol(usuario.getRol());
          usuarioRepository.save(registro);
          return  registro;

        }catch(ValidateServiceException | NoDataFoundException e){
            logger.info(e.getMessage());
            throw e;
        }catch(Exception e){
            logger.error(e.getMessage());
            throw new GeneralServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        try{
            UsuarioValidator.save(usuario);
            Optional<Usuario> reg= usuarioRepository.findByEmail(usuario.getEmail());
            if(reg.isPresent()){
                throw new ValidateServiceException("Ya existe un registro con el email :" +usuario.getEmail());
            }
            String hashPassword=encoder.encode(usuario.getPassword());
            usuario.setPassword(hashPassword);
            usuario.setActivo(true);
            Usuario registro=usuarioRepository.save(usuario);
            return registro;
        }catch(ValidateServiceException | NoDataFoundException e) {
            logger.info(e.getMessage());
            throw e;
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new GeneralServiceException(e.getMessage());

       }
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(int id) {
            try {
                Usuario registro=usuarioRepository.findById(id).orElseThrow(()->new NoDataFoundException("No existe un registro con ese Id"));
                usuarioRepository.delete(registro);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new GeneralServiceException(e.getMessage());
            }
        }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            var usuario=usuarioRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow();
            var jwtToken=jwtService.generateToken(usuario);
            var refreshJwtToken=jwtService.generateRefreshToken(usuario);
            return new LoginResponseDto(usuarioConverter.fromEntity(usuario),jwtToken,refreshJwtToken);
        } catch (JwtException e) {
            logger.info(e.getMessage(),e);
            throw new ValidateServiceException(e.getMessage());
        }catch (Exception e) {
            logger.info(e.getMessage(),e);
            throw new ValidateServiceException(e.getMessage());
        }
    }

}
