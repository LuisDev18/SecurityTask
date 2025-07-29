package pe.edu.utp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.utp.converter.ArticuloConverter;
import pe.edu.utp.converter.ArticuloResponseConverter;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.dto.ArticuloResponseDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.repository.ArticuloRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticuloServiceImpl implements ArticuloService {

	private final ArticuloRepository articuloRepository;
	private final ArticuloConverter articuloConverter;
	private final ArticuloResponseConverter articuloResponseConverter;

	@Override
	public List<ArticuloResponseDto> findAll(Pageable page) {
		var articulos = articuloRepository.findAll(page).toList();
		return articuloResponseConverter.fromEntity(articulos);

	}

	@Override
	public List<ArticuloResponseDto> findByCategoriaAndMarcaAndPrecio(String categoria, String marca, Double precioMin,
			Double precioMax, Pageable pageable) {
		var result = articuloResponseConverter.fromEntity(
				articuloRepository.findByCategoriaAndMarcaAndPrecioBetween(categoria, marca, precioMin, precioMax,
						pageable));
		return result;
	}

	@Override
	public ArticuloResponseDto findById(int id) {
		Articulo articuloDB = findByArticleId(id);
		return articuloResponseConverter.fromEntity(articuloDB);
	}

	@Override
	public Articulo save(ArticuloDto articulo) {
		var entity = articuloConverter.fromDto(articulo);
		return articuloRepository.save(entity);
	}

	@Override
	public Articulo update(ArticuloDto articuloDto, int id) {
		Articulo registro = findByArticleId(id);
		registro.setNombre(articuloDto.getNombre());
		registro.setPrecio(articuloDto.getPrecio());
		return articuloRepository.save(registro);
	}

	@Override
	public void delete(int id) {
		Articulo registro = findByArticleId(id);
		articuloRepository.delete(registro);

	}

	private Articulo findByArticleId(int id) {
		return articuloRepository.findById(id).orElseThrow(
				() -> new NoDataFoundException("No existe un articulo con ese id: %d".formatted(id)));
	}

}
