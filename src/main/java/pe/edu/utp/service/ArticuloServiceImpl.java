package pe.edu.utp.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.Exception.GeneralServiceException;
import pe.edu.utp.Exception.NoDataFoundException;
import pe.edu.utp.Exception.ValidateServiceException;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.repository.ArticuloRepository;

@Service
@Slf4j
public class ArticuloServiceImpl implements ArticuloService {

	@Autowired
	private ArticuloRepository articuloRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Articulo> findAll(Pageable page) {
		try {
			return articuloRepository.findAll(page).toList();
		}catch (ValidateServiceException| NoDataFoundException e){
			log.info(e.getMessage());
			throw e;
		}catch(Exception e){
			log.error(e.getMessage());
			throw new GeneralServiceException(e.getMessage());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<Articulo> finByNombre(String nombre, Pageable pageable) {
		return articuloRepository.findByNombreContaining(nombre, pageable);

	}

	@Override
	@Transactional(readOnly = true)
	public Articulo findById(int id) throws NoDataFoundException {
		Articulo articuloDB = articuloRepository.findById(id).orElse(null);
		if (articuloDB != null) {
			return articuloDB;
		} else {
			throw new NoDataFoundException("No existe un articulo con ese id");
		}

	}

	@Override
	@Transactional
	public Articulo save(Articulo articulo) {

		return articuloRepository.save(articulo);
	}

	@Override
	@Transactional
	public Articulo update(Articulo articulo) {
		Articulo registro = articuloRepository.findById(articulo.getId()).orElseThrow();
		registro.setNombre(articulo.getNombre());
		registro.setPrecio(articulo.getPrecio());
		return articuloRepository.save(registro);
	}

	@Override
	@Transactional
	public void delete(int id) throws NoDataFoundException {
		Articulo registro = articuloRepository.findById(id).orElse(null);
		if (registro != null) {
			articuloRepository.delete(registro);
		} else {
			throw new NoDataFoundException("No existe un articulo con ese id");
		}

	}

}
