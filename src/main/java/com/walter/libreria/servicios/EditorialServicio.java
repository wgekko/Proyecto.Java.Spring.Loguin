package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Editorial;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.repositorios.EditorialRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EditorialServicio {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Autowired
    private LibroServicio libroServicio;
   
    @Transactional
    public void agregarEditorial(String nombre) throws Exception {
        try {
            Editorial editorial = new Editorial();           
            validar(nombre);          
            editorial.setAlta(true);
            editorial.setNombre(nombre);           
            editorialRepositorio.save(editorial);
        } catch (Exception e) {
            throw new Exception("Error al intentar guardar la editorial.");
        }
    }

    @Transactional
    public void modificarEditorial(String id, String nombre) throws Exception {
        try {            
            validar(nombre);
            Optional<Editorial> respuesta = editorialRepositorio.findById(id);
            if (respuesta.isPresent()) { 
                Editorial editorial = respuesta.get();              
                editorial.setNombre(nombre);                
                editorialRepositorio.save(editorial);
            } else { 
                throw new Exception("No existe la Editorial con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("Error al intentar modificar la Editorial.");
        }
    }
    
    @Transactional
    public void eliminarEditorial(String id) throws Exception {
        try {
            Editorial editorial = editorialRepositorio.getById(id);
            if (editorial != null) {                
                List<Libro> libros = libroServicio.buscarPorEditorial(editorial.getId());
                for (Libro libro : libros) {
                    libroServicio.eliminarLibro(libro.getId());
                }               
                editorialRepositorio.delete(editorial);
            } else { 
                throw new Exception("No existe la editorial con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("Error al intentar eliminar la editorial.");
        }
    }
  
    @Transactional
    public void baja(String id) throws Exception {
        try {
            Editorial editorial = editorialRepositorio.getById(id);
            if (editorial != null) {                 
                List<Libro> libros = libroServicio.buscarPorEditorial(id);
                if (libros != null) {
                    for (Libro libro : libros) {
                        if (libro.isAlta()) {
                            libroServicio.baja(libro.getId());
                        }
                    }
                }
                editorial.setAlta(false);              
                editorialRepositorio.save(editorial);
            } else { 
                throw new Exception("No existe la editorial con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("Error al intentar dar de baja la editorial.");
        }
    }
 
    @Transactional
    public void alta(String id) throws Exception {
        try {
            Editorial editorial = editorialRepositorio.getById(id);
            if (editorial != null) { 
                editorial.setAlta(true);               
                editorialRepositorio.save(editorial);
            } else { 
                throw new Exception("No existe la editorial con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("Error al intentar dar de alta la editorial.");
        }
    }
   
    public void validar(String nombre) throws Exception {
        if (nombre == null) {
            throw new Exception("Nombre no válido.");
        }
    }
 
    public Editorial buscarPorNombre(String nombre) {
        return editorialRepositorio.buscarPorNombre(nombre);
    }
  
    public List<Editorial> findAll() {
        return editorialRepositorio.findAll();
    }

    public Editorial getById(String id) {
        return editorialRepositorio.getById(id);
    }

}
