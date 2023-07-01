package pe.edu.utp.converter;

import org.springframework.stereotype.Component;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.entity.Articulo;

@Component
public class ArticuloConverter extends AbstractConverter<Articulo, ArticuloDto> {

  @Override
  public ArticuloDto fromEntity(Articulo entity) {

    if (entity == null) {
      return null;
    } else {
      return ArticuloDto.builder()
          .id(entity.getId())
          .nombre(entity.getNombre())
          .precio(entity.getPrecio())
          .build();
    }
  }

  @Override
  public Articulo fromDto(ArticuloDto dto) {
    if (dto == null) {
      return null;
    } else {
      return Articulo.builder()
          .id(dto.getId())
          .nombre(dto.getNombre())
          .precio(dto.getPrecio())
          .build();
    }
  }
}
