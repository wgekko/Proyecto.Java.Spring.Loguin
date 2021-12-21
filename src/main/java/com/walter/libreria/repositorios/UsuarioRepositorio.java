package com.walter.libreria.repositorios;

import com.walter.libreria.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
 
    @Query("SELECT u FROM Usuario u WHERE u.mail = :mail")
    public Usuario buscarPorMail(@Param("mail") String mail);
    
    @Query("SELECT u FROM Usuario u WHERE u.baja IS null")
    public List<Usuario> buscarActivos();
  
    @Query("SELECT u FROM Usuario u WHERE u.baja IS NOT null")
    public List<Usuario> buscarInactivos();
}
