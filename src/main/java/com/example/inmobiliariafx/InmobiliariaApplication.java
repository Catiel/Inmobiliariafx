package com.example.inmobiliariafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación de Inmobiliaria.
 * Esta clase extiende la clase Application de JavaFX y proporciona el punto de entrada del programa.
 */
public class InmobiliariaApplication extends Application {

    /**
     * Punto de entrada del programa.
     *
     * @param args Los argumentos de línea de comandos. No se utilizan en esta aplicación.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Método start() de la clase Application.
     * Este método es llamado cuando se inicia la aplicación y se encarga de configurar la interfaz de usuario.
     *
     * @param stage El objeto Stage principal de la aplicación.
     * @throws IOException Si ocurre un error al cargar el archivo FXML de la interfaz de usuario.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InmobiliariaApplication.class.getResource("Inmobiliaria-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
