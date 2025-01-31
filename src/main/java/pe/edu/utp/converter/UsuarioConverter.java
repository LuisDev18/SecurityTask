package pe.edu.utp.converter;

import org.springframework.stereotype.Component;
import pe.edu.utp.dto.UsuarioRequestDto;
import pe.edu.utp.dto.UsuarioResponseDto;
import pe.edu.utp.entity.Rol;
import pe.edu.utp.entity.Usuario;

@Component
public class UsuarioConverter extends AbstractConverter<Usuario, UsuarioResponseDto> {

  @Override
  public UsuarioResponseDto fromEntity(Usuario entity) {
    if (entity == null) return null;

    return UsuarioResponseDto.builder()
        .id(entity.getId())
        .email(entity.getEmail())
        .activo(entity.isActivo())
        .rol(entity.getRol().name())
        .build();
  }

  @Override
  public Usuario fromDto(UsuarioResponseDto dto) {
    if (dto == null) {
      return null;
    } else {
      Rol rol = Rol.valueOf(dto.getRol().toUpperCase());
      return Usuario.builder()
          .id(dto.getId())
          .email(dto.getEmail())
          .activo(dto.isActivo())
          .rol(rol)
          .build();
    }
  }

  public Usuario registro(UsuarioRequestDto dto) {
    if (dto == null) return null;
    Rol rol = Rol.valueOf(dto.getRol().toUpperCase());
    return Usuario.builder().email(dto.getEmail()).password(dto.getPassword()).rol(rol).build();
  }
}
