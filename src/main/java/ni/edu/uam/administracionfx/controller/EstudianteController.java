package ni.edu.uam.administracionfx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ni.edu.uam.administracionfx.model.Estudiante;
import ni.edu.uam.administracionfx.service.ServiceEstudiante;
import ni.edu.uam.administracionfx.util.Alertas;
import ni.edu.uam.administracionfx.util.Navegador;

import java.util.UUID;

public class EstudianteController {

    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, String> colCarnet;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private TableColumn<Estudiante, String> colCorreo;
    @FXML private TableColumn<Estudiante, String> colTelefono;
    @FXML private TableColumn<Estudiante, String> colCarrera;

    @FXML private TextField txtBuscar;
    @FXML private TextField txtCarnet;
    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCarrera;

    @FXML private Label lblModo;
    @FXML private Label lblTotal;
    @FXML private Button btnEliminar;

    private final ServiceEstudiante service = new ServiceEstudiante();
    private final ObservableList<Estudiante> datos = FXCollections.observableArrayList();
    private FilteredList<Estudiante> filtrados;
    private Estudiante seleccionado;

    @FXML
    public void initialize() {
        colCarnet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCarnet()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colCarrera.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCarrera()));

        filtrados = new FilteredList<>(datos, e -> true);
        SortedList<Estudiante> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tablaEstudiantes.comparatorProperty());
        tablaEstudiantes.setItems(ordenados);
        tablaEstudiantes.setPlaceholder(new Label("No hay estudiantes para mostrar"));

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> filtrar(nuevo));
        tablaEstudiantes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                cargarFormulario(nuevo);
            }
        });

        limpiarFormulario();
        cargarDatos();
    }

    // ---------- Acciones (botones) ----------

    @FXML
    private void onNuevo() {
        tablaEstudiantes.getSelectionModel().clearSelection();
        limpiarFormulario();
        txtCarnet.requestFocus();
    }

    @FXML
    private void onGuardar() {
        Estudiante e = new Estudiante();
        if (seleccionado != null) {
            e.setId(seleccionado.getId());   // con id -> UPDATE, sin id -> INSERT
        }
        e.setCarnet(txtCarnet.getText());
        e.setPrimerNombre(txtPrimerNombre.getText());
        e.setSegundoNombre(txtSegundoNombre.getText());
        e.setPrimerApellido(txtPrimerApellido.getText());
        e.setSegundoApellido(txtSegundoApellido.getText());
        e.setCorreo(txtCorreo.getText());
        e.setTelefono(txtTelefono.getText());
        e.setCarrera(txtCarrera.getText());

        boolean eraNuevo = seleccionado == null;
        try {
            Estudiante guardado = service.guardar(e);
            cargarDatos();
            seleccionarPorId(guardado.getId());
            Alertas.info("Estudiantes",
                    eraNuevo ? "Estudiante registrado correctamente." : "Estudiante actualizado correctamente.");
        } catch (IllegalArgumentException ex) {
            Alertas.advertencia("Datos inválidos", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudo guardar el estudiante: " + ex.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionado == null) {
            Alertas.advertencia("Estudiantes", "Seleccione un estudiante de la tabla.");
            return;
        }
        boolean confirmado = Alertas.confirmar("Eliminar estudiante",
                "¿Desea eliminar a " + seleccionado.getNombreCompleto() + " (" + seleccionado.getCarnet() + ")?");
        if (!confirmado) {
            return;
        }
        try {
            service.eliminar(seleccionado);
            cargarDatos();
            limpiarFormulario();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudo eliminar el estudiante: " + ex.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.irAMenu();
    }

    // ---------- Auxiliares ----------

    private void cargarDatos() {
        try {
            datos.setAll(service.getAll());
            lblTotal.setText(datos.size() + " estudiante(s)");
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudieron cargar los estudiantes: " + ex.getMessage());
        }
    }

    private void filtrar(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        filtrados.setPredicate(e -> t.isEmpty()
                || contiene(e.getCarnet(), t)
                || contiene(e.getNombreCompleto(), t)
                || contiene(e.getCorreo(), t)
                || contiene(e.getCarrera(), t));
    }

    private boolean contiene(String valor, String t) {
        return valor != null && valor.toLowerCase().contains(t);
    }

    private void seleccionarPorId(UUID id) {
        for (Estudiante e : tablaEstudiantes.getItems()) {
            if (e.getId().equals(id)) {
                tablaEstudiantes.getSelectionModel().select(e);
                tablaEstudiantes.scrollTo(e);
                return;
            }
        }
        limpiarFormulario();   // el registro guardado quedó oculto por el filtro de búsqueda
    }

    private void cargarFormulario(Estudiante e) {
        seleccionado = e;
        txtCarnet.setText(e.getCarnet());
        txtPrimerNombre.setText(e.getPrimerNombre());
        txtSegundoNombre.setText(e.getSegundoNombre());
        txtPrimerApellido.setText(e.getPrimerApellido());
        txtSegundoApellido.setText(e.getSegundoApellido());
        txtCorreo.setText(e.getCorreo());
        txtTelefono.setText(e.getTelefono());
        txtCarrera.setText(e.getCarrera());
        lblModo.setText("Editando estudiante");
        btnEliminar.setDisable(false);
    }

    private void limpiarFormulario() {
        seleccionado = null;
        txtCarnet.clear();
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCarrera.clear();
        lblModo.setText("Nuevo estudiante");
        btnEliminar.setDisable(true);
    }
}