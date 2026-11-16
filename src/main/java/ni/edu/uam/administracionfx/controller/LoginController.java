package ni.edu.uam.administracionfx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ni.edu.uam.administracionfx.model.Usuario;
import ni.edu.uam.administracionfx.service.ServiceUsuario;
import ni.edu.uam.administracionfx.util.Alertas;
import ni.edu.uam.administracionfx.util.Navegador;
import ni.edu.uam.administracionfx.util.Sesion;

import java.util.Optional;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final ServiceUsuario service = new ServiceUsuario();

    @FXML
    private void onIngresar() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Ingrese usuario y contraseña.");
            return;
        }
        try {
            Optional<Usuario> resultado = service.login(usuario, password);
            if (resultado.isPresent()) {
                Sesion.setUsuarioActual(resultado.get());
                Navegador.irAMenu();
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                txtPassword.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Alertas.error("Error", "No se pudo validar el usuario: " + ex.getMessage());
        }
    }
}