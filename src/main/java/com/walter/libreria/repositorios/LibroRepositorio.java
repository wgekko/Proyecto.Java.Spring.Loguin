package com.walter.libreria.repositorios;

import com.walter.libreria.entidades.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    @Query("SELECT lib FROM Libro lib WHERE lib.isbn = :isbn")
    public Libro buscarPorIsbn(@Param("isbn") Long isbn);

    @Query("SELECT lib FROM Libro lib WHERE lib.autor.id = :id")
    public List<Libro> buscarPorAutor(@Param("id") String id);

    @Query("SELECT lib FROM Libro lib WHERE lib.editorial.id = :id")
    public List<Libro> buscarPorEditorial(@Param("id") String id);    
 
    @Override
    @Query("SELECT lib FROM Libro lib WHERE lib.alta IS true ORDER BY lib.titulo ASC")
    public List<Libro> findAll();    
    
    @Query("SELECT lib FROM Libro lib WHERE lib.alta IS false ORDER BY lib.titulo ASC")
    public List<Libro> listarDeBaja();
    
}