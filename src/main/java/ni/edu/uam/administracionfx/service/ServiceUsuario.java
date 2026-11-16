package ni.edu.uam.administracionfx.service;

import ni.edu.uam.administracionfx.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class ServiceUsuario extends ServiceGeneric {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    public ServiceUsuario() {
        super();
    }

    public List<Usuario> getAll() {
        return dao.getAll("Usuario.findAll", Usuario.class);
    }

    public Usuario getById(String id) {
        return dao.findById(Usuario.class, UUID.fromString(id));
    }

    public void save(Usuario usuario) {
        dao.insert(usuario);
    }

    public void delete(Usuario usuario) {
        dao.remove(usuario);
    }

    /** Inserta si no tiene id; actualiza si ya lo tiene. Lanza IllegalArgumentException si los datos no son válidos. */
    public Usuario guardar(Usuario u) {
        u.setUsuario(limpiar(u.getUsuario()));
        u.setPrimerNombre(limpiar(u.getPrimerNombre()));
        u.setSegundoNombre(limpiar(u.getSegundoNombre()));
        u.setPrimerApellido(limpiar(u.getPrimerApellido()));
        u.setSegundoApellido(limpiar(u.getSegundoApellido()));
        u.setCorreo(limpiar(u.getCorreo()));
        validar(u);
        if (u.getId() == null) {
            dao.insert(u);
            return u;
        }
        return dao.update(u);
    }

    private String limpiar(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void validar(Usuario u) {
        if (u.getUsuario() == null) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (u.getPassword() == null || u.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (u.getPrimerNombre() == null) {
            throw new IllegalArgumentException("El primer nombre es obligatorio.");
        }
        if (u.getPrimerApellido() == null) {
            throw new IllegalArgumentException("El primer apellido es obligatorio.");
        }
        if (u.getCorreo() != null && !CORREO.matcher(u.getCorreo()).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
        boolean duplicado = dao.getByQuery("Usuario.findByUsuario", Usuario.class, u.getUsuario())
                .stream()
                .anyMatch(x -> !x.getId().equals(u.getId()));
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un usuario llamado " + u.getUsuario() + ".");
        }
    }

    /** Devuelve el usuario si las credenciales son correctas. */
    public Optional<Usuario> login(String usuario, String password) {
        return dao.getByQuery("Usuario.findByUsuario", Usuario.class, usuario).stream()
                .filter(u -> password != null && password.equals(u.getPassword()))
                .findFirst();
    }

    /** Si la tabla de usuarios está vacía crea admin / admin para poder entrar. */
    public void crearAdminPorDefecto() {
        if (getAll().isEmpty()) {
            Usuario u = new Usuario();
            u.setPrimerNombre("Administrador");
            u.setPrimerApellido("Sistema");
            u.setUsuario("admin");
            u.setPassword("admin");
            save(u);
        }
    }
}