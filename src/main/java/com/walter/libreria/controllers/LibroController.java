package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.entidades.Editorial;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.servicios.AutorServicio;
import com.walter.libreria.servicios.EditorialServicio;
import com.walter.libreria.servicios.LibroServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/libros")
public class LibroController {

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private AutorServicio autorServicio;

    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/admin-libros")
    public String administradorLibros(ModelMap model) {       
        List<Libro> libros = libroServicio.findAll();
        model.put("libros", libros);
        List<Libro> librosDeBaja = libroServicio.listarDeBaja();
        model.put("librosDeBaja", librosDeBaja);
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        List<Editorial> editoriales = editorialServicio.findAll();
        model.put("editoriales", editoriales);
        return "admin-libro.html";
    }
 
    @PostMapping("/registrar-libro")
    public String registrarLibro(ModelMap model, @RequestParam(required = false) String id, MultipartFile archivo, Long isbn, String titulo, Integer anio, String descripcion, Integer ejemplares, String idAutor, String nuevoAutor, String idEditorial, String nuevaEditorial) {
        Autor autor;
        Editorial editorial;
        try {           
            try {
                if (nuevoAutor == null || nuevoAutor.isEmpty()) {
                    autor = autorServicio.getById(idAutor);
                } else {
                    autorServicio.agregarAutor(nuevoAutor);
                    autor = autorServicio.buscarPorNombre(nuevoAutor);
                }
            } catch (Exception e) {
                throw new Exception("... Debe seleccionar un Autor.");
            }            
            try {
                if (nuevaEditorial == null || nuevaEditorial.isEmpty()) {
                    editorial = editorialServicio.getById(idEditorial);
                } else {
                    editorialServicio.agregarEditorial(nuevaEditorial);
                    editorial = editorialServicio.buscarPorNombre(nuevaEditorial);
                }
            } catch (Exception e) {
                throw new Exception("... Debe seleccionar una Editorial.");
            }

            libroServicio.agregarLibro(archivo, isbn, titulo, anio, descripcion, ejemplares, autor, editorial);         
            model.put("success", "El libro '" + libroServicio.buscarPorIsbn(isbn).getTitulo().toUpperCase() + "' fue registrado en forma exitosa .");           
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Libro> librosDeBaja = libroServicio.listarDeBaja();
            model.put("librosDeBaja", librosDeBaja);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
        } catch (Exception e) {            
            if (e.getMessage() == null || isbn == null || anio == null || descripcion == null || ejemplares == null || idAutor == null || idEditorial == null) {
                model.put("error", "ERROR en la elección de guardar el libro: faltó completar algún dato .");
            } else {
                model.put("error", "ERROR en la elección de guardar el libro  : " + e.getMessage());
            }          
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Libro> librosDeBaja = libroServicio.listarDeBaja();
            model.put("librosDeBaja", librosDeBaja);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
        }
        return "admin-libro.html";
    }
  
    @GetMapping("/modificar-libro-datos/{idLibroModif}")
    public String datosLibro(ModelMap model,
            @PathVariable String idLibroModif
    ) {
        Libro libro = libroServicio.getById(idLibroModif);
        model.put("libroModif", libro);
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        List<Editorial> editoriales = editorialServicio.findAll();
        model.put("editoriales", editoriales);
        return "modif-libro.html";
    }
   
    @PostMapping("/modificar-libro")
    public String modificarLibro(ModelMap model, @RequestParam String id, MultipartFile archivo, @RequestParam String isbn, @RequestParam String titulo, @RequestParam Integer anio, @RequestParam String descripcion, @RequestParam Integer ejemplares, @RequestParam String idAutor, @RequestParam String idEditorial) {

        try {
            Autor autor = autorServicio.getById(idAutor);
            Editorial editorial = editorialServicio.getById(idEditorial);
            if (isbn.isEmpty()) {
                throw new Exception("ISBN no válido.");
            }
            libroServicio.modificarLibro(id, archivo, Long.parseLong(isbn), titulo, anio, descripcion, ejemplares, autor, editorial);            
            model.put("success", "El libro '" + libroServicio.getById(id).getTitulo().toUpperCase() + "' el registro fue modificado en forma exitosa .");            
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Libro> librosDeBaja = libroServicio.listarDeBaja();
            model.put("librosDeBaja", librosDeBaja);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
            return "admin-libro.html";
        } catch (Exception e) {
            if (e.getMessage() == null || isbn == null || anio == null || descripcion == null || ejemplares == null || idAutor == null || idEditorial == null) {
                model.put("error", "ERROR en la elección de modificar el libro: faltó completar algún dato.");
            } else {
                model.put("error", "ERROR en la elección de modificar el libro : " + e.getMessage());
            }          
            Libro libro = libroServicio.getById(id);
            model.put("libroModif", libro);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
            return "modif-libro.html";
        }
    }

    @GetMapping("/eliminar-libro/{id}")
    public String eliminarLibro(ModelMap model, @PathVariable String id) {
        try {
            String titulo = libroServicio.getById(id).getTitulo().toUpperCase();           
            libroServicio.eliminarLibro(id);           
            model.put("success", "El libro '" + titulo + "' el registro fue eliminado en forma exitosa .");         
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Libro> librosDeBaja = libroServicio.listarDeBaja();
            model.put("librosDeBaja", librosDeBaja);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
        } catch (Exception e) {         
            model.put("error", "ERROR, en la elección  de eliminar el libro : " + e.getMessage());           
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
        }
        return "admin-libro.html";
    }

    @GetMapping("/baja/{id}")
    public String baja(ModelMap model, @PathVariable String id) {
        try {
            libroServicio.baja(id);           
            model.put("success", "El libro '" + libroServicio.getById(id).getTitulo().toUpperCase() + "' ha sido  borrado de forma exitosa.");
        } catch (Exception e) {            
            model.put("error", "ERROR en la elección de dar de baja el libro : " + e.getMessage());
        }       
        List<Libro> libros = libroServicio.findAll();
        model.put("libros", libros);
        List<Libro> librosDeBaja = libroServicio.listarDeBaja();
        model.put("librosDeBaja", librosDeBaja);
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        List<Editorial> editoriales = editorialServicio.findAll();
        model.put("editoriales", editoriales);
        return "admin-libro.html";
    }

    @GetMapping("/alta/{id}")
    public String alta(ModelMap model, @PathVariable String id) {
        try {
            Autor autor = libroServicio.getById(id).getAutor();
            if (autor.isAlta()) {               
                model.put("success", "El libro '" + libroServicio.getById(id).getTitulo().toUpperCase() + "' se ha registrado en forma  exitosa. ");
            } else {
                model.put("success", "El libro '" + libroServicio.getById(id).getTitulo().toUpperCase() + "' se ha registrado en forma  exitosa. "
                        + " al igual que su autor '" + autor.getNombre().toUpperCase() + "'.");
            }
            libroServicio.alta(id);           
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Libro> librosDeBaja = libroServicio.listarDeBaja();
            model.put("librosDeBaja", librosDeBaja);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
            return "admin-libro.html";
        } catch (Exception e) {      
            model.put("error", "ERROR en la elección de dar de alta el libro  : " + e.getMessage());
         
            List<Libro> libros = libroServicio.findAll();
            model.put("libros", libros);
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            List<Editorial> editoriales = editorialServicio.findAll();
            model.put("editoriales", editoriales);
            return "admin-libro.html";
        }
    }
}
