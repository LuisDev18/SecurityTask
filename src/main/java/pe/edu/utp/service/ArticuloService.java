package pe.edu.utp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.entity.Articulo;


public interface ArticuloService {

	 List<Articulo> findAll(Pageable page);

	 List<Articulo> findByCategoriaAndMarcaAndPrecio(String categoria, String marca,Double precioMin, Double precioMax, Pageable pageable);
	 Articulo findById(int id)throws NoDataFoundException;
	 Articulo save(Articulo articulo);
	 Articulo update(Articulo articulo);
	 void delete(int id)throws NoDataFoundException;
}
