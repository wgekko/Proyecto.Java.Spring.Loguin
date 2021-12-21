package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.repositorios.AutorRepositorio;
import com.walter.libreria.repositorios.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AutorServicio {

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private LibroServicio libroServicio;
 
    @Transactional
    public void agregarAutor(String nombre) throws Exception {
        try {
            Autor autor = new Autor();        
            validar(nombre);            
            autor.setAlta(true);
            autor.setNombre(nombre);        
            autorRepositorio.save(autor);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void modificarAutor(String id, String nombre) throws Exception {
        try {         
            validar(nombre);
            Optional<Autor> respuesta = autorRepositorio.findById(id);
            if (respuesta.isPresent()) { 
                Autor autor = respuesta.get();               
                autor.setNombre(nombre);              
                autorRepositorio.save(autor);
            } else { 
                throw new Exception("No existe el autor con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void eliminarAutor(String id) throws Exception {
        try {
            Autor autor = autorRepositorio.getById(id);
            if (autor != null) { 
               
                List<Libro> libros = libroServicio.buscarPorAutor(id);
                for (Libro libro : libros) {
                    libroServicio.eliminarLibro(libro.getId());
                }
                
                autorRepositorio.delete(autor);
            } else { 
                throw new Exception("No existe el autor con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void baja(String id) throws Exception {
        try {
            Autor autor = autorRepositorio.getById(id);
            if (autor != null) {               
                List<Libro> libros = libroServicio.buscarPorAutor(id);
                if (libros != null) {
                    for (Libro libro : libros) {
                        if (libro.isAlta()) {
                            libroServicio.baja(libro.getId());
                        }
                    }
                }
                autor.setAlta(false);               
                autorRepositorio.save(autor);
            } else { 
                throw new Exception("No existe el autor con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("ERROR al intentar dar de baja el Autor.");
        }
    }

    @Transactional
    public void alta(String id) throws Exception {
        try {
            Optional<Autor> respuesta = autorRepositorio.findById(id);
            if (respuesta.isPresent()) { 
                Autor autor = respuesta.get();
                autor.setAlta(true);               
                autorRepositorio.save(autor);          
                List<Libro> libros = libroRepositorio.buscarPorAutor(id);
                for (Libro libro : libros) {
                    libroServicio.alta(libro.getId());
                }
            } else { 
                throw new Exception("No existe el autor con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("ERROR al intentar dar de alta el Autor.");
        }
    }

    public void validar(String nombre) throws Exception {
        if (nombre == null || nombre.isEmpty()) {
            throw new Exception("Nombre no válido.");
        }
    }

    public Autor buscarPorNombre(String nombre) {
        return autorRepositorio.buscarPorNombre(nombre);
    }
    
    public List<Autor> findAll() {
        return autorRepositorio.findAll();
    }


    public Autor getById(String id) {
        return autorRepositorio.getById(id);
    }
}
