package com.example.sistemadetrafico;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GrafoController {
    @FXML
    private MenuItem menuItemAgParada;
    @FXML
    private Pane paneArea;
    @FXML
    private MenuItem menuItemAgRuta;
    @FXML
    private MenuItem menuItemEliminarRuta;
    @FXML
    private MenuItem menuItemEliminarParada;
    @FXML
    private MenuItem menuItemModificarRuta;
    @FXML
    private MenuItem menuItemModificarParada;
    @FXML
    private ComboBox<String> cmbGestionador;
    @FXML
    private Button btnBuscarRuta;
    @FXML
    private TextArea rutaInfoTextArea;


    private ParadaManager paradaManager;
    private RutaManager rutaManager;
    private Grafo grafo;
    private List<Parada> paradasSeleccionadas = new ArrayList<>();
    private String eleccionCriterio = "tiempo";

    private Parada Origen; // Variable para la parada de origen
    private Parada Destino;

    public GrafoController() {
    }

    @FXML
    public void initialize() {
        paradaManager = new ParadaManager(paneArea);
        rutaManager = new RutaManager(paradaManager.getParadas());
        grafo = new Grafo(paradaManager.getParadas(), rutaManager.getRutas());

        // Configurar los eventos de menú
        menuItemAgParada.setOnAction(event -> paradaManager.abrirVentanaAgregarParada());
        menuItemAgRuta.setOnAction(event -> rutaManager.abrirVentanaAgregarRuta(paneArea));
        menuItemEliminarParada.setOnAction(event -> paradaManager.abrirVentanaEliminarParada());
        menuItemEliminarRuta.setOnAction(event -> rutaManager.abrirVentanaEliminarRuta(paneArea));
        menuItemModificarParada.setOnAction(event -> paradaManager.abrirVentanaModificarParada());
        menuItemModificarRuta.setOnAction(event -> rutaManager.abrirVentanaModificarRuta(paneArea));

        configurarEventoSeleccionParada();
        configurarComboBoxGestionador();

        btnBuscarRuta.setDisable(paradasSeleccionadas.size() == 2);

        // Configurar el evento de búsqueda de ruta
        btnBuscarRuta.setOnAction(event -> {
            reiniciarColorLineas();
            if (Origen != null && Destino != null) {
                List<Parada> ruta;
                switch (eleccionCriterio) {
                    case "tiempo":
                        ruta = grafo.buscarPorTiempo(Origen, Destino);
                        break;
                    case "costo":
                        ruta = grafo.buscarPorCosto(Origen, Destino);
                        break;
                    case "transbordos":
                        ruta = grafo.buscarPorTransbordos(Origen, Destino);
                        break;
                    default:
                        ruta = grafo.buscarPorTiempo(Origen, Destino);
                }

                if (ruta.isEmpty()) {
                    mostrarMensaje("Sin ruta", "No se encontró una ruta entre los puntos seleccionados.");
                } else {
                    mostrarDetallesRuta(ruta);
                    actualizarColorLineas(ruta);
                    mostrarMensaje("Ruta encontrada", "Ruta calculada con éxito.");
                }
            }
        });

    }

    private void configurarEventoSeleccionParada() {
        paneArea.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof Circle circle) {
                String idParada = circle.getId();
                if (idParada != null && idParada.startsWith("Parada-")) {
                    int numeroParada = Integer.parseInt(idParada.replace("Parada-", ""));
                    Parada parada = paradaManager.buscarParada(numeroParada);

                    if (parada != null) {
                        manejarSeleccionParada(parada, circle);
                    }
                }
            }
        });
    }

    private int manejarSeleccionParada(Parada parada, Circle circle) {
        if (paradasSeleccionadas.contains(parada)) {
            // Deseleccionar parada
            paradasSeleccionadas.remove(parada);
            circle.setFill(Color.RED);
        } else {
            // Seleccionar nueva parada
            if (paradasSeleccionadas.size() >= 2) {
                // Deseleccionar la primera parada de la lista
                Parada primeraParada = paradasSeleccionadas.get(0);
                Circle primerCirculo = (Circle) paneArea.lookup("#Parada-" + primeraParada.getNumero());
                if (primerCirculo != null) {
                    primerCirculo.setFill(Color.RED);
                }
                paradasSeleccionadas.remove(0);
            }

            paradasSeleccionadas.add(parada);
            circle.setFill(Color.GREEN);
        }

        // Actualizar origen y destino
        if (paradasSeleccionadas.size() == 2) {
            Origen = paradasSeleccionadas.get(0);
            Destino = paradasSeleccionadas.get(1);
        } else {
            Origen = null;
            Destino = null;
        }

        return paradasSeleccionadas.size();
    }

    private void configurarComboBoxGestionador() {
        // Agregar opciones al ComboBox
        cmbGestionador.getItems().addAll("tiempo", "transbordos", "costo");

        // Manejar selección
        cmbGestionador.setOnAction(event -> {
            String seleccion = cmbGestionador.getSelectionModel().getSelectedItem();

            if (seleccion == null || seleccion.isBlank()) {
                eleccionCriterio = "tiempo"; // Selección general por defecto
            } else {
                eleccionCriterio = seleccion; // Guardar selección específica
            }
        });
    }

    private void actualizarColorLineas(List<Parada> ruta) {
        for (int i = 0; i < ruta.size() - 1; i++) {
            Parada origen = ruta.get(i);
            Parada destino = ruta.get(i + 1);

            Line linea = (Line) paneArea.lookup("#Ruta-" + origen.getNumero() + "-" + destino.getNumero());
            if (linea != null) {
                linea.setStroke(Color.GREEN); // Color para resaltar la ruta encontrada
            }
        }
    }

    private void reiniciarColorLineas() {
        for (Ruta ruta : rutaManager.getRutas()) {
            Line linea = (Line) paneArea.lookup("#Ruta-" + ruta.getOrigen().getNumero() + "-" + ruta.getDestino().getNumero());
            if (linea != null) {
                linea.setStroke(Color.RED); // Cambia a un color por defecto (gris o el que prefieras)
            }
        }
    }

    private void mostrarDetallesRuta(List<Parada> ruta) {
        if (ruta == null || ruta.isEmpty()) {
            rutaInfoTextArea.setText("No se encontró ninguna ruta.");
            return;
        }

        StringBuilder detallesRuta = new StringBuilder();

        // Muestra las paradas
        for (Parada parada : ruta) {
            detallesRuta.append("Parada: ").append(parada.getNumero()).append("\n");
        }

        // Usa los métodos auxiliares del grafo para calcular totales
        int distanciaTotal = grafo.calculoDistanciaTotal(ruta);
        int tiempoTotal = grafo.calculoTiempoTotal(ruta);
        int transbordosTotales = grafo.calculoTransbordosTotales(ruta);
        float costoTotal = grafo.calculoCostoTotal(ruta);

        // Agrega los totales al final del texto
        detallesRuta.append("\nDistancia Total: ").append(distanciaTotal).append(" Km\n");
        detallesRuta.append("Tiempo Total: ").append(tiempoTotal).append(" minutos\n");
        detallesRuta.append("Total de Transbordos: ").append(transbordosTotales).append("\n");
        detallesRuta.append("Costo Total: ").append(costoTotal).append(" $\n");

        // Establece el texto en el TextArea
        rutaInfoTextArea.setText(detallesRuta.toString());
    }


    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
