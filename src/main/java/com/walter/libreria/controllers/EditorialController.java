package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Editorial;
import com.walter.libreria.repositorios.EditorialRepositorio;
import com.walter.libreria.servicios.EditorialServicio;
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
@RequestMapping("/admin/editoriales")
public class EditorialController {

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/admin-editoriales")
    public String administradorEditoriales(ModelMap model) {
        List<Editorial> editoriales = editorialRepositorio.findAll();
        model.put("editoriales", editoriales);
        return "admin-editorial.html";
    }

    @PostMapping("/registrar-editorial")
    public String registrarEditorial(ModelMap model, @RequestParam(required = false) String id, @RequestParam String nombre) {

        try {
            editorialServicio.agregarEditorial(nombre);         
            model.put("success", "La editorial '" + nombre.toUpperCase() + "' fue registrada en forma exitosa.");
        } catch (Exception e) {          
            if (e.getMessage() == null || nombre == null) {
                model.put("error", "ERROR al intentar guardar la editorial: faltó completar algún dato.");
            } else {
                model.put("error", "ERROR al intentar guardar la editorial : " + e.getMessage());
            }
        }
        List<Editorial> editoriales = editorialRepositorio.findAll();
        model.put("editoriales", editoriales);
        return "admin-editorial.html";
    }

    @GetMapping("/modificar-editorial-datos/{idEditorialModif}")
    public String datosEditorial(ModelMap model, @PathVariable String idEditorialModif) {
        Editorial editorial = editorialRepositorio.getById(idEditorialModif);
        model.put("editorialModif", editorial);
        return "modif-editorial.html";
    }
  
    @PostMapping("/modificar-editorial")
    public String modificarEditorial(ModelMap model, @RequestParam String id, @RequestParam String nombre) {

        try {
            editorialServicio.modificarEditorial(id, nombre);           
            model.put("success", "La editorial '" + nombre.toUpperCase() + "' fue modificada en forma exitosa.");
            List<Editorial> editoriales = editorialRepositorio.findAll();
            model.put("editoriales", editoriales);
            return "admin-editorial.html";
        } catch (Exception e) {      
            Editorial editorial = editorialRepositorio.getById(id);
            model.put("editorialModif", editorial);
            model.put("error", "ERROR al intentar modificar la editorial : " + e.getMessage());
            return "modif-editorial.html";
        }
    }

    @GetMapping("/eliminar-editorial/{id}")
    public String eliminarEditorial(ModelMap model, @PathVariable String id) {
        try {
            String nombre = editorialRepositorio.getById(id).getNombre().toUpperCase();          
            editorialServicio.eliminarEditorial(id);        
            model.put("success", "La editorial '" + nombre + "' fue eliminada en forma exitosa.");
        } catch (Exception e) {           
            model.put("error", "ERROR al intentar eliminar la editorial: " + e.getMessage());
        }
        List<Editorial> editoriales = editorialRepositorio.findAll();
        model.put("editoriales", editoriales);
        return "admin-editorial.html";
    }

    @GetMapping("/baja/{id}")
    public String baja(ModelMap model, @PathVariable String id) {
        try {
            editorialServicio.baja(id);       
            model.put("success", "La editorial '" + editorialRepositorio.getOne(id).getNombre().toUpperCase() + "' fue dada de baja en forma exitosa.");
        } catch (Exception e) {           
            model.put("error", "ERROR al intentar dar de baja la editorial : " + e.getMessage());
        }
        List<Editorial> editoriales = editorialRepositorio.findAll();
        model.put("editoriales", editoriales);
        return "admin-editorial.html";
    }

    @GetMapping("/alta/{id}")
    public String alta(ModelMap model, @PathVariable String id) {
        try {
            editorialServicio.alta(id);          
            model.put("success", "La editorial '" + editorialRepositorio.getById(id).getNombre().toUpperCase() + "' fue dada de alta en forma exitosa.");
        } catch (Exception e) {       
            model.put("error", "ERROR al intentar dar de alta la editorial : " + e.getMessage());
        }
        List<Editorial> editoriales = editorialRepositorio.findAll();
        model.put("editoriales", editoriales);
        return "admin-editorial.html";
    }
}
