package com.example.sistemadetrafico;

import java.util.ArrayList;
import java.util.List;

public class Grafo {
    private List<Parada> paradas;
    private List<Ruta> rutas;

    public Grafo(List<Parada> paradas, List<Ruta> rutas) {
        this.paradas = paradas;
        this.rutas = rutas;
    }

    private List<Parada> Dijkstra(Parada inicio, Parada destino, String criterio) {
        int n = paradas.size();
        boolean[] visitado = new boolean[n];
        Parada[] anterior = new Parada[n];

        // Arreglos para almacenar tiempos, costos, transbordos y distancias
        int[] tiempos = new int[n];
        float[] costos = new float[n];
        int[] transbordos = new int[n];
        int[] distancias = new int[n];

        // Inicializar los valores a infinito o máximos
        for (int i = 0; i < n; i++) {
            tiempos[i] = Integer.MAX_VALUE;
            costos[i] = Float.MAX_VALUE;
            transbordos[i] = Integer.MAX_VALUE;
            distancias[i] = Integer.MAX_VALUE;
            visitado[i] = false;
            anterior[i] = null;
        }

        // Establecer el valor inicial para la parada de inicio
        int inicioIndex = paradas.indexOf(inicio);
        tiempos[inicioIndex] = 0;
        costos[inicioIndex] = 0;
        transbordos[inicioIndex] = 0;
        distancias[inicioIndex] = 0; // La distancia desde el origen es 0

        while (true) {
            // Encontrar la parada no visitada con el menor costo
            float menorCosto = Float.MAX_VALUE;
            int minIndex = -1;

            for (int i = 0; i < n; i++) {
                if (!visitado[i] && costos[i] < menorCosto) {
                    menorCosto = costos[i];
                    minIndex = i;
                }
            }

            if (minIndex == -1) break; // No hay más nodos accesibles

            visitado[minIndex] = true;
            Parada paradaActual = paradas.get(minIndex);

            // Relajar las rutas conectadas
            for (Ruta ruta : rutas) {
                if (ruta.getOrigen().equals(paradaActual)) {
                    Parada paradaDestino = ruta.getDestino();
                    int destinoIndex = paradas.indexOf(paradaDestino);

                    if (!visitado[destinoIndex]) {
                        float peso = obtenerPeso(ruta, criterio); // Obtener el peso dependiendo del criterio
                        float nuevoCosto = costos[minIndex] + peso;

                        // Calculando el nuevo tiempo, costo, transbordos y distancia
                        int nuevoTiempo = tiempos[minIndex] + ruta.getTiempo();
                        int nuevoTransbordo = transbordos[minIndex] + ruta.getTransbordos();
                        int nuevaDistancia = distancias[minIndex] + ruta.getDistancia();

                        if (nuevoCosto < costos[destinoIndex]) {
                            costos[destinoIndex] = nuevoCosto;
                            tiempos[destinoIndex] = nuevoTiempo;
                            transbordos[destinoIndex] = nuevoTransbordo;
                            distancias[destinoIndex] = nuevaDistancia;
                            anterior[destinoIndex] = paradaActual;
                        }
                    }
                }
            }
        }

        // Reconstruir la ruta
        List<Parada> trayecto = new ArrayList<>();
        Parada actual = destino;
        while (actual != null) {
            trayecto.add(0, actual);
            int actualIndex = paradas.indexOf(actual);
            actual = anterior[actualIndex];
        }

        // Validar si se encontró una ruta
        return (trayecto.isEmpty() || trayecto.get(0) != inicio) ? new ArrayList<>() : trayecto;
    }



    // Métodos públicos para buscar rutas según criterio
    public List<Parada> buscarPorTiempo(Parada inicio, Parada destino) {
        return Dijkstra(inicio, destino, "tiempo");
    }

    public List<Parada> buscarPorCosto(Parada inicio, Parada destino) {
        return Dijkstra(inicio, destino, "costo");
    }

    public List<Parada> buscarPorTransbordos(Parada inicio, Parada destino) {
        return Dijkstra(inicio, destino, "transbordos");
    }

    private float obtenerPeso(Ruta ruta, String criterio) {
        switch (criterio) {
            case "tiempo":
                return ruta.getTiempo(); // Tiempo en minutos
            case "costo":
                return ruta.getCosto(); // Costo en dólares
            case "transbordos":
                return ruta.getTransbordos();
            default:
                return ruta.getTiempo();
        }
    }

    public int calculoDistanciaTotal(List<Parada> ruta) {
        int distanciaTotal = 0;

        for (int i = 1; i < ruta.size(); i++) {
            Parada origen = ruta.get(i - 1);
            Parada destino = ruta.get(i);

            for (Ruta r : rutas) {
                if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                    distanciaTotal += r.getDistancia();
                    break;
                }
            }
        }

        return distanciaTotal;
    }

    public int calculoTiempoTotal(List<Parada> ruta) {
        int tiempoTotal = 0;

        for (int i = 1; i < ruta.size(); i++) {
            Parada origen = ruta.get(i - 1);
            Parada destino = ruta.get(i);

            for (Ruta r : rutas) {
                if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                    tiempoTotal += r.getTiempo();
                    break;
                }
            }
        }

        return tiempoTotal;
    }

    public float calculoCostoTotal(List<Parada> ruta) {
        float costoTotal = 0.0f;

        for (int i = 1; i < ruta.size(); i++) {
            Parada origen = ruta.get(i - 1);
            Parada destino = ruta.get(i);

            for (Ruta r : rutas) {
                if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                    costoTotal += r.getCosto();
                    break;
                }
            }
        }

        return costoTotal;
    }

    public int calculoTransbordosTotales(List<Parada> ruta) {
        int transbordosTotales = 0;

        for (int i = 1; i < ruta.size(); i++) {
            Parada origen = ruta.get(i - 1);
            Parada destino = ruta.get(i);

            for (Ruta r : rutas) {
                if (r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                    transbordosTotales += r.getTransbordos();
                    break;
                }
            }
        }

        return transbordosTotales;
    }
}

