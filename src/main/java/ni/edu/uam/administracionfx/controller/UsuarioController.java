package ni.edu.uam.administracionfx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ni.edu.uam.administracionfx.model.Usuario;
import ni.edu.uam.administracionfx.service.ServiceUsuario;
import ni.edu.uam.administracionfx.util.Alertas;
import ni.edu.uam.administracionfx.util.Navegador;
import ni.edu.uam.administracionfx.util.Sesion;

import java.util.UUID;

public class UsuarioController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private TextField txtCorreo;

    @FXML private Label lblModo;
    @FXML private Label lblTotal;
    @FXML private Button btnEliminar;

    private final ServiceUsuario service = new ServiceUsuario();
    private final ObservableList<Usuario> datos = FXCollections.observableArrayList();
    private Usuario seleccionado;

    @FXML
    public void initialize() {
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        tablaUsuarios.setItems(datos);
        tablaUsuarios.setPlaceholder(new Label("No hay usuarios registrados"));

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
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
        tablaUsuarios.getSelectionModel().clearSelection();
        limpiarFormulario();
        txtUsuario.requestFocus();
    }

    @FXML
    private void onGuardar() {
        Usuario u = new Usuario();
        if (seleccionado != null) {
            u.setId(seleccionado.getId());               // con id -> UPDATE, sin id -> INSERT
            u.setRutaFoto(seleccionado.getRutaFoto());   // conserva el dato que no está en el formulario
        }
        u.setUsuario(txtUsuario.getText());
        u.setPrimerNombre(txtPrimerNombre.getText());
        u.setSegundoNombre(txtSegundoNombre.getText());
        u.setPrimerApellido(txtPrimerApellido.getText());
        u.setSegundoApellido(txtSegundoApellido.getText());
        u.setCorreo(txtCorreo.getText());

        String password = txtPassword.getText();
        if (seleccionado != null && (password == null || password.isEmpty())) {
            password = seleccionado.getPassword();       // contraseña vacía al editar = no cambiarla
        }
        u.setPassword(password);

        boolean eraNuevo = seleccionado == null;
        try {
            Usuario guardado = service.guardar(u);
            Usuario actual = Sesion.getUsuarioActual();
            if (actual != null && actual.getId().equals(guardado.getId())) {
                Sesion.setUsuarioActual(guardado);
            }
            cargarDatos();
            seleccionarPorId(guardado.getId());
            Alertas.info("Usuarios",
                    eraNuevo ? "Usuario creado correctamente." : "Usuario actualizado correctamente.");
        } catch (IllegalArgumentException ex) {
            Alertas.advertencia("Datos inválidos", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudo guardar el usuario: " + ex.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        if (seleccionado == null) {
            Alertas.advertencia("Usuarios", "Seleccione un usuario de la tabla.");
            return;
        }
        Usuario actual = Sesion.getUsuarioActual();
        if (actual != null && actual.getId().equals(seleccionado.getId())) {
            Alertas.advertencia("Usuarios", "No puede eliminar el usuario con el que inició sesión.");
            return;
        }
        if (!Alertas.confirmar("Eliminar usuario", "¿Desea eliminar al usuario " + seleccionado.getUsuario() + "?")) {
            return;
        }
        try {
            service.delete(seleccionado);
            cargarDatos();
            limpiarFormulario();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudo eliminar el usuario: " + ex.getMessage());
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
            lblTotal.setText(datos.size() + " usuario(s)");
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudieron cargar los usuarios: " + ex.getMessage());
        }
    }

    private void seleccionarPorId(UUID id) {
        for (Usuario u : tablaUsuarios.getItems()) {
            if (u.getId().equals(id)) {
                tablaUsuarios.getSelectionModel().select(u);
                tablaUsuarios.scrollTo(u);
                return;
            }
        }
        limpiarFormulario();
    }

    private void cargarFormulario(Usuario u) {
        seleccionado = u;
        txtUsuario.setText(u.getUsuario());
        txtPassword.clear();
        txtPassword.setPromptText("Vacía = no cambiarla");
        txtPrimerNombre.setText(u.getPrimerNombre());
        txtSegundoNombre.setText(u.getSegundoNombre());
        txtPrimerApellido.setText(u.getPrimerApellido());
        txtSegundoApellido.setText(u.getSegundoApellido());
        txtCorreo.setText(u.getCorreo());
        lblModo.setText("Editando usuario");
        btnEliminar.setDisable(false);
    }

    private void limpiarFormulario() {
        seleccionado = null;
        txtUsuario.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Obligatoria");
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        txtCorreo.clear();
        lblModo.setText("Nuevo usuario");
        btnEliminar.setDisable(true);
    }
}