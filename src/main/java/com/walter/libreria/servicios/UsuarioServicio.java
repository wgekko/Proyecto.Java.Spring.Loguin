package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Foto;
import com.walter.libreria.entidades.Prestamo;
import com.walter.libreria.entidades.Usuario;
import com.walter.libreria.enums.Rol;
import com.walter.libreria.repositorios.UsuarioRepositorio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String dni, String telefono, String mail, String clave, String clave2) throws IOException, Exception {      
        validar(nombre, apellido, dni, telefono, mail, clave, clave2);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setTelefono(telefono);
        usuario.setMail(mail);
        usuario.setAlta(new Date());       
        usuario.setRol(Rol.USUARIO);      
        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);      
        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);        
        usuarioRepositorio.save(usuario);
    }
 
    @Transactional
    public void modificar(String id, MultipartFile archivo, String nombre, String apellido, String dni, String telefono, String mail, String clave, String clave2) throws Exception {
        
        validar(nombre, apellido, dni, telefono, mail, clave, clave2);       
        Usuario usuario = usuarioRepositorio.getById(id);
        if (usuario != null) {           
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setDni(dni);
            usuario.setMail(mail);
            usuario.setTelefono(telefono);    
            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);          
            if (!archivo.isEmpty()) {
                String idFoto = null;
                if (usuario.getFoto() != null) {
                    idFoto = usuario.getFoto().getId();
                }
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                usuario.setFoto(foto);
            }           
            usuarioRepositorio.save(usuario);
        } else {        
            throw new Exception("No se encontró el usuario solicitado.");
        }
    }
 
    @Transactional
    public void eliminar(String id) throws Exception {
        try {           
            Usuario usuario = usuarioRepositorio.getById(id);       
            List<Prestamo> prestamosUsuario = prestamoServicio.buscarPorUsuario(id);
            if (prestamosUsuario != null) {
                for (Prestamo prestamo : prestamosUsuario) {
                    prestamoServicio.eliminarPrestamo(prestamo.getId());
                }
            }
            if (usuario != null) {                
                usuarioRepositorio.delete(usuario);
            } else {
                throw new Exception("No existe el usuario vinculado a ese ID.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
 
    @Transactional
    public void deshabilitar(String id) throws Exception {
        Usuario usuario = usuarioRepositorio.getById(id);
        if (usuario != null) {            
            usuario.setBaja(new Date());           
            usuarioRepositorio.save(usuario);
        } else {        
            throw new Exception("No se encontró el usuario solicitado.");
        }
    }

    @Transactional
    public void habilitar(String id) throws Exception {
        Usuario usuario = usuarioRepositorio.getById(id);
        if (usuario != null) {          
            if (usuario.getBaja() == null) {
                throw new Exception("El usuario no se encuentra dado de baja.");
            } else {
                usuario.setBaja(null);            
                usuarioRepositorio.save(usuario);
            }
        } else {           
            throw new Exception("No se encontró el usuario solicitado.");
        }
    }
 
    @Transactional
    public void cambiarRol(String id) {
        Usuario usuario = usuarioRepositorio.getById(id);
        if (usuario != null) {           
            if (usuario.getRol().equals(Rol.USUARIO)) { 
                usuario.setRol(Rol.ADMIN);
            } else if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USUARIO);
            }
        }
    }

    public void validar(String nombre, String apellido, String dni, String telefono, String mail, String clave, String clave2) throws Exception {
        if (nombre == null || nombre.isEmpty()) {
            throw new Exception("El nombre del usuario es obligatorio.");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new Exception("El apellido del usuario es obligatorio.");
        }
        if (dni == null || dni.isEmpty()) {
            throw new Exception("El DNI del usuario es obligatorio.");
        }
        if (telefono == null || telefono.isEmpty()) {
            throw new Exception("El telefono del usuario es obligatorio.");
        }
        if (mail == null || mail.isEmpty()) {
            throw new Exception("El mail del usuario es obligatorio.");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new Exception("La clave del usuario es obligatoria y debe tener al menos 6 caracteres.");
        }
        if (!clave2.equals(clave)) {
            throw new Exception("Las claves ingresadas deben coincidir.");
        }
    }
  
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();         
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());
            permisos.add(p1);
           
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);          
            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }

    public Usuario getById(String id) {
        return usuarioRepositorio.getById(id);
    }

 
    public List<Usuario> findAll() {
        return usuarioRepositorio.findAll();
    }

    public List<Usuario> buscarActivos() {
        return usuarioRepositorio.buscarActivos();
    }
    
    public List<Usuario> buscarInactivos() {
        return usuarioRepositorio.buscarInactivos();
    }
}
