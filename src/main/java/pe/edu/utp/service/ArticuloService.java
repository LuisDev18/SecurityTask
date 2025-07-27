package pe.edu.utp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.entity.Articulo;


public interface ArticuloService {

	 List<ArticuloDto> findAll(Pageable page);

	 List<ArticuloDto> findByCategoriaAndMarcaAndPrecio(String categoria, String marca,Double precioMin, Double precioMax, Pageable pageable);
	 ArticuloDto findById(int id);
	 Articulo save(ArticuloDto articulo);
	 Articulo update(ArticuloDto articulo, int id);
	 void delete(int id)throws NoDataFoundException;
}
