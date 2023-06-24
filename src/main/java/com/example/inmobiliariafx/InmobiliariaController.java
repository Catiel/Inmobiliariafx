package com.example.inmobiliariafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

/**
 * Controlador de la aplicación de inmobiliaria.
 */
public class InmobiliariaController {
    private int idContratoBusquedaCC = 0; // Almacena el ID de contrato para la búsqueda por código de cliente

    @FXML
    private TextField MONTOTXT, ESTADOTXT, OBSERVACIONTXT, NACIONALIDADTXT, DIRECCIONTXT, NOMBRETXT, INGRESOSTXT, OCUPACIONTXT, GENEROTXT, IDENTIFICACIONTXT, TELEFONOSTXT, REFERENCIASTXT, TIPOTXT, CANTIDADHABITACIONESTXT, TIPOTRANSACCION, GARAJETXT, PRECIOMINIMOTXT, SUPERFICIETXT, ZONATXT, CANTIDADBANOS, PATIOTXT, COMENTARIOADICIONALTXT, PRECIOMAXIMOTXT, SUPERFICIEMAX;
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
     * Busca un cliente en la base de datos utilizando su ID de cliente y muestra un mensaje con su información.
     * Además, brinda la opción de agregar un contrato para el cliente encontrado.
     * <p>
     * Este método obtiene el ID del cliente ingresado en un campo de texto de la interfaz gráfica.
     * Luego, realiza una consulta SQL para buscar al cliente en la tabla CLIENTE utilizando su ID.
     * Si se encuentra un cliente con el ID proporcionado, se obtienen sus datos y se muestra un cuadro de diálogo
     * con la información del cliente. El cuadro de diálogo ofrece las opciones "Agregar Contrato" y "Cancelar".
     * Si se selecciona "Agregar Contrato", se desactiva la confirmación automática de cambios en la base de datos
     * y se cambia a la pestaña de contrato en la interfaz gráfica. En caso de no encontrar un cliente con el ID
     * ingresado, se muestra un cuadro de diálogo informando que el cliente no ha sido encontrado.
     * </p>
     */
    @FXML
    void BuscarCliente() {
        // Obtener el ID del cliente desde un campo de texto
        String clienteId = IDCLIENTETXT.getText();

        // Consulta SQL para buscar el cliente por su ID
        String query = "SELECT * FROM CLIENTE WHERE IDCLIENTE = ?";

        try {
            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, clienteId);

            // Ejecutar la consulta y obtener los resultados
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Si se encontró un cliente, obtener los datos del resultado
                String id = resultSet.getString("IDCLIENTE");
                String nombre = resultSet.getString("NOMCLIENTE");
                String numeroIdentificacion = resultSet.getString("NUMEROIDENTIFICACIONCLIENTE");

                // Crear un mensaje con la información del cliente encontrado
                String mensaje = "Cliente encontrado:\n" + "ID: " + id + "\n" + "Nombre: " + nombre + "\n" + "Número de Identificación: " + numeroIdentificacion;

                // Mostrar un cuadro de diálogo con el mensaje y las opciones "Agregar Contrato" y "Cancelar"
                Object[] options = {"Agregar Contrato", "Cancelar"};
                int choice = JOptionPane.showOptionDialog(null, mensaje, "Cliente encontrado", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    // Si se selecciona "Agregar Contrato", desactivar la confirmación automática de cambios en la base de datos
                    connection.setAutoCommit(false);
                    cambiarPestana(tabContrato);
                }
            } else {
                // Si no se encontró un cliente, mostrar un cuadro de diálogo de mensaje
                JOptionPane.showMessageDialog(null, "Cliente no encontrado.");
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
     * Registra un nuevo cliente en la base de datos con la información proporcionada,
     * incluyendo los números de teléfono asociados al cliente.
     * <p>
     * Este método toma los valores ingresados en los campos de texto de la interfaz gráfica,
     * como el género, número de identificación, ingresos, nombre, dirección, fecha de nacimiento,
     * nacionalidad, ocupación, referencias personales y números de teléfono. Luego, registra al
     * cliente en la tabla CLIENTE de la base de datos y obtiene el ID del cliente generado. A
     * continuación, registra los números de teléfono asociados al cliente en la tabla
     * TELEFONO_CLIENTE. Si el registro se realiza correctamente, se muestran mensajes de éxito
     * correspondientes. En caso de error, se muestran mensajes de error. Finalmente, cambia a la
     * pestaña de inicio en la interfaz gráfica.
     * </p>
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
            // Consulta para insertar el cliente en la tabla CLIENTE
            String sqlCliente = "INSERT INTO CLIENTE (NOMCLIENTE, GENEROCLIENTE, NUMEROIDENTIFICACIONCLIENTE, " + "INGRESOSCLIENTE, DIRCLIENTE, FECHANACIMIENTOCLIENTE, NACIONALIDADCLIENTE, " + "OCUPACIOCLIENTE, REFERENCIASPERSONALESCLIENTE) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statementCliente = connection.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
            statementCliente.setString(1, nombre);
            statementCliente.setString(2, genero);
            statementCliente.setString(3, numeroIdentificacion);
            statementCliente.setBigDecimal(4, ingresos);
            statementCliente.setString(5, direccion);
            statementCliente.setDate(6, java.sql.Date.valueOf(fechaNacimiento));
            statementCliente.setString(7, nacionalidad);
            statementCliente.setString(8, ocupacion);
            statementCliente.setString(9, referenciasPersonales);

            // Ejecutar la consulta para insertar el cliente y obtener el número de filas afectadas
            int filasAfectadasCliente = statementCliente.executeUpdate();

            if (filasAfectadasCliente > 0) {
                // Si se registró el cliente correctamente, mostrar un mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registro exitoso");
                alert.setContentText("Registro exitoso");
                alert.showAndWait();

                // Obtener el ID del cliente generado
                int idCliente = obtenerIdCliente(statementCliente);

                // Mostrar el ID del cliente generado en un mensaje
                Alert idClienteAlert = new Alert(Alert.AlertType.INFORMATION);
                idClienteAlert.setTitle("IDCLIENTE GENERADO");
                idClienteAlert.setContentText("El IDCLIENTE GENERADO ES " + idCliente);
                idClienteAlert.showAndWait();

                // Dividir los números de teléfono ingresados en un arreglo
                String[] telefonosArray = telefonos.split(",");

                // Consulta para insertar los números de teléfono en la tabla TELEFONO_CLIENTE
                String sqlTelefonos = "INSERT INTO TELEFONO_CLIENTE (IDCLIENTE, TELEFONOCLIENTE) VALUES (?, ?)";
                PreparedStatement statementTelefonos = connection.prepareStatement(sqlTelefonos);

                // Iterar sobre los números de teléfono y agregarlos a la consulta como lotes
                for (String telefono : telefonosArray) {
                    statementTelefonos.setInt(1, idCliente);
                    statementTelefonos.setString(2, telefono);
                    statementTelefonos.addBatch();
                }

                // Ejecutar la consulta para insertar los números de teléfono y obtener el número de filas afectadas
                int[] filasAfectadasTelefonos = statementTelefonos.executeBatch();
                int totalFilasAfectadasTelefonos = obtenerTotalFilasAfectadas(filasAfectadasTelefonos);

                if (totalFilasAfectadasTelefonos > 0) {
                    // Si se registraron los números de teléfono correctamente, mostrar un mensaje de éxito
                    Alert telefonosAlert = new Alert(Alert.AlertType.INFORMATION);
                    telefonosAlert.setTitle("Números de teléfono registrados exitosamente");
                    telefonosAlert.setContentText("Números de teléfono registrados exitosamente");
                    telefonosAlert.showAndWait();
                } else {
                    // Si ocurrió un error al registrar los números de teléfono, mostrar un mensaje de error
                    Alert errorTelefonosAlert = new Alert(Alert.AlertType.ERROR);
                    errorTelefonosAlert.setTitle("Error al registrar los números de teléfono");
                    errorTelefonosAlert.setContentText("Error al registrar los números de teléfono");
                    errorTelefonosAlert.showAndWait();
                }

                statementTelefonos.close();
            } else {
                // Si ocurrió un error al registrar el cliente, mostrar un mensaje de error
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error al registrar");
                errorAlert.setContentText("Error al registrar");
                errorAlert.showAndWait();
            }

            statementCliente.close();
        } catch (SQLException ex) {
            // Manejar cualquier error de SQL imprimiendo la traza de la excepción
            ex.printStackTrace();
        }

        // Cambiar a la pestaña de inicio después de registrar el cliente
        cambiarPestana(tabInicio);
    }


    /**
     * Obtiene el ID del cliente generado después de ejecutar una consulta INSERT en la base de datos.
     *
     * @param statement el objeto PreparedStatement utilizado para ejecutar la consulta INSERT.
     * @return el ID del cliente generado.
     * @throws SQLException si no se puede obtener el ID del cliente generado.
     */
    private int obtenerIdCliente(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("No se pudo obtener el ID del cliente generado.");
        }
    }

    /**
     * Obtiene el total de filas afectadas por una operación en la base de datos.
     *
     * @param filasAfectadas el arreglo de enteros que representa las filas afectadas.
     * @return el total de filas afectadas.
     */
    private int obtenerTotalFilasAfectadas(int[] filasAfectadas) {
        int totalFilasAfectadas = 0;
        for (int filas : filasAfectadas) {
            totalFilasAfectadas += filas;
        }
        return totalFilasAfectadas;
    }

    /**
     * Cambia a la ventana de registro en la interfaz gráfica.
     */
    @FXML
    void CambiarVentanaRegistro() {
        cambiarPestana(tabRegistro);
    }

    /**
     * Cambia a la ventana de requisitos en la interfaz gráfica y guarda los valores del contrato en la base de datos.
     * <p>
     * Este método obtiene los valores de los campos de texto de la ventana de contrato, incluyendo el monto, estado, observaciones,
     * fecha de inicio y fecha de fin. Luego, recupera el ID de cliente de la pestaña Inicio. A continuación, inserta los valores
     * del contrato en la tabla CONTRATO_BUSQUEDA de la base de datos, estableciendo una transacción para asegurar la integridad de los datos.
     * Si la inserción es exitosa, se obtiene el ID del contrato generado y se almacena en la variable idContratoBusquedaCC.
     * <p>
     * En caso de producirse una excepción SQL, se realiza un rollback para deshacer cualquier cambio en la base de datos.
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

        // Insertar los valores en la tabla CONTRATO_BUSQUEDA de la base de datos
        String insertQuery = "INSERT INTO CONTRATO_BUSQUEDA (IDCLIENTE, OBSERVACIONESADICIONALES, ESTADOCONTRATOBUSQUEDA, FECHAINICIOBUSQUEDA, FECHAFINBUSQUEDA, MONTOBUSQUEDA) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setInt(1, Integer.parseInt(idCliente));
            insertStatement.setString(2, observaciones);
            insertStatement.setString(3, estado);
            insertStatement.setDate(4, java.sql.Date.valueOf(fechaInicio));
            insertStatement.setDate(5, java.sql.Date.valueOf(fechaFin));
            insertStatement.setBigDecimal(6, new BigDecimal(monto));

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idContratoBusquedaCC = generatedKeys.getInt(1);
                }
            }

            insertStatement.close();

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
     * Cancela el proceso de registro del contrato y los requisitos, vuelve a la ventana de inicio y realiza un rollback en la base de datos.
     * <p>
     * Este método cambia a la pestaña de inicio en la interfaz gráfica y limpia los campos de texto del registro.
     * Luego, realiza un rollback en la base de datos para deshacer cualquier cambio realizado durante el proceso de registro.
     * Si el rollback se realiza correctamente, se muestra un mensaje de éxito en la consola. En caso de producirse
     * un error durante el rollback, se muestra un mensaje de error en la consola.
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
     * Finaliza el proceso de registro del contrado busqueda, vuelve a la ventana de inicio y guarda los valores en la base de datos.
     * <p>
     * Este método cambia a la pestaña de inicio en la interfaz gráfica. Luego, obtiene los valores de los campos de texto
     * de la ventana de registro, incluyendo el tipo de inmueble, la zona, el precio mínimo, la cantidad de habitaciones,
     * la cantidad de baños, la superficie mínima, el comentario adicional, el tipo de transacción, la cantidad de patios,
     * la cantidad de garajes, la superficie máxima y el precio máximo. A continuación, inserta estos valores en la tabla
     * REQUERIMIENTOS_CLIENTE de la base de datos. Si la inserción es exitosa, se realiza un commit para confirmar los cambios
     * en la base de datos y se muestra un mensaje de éxito en la consola. En caso de producirse un error durante la inserción,
     * se realiza un rollback para deshacer cualquier cambio y se muestra un mensaje de error en la consola.
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

        // Insertar los valores en la tabla REQUERIMIENTOS_CLIENTE de la base de datos
        try {
            String query = "INSERT INTO REQUERIMIENTOS_CLIENTE (IDCONTRATOBUSQUEDA, TIPOINMUEBLE, ZONA, PRECIOMIN, CANTIDADHABITACIONES, CANTIDADBANOS, SUPERFICIEMIN, COMENTARIOADICIONAL, TIPOTRANSACCION, PATIOS, GARAJES, SUPERFICIEMAX, PRECIOMAX) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
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
            statement.executeUpdate();
            statement.close();
            connection.commit();
            // Mostrar mensaje de éxito
            System.out.println("Éxito: Los datos se han guardado correctamente en la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                System.out.println("Error: Se ha realizado un rollback debido a un error al guardar los datos en la base de datos.");
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
                System.out.println("Error: No se pudo realizar el rollback.");
            }
        }
    }


    /**
     * Limpia los campos de texto y selección.
     */
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

    /**
     * Cambia a la pestaña especificada en el TabPane.
     *
     * @param pestana La pestaña a la que se quiere cambiar.
     */
    private void cambiarPestana(Tab pestana) {
        tabPane.getSelectionModel().select(pestana);
    }

    /**
     * Cambia a la pestaña de inicio en la interfaz gráfica y limpia los campos.
     * <p>
     * Este método cambia la pestaña activa en la interfaz gráfica a la pestaña de inicio
     * y realiza la limpieza de los campos en dicha pestaña. Es útil para restablecer el
     * estado inicial de la aplicación después de completar ciertas operaciones, especialmente
     * cuando no se ha realizado ninguna transacción todavía y se desea volver al punto de partida.
     * </p>
     */
    public void VolverInicio() {
        cambiarPestana(tabInicio);
        limpiarcampos();
    }


}
