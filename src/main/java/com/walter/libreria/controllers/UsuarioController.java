package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Usuario;
import com.walter.libreria.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
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
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;
  
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/modificar-usuario-datos/{idUsuarioModif}")
    public String datosUsuario(ModelMap model, @PathVariable String idUsuarioModif) {
        Usuario usuario = usuarioServicio.getById(idUsuarioModif);
        model.addAttribute("usuarioModif", usuario);
        return "modif-usuario.html";
    }
   
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");     
        if (login == null) {
            return "redirect:/";
        }
        if (!login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {
            Usuario usuario = usuarioServicio.getById(id);
            model.addAttribute("usuarioModif", usuario);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "modif-usuario.html";
    }
 
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @PostMapping("/actualizar-perfil")
    public String actualizar(ModelMap model, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String telefono, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2) {
        Usuario usuario = null;
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            usuario = usuarioServicio.getById(id);
            usuarioServicio.modificar(id, archivo, nombre, apellido, dni, telefono, mail, clave, clave2);
            session.setAttribute("usuariosession", usuario);
        } catch (Exception e) {
            model.put("error", e.getMessage());
            model.put("perfil", usuario);
            return "perfil.html";
        }
        return "redirect:/inicio";
    }
}
