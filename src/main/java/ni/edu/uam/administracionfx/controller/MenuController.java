package ni.edu.uam.administracionfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ni.edu.uam.administracionfx.model.Usuario;
import ni.edu.uam.administracionfx.util.Navegador;
import ni.edu.uam.administracionfx.util.Sesion;

public class MenuController {
    @FXML private Label lblBienvenida;

    @FXML
    public void initialize() {
        Usuario u = Sesion.getUsuarioActual();
        lblBienvenida.setText(u != null ? "Bienvenido, " + u.getPrimerNombre() : "Bienvenido");
    }

    @FXML private void onEstudiantes() { Navegador.irAEstudiantes(); }

    @FXML private void onUsuarios() { Navegador.irAUsuarios(); }

    @FXML
    private void onCerrarSesion() {
        Sesion.cerrar();
        Navegador.irALogin();
    }
}