package pe.edu.utp.converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConverter<E, D> {

  public abstract D fromEntity(E entity);

  public List<D> fromEntity(List<E> entitys) {
    return entitys.stream().map(e -> fromEntity(e)).collect(Collectors.toList());
  }

  public abstract E fromDto(D dto);

  public List<E> fromDto(List<D> DtoList) {
    return DtoList.stream().map(e -> fromDto(e)).collect(Collectors.toList());
  }
}
