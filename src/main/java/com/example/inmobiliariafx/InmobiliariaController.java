package com.example.inmobiliariafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controlador de la aplicación de inmobiliaria.
 */
public class InmobiliariaController {
    private int idContratoBusquedaCC = 0; // Almacena el ID de contrato para la búsqueda por código de cliente

    @FXML
    private TextField MONTOTXT, ESTADOTXT, OBSERVACIONTXT, NACIONALIDADTXT, DIRECCIONTXT, NOMBRETXT, INGRESOSTXT,
            OCUPACIONTXT, GENEROTXT, IDENTIFICACIONTXT, TELEFONOSTXT, REFERENCIASTXT, TIPOTXT, CANTIDADHABITACIONESTXT,
            TIPOTRANSACCION, GARAJETXT, PRECIOMINIMOTXT, SUPERFICIETXT, ZONATXT, CANTIDADBANOS, PATIOTXT,
            COMENTARIOADICIONALTXT, PRECIOMAXIMOTXT, SUPERFICIEMAX;
// Campos de texto para ingresar información en la interfaz gráfica

    @FXML
    private DatePicker FECHAINICIOTXT, FECHAFINTXT, NACIMIENTOTXT;
// Campos de selección de fecha en la interfaz gráfica

    @FXML
    private Tab tabContrato, tabInicio, tabRegistro, tabRequisitos;
// Pestañas en el panel de pestañas de la interfaz gráfica

    @FXML
    private TabPane tabPane;
// Panel de pestañas en la interfaz gráfica

    @FXML
    private TextField IDCLIENTETXT;
// Campo de texto para ingresar el ID del cliente en la interfaz gráfica

    private Connection connection; // Conexión a la base de datos


    /**
     * Método de inicialización del controlador.
     */
    @FXML
    void initialize() {
        conectarBaseDatos();
    }

    /**
     * Establece la conexión a la base de datos.
     */
    private void conectarBaseDatos() {
        try {
            connection = SQLServerConnection.getConnection();
            System.out.println("Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca un cliente en la base de datos utilizando el ID proporcionado en un campo de texto.
     * Realiza una llamada al procedimiento almacenado 'BuscarCliente' y muestra los resultados en un cuadro de diálogo.
     * Permite al usuario agregar un contrato para el cliente encontrado.
     * <p>
     *
     * @FXML Este método está asociado a un evento de acción en la interfaz gráfica definida en un archivo FXML.
     * Se invoca cuando el usuario hace clic en el botón de búsqueda de cliente.
     */
    @FXML
    void BuscarCliente() {
        // Obtener el ID del cliente desde un campo de texto
        int clienteId = Integer.parseInt(IDCLIENTETXT.getText());

        try {
            // Preparar la llamada al procedimiento almacenado 'BuscarCliente'
            String sql = "{CALL BuscarCliente(?)}";
            CallableStatement statement = connection.prepareCall(sql);

            // Establecer el parámetro de entrada
            statement.setInt(1, clienteId);

            // Ejecutar la llamada al procedimiento almacenado
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Si se encontró un cliente, obtener los datos del resultado
                int id = resultSet.getInt("IDCLIENTE");
                String nombre = resultSet.getString("NOMCLIENTE");
                String numeroIdentificacion = resultSet.getString("NUMEROIDENTIFICACIONCLIENTE");

                // Construir un mensaje con la información del cliente encontrado
                String mensaje = "Cliente encontrado:\n" + "ID: " + id + "\n" + "Nombre: " + nombre + "\n" +
                        "Número de Identificación: " + numeroIdentificacion;

                // Mostrar un cuadro de diálogo con el mensaje y las opciones "Agregar Contrato" y "Cancelar"
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cliente encontrado");
                alert.setHeaderText(null);
                alert.setContentText(mensaje);

                // Crear botones personalizados para las opciones
                ButtonType agregarContratoButton = new ButtonType("Agregar Contrato");
                ButtonType cancelarButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                // Agregar los botones al cuadro de diálogo
                alert.getButtonTypes().setAll(agregarContratoButton, cancelarButton);

                // Mostrar el cuadro de diálogo y esperar la respuesta del usuario
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == agregarContratoButton) {
                    // Si se selecciona "Agregar Contrato", desactivar la confirmación automática de cambios en la base de datos
                    connection.setAutoCommit(false);
                    cambiarPestana(tabContrato);
                }
            } else {
                // Si no se encontró un cliente, mostrar un cuadro de diálogo de mensaje
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cliente no encontrado");
                alert.setHeaderText(null);
                alert.setContentText("No se encontró un cliente con el ID especificado.");

                // Mostrar el cuadro de diálogo de mensaje
                alert.showAndWait();
            }

            // Cerrar el conjunto de resultados y la declaración
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            // Manejar cualquier error de SQL imprimiendo la traza de la excepción
            e.printStackTrace();
        }
    }


    /**
     * Registra un nuevo cliente en la base de datos con los valores proporcionados en los campos de texto.
     * Llama al procedimiento almacenado "RegistrarCliente" para realizar el registro.
     * Muestra mensajes de éxito o error según el resultado del registro.
     *
     * @FXML Este método está asociado a un evento de acción en la interfaz gráfica definida en un archivo FXML.
     * Se invoca cuando el usuario hace clic en el botón de registro de cliente.
     */
    @FXML
    void RegistrarCliente() {
        // Obtener los valores de los campos de texto
        String genero = GENEROTXT.getText();
        String numeroIdentificacion = IDENTIFICACIONTXT.getText();
        BigDecimal ingresos = new BigDecimal(INGRESOSTXT.getText());
        String nombre = NOMBRETXT.getText();
        String direccion = DIRECCIONTXT.getText();
        LocalDate fechaNacimiento = NACIMIENTOTXT.getValue();
        String nacionalidad = NACIONALIDADTXT.getText();
        String ocupacion = OCUPACIONTXT.getText();
        String referenciasPersonales = REFERENCIASTXT.getText();
        String telefonos = TELEFONOSTXT.getText();

        try {
            // Consulta para llamar al procedimiento almacenado RegistrarCliente
            String sql = "{CALL RegistrarCliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);

            // Establecer los parámetros de entrada
            statement.setString(1, genero);
            statement.setString(2, numeroIdentificacion);
            statement.setBigDecimal(3, ingresos);
            statement.setString(4, nombre);
            statement.setString(5, direccion);
            statement.setDate(6, java.sql.Date.valueOf(fechaNacimiento));
            statement.setString(7, nacionalidad);
            statement.setString(8, ocupacion);
            statement.setString(9, referenciasPersonales);
            statement.setString(10, telefonos);

            // Establecer los parámetros de salida
            statement.registerOutParameter(11, Types.NVARCHAR);  // @mensaje
            statement.registerOutParameter(12, Types.INTEGER);   // @idClienteGenerado

            // Ejecutar el procedimiento almacenado
            statement.execute();

            // Obtener los valores de salida
            String mensaje = statement.getString(11);
            int idClienteGenerado = statement.getInt(12);

            if (idClienteGenerado > 0) {
                // Si se generó correctamente el cliente, mostrar un mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registro exitoso");
                alert.setContentText(mensaje);
                alert.showAndWait();

                // Mostrar el ID del cliente generado en un mensaje
                Alert idClienteAlert = new Alert(Alert.AlertType.INFORMATION);
                idClienteAlert.setTitle("IDCLIENTE GENERADO");
                idClienteAlert.setContentText("El IDCLIENTE GENERADO ES " + idClienteGenerado);
                idClienteAlert.showAndWait();
            } else {
                // Si ocurrió un error al registrar el cliente, mostrar un mensaje de error
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error al registrar");
                errorAlert.setContentText(mensaje);
                errorAlert.showAndWait();
            }

            statement.close();
        } catch (SQLException ex) {
            // Manejar cualquier error de SQL imprimiendo la traza de la excepción
            ex.printStackTrace();
        }

        // Cambiar a la pestaña de inicio después de registrar el cliente
        cambiarPestana(tabInicio);
    }


    @FXML
    void CambiarVentanaRegistro() {
        cambiarPestana(tabRegistro);
    }

    /**
     * Cambia a la pestaña de Requisitos y registra un contrato de búsqueda en la base de datos.
     * Recupera los valores de los campos de texto de la ventana Contrato y el ID de cliente de la pestaña Inicio.
     * Llama al procedimiento almacenado 'RegistrarContratoBusqueda' para insertar los datos del contrato en la base de datos.
     *
     * @FXML Este método está asociado a un evento de acción en la interfaz gráfica definida en un archivo FXML.
     * Se invoca cuando el usuario realiza una acción para cambiar a la pestaña de Requisitos.
     */
    @FXML
    void CambiarVentanaRequisitos() {
        cambiarPestana(tabRequisitos);

        // Obtener los valores de los campos de texto de la ventana Contrato
        String monto = MONTOTXT.getText();
        String estado = ESTADOTXT.getText();
        String observaciones = OBSERVACIONTXT.getText();
        LocalDate fechaInicio = FECHAINICIOTXT.getValue();
        LocalDate fechaFin = FECHAFINTXT.getValue();

        // Obtener el ID de cliente de la pestaña Inicio
        String idCliente = IDCLIENTETXT.getText();

        // Llamar al procedimiento almacenado RegistrarContratoBusqueda
        String insertProcedure = "{CALL RegistrarContratoBusqueda(?, ?, ?, ?, ?, ?, ?)}";

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement statement = connection.prepareCall(insertProcedure);
            statement.setInt(1, Integer.parseInt(idCliente));
            statement.setString(2, observaciones);
            statement.setString(3, estado);
            statement.setDate(4, java.sql.Date.valueOf(fechaInicio));
            statement.setDate(5, java.sql.Date.valueOf(fechaFin));
            statement.setBigDecimal(6, new BigDecimal(monto));
            statement.registerOutParameter(7, Types.INTEGER);  // Parámetro de salida: idContratoBusqueda

            // Ejecutar la llamada al procedimiento almacenado
            statement.execute();

            // Obtener el valor del parámetro de salida idContratoBusqueda
            idContratoBusquedaCC = statement.getInt(7);

            statement.close();

            System.out.println("Datos del contrato de búsqueda insertados en la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                System.out.println("Rollback realizado debido a una excepción.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cancela la operación de registro de un contrato de búsqueda o de requisitos y regresa a la pestaña de Inicio.
     * Llama al método 'cambiarPestana()' para cambiar a la pestaña de Inicio y al método 'limpiarcampos()' para limpiar los campos de texto.
     * Realiza un rollback en la conexión de base de datos para deshacer cualquier cambio realizado.
     *
     * @FXML Este método está asociado a un evento de acción en la interfaz gráfica definida en un archivo FXML.
     * Se invoca cuando el usuario realiza una acción para cancelar el registro de un contrato de búsqueda o los
     * requisitos.
     */
    @FXML
    void CancelarR() {
        cambiarPestana(tabInicio);
        limpiarcampos();

        try {
            connection.rollback();
            System.out.println("Rollback realizado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al realizar el rollback.");
        }
    }

    /**
     * Finaliza el proceso de registro de requerimientos de un cliente y regresa a la pestaña de Inicio.
     * Llama al método 'cambiarPestana()' para cambiar a la pestaña de Inicio.
     * Obtiene los valores de los campos de texto de la ventana de registro.
     * Llama al procedimiento almacenado 'RegistrarRequerimientosCliente' para registrar los requerimientos del cliente en la base de datos.
     * Realiza un commit en la conexión de base de datos para confirmar los cambios realizados.
     *
     * @FXML Este método está asociado a un evento de acción en la interfaz gráfica definida en un archivo FXML.
     * Se invoca cuando el usuario realiza una acción para finalizar el registro de requerimientos de un cliente.
     */
    @FXML
    void FinalizarC() {
        cambiarPestana(tabInicio);

        // Obtener los valores de los campos de texto de la ventana de registro
        String tipoInmueble = TIPOTXT.getText();
        String zona = ZONATXT.getText();
        BigDecimal precioMin = new BigDecimal(PRECIOMINIMOTXT.getText());
        int cantidadHabitaciones = Integer.parseInt(CANTIDADHABITACIONESTXT.getText());
        int cantidadBanos = Integer.parseInt(CANTIDADBANOS.getText());
        String superficieMin = SUPERFICIETXT.getText();
        String comentarioAdicional = COMENTARIOADICIONALTXT.getText();
        String tipoTransaccion = TIPOTRANSACCION.getText();
        int patios = Integer.parseInt(PATIOTXT.getText());
        int garajes = Integer.parseInt(GARAJETXT.getText());
        BigDecimal superficieMax = new BigDecimal(SUPERFICIEMAX.getText());
        BigDecimal precioMax = new BigDecimal(PRECIOMAXIMOTXT.getText());

        // Llamar al procedimiento almacenado RegistrarRequerimientosCliente
        String insertProcedure = "{CALL RegistrarRequerimientosCliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            // Preparar la llamada al procedimiento almacenado
            CallableStatement statement = connection.prepareCall(insertProcedure);
            statement.setInt(1, idContratoBusquedaCC);
            statement.setString(2, tipoInmueble);
            statement.setString(3, zona);
            statement.setBigDecimal(4, precioMin);
            statement.setInt(5, cantidadHabitaciones);
            statement.setInt(6, cantidadBanos);
            statement.setString(7, superficieMin);
            statement.setString(8, comentarioAdicional);
            statement.setString(9, tipoTransaccion);
            statement.setInt(10, patios);
            statement.setInt(11, garajes);
            statement.setBigDecimal(12, superficieMax);
            statement.setBigDecimal(13, precioMax);

            // Ejecutar la llamada al procedimiento almacenado
            statement.execute();
            statement.close();

            connection.commit();
            // Mostrar mensaje de éxito
            System.out.println("Éxito: Los datos se han guardado correctamente en la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                System.out.println(
                        "Error: Se ha realizado un rollback debido a un error al guardar los datos en la base de datos.");
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
                System.out.println("Error: No se pudo realizar el rollback.");
            }
        }
    }

    private void limpiarcampos() {
        MONTOTXT.clear();
        ESTADOTXT.clear();
        OBSERVACIONTXT.clear();
        NACIONALIDADTXT.clear();
        DIRECCIONTXT.clear();
        NOMBRETXT.clear();
        INGRESOSTXT.clear();
        OCUPACIONTXT.clear();
        GENEROTXT.clear();
        IDENTIFICACIONTXT.clear();
        TELEFONOSTXT.clear();
        REFERENCIASTXT.clear();
        TIPOTXT.clear();
        TIPOTRANSACCION.clear();
        CANTIDADHABITACIONESTXT.clear();
        GARAJETXT.clear();
        PRECIOMINIMOTXT.clear();
        SUPERFICIETXT.clear();
        ZONATXT.clear();
        CANTIDADBANOS.clear();
        PATIOTXT.clear();
        COMENTARIOADICIONALTXT.clear();
        PRECIOMAXIMOTXT.clear();
        SUPERFICIEMAX.clear();
        FECHAINICIOTXT.setValue(null);
        FECHAFINTXT.setValue(null);
        NACIMIENTOTXT.setValue(null);
    }

    private void cambiarPestana(Tab pestana) {
        tabPane.getSelectionModel().select(pestana);
    }

    public void VolverInicio() {
        cambiarPestana(tabInicio);
        limpiarcampos();
    }


}
