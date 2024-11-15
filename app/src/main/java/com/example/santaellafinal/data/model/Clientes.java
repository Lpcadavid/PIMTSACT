package com.example.santaellafinal.data.model;

public class Clientes {

    private String id;
    private String name;
    private String email;
    private String edad;
    private String ciudad;

    public Clientes(String name, String email, String edad, String ciudad) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.edad = edad;
        this.ciudad = ciudad;
    }

    public Clientes() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
