package pe.edu.utp.repository;

import java.util.List;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.utp.entity.Articulo;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
  @Query(
    """
           SELECT a FROM Articulo a
           WHERE (:categoria IS NULL OR  a.categoria = :categoria)
           AND (:marca IS NULL OR a.marca = :marca)
           AND (:precioMin IS NULL OR a.precio >= :precioMin)
           AND (:precioMax IS NULL OR a.precio <= :precioMax)
           """
  )
  @QueryHints(
    {
      @QueryHint(name = "org.hibernate.readOnly", value = "true"),
      @QueryHint(name = "org.hibernate.fetchSize", value = "50"),
      @QueryHint(name = "org.hibernate.cacheable", value = "true"),
      @QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "USE"),
      @QueryHint(name = "jakarta.persistence.cache.storeMode", value = "USE"),
    }
  )
  List<Articulo> findByCategoriaAndMarcaAndPrecioBetween(
    @Param("categoria") String categoria,
    @Param("marca") String marca,
    @Param("precioMin") Double precioMin,
    @Param("precioMax") Double precioMax,
    Pageable pageable
  );

  @Procedure(name = "updateStockProcedure")
  void updateStook(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

  @Query(value = "Select get_total_price(:productId)", nativeQuery = true)
  Double getTotalPrice(Integer productId);
}
