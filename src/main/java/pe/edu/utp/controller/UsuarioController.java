package pe.edu.utp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.converter.UsuarioConverter;
import pe.edu.utp.dto.LoginRequestDto;
import pe.edu.utp.dto.LoginResponseDto;
import pe.edu.utp.dto.UsuarioRequestDTO;
import pe.edu.utp.dto.UsuarioResponseDTO;
import pe.edu.utp.entity.Usuario;
import pe.edu.utp.service.UsuarioService;
import pe.edu.utp.util.WrapperResponse;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioConverter converter;

    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> findAll(
            @RequestParam(value="email", required=false) String email,
            @RequestParam(value="offset",required=false, defaultValue = "0") int pageNumber,
            @RequestParam(value="limit",required=false, defaultValue = "5") int pageSize){

        Pageable pagina= PageRequest.of(pageNumber, pageSize);
        List<Usuario> registros;
        if(email==null) {
            registros=usuarioService.findAll(pagina);
        }else {
            registros=usuarioService.findByEmail(email, pagina);
        }
        List<UsuarioResponseDTO> registrosDTO=converter.fromEntity(registros);

        return new WrapperResponse(true,"success",registrosDTO).createResponse(HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UsuarioResponseDTO> create (@RequestBody UsuarioRequestDTO usuario){
        Usuario registro = usuarioService.save(converter.registro(usuario));
        return new WrapperResponse(true,"success",converter.fromEntity(registro)).createResponse(HttpStatus.CREATED);
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable("id") int id, @RequestBody UsuarioRequestDTO usuario){
        Usuario registro=usuarioService.update(converter.registro(usuario));
        if(registro==null) {
            return ResponseEntity.notFound().build();
        }
        return new WrapperResponse(true,"success",converter.fromEntity(registro)).createResponse(HttpStatus.OK);
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<UsuarioRequestDTO> delete(@PathVariable("id") int id){
        usuarioService.delete(id);
        return new WrapperResponse(true,"success",null).createResponse(HttpStatus.OK);
    }

    @PostMapping(value="/login")
    public ResponseEntity<WrapperResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto request){
        LoginResponseDto response=usuarioService.login(request);
        return new WrapperResponse<>(true,"success",response).createResponse(HttpStatus.OK);
    }
}
