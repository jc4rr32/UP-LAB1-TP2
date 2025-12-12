package base;

public abstract class Usuario {
    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private ObraSocial obraSocial;
    /**
     * Constructor completo, con ID y DNI conocidos (por ejemplo, al leer de la base de datos)
     */
    public Usuario(int id, String dni, String nombre, String apellido, String email, Rol rol, ObraSocial obraSocial) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.obraSocial = obraSocial;
    }

    /**
     * Constructor para nuevos usuarios (ID asignado automáticamente al guardar)
     */
    public Usuario(String dni, String nombre, String apellido, String email, Rol rol, ObraSocial obraSocial) {
        this(0, dni, nombre, apellido, email, rol, obraSocial);
    }

    // Getters y setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    public ObraSocial getObraSocial() {
        return obraSocial;
    }
    
    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial;
    }
}

