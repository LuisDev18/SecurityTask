package pe.edu.utp.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.utp.entity.Articulo;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {

  List<Articulo> findByCategoriaAndMarcaAndPrecioBetween(String categoria, String marca,Double precioMin,Double precioMax,Pageable pageable);

}
