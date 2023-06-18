package com.example.inmobiliariafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class InmobiliariaController {
    private int idContratoBusquedaCC = 0;

    @FXML
    private TextField MONTOTXT, ESTADOTXT, OBSERVACIONTXT, NACIONALIDADTXT, DIRECCIONTXT, NOMBRETXT,
            INGRESOSTXT, OCUPACIONTXT, GENEROTXT, IDENTIFICACIONTXT, TELEFONOSTXT, REFERENCIASTXT,
            TIPOTXT, CANTIDADHABITACIONESTXT, TIPOTRANSACCION, GARAJETXT, PRECIOMINIMOTXT, SUPERFICIETXT,
            ZONATXT, CANTIDADBANOS, PATIOTXT, COMENTARIOADICIONALTXT, PRECIOMAXIMOTXT, SUPERFICIEMAX;

    @FXML
    private DatePicker FECHAINICIOTXT, FECHAFINTXT, NACIMIENTOTXT;

    @FXML
    private Tab tabContrato, tabInicio, tabRegistro, tabRequisitos;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField IDCLIENTETXT;

    private Connection connection;

    @FXML
    void initialize() {
        conectarBaseDatos();
    }


    private void conectarBaseDatos() {
        try {
            connection = SQLServerConnection.getConnection();
            System.out.println("Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void BuscarCliente() {
        String clienteId = IDCLIENTETXT.getText();
        String query = "SELECT * FROM CLIENTE WHERE IDCLIENTE = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, clienteId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("IDCLIENTE");
                String nombre = resultSet.getString("NOMCLIENTE");
                String numeroIdentificacion = resultSet.getString("NUMEROIDENTIFICACIONCLIENTE");

                String mensaje = "Cliente encontrado:\n" +
                        "ID: " + id + "\n" +
                        "Nombre: " + nombre + "\n" +
                        "Número de Identificación: " + numeroIdentificacion;

                Object[] options = {"Agregar Contrato", "Cancelar"};
                int choice = JOptionPane.showOptionDialog(null, mensaje, "Cliente encontrado",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    connection.setAutoCommit(false);
                    cambiarPestana(tabContrato);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Cliente no encontrado.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            String sqlCliente = "INSERT INTO CLIENTE (NOMCLIENTE, GENEROCLIENTE, NUMEROIDENTIFICACIONCLIENTE, " +
                    "INGRESOSCLIENTE, DIRCLIENTE, FECHANACIMIENTOCLIENTE, NACIONALIDADCLIENTE, " +
                    "OCUPACIOCLIENTE, REFERENCIASPERSONALESCLIENTE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            int filasAfectadasCliente = statementCliente.executeUpdate();

            if (filasAfectadasCliente > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registro exitoso");
                alert.setContentText("Registro exitoso");
                alert.showAndWait();

                int idCliente = obtenerIdCliente(statementCliente);

                Alert idClienteAlert = new Alert(Alert.AlertType.INFORMATION);
                idClienteAlert.setTitle("IDCLIENTE GENERADO");
                idClienteAlert.setContentText("El IDCLIENTE GENERADO ES " + idCliente);
                idClienteAlert.showAndWait();

                String[] telefonosArray = telefonos.split(",");
                String sqlTelefonos = "INSERT INTO TELEFONO_CLIENTE (IDCLIENTE, TELEFONOCLIENTE) VALUES (?, ?)";
                PreparedStatement statementTelefonos = connection.prepareStatement(sqlTelefonos);

                for (String telefono : telefonosArray) {
                    statementTelefonos.setInt(1, idCliente);
                    statementTelefonos.setString(2, telefono);
                    statementTelefonos.addBatch();
                }

                int[] filasAfectadasTelefonos = statementTelefonos.executeBatch();
                int totalFilasAfectadasTelefonos = obtenerTotalFilasAfectadas(filasAfectadasTelefonos);

                if (totalFilasAfectadasTelefonos > 0) {
                    Alert telefonosAlert = new Alert(Alert.AlertType.INFORMATION);
                    telefonosAlert.setTitle("Números de teléfono registrados exitosamente");
                    telefonosAlert.setContentText("Números de teléfono registrados exitosamente");
                    telefonosAlert.showAndWait();
                } else {
                    Alert errorTelefonosAlert = new Alert(Alert.AlertType.ERROR);
                    errorTelefonosAlert.setTitle("Error al registrar los números de teléfono");
                    errorTelefonosAlert.setContentText("Error al registrar los números de teléfono");
                    errorTelefonosAlert.showAndWait();
                }

                statementTelefonos.close();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error al registrar");
                errorAlert.setContentText("Error al registrar");
                errorAlert.showAndWait();
            }

            statementCliente.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        cambiarPestana(tabInicio);
    }

    private int obtenerIdCliente(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        } else {
            throw new SQLException("No se pudo obtener el ID del cliente generado.");
        }
    }

    private int obtenerTotalFilasAfectadas(int[] filasAfectadas) {
        int totalFilasAfectadas = 0;
        for (int filas : filasAfectadas) {
            totalFilasAfectadas += filas;
        }
        return totalFilasAfectadas;
    }

    @FXML
    void CambiarVentanaRegistro() {
        cambiarPestana(tabRegistro);
    }

    @FXML
    void CambiarVentanaRequisitos() {
        cambiarPestana(tabRequisitos);
        // Obtener los valores de los jtxtfield de la ventana Contrato
        String monto = MONTOTXT.getText();
        String estado = ESTADOTXT.getText();
        String observaciones = OBSERVACIONTXT.getText();
        LocalDate fechaInicio = FECHAINICIOTXT.getValue();
        LocalDate fechaFin = FECHAFINTXT.getValue();

        // Obtener el ID de cliente de la pestaña Inicio
        String idCliente = IDCLIENTETXT.getText();

        // Insertar los valores en la base de datos CONTRATO_BUSQUEDA
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

    @FXML
    void CancelarR() {
        cambiarPestana(tabInicio);
        limpiarCamposRegistro();

        try {
            connection.rollback();
            System.out.println("Rollback realizado con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al realizar el rollback.");
        }
    }


    @FXML
    void FinalizarC() {
        cambiarPestana(tabInicio);

        // Obtener los valores de los JTextField
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

        // Insertar los valores en la tabla REQUERIMIENTOS_CLIENTE
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
            System.out.println("Éxito Los datos se han guardado correctamente en la base de datos.");
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

    private void limpiarCamposRegistro() {
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
        limpiarCamposRegistro();
    }
}
