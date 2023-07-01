package pe.edu.utp.converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConverter<E, D> {

  public abstract D fromEntity(E entity);

  public abstract E fromDTO(D dto);

  public List<D> fromEntity(List<E> entitys) {
    return entitys.stream().map(e -> fromEntity(e)).collect(Collectors.toList());
  }

  public List<E> fromDTO(List<D> DTOList) {
    return DTOList.stream().map(e -> fromDTO(e)).collect(Collectors.toList());
  }
}
