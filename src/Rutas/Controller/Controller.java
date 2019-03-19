package Rutas.Controller;

import Rutas.Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> mOrigen;
    @FXML
    private ComboBox<String> mDestino;
    @FXML
    private ToggleGroup group;
    @FXML
    private TableView<Ruta> table;
    @FXML
    private TableColumn columnRuta;
    @FXML
    private TableColumn columnEstado;
    @FXML
    private TableColumn columnCapital;
    @FXML
    private TableColumn columnTiempo;
    @FXML
    private TableColumn columnPrecio;
    @FXML
    private TableColumn columnDistancia;

    @FXML
    private Button mBuscar;

    ObservableList<Ruta> rutaList;

    List<Estado> listaEstados;

    int[][] matrizMDistancia;
    int[][] matrizMTiempo;

    int[][] matrizMDistanciaAerea;
    int[][] matrizMTiempoAerea;

    int[][] matrizMCombinadaDistancia;
    int[][] matrizTCombinadaDistancia;
    int[][] matrizMCombinadaTiempo;
    int[][] matrizTCombinadaTiempo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getEstados();
        /*TERRESTRE*/
        matrizMDistancia = ReadDatabase.getMatrizM();
        matrizMTiempo = getMatrizMTiempo(ReadDatabase.getMatrizM(), 90);
        matrizMCombinadaDistancia = ReadDatabase.getMatrizM();
        matrizMCombinadaDistancia = combinarMatrices(ReadDatabase.getMatrizM(), ReadDatabase.getMatrizMAerea(), matrizMCombinadaDistancia);
        matrizTCombinadaDistancia = llenarMatriz99();
        matrizTCombinadaDistancia = floyd(matrizMCombinadaDistancia, matrizTCombinadaDistancia);
        imprimirMatriz(matrizTCombinadaDistancia);

        /*AEREA*/
        matrizMDistanciaAerea = ReadDatabase.getMatrizMAerea();;
        matrizMTiempoAerea = getMatrizMTiempo(ReadDatabase.getMatrizMAerea(), 940);
        matrizMCombinadaTiempo = combinarMatrices(matrizMTiempo, matrizMTiempoAerea, matrizMTiempo);
        matrizTCombinadaTiempo = llenarMatriz99();
        matrizTCombinadaTiempo = floydTiempo(matrizMCombinadaTiempo, matrizTCombinadaTiempo);
        imprimirMatriz(matrizTCombinadaTiempo);


    }

    public void getEstados(){
        listaEstados = ReadDatabase.getAllEstados();
        ObservableList<String> list = FXCollections.observableArrayList();
        for(Estado estado : listaEstados){
            list.add(estado.getNombre());
        }
        mOrigen.setItems(list);
        mDestino.setItems(list);
    }

    @FXML
    public void onButtonBuscarClick() {
        if(mOrigen.getSelectionModel().getSelectedItem() != null && mDestino.getSelectionModel().getSelectedItem() != null) {
            if (group.getSelectedToggle() != null) {
                RadioButton radio = (RadioButton) group.getSelectedToggle();
                String origen = mOrigen.getSelectionModel().getSelectedItem().toString();
                String destino = mDestino.getSelectionModel().getSelectedItem().toString();
                int idOrigen = getIdEstado(origen);
                int idOrigenTemp = idOrigen;
                int idDestino = getIdEstado(destino);
                switch (radio.getText()) {
                    case "Ruta mas corta":
                        List<Ruta> ruta = getRutaCorta(idOrigen, idDestino);
                        llenarTabla(ruta);
                        break;
                    case "Ruta mas rapida":
                        List<Ruta> rutaRapida = getRutaRapida(idOrigen, idDestino);
                        llenarTabla(rutaRapida);
                        break;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Debes seleccionar un metodo de busqueda ");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Debes seleccionar Origen y Destino ");
            alert.showAndWait();
        }

    }

    private void llenarTabla(List<Ruta> ruta){
        if(table.getItems().size() > 0){
            table.getItems().clear();
        }
        columnRuta.setCellValueFactory(new PropertyValueFactory<>("nombreRuta"));
        columnEstado.setCellValueFactory(new PropertyValueFactory<>("nombreEstado"));
        columnCapital.setCellValueFactory(new PropertyValueFactory<>("nombreCapital"));
        columnPrecio.setCellValueFactory(new PropertyValueFactory<>(""));
        columnTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        columnDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
        rutaList = FXCollections.observableArrayList();
        for(Ruta r : ruta){
            rutaList.add(r);
        }
        table.setItems(rutaList);

    }


    private int getIdEstado(String estado){
        for(Estado x : listaEstados){
            if(x.getNombre().equals(estado)){
                return x.getIdEstado();
            }
        }
        return 99;
    }

    private List<Ruta> getRutaCorta(int idOrigen, int idDestino) {
        int idDestinoO = idDestino;
        int idOrigenO = idOrigen;
       Queue<Integer> cola = getRutaDistancia(idOrigen, idDestino);
        ((LinkedList<Integer>) cola).addFirst(idOrigenO);
        ((LinkedList<Integer>) cola).addLast(idDestinoO);
        imprimirCola(cola);
        List<Ruta> ruta = armarRuta(cola);

        return ruta;
    }

    private List<Ruta> getRutaRapida(int idOrigen, int idDestino){
        int idDestinoO = idDestino;
        int idOrigenO = idOrigen;
        Queue<Integer> cola = getRutaTiempo(idOrigen, idDestino);
        ((LinkedList<Integer>) cola).addFirst(idOrigenO);
        ((LinkedList<Integer>) cola).addLast(idDestinoO);
        imprimirCola(cola);
        List<Ruta> ruta = armarRuta(cola);

        return ruta;
    }


    private Queue<Integer> getRutaDistancia(int idOrigen, int idDestino){
        Stack<Integer> pila = new Stack<>();
        Queue<Integer> cola = new LinkedList<>();
        int idTemporal = idOrigen;
        int idDestinoO = idDestino;
        boolean salir = false;
        pila.push(idDestino);
        while (!salir) {
            if (matrizTCombinadaDistancia[idTemporal][idDestino] != 9999999) {
                pila.push(matrizTCombinadaDistancia[idTemporal][idDestino]);
                idDestino = matrizTCombinadaDistancia[idTemporal][idDestino];
            } else {
                idTemporal = idDestino;
                if (!pila.isEmpty()) {
                    if (idTemporal == idDestinoO) {
                        salir = true;
                    } else {
                        if (idTemporal == pila.peek()) {
                            cola.add(pila.pop());
                            idDestino = pila.peek();
                        }
                    }
                } else salir = true;
            }
        }

        return cola;
    }

    private Queue<Integer> getRutaTiempo(int idOrigen, int idDestino){
        Stack<Integer> pila = new Stack<>();
        Queue<Integer> cola = new LinkedList<>();
        int idTemporal = idOrigen;
        int idDestinoO = idDestino;
        boolean salir = false;
        pila.push(idDestino);
        while (!salir) {
            if (matrizTCombinadaTiempo[idTemporal][idDestino] != 9999999) {
                pila.push(matrizTCombinadaTiempo[idTemporal][idDestino]);
                idDestino = matrizTCombinadaTiempo[idTemporal][idDestino];
            } else {
                idTemporal = idDestino;
                if (!pila.isEmpty()) {
                    if (idTemporal == idDestinoO) {
                        salir = true;
                    } else {
                        if (idTemporal == pila.peek()) {
                            cola.add(pila.pop());
                            idDestino = pila.peek();
                        }
                    }
                } else salir = true;
            }
        }

        return cola;
    }


    private List<Ruta> armarRuta(Queue<Integer> cola){
        List<Ruta> ruta = new ArrayList<>();
        while(!cola.isEmpty() && cola.size() >= 2){
            int idOrigen = cola.poll();
            if(!wichMatriz(idOrigen, cola.peek())){
                RutaAerea rAerea = new RutaAerea(idOrigen, cola.peek());
                rAerea.setTiempo(matrizMTiempoAerea[idOrigen][cola.peek()]);
                rAerea.setDistancia(matrizMDistanciaAerea[idOrigen][cola.peek()]);
                ruta.add(rAerea);
            }
            else{
                RutaTerrestre rTerrestre = new RutaTerrestre(idOrigen, cola.peek());
                rTerrestre.setTiempo(matrizMTiempo[idOrigen][cola.peek()]);
                rTerrestre.setDistancia(matrizMDistancia[idOrigen][cola.peek()]);
                ruta.add(rTerrestre);
            }
        }
        return ruta;
    }

    private boolean wichMatriz(int idOrigen, int idDestino){//false =  TERRESTRE, true = Aerea
        return (matrizMDistancia[idOrigen][idDestino] < matrizMDistanciaAerea[idOrigen][idDestino]) ? true : false;
    }



    private static int[][] llenarMatriz99(){
        int[][] matrizT = new int[32][32];
        for(int i =0; i<matrizT.length; i++){
            matrizT[0][i] = i;
        }
        for(int i = 0;i<matrizT.length;i++){
            matrizT[i][0] = i;
        }
        for (int i = 1; i < matrizT.length; i++) {
            for (int j = 1; j < matrizT.length; j++) {
                matrizT[i][j] = 9999999;
            }
        }
        return matrizT;
    }



    private static int[][] floyd(int[][] matrizM, int[][] matrizT){
        int n = 32;
        for(int k=1;k<n;k++){
            for(int i=1;i<n;i++){
                for(int j=1;j<n;j++){
                    if(matrizM[i][k]+matrizM[k][j]<matrizM[i][j]){

                        matrizM[i][j]=matrizM[i][k]+matrizM[k][j];
                        matrizT[i][j]=k;
                    }
                }
            }
        }
        return matrizT;
    }

    private static int[][] floydTiempo(int[][] matrizM, int[][] matrizT){
        int n = 32;
        for(int k=1;k<n;k++){
            for(int i=1;i<n;i++){
                for(int j=1;j<n;j++){

                    if(matrizM[i][k]+matrizM[k][j] < matrizM[i][j]){

                        matrizM[i][j]= matrizM[i][k]+matrizM[k][j];
                        matrizT[i][j]=k;
                    }
                }
            }
        }
        return matrizT;
    }


    private void imprimirMatriz(int[][] matrizT){
        System.out.println("MATRIZ");
        for(int i=0; i<matrizT.length;i++){
            for(int j=0;j<matrizT[i].length;j++){
                System.out.print(matrizT[i][j]+"\t");
            }
            System.out.println();
        }
    }



    private static int[][] getMatrizMTiempo(int[][] matrizDistancia, int division){
        int[][] matrizMTiempo = new int[32][32];
        for(int i =0; i<matrizDistancia.length; i++){
            matrizMTiempo[0][i] = i;
        }
        for(int i = 0;i<matrizMTiempo.length;i++){
            matrizMTiempo[i][0] = i;
        }
        for(int i=1;i<matrizDistancia.length;i++){
            for(int j=1;j<matrizDistancia.length;j++){
                if(matrizDistancia[i][j] != 9999999){
                    int valor = matrizDistancia[i][j];
                    matrizMTiempo[i][j] = convertirDistanciaSegundos(matrizDistancia[i][j], division);
                }else{
                    matrizMTiempo[i][j] = 9999999;
                }
            }
        }
        return matrizMTiempo;
    }

    private static int convertirDistanciaSegundos(int distancia, int division){
        float tiempo = (float) distancia / division;
        return Math.round( tiempo * 3600);
    }

    private static void imprimirCola(Queue<Integer> cola){
        for(int i : cola){
            System.out.println(i);
        }
    }

    private static int[][] combinarMatrices(int[][] matrizTerrestre, int[][] matrizAerea, int[][] matrizNueva){
        for(int i =0; i<matrizNueva.length; i++){
            matrizNueva[0][i] = i;
        }
        for(int i = 0;i<matrizNueva.length;i++){
            matrizNueva[i][0] = i;
        }
        for(int i=1;i<matrizTerrestre.length;i++){
            for(int j=1;j<matrizTerrestre.length;j++){
                if(matrizAerea[i][j] < matrizTerrestre[i][j]){
                    matrizNueva[i][j] = matrizAerea[i][j];
                }
                else{
                    matrizNueva[i][j] = matrizTerrestre[i][j];
                }
            }
        }
        return matrizNueva;
    }

}
