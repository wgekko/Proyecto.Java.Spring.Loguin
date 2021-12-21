package com.walter.libreria.entidades;

import com.walter.libreria.enums.Rol;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String mail;
    private String clave;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date alta;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date baja;

    @OneToOne
    private Foto foto;

    @Enumerated(EnumType.STRING)
    private Rol rol;

   
 
    public String getId() {
        return id;
    }

  
    public void setId(String id) {
        this.id = id;
    }

 
    public String getNombre() {
        return nombre;
    }

   
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    public String getApellido() {
        return apellido;
    }

    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

  
    public String getDni() {
        return dni;
    }

   
    public void setDni(String dni) {
        this.dni = dni;
    }


    public String getMail() {
        return mail;
    }

  
    public void setMail(String mail) {
        this.mail = mail;
    }

   
    public String getClave() {
        return clave;
    }

  
    public void setClave(String clave) {
        this.clave = clave;
    }

  
    public Date getAlta() {
        return alta;
    }

  
    public void setAlta(Date alta) {
        this.alta = alta;
    }

 
    public Date getBaja() {
        return baja;
    }

   
    public void setBaja(Date baja) {
        this.baja = baja;
    }

   
    public Foto getFoto() {
        return foto;
    }

 
    public void setFoto(Foto foto) {
        this.foto = foto;
    }

   
    public Rol getRol() {
        return rol;
    }

  
    public void setRol(Rol rol) {
        this.rol = rol;
    }

 
    public String getTelefono() {
        return telefono;
    }

    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
