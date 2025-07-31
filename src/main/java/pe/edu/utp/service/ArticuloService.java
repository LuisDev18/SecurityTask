package pe.edu.utp.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.dto.ArticuloResponseDto;
import pe.edu.utp.entity.Articulo;


public interface ArticuloService {

	 List<ArticuloResponseDto> findAll(Pageable page);
	 List<ArticuloResponseDto> findByCategoriaAndMarcaAndPrecio(String categoria, String marca,Double precioMin, Double precioMax, Pageable pageable);
	 ArticuloResponseDto findById(Integer id);
	 Articulo save(ArticuloDto articulo);
	 Articulo update(ArticuloDto articulo, Integer id);
	 Articulo partialUpdate(Integer id, Map<String, Object> fields);
	 void delete(Integer id);
}
