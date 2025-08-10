package pe.edu.utp.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import pe.edu.utp.converter.ArticuloConverter;
import pe.edu.utp.converter.ArticuloResponseConverter;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.dto.ArticuloResponseDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.repository.ArticuloRepository;
import pe.edu.utp.repository.UsuarioRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticuloServiceImpl implements ArticuloService {

  private final ArticuloRepository articuloRepository;
  private final ArticuloConverter articuloConverter;
  private final ArticuloResponseConverter articuloResponseConverter;
  private final UsuarioRepository usuarioRepository;

  @Override
  public List<ArticuloResponseDto> findAll(Pageable page) {
    var articulos = articuloRepository.findAll(page).toList();
    return articuloResponseConverter.fromEntity(articulos);
  }

  @Override
  public List<ArticuloResponseDto> findByCategoriaAndMarcaAndPrecio(
    String categoria,
    String marca,
    Double precioMin,
    Double precioMax,
    Pageable pageable
  ) {
    return articuloResponseConverter.fromEntity(
      articuloRepository.findByCategoriaAndMarcaAndPrecioBetween(
        categoria,
        marca,
        precioMin,
        precioMax,
        pageable
      )
    );
  }

  @Override
  public ArticuloResponseDto findById(Integer id) {
    Articulo articuloDB = findByArticleId(id);
    return articuloResponseConverter.fromEntity(articuloDB);
  }

  @Override
  public Articulo save(ArticuloDto articulo, String email) {
    var user = usuarioRepository.findByEmail(email);
    var entity = articuloConverter.fromDto(articulo);
    entity.setUsuario(user.get());
    return articuloRepository.save(entity);
  }

  @Override
  public Articulo update(ArticuloDto articuloDto, Integer id) {
    Articulo registro = findByArticleId(id);
    registro.setNombre(articuloDto.getNombre());
    registro.setPrecio(articuloDto.getPrecio());
    return articuloRepository.save(registro);
  }

  @Override
  public void delete(Integer id) {
    Articulo registro = findByArticleId(id);
    articuloRepository.delete(registro);
  }

  private Articulo findByArticleId(Integer id) {
    return articuloRepository
      .findById(id)
      .orElseThrow(() ->
        new NoDataFoundException("No existe un articulo con ese id: %d".formatted(id))
      );
  }

  @Override
  public Articulo partialUpdate(Integer id, Map<String, Object> fields) {
    var articuloDb = findByArticleId(id);
    fields.forEach((key, value) -> {
      Field field = ReflectionUtils.findField(Articulo.class, key);
      field.setAccessible(true);
      ReflectionUtils.setField(field, articuloDb, value);
    });
    return articuloRepository.save(articuloDb);
  }

  @Override
  public Articulo discountStoock(Integer id, Integer stook) {
    articuloRepository.updateStook(id, stook);
    return findByArticleId(id);
  }

  @Override
  public Double calculateTotalPrice(Integer productId) {
    return articuloRepository.getTotalPrice(productId);
  }
}
