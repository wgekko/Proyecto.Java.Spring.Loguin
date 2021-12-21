package com.walter.libreria.repositorios;

import com.walter.libreria.entidades.Prestamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, String> {
  
    @Query("SELECT p FROM Prestamo p WHERE p.libro.id = :idLibro")
    public List<Prestamo> buscarPorLibro(@Param("idLibro") String idLibro);
    
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.id = :idUsuario")
    public List<Prestamo> buscarPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT p FROM Prestamo p WHERE p.alta = true")
    public List<Prestamo> buscarPrestamosAlta();

    @Query("SELECT p FROM Prestamo p WHERE p.alta = false")
    public List<Prestamo> buscarPrestamosBaja();
 
    @Query("SELECT p FROM Prestamo p WHERE p.alta = true AND p.usuario.id = :idUsuario")
    public List<Prestamo> buscarPrestamosAltaUsuario(@Param("idUsuario") String idUsuario);
   
    @Query("SELECT p FROM Prestamo p WHERE p.alta = false AND p.usuario.id = :idUsuario")
    public List<Prestamo> buscarPrestamosBajaUsuario(@Param("idUsuario") String idUsuario);

}
