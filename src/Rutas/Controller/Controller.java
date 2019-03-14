package Rutas.Controller;

import Rutas.Model.Estado;
import Rutas.Model.ReadDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
    private TableView<String> table;
    @FXML
    private TableColumn columnRuta;
    @FXML
    private TableColumn columnVuelo;
    @FXML
    private TableColumn columnEstado;
    @FXML
    private TableColumn columnCapital;
    @FXML
    private TableColumn columnTiempo;
    @FXML
    private TableColumn columnPrecio;

    @FXML
    private Button mBuscar;

    List<Estado> listaEstados;
    int[][] matrizMDistancia;
    int[][] matrizTDistancia;
    int[][] matrizMTiempo;
    int[][] matrizTTiempo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getEstados();
        matrizMDistancia = ReadDatabase.getMatrizM();
        imprimirMatriz(matrizMDistancia);
        matrizMTiempo = getMatrizMTiempo(matrizMDistancia);
        imprimirMatriz(matrizMTiempo);

        matrizTDistancia = llenarMatriz99();
        matrizTTiempo = llenarMatriz99();

        matrizTDistancia = floyd(matrizMDistancia, matrizTDistancia);
        imprimirMatriz(matrizTDistancia);

        matrizTTiempo = floydTiempo(matrizMTiempo, matrizTTiempo);
        imprimirMatriz(matrizTTiempo);
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
                        getRutaCorta(matrizTDistancia, idOrigen, idDestino);
                        break;
                    case "Ruta mas rapida":
                        getRutaRapida(matrizTTiempo, idOrigen, idDestino);
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


    private int getIdEstado(String estado){
        for(Estado x : listaEstados){
            if(x.getNombre().equals(estado)){
                return x.getIdEstado();
            }
        }
        return 99;
    }

    private Queue<Integer> getRutaCorta(int[][] matrizT, int idOrigen, int idDestino){
        Stack<Integer> pila = new Stack<>();
        Stack<Integer> ruta = new Stack<>();
        Queue<Integer> cola = new LinkedList<>();
        int idTemporal = idOrigen;
        int idDestinoO = idDestino;
        boolean salir = false;
        pila.push(idDestino);
        ruta.push(idDestino);
        while(!salir){
            if(matrizT[idTemporal][idDestino] != 9999999){
                pila.push(matrizT[idTemporal][idDestino]);
                idDestino = matrizT[idTemporal][idDestino];
            }
            else{
                idTemporal = idDestino;
                if(!pila.isEmpty()) {
                    if (idTemporal == idDestinoO){
                        salir = true;
                    }
                    else{
                        if(idTemporal == pila.peek()) {
                            cola.add(pila.pop());
                            idDestino = pila.peek();
                        }
                    }
                }else salir =true;
            }
        }
        ((LinkedList<Integer>) cola).addFirst(idOrigen);
        ((LinkedList<Integer>) cola).addLast(idDestinoO);
        while(!cola.isEmpty()){
            System.out.println(cola.poll());
        }
        System.out.println();
        return cola;
    }

    private Queue<Integer> getRutaRapida(int[][] matrizT, int idOrigen, int idDestino){
        Stack<Integer> pila = new Stack<>();
        Stack<Integer> ruta = new Stack<>();
        Queue<Integer> cola = new LinkedList<>();
        int idTemporal = idOrigen;
        int idDestinoO = idDestino;
        boolean salir = false;
        pila.push(idDestino);
        ruta.push(idDestino);
        while(!salir){
            if(matrizT[idTemporal][idDestino] != 9999999){
                pila.push(matrizT[idTemporal][idDestino]);
                idDestino = matrizT[idTemporal][idDestino];
            }
            else{
                idTemporal = idDestino;
                if(!pila.isEmpty()) {
                    if (idTemporal == idDestinoO){
                        salir = true;
                    }
                    else{
                        if(idTemporal == pila.peek()) {
                            cola.add(pila.pop());
                            idDestino = pila.peek();
                        }
                    }
                }else salir =true;
            }
        }
        ((LinkedList<Integer>) cola).addFirst(idOrigen);
        ((LinkedList<Integer>) cola).addLast(idDestinoO);
        while(!cola.isEmpty()){
            System.out.println(cola.poll());
        }
        return cola;
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


    public static int[][] getMatrizMTiempo(int[][] matrizDistancia){
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
                    matrizMTiempo[i][j] = convertirDistanciaSegundos(matrizDistancia[i][j]);
                }else{
                    matrizMTiempo[i][j] = 9999999;
                }
            }
        }
        return matrizMTiempo;
    }

    public static int convertirDistanciaSegundos(int distancia){
        float tiempo = (float) distancia / 90;
        return Math.round( tiempo * 3600);
    }

}
