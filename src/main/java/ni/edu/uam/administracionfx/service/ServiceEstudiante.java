package ni.edu.uam.administracionfx.service;

import ni.edu.uam.administracionfx.model.Estudiante;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ServiceEstudiante extends ServiceGeneric {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");

    public ServiceEstudiante() {
        super();
    }

    public List<Estudiante> getAll() {
        return dao.getAll("Estudiante.findAll", Estudiante.class);
    }

    public Estudiante getById(UUID id) {
        return dao.findById(Estudiante.class, id);
    }

    /** Inserta si no tiene id; actualiza si ya lo tiene. Lanza IllegalArgumentException si los datos no son válidos. */
    public Estudiante guardar(Estudiante e) {
        normalizar(e);
        validar(e);
        if (e.getId() == null) {
            dao.insert(e);
            return e;
        }
        return dao.update(e);
    }

    public void eliminar(Estudiante e) {
        dao.remove(e);
    }

    private void normalizar(Estudiante e) {
        e.setCarnet(limpiar(e.getCarnet()));
        e.setPrimerNombre(limpiar(e.getPrimerNombre()));
        e.setSegundoNombre(limpiar(e.getSegundoNombre()));
        e.setPrimerApellido(limpiar(e.getPrimerApellido()));
        e.setSegundoApellido(limpiar(e.getSegundoApellido()));
        e.setCorreo(limpiar(e.getCorreo()));
        e.setTelefono(limpiar(e.getTelefono()));
        e.setCarrera(limpiar(e.getCarrera()));
    }

    private String limpiar(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private void validar(Estudiante e) {
        if (e.getCarnet() == null) {
            throw new IllegalArgumentException("El carnet es obligatorio.");
        }
        if (e.getCarnet().length() > 20) {
            throw new IllegalArgumentException("El carnet no puede tener más de 20 caracteres.");
        }
        if (e.getPrimerNombre() == null) {
            throw new IllegalArgumentException("El primer nombre es obligatorio.");
        }
        if (e.getPrimerApellido() == null) {
            throw new IllegalArgumentException("El primer apellido es obligatorio.");
        }
        if (e.getCorreo() != null && !CORREO.matcher(e.getCorreo()).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido.");
        }
        if (e.getTelefono() != null && !TELEFONO.matcher(e.getTelefono()).matches()) {
            throw new IllegalArgumentException(
                    "El teléfono solo puede tener números, espacios, + y guiones (7 a 20 caracteres).");
        }
        boolean duplicado = dao.getByQuery("Estudiante.findByCarnet", Estudiante.class, e.getCarnet())
                .stream()
                .anyMatch(x -> !x.getId().equals(e.getId()));
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un estudiante con el carnet " + e.getCarnet() + ".");
        }
    }
}