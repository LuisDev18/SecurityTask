package pe.edu.utp.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.Exception.NoDataFoundException;
import pe.edu.utp.converter.ArticuloConverter;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.service.ArticuloService;

@RestController
@RequestMapping("/v1/articulos")
@Validated
public class ArticuloController {

  private final ArticuloService articuloService;

  private final ArticuloConverter articuloConverter;

  @Autowired
  public ArticuloController(ArticuloService articuloService, ArticuloConverter articuloConverter) {
    this.articuloService = articuloService;
    this.articuloConverter = articuloConverter;
  }

  @GetMapping
  public ResponseEntity<List<ArticuloDto>> getAll(
      @RequestParam(value = "nombreArticulo", required = false, defaultValue = "")
          String nombreArticulo,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    List<Articulo> articulos;
    if (nombreArticulo == null) {
      articulos = articuloService.findAll(pageable);
    } else {
      articulos = articuloService.finByNombre(nombreArticulo, pageable);
    }

    if (articulos.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    List<ArticuloDto> articulosDto = articuloConverter.fromEntity(articulos);
    return ResponseEntity.ok(articulosDto);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> findById(@PathVariable("id") int id)
      throws NoDataFoundException {
    Articulo articulo = articuloService.findById(id);
    if (articulo == null) {
      return ResponseEntity.notFound().build();
    }
    ArticuloDto articuloDto = articuloConverter.fromEntity(articulo);
    return ResponseEntity.ok(articuloDto);
  }

  @PostMapping
  public ResponseEntity<ArticuloDto> create(@Valid @RequestBody ArticuloDto articulo) {
    Articulo registro = articuloService.save(articuloConverter.fromDTO(articulo));
    ArticuloDto registroDto = articuloConverter.fromEntity(registro);
    return ResponseEntity.status(HttpStatus.CREATED).body(registroDto);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> update(
      @PathVariable("id") int id, @Valid @RequestBody ArticuloDto articuloDto) {
    Articulo articuloUpdate = articuloService.update(articuloConverter.fromDTO(articuloDto));
    if (articuloUpdate == null) {
      return ResponseEntity.notFound().build();
    }
    ArticuloDto articuloUpdateDto = articuloConverter.fromEntity(articuloUpdate);
    return ResponseEntity.ok(articuloUpdateDto);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> delete(@PathVariable("id") int id)
      throws NoDataFoundException {
    articuloService.delete(id);
    return ResponseEntity.ok(null);
  }
}
