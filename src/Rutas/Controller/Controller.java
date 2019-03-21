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
    @FXML
    private Label lblTransbordos;
    @FXML
    private Label lblPrecio;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblDistancia;


    private List<Estado> listaEstados;

    private final int[][] MATRIZM_DISTANCIA = ReadDatabase.getMatrizM();
    private final int[][] MATRIZM_TIEMPO = getMatrizMTiempo(ReadDatabase.getMatrizM(), 90);

    private final int[][] MATRIZM_DISTANCIA_AEREA = ReadDatabase.getMatrizMAerea();
    private final int[][] MATRIZM_TIEMPO_AEREA = getMatrizMTiempo(ReadDatabase.getMatrizMAerea(), 940);;

    private int[][] matrizTCombinadaDistancia;
    private int[][] matrizTCombinadaTiempo;

    private  int[][] matrizMPrecio;
    private int[][] matrizMPrecioA;
    private int[][] matrizTPrecio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getEstados();

        /*TERRESTRE*/
        int[][] matrizMCombinadaTiempo = getMatrizMTiempo(ReadDatabase.getMatrizM(), 90);
        int[][] matrizMCombinadaDistancia = ReadDatabase.getMatrizM();
        matrizMCombinadaDistancia = combinarMatrices(ReadDatabase.getMatrizM(), ReadDatabase.getMatrizMAerea(), matrizMCombinadaDistancia);
        matrizTCombinadaDistancia = llenarMatriz99();
        matrizTCombinadaDistancia = floyd(matrizMCombinadaDistancia, matrizTCombinadaDistancia);

        /*AEREA*/
        matrizMCombinadaTiempo = combinarMatrices(MATRIZM_TIEMPO, MATRIZM_TIEMPO_AEREA, matrizMCombinadaTiempo);
        matrizTCombinadaTiempo = llenarMatriz99();
        matrizTCombinadaTiempo = floyd(matrizMCombinadaTiempo, matrizTCombinadaTiempo);

        matrizMPrecio = getMatrizMTiempo(ReadDatabase.getMatrizM(), 90);
        matrizMPrecio = hacerMatrizPrecio(matrizMPrecio, Tipo.CAMION);
        matrizMPrecioA = getMatrizMTiempo(ReadDatabase.getMatrizMAerea(), 940);
        matrizMPrecioA = hacerMatrizPrecio(matrizMPrecioA, Tipo.AVION);

        int[][] matrizMCombinadaPrecio = getMatrizMTiempo(ReadDatabase.getMatrizM(), 90);
        matrizMCombinadaPrecio = combinarMatricesPrecio(MATRIZM_TIEMPO, MATRIZM_TIEMPO_AEREA, matrizMCombinadaPrecio);
        matrizTPrecio = llenarMatriz99();
        matrizTPrecio = floyd(matrizMCombinadaPrecio, matrizTPrecio);

    }

    private void getEstados(){
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
                int idDestino = getIdEstado(destino);
                switch (radio.getText()) {
                    case "Ruta mas corta":
                        List<Ruta> ruta = getRuta(idOrigen, idDestino, matrizTCombinadaDistancia,0);
                        llenarTabla(ruta);
                        break;
                    case "Ruta mas rapida":
                        List<Ruta> rutaRapida = getRuta(idOrigen, idDestino, matrizTCombinadaTiempo,1);
                        llenarTabla(rutaRapida);
                        break;
                    case "Ruta mas economica":
                        List<Ruta> rutaEconomica = getRuta(idOrigen, idDestino, matrizTPrecio,2);
                        llenarTabla(rutaEconomica);
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
        columnPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        columnDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
        ObservableList<Ruta> rutaList = FXCollections.observableArrayList();
        rutaList.addAll(ruta);
        table.setItems(rutaList);
        mostrarTotales(ruta);
    }
    private void mostrarTotales(List<Ruta> ruta){
        int totalDistancia = 0;
        int tiempo = 0;
        int precio = 0;
        for(Ruta r : ruta){
            totalDistancia = totalDistancia + r.getDistancia();
            tiempo = tiempo + r.getTiempoEnSegundos();
            precio = precio + r.getPrecio();
        }

        lblTransbordos.setText(String.valueOf(ruta.size()));
        lblDistancia.setText(totalDistancia + "Km");
        lblTiempo.setText(convertirSegundosHoras(tiempo));
        lblPrecio.setText("$"+precio);
    }


    private int getIdEstado(String estado){
        for(Estado x : listaEstados){
            if(x.getNombre().equals(estado)){
                return x.getIdEstado();
            }
        }
        return 99;
    }

    private List<Ruta> getRuta(int idOrigen, int idDestino, int[][] matrizTCombinada, int opcion) {
       Queue<Integer> cola = getTransbordos(idOrigen, idDestino, matrizTCombinada);
        ((LinkedList<Integer>) cola).addFirst(idOrigen);
        ((LinkedList<Integer>) cola).addLast(idDestino);
        imprimirCola(cola);
        return armarRuta(cola,opcion);
    }


    private Queue<Integer> getTransbordos(int idOrigen, int idDestino, int[][] matrizTCombinada){
        Stack<Integer> pila = new Stack<>();
        Queue<Integer> cola = new LinkedList<>();
        int idTemporal = idOrigen;
        int idDestinoO = idDestino;
        boolean salir = false;
        pila.push(idDestino);
        while (!salir) {
            if (matrizTCombinada[idTemporal][idDestino] != 9999999) {
                pila.push(matrizTCombinada[idTemporal][idDestino]);
                idDestino = matrizTCombinada[idTemporal][idDestino];
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


    private List<Ruta> armarRuta(Queue<Integer> cola, int opcion){
        List<Ruta> ruta = new ArrayList<>();
        while(!cola.isEmpty() && cola.size() >= 2){
            int idOrigen = cola.poll();
            if(!wichMatriz(idOrigen, cola.peek(), opcion)){
                RutaAerea rAerea = new RutaAerea(idOrigen, cola.peek());
                rAerea.setTiempo(MATRIZM_TIEMPO_AEREA[idOrigen][cola.peek()]);
                rAerea.setDistancia(MATRIZM_DISTANCIA_AEREA[idOrigen][cola.peek()]);
                int horas = MATRIZM_TIEMPO_AEREA[idOrigen][cola.peek()] / 3600;
                int minutos = ((MATRIZM_TIEMPO_AEREA[idOrigen][cola.peek()]-horas*3600)/60);
                if(minutos >= 15)
                    horas++;
                rAerea.setPrecio(Tipo.AVION.getCostoHora() * horas);
                ruta.add(rAerea);
            }
            else{
                RutaTerrestre rTerrestre = new RutaTerrestre(idOrigen, cola.peek());
                rTerrestre.setTiempo(MATRIZM_TIEMPO[idOrigen][cola.peek()]);
                rTerrestre.setDistancia(MATRIZM_DISTANCIA[idOrigen][cola.peek()]);
                int horas = MATRIZM_TIEMPO[idOrigen][cola.peek()] / 3600;
                int minutos = ((MATRIZM_TIEMPO[idOrigen][cola.peek()]-horas*3600)/60);
                if(minutos >= 15)
                    horas ++;
                rTerrestre.setPrecio(Tipo.CAMION.getCostoHora() * horas);
                ruta.add(rTerrestre);
            }
        }
        return ruta;
    }

    private boolean wichMatriz(int idOrigen, int idDestino, int opcion){//false =  TERRESTRE, true = Aerea // 0 = distancia, 1 = tiempo, 2 = precio
        if (opcion == 0)
            return (MATRIZM_DISTANCIA[idOrigen][idDestino] < MATRIZM_DISTANCIA_AEREA[idOrigen][idDestino]);
        else if(opcion ==1)
            return (MATRIZM_TIEMPO[idOrigen][idDestino] < MATRIZM_TIEMPO_AEREA[idOrigen][idDestino]);
        else
            return (matrizMPrecio[idOrigen][idDestino] < matrizMPrecioA[idOrigen][idDestino]);
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
        return (division == 940) ? Math.round( tiempo * 3600) + Tipo.AVION.getTiempo() : Math.round( tiempo * 3600) + Tipo.CAMION.getTiempo();
    }

    private static String convertirSegundosHoras(int tiempo){
        int horas = tiempo / 3600;
        int minutos = ((tiempo-horas*3600)/60);
        return horas+"h "+minutos+"min";
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
    private static int[][] combinarMatricesPrecio(int[][] matrizTerrestre, int[][] matrizAerea, int[][] matrizNueva){
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

    private static int[][] hacerMatrizPrecio(int[][] matriz, Tipo tipo){
        for(int i =1;i<matriz.length;i++){
            for(int j =1; j<matriz.length;j++){
                if(matriz[i][j] != 9999999) {
                    int horas = matriz[i][j] / 3600;
                    int minutos = ((matriz[i][j] - horas * 3600) / 60);
                    if (minutos >= 15)
                        horas++;
                    matriz[i][j] = horas * tipo.getCostoHora();
                }

            }
        }
        return  matriz;
    }

}
