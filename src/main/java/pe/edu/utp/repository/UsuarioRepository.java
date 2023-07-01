package pe.edu.utp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.entity.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
  List<Usuario> findByEmailContaining(String email, Pageable page);

  Optional<Usuario> findByEmail(String email);
}
