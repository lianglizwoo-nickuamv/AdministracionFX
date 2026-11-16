package ni.edu.uam.administracionfx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Cambia de pantalla reutilizando siempre el mismo Stage. */
public final class Navegador {
    private static final String VISTAS = "/ni/edu/uam/administracionfx/view/";
    private static final String CSS = "/ni/edu/uam/administracionfx/css/app.css";

    private static Stage stage;

    private Navegador() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void irALogin() {
        mostrar("login-view.fxml", "Administración - Iniciar sesión", 420, 360);
    }

    public static void irAMenu() {
        mostrar("menu-view.fxml", "Administración - Menú principal", 640, 420);
    }

    public static void irAEstudiantes() {
        mostrar("estudiantes-view.fxml", "Administración - Estudiantes", 1120, 640);
    }

    public static void irAUsuarios() {mostrar("usuarios-view.fxml", "Administración - Usuarios", 1000, 560);}

    private static void mostrar(String fxml, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(VISTAS + fxml));
            Scene scene = new Scene(loader.load(), ancho, alto);
            scene.getStylesheets().add(Navegador.class.getResource(CSS).toExternalForm());
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + fxml, e);
        }
    }


}