package pe.edu.utp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.converter.UsuarioConverter;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.dto.UsuarioRequestDto;
import pe.edu.utp.dto.UsuarioResponseDto;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.service.UsuarioService;
import pe.edu.utp.util.ConstantesHelpers;
import pe.edu.utp.util.WrapperResponse;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final UsuarioConverter converter;


  @GetMapping()
  public ResponseEntity<List<UsuarioResponseDto>> findAll(
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "offset", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int pageSize) {

    Pageable pagina = PageRequest.of(pageNumber, pageSize);
    List<Usuario> registros;
    if (email == null) {
      registros = usuarioService.findAll(pagina);
    } else {
      registros = usuarioService.findByEmail(email, pagina);
    }
    List<UsuarioResponseDto> registrosDto = converter.fromEntity(registros);

    return new WrapperResponse(true, ConstantesHelpers.MESSAGE_SUCCESS, registrosDto).createResponse(HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<UsuarioResponseDto> create(@RequestBody UsuarioRequestDto usuario) {
    Usuario registro = usuarioService.save(converter.registro(usuario));
    return new WrapperResponse(true, ConstantesHelpers.MESSAGE_SUCCESS, converter.fromEntity(registro))
        .createResponse(HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<UsuarioResponseDto> update(
      @PathVariable("id") int id, @RequestBody UsuarioRequestDto usuario) {
    Usuario registro = usuarioService.update(converter.registro(usuario));
    if (registro == null) {
      return ResponseEntity.notFound().build();
    }
    return new WrapperResponse(true, ConstantesHelpers.MESSAGE_SUCCESS, converter.fromEntity(registro))
        .createResponse(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<UsuarioRequestDto> delete(@PathVariable("id") int id) {
    usuarioService.delete(id);
    return new WrapperResponse(true, ConstantesHelpers.MESSAGE_SUCCESS, null).createResponse(HttpStatus.OK);
  }

  @PostMapping(value = "/login")
  public ResponseEntity<WrapperResponse<LoginResponseDto>> login(
      @RequestBody LoginRequestDto request) {
    LoginResponseDto response = usuarioService.login(request);
    return new WrapperResponse<>(true, ConstantesHelpers.MESSAGE_SUCCESS, response).createResponse(HttpStatus.OK);
  }
}
