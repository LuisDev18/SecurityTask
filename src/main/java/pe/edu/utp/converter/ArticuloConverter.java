package pe.edu.utp.converter;

import org.springframework.stereotype.Component;

import pe.edu.utp.dto.ArticuloDTO;
import pe.edu.utp.entity.Articulo;

@Component
public class ArticuloConverter extends AbstractConverter<Articulo,ArticuloDTO>{

	@Override
	public ArticuloDTO fromEntity(Articulo entity) {
		
		if(entity==null)return null;
		return ArticuloDTO.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.precio(entity.getPrecio())
				.build();
	}

	@Override
	public Articulo fromDTO(ArticuloDTO dto) {
		if(dto==null)return null;
		return Articulo.builder()
				.id(dto.getId())
				.nombre(dto.getNombre())
				.precio(dto.getPrecio())
				.build();
	}

}
