package pe.edu.utp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.utp.converter.ArticuloConverter;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.repository.ArticuloRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticuloServiceImpl implements ArticuloService {

	private final ArticuloRepository articuloRepository;
	private final ArticuloConverter articuloConverter;

	@Override
	public List<ArticuloDto> findAll(Pageable page) {
		var articulos = articuloRepository.findAll(page).toList();
		return articuloConverter.fromEntity(articulos);

	}

	@Override
	@Transactional(readOnly = true)
	public List<ArticuloDto> findByCategoriaAndMarcaAndPrecio(String categoria, String marca, Double precioMin,
			Double precioMax, Pageable pageable) {
		var result = articuloConverter.fromEntity(
				articuloRepository.findByCategoriaAndMarcaAndPrecioBetween(categoria, marca, precioMin, precioMax,
						pageable));
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public ArticuloDto findById(int id) {
		Articulo articuloDB = findByArticleId(id);
		return articuloConverter.fromEntity(articuloDB);
	}

	@Override
	@Transactional
	public Articulo save(ArticuloDto articulo) {
		var entity = articuloConverter.fromDto(articulo);
		return articuloRepository.save(entity);
	}

	@Override
	@Transactional
	public Articulo update(ArticuloDto articuloDto, int id) {
		Articulo registro = findByArticleId(id);
		registro.setNombre(articuloDto.getNombre());
		registro.setPrecio(articuloDto.getPrecio());
		return articuloRepository.save(registro);
	}

	@Override
	@Transactional
	public void delete(int id) {
		Articulo registro = articuloRepository.findById(id).orElse(null);
		if (registro != null)
			articuloRepository.delete(registro);

	}

	private Articulo findByArticleId(int id) {

		return articuloRepository.findById(id).orElseThrow(
				() -> new NoDataFoundException("No existe un articulo con ese id: %d".formatted(id)));
	}

}
