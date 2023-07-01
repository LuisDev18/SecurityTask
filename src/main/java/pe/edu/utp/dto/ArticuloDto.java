package pe.edu.utp.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ArticuloDto {

  private int id;

  @NotBlank(message = "El campo nombre no puede estar vacio")
  private String nombre;

  private Double precio;
}
