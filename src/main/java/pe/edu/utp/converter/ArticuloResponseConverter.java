package pe.edu.utp.converter;

import org.springframework.stereotype.Component;

import pe.edu.utp.dto.ArticuloResponseDto;
import pe.edu.utp.entity.Articulo;

@Component
public class ArticuloResponseConverter extends AbstractConverter<Articulo, ArticuloResponseDto> {

  @Override
  public ArticuloResponseDto fromEntity(Articulo entity) {
    if (entity == null) {
      return null;
    } else {
      return ArticuloResponseDto
        .builder()
        .id(entity.getId())
        .nombre(entity.getNombre())
        .precio(entity.getPrecio())
        .marca(entity.getMarca())
        .categoria(entity.getCategoria())
        .stock(entity.getStock())
        .build();
    }
  }

  @Override
  public Articulo fromDto(ArticuloResponseDto dto) {
    if (dto == null) {
      return null;
    } else {
      return Articulo
        .builder()
        .id(dto.getId())
        .nombre(dto.getNombre())
        .precio(dto.getPrecio())
        .marca(dto.getMarca())
        .categoria(dto.getCategoria())
        .stock(dto.getStock())
        .build();
    }
  }
}
