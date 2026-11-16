package ni.edu.uam.administracionfx.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "estudiante")
@NamedQueries({
        @NamedQuery(
                name = "Estudiante.findAll",
                query = "SELECT e FROM Estudiante e ORDER BY e.primerApellido, e.primerNombre"
        ),
        @NamedQuery(
                name = "Estudiante.findByCarnet",
                query = "SELECT e FROM Estudiante e WHERE e.carnet = ?1"
        )
})
public class Estudiante {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "carnet", nullable = false, unique = true, length = 20)
    private String carnet;

    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;

    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "carrera")
    private String carrera;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCarnet() { return carnet; }
    public void setCarnet(String carnet) { this.carnet = carnet; }

    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }

    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getNombreCompleto() {
        return Stream.of(primerNombre, segundoNombre, primerApellido, segundoApellido)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id=" + id +
                ", carnet='" + carnet + '\'' +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", carrera='" + carrera + '\'' +
                '}';
    }
}