package ni.edu.uam.administracionfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ni.edu.uam.administracionfx.dao.EntityManagerAdmin;
import ni.edu.uam.administracionfx.service.ServiceUsuario;
import ni.edu.uam.administracionfx.util.Alertas;
import ni.edu.uam.administracionfx.util.Navegador;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        Navegador.init(stage);
        try {
            // Inicializa JPA y garantiza que exista al menos un usuario para iniciar sesión.
            new ServiceUsuario().crearAdminPorDefecto();
        } catch (Throwable t) {
            t.printStackTrace();
            Alertas.error("Base de datos",
                    "No se pudo conectar a la base de datos 'estudiante' (PostgreSQL, localhost:5432).\n"
                            + "Verifique que el servidor esté activo.\n\nDetalle: " + t.getMessage());
            Platform.exit();
            return;
        }
        Navegador.irALogin();
    }

    @Override
    public void stop() {
        try {
            EntityManagerAdmin.close();
        } catch (Throwable ignored) {
            // si JPA nunca llegó a iniciar, no hay nada que cerrar
        }
    }

    public static void main(String[] args) {
        launch();
    }
}