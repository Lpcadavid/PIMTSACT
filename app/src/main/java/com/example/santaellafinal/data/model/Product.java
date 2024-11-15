package com.example.santaellafinal.data.model;

public class Product {
    private String id;
    private String nombre;
    private String precio;
    private String categoria;
    private String cantidad;
    private String fecha;
    private String imageUrl;  // Nueva propiedad para almacenar la URL de la imagen

    public Product() {
        // Constructor vacío requerido por Firestore
    }

    public Product(String id, String nombre, String precio, String categoria, String cantidad, String fecha, String imageUrl) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.imageUrl = imageUrl;  // Asigna la URL de la imagen
    }

    // Getters y setters para todos los campos
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

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImageUrl() {
        return imageUrl;  // Nuevo getter
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;  // Nuevo setter
    }
}
