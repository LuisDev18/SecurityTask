package pe.edu.utp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ArticuloResponseDto {

  private Integer id;
  private String nombre;
  private String marca;
  private String categoria;
  private Double precio;
  private int stock;
}
