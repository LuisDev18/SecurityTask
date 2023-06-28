package pe.edu.utp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import pe.edu.utp.Exception.NoDataFoundException;
import pe.edu.utp.entity.Articulo;


public interface ArticuloService {

	public List<Articulo> findAll(Pageable page);
	public List<Articulo> finByNombre(String nombre, Pageable pageable);
	public Articulo findById(int id)throws NoDataFoundException;
	public Articulo save(Articulo articulo);
	public Articulo update(Articulo articulo);
	public void delete(int id)throws NoDataFoundException;
}
