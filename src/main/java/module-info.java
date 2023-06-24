module com.example.inmobiliariafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.inmobiliariafx to javafx.fxml;
    exports com.example.inmobiliariafx;

    // Módulo principal de la aplicación InmobiliariaFX
    // Requiere las librerías de JavaFX, java.sql, org.controlsfx, org.kordamp.bootstrapfx y java.desktop
    // Expone el paquete com.example.inmobiliariafx para su uso externo
}
