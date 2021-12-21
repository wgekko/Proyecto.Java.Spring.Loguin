package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.servicios.AutorServicio;
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

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/autores")
public class AutorController {

    @Autowired
    private AutorServicio autorServicio;

    @GetMapping("/admin-autores")
    public String administradorAutores(ModelMap model) {
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        return "admin-autor.html";
    }

    @PostMapping("/registrar-autor")
    public String registrarAutor(ModelMap model, @RequestParam(required = false) String id, String nombre) {

        try {
            autorServicio.agregarAutor(nombre);            
            model.put("success", "El autor '" + nombre.toUpperCase() + "' fue registrado en forma exitosa.");       
        } catch (Exception e) {
            if (e.getMessage() == null || nombre == null) {
                model.put("error", "ERROR en la elección de guardar el autor: faltó completar algún dato.");
            } else {
                model.put("error", "ERROR en la elección de guardar el autor : " + e.getMessage());
            }
        }
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        return "admin-autor.html";
    }

    @GetMapping("/modificar-autor-datos/{idAutorModif}")
    public String datosAutor(ModelMap model, @PathVariable String idAutorModif) {
        Autor autor = autorServicio.getById(idAutorModif);
        model.put("autorModif", autor);
        return "modif-autor.html";
    }

    @PostMapping("/modificar-autor")
    public String modificarAutor(ModelMap model, @RequestParam String id, @RequestParam String nombre) {

        try {
            autorServicio.modificarAutor(id, nombre);         
            model.put("success", "El autor '" + nombre.toUpperCase() + "' fue modificado en forma exitosa.");          
            List<Autor> autores = autorServicio.findAll();
            model.put("autores", autores);
            return "admin-autor.html";
        } catch (Exception e) {            
            Autor autor = autorServicio.getById(id);
            model.put("autorModif", autor);
            model.put("error", "ERROR en la elección de modificar el autor : " + e.getMessage());
            return "modif-autor.html";
        }
    }

    @GetMapping("/eliminar-autor/{id}")
    public String eliminarAutor(ModelMap model, @PathVariable String id) {
        try {
            String nombre = autorServicio.getById(id).getNombre().toUpperCase();      
            autorServicio.eliminarAutor(id);
            model.put("success", "El autor '" + nombre + "' fue eliminado en forma  exitosa.");
        } catch (Exception e) {       
            model.put("error", "ERROR en la elección de eliminar el autor : " + e.getMessage());
        }    
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        return "admin-autor.html";
    }

    @GetMapping("/baja/{id}")
    public String baja(ModelMap model, @PathVariable String id) {
        try {
            autorServicio.baja(id);         
            model.put("success", "El autor '" + autorServicio.getById(id).getNombre().toUpperCase() + "' fue dado de baja en forma exitosa.");
        } catch (Exception e) {   
            model.put("error", "ERROR en la elección de baja el autor : " + e.getMessage());
        }      
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        return "admin-autor.html";
    }

    @GetMapping("/alta/{id}")
    public String alta(ModelMap model, @PathVariable String id) {
        try {
            autorServicio.alta(id);   
            model.put("success", "El autor '" + autorServicio.getById(id).getNombre().toUpperCase() + "' fue dado de alta en forma exitosa.");
        } catch (Exception e) {       
            model.put("error", "ERROR en la elección de dar de alta el autor : " + e.getMessage());
        }
        List<Autor> autores = autorServicio.findAll();
        model.put("autores", autores);
        return "admin-autor.html";
    }

}
