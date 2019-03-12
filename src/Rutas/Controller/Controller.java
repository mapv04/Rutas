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
    String[][] matrizMTiem;
    int[][] matrizMTiempo;
    int[][] matrizTTiempo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getEstados();
        matrizMDistancia = ReadDatabase.getMatrizM();
        imprimirMatriz(matrizMDistancia);
        matrizMTiem = ReadDatabase.getMatrizMTiempo();
        imprimirMatriz(matrizMTiem);

        matrizTDistancia = llenarMatriz99();
        imprimirMatriz(matrizTDistancia);
        matrizTTiempo = llenarMatriz99Tiempo();
        imprimirMatriz(matrizTTiempo);

        matrizTDistancia = floyd(matrizMDistancia, matrizTDistancia);
        imprimirMatriz(matrizTDistancia);

        matrizMTiempo = convertirMatrizASegundos(matrizMTiem);
        imprimirMatriz(matrizMTiempo);

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

    private static int[][] llenarMatriz99Tiempo(){
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

    private static int[][] convertirMatrizASegundos(String[][] matriz){
        int[][] matrizSeg = new int[32][32];
        for(int i=1; i<matriz.length; i++){
            for(int j =1;j<matriz.length;j++){
                String[] as = matriz[i][j].split(":");
                int[] ai = convertirArrayInt(as);
                matrizSeg[i][j] = convertirASegundos(ai);
            }
        }
        return matrizSeg;
    }

    private static int[] convertirArrayInt(String[] array){
        int[] a = new int[3];
        for(int i = 0; i<3;i++){
            a[i] = Integer.parseInt(array[i]);
        }
        return a;
    }

    private static int convertirASegundos(int[] array){
        int h = array[0]*3600;
        int min = array[1]*60;
        int seg = array[2];
        return h+min+seg;
    }

    private static String convertirAHoras(int s){
        int h = s/3600;
        int min = (s - (3600 * h)) / 60;
        int seg = s - ((h*3600)+(min*60));
        String horas = "";
        String minutos="";
        String segundos="";
        if(h > 0 && h < 10){
            horas = "0"+h;
        }
        else if(h == 0){
            horas="0"+h;
        }
        else{ horas = String.valueOf(h);}
        //
        if(min > 0 && min < 10){
            minutos = "0"+min;
        }
        else if(min == 0){
            minutos="0"+min;
        }
        else{ minutos = String.valueOf(min);}
        //
        if(seg > 0 && seg < 9){
            segundos = "0"+seg;
        }
        else if(seg == 0){
            segundos="0"+seg;
        }
        else{ segundos = String.valueOf(seg);}

        String a = horas+":"+minutos+":"+segundos;


        return horas+":"+minutos+":"+segundos;
    }

    private void imprimirMatriz(int[][] matrizT){
        System.out.println("MATRIZ T RESUELTA");
        for(int i=0; i<matrizT.length;i++){
            for(int j=0;j<matrizT[i].length;j++){
                System.out.print(matrizT[i][j]+"\t");
            }
            System.out.println();
        }
    }

    private void imprimirMatriz(String[][] matrizT){
        System.out.println("MATRIZ T RESUELTA");
        for(int i=0; i<matrizT.length;i++){
            for(int j=0;j<matrizT[i].length;j++){
                System.out.print(matrizT[i][j]+"\t");
            }
            System.out.println();
        }
    }

}
