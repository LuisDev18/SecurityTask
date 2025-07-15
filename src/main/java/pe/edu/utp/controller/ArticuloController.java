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
import pe.edu.utp.exception.NoDataFoundException;
import pe.edu.utp.converter.ArticuloConverter;
import pe.edu.utp.dto.ArticuloDto;
import pe.edu.utp.entity.Articulo;
import pe.edu.utp.service.ArticuloService;

@RestController
@RequestMapping("/articulos")
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

      @RequestParam(value = "marca", required = false, defaultValue = "") String marca,
      @RequestParam(value = "categoria", required = false, defaultValue = "") String categoria,
      @RequestParam(value = "precioMin", required = false, defaultValue = "0") Double precioMin,
      @RequestParam(value = "precioMax", required = false, defaultValue = "0") Double precioMax,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", required = false, defaultValue = "5") int pageSize) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    List<Articulo> articulos;
    if (marca == null || categoria == null || precioMin == null || precioMax == null) {
      articulos = articuloService.findAll(pageable);
    } else {
      articulos = articuloService.findByCategoriaAndMarcaAndPrecio(categoria,marca,precioMin,precioMax, pageable);

    }

    if (articulos.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    List<ArticuloDto> articulosDTO = articuloConverter.fromEntity(articulos);
    return ResponseEntity.ok(articulosDTO);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> findById(@PathVariable("id") int id)
      throws NoDataFoundException {
    Articulo articulo = articuloService.findById(id);
    if (articulo == null) {
      return ResponseEntity.notFound().build();
    }
    ArticuloDto articuloDTO = articuloConverter.fromEntity(articulo);
    return ResponseEntity.ok(articuloDTO);
  }

  @PostMapping
  public ResponseEntity<ArticuloDto> create(@Valid @RequestBody ArticuloDto articulo) {
    Articulo registro = articuloService.save(articuloConverter.fromDto(articulo));
    ArticuloDto registroDTO = articuloConverter.fromEntity(registro);
    return ResponseEntity.status(HttpStatus.CREATED).body(registroDTO);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> update(
      @PathVariable("id") int id, @Valid @RequestBody ArticuloDto articuloDto) {
    Articulo articuloUpdate = articuloService.update(articuloConverter.fromDto(articuloDto));

    if (articuloUpdate == null) {
      return ResponseEntity.notFound().build();
    }
    ArticuloDto articuloUpdateDTO = articuloConverter.fromEntity(articuloUpdate);
    return ResponseEntity.ok(articuloUpdateDTO);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<ArticuloDto> delete(@PathVariable("id") int id)
      throws NoDataFoundException {
    articuloService.delete(id);
    return ResponseEntity.ok(null);
  }
}
