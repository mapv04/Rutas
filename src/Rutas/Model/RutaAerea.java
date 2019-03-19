package Rutas.Model;

public class RutaAerea extends Ruta {

    public RutaAerea (){
        super.tipoRuta = Tipo.AVION;
    }

    @Override
    public int getIdEstadoOrigen() {
        return idEstadoOrigen;
    }

    @Override
    public int getIdEstadoDestino() {
        return idEstadoDestino;
    }

    @Override
    public String getNombreRuta() {
        return "Vuelo "+ReadDatabase.getAbvEstado(super.idEstadoOrigen)+" - "+ReadDatabase.getAbvEstado(super.idEstadoDestino);
    }

    @Override
    public int getPrecio() {
        return 1;
    }

    @Override
    public String getTiempo() {
        return "";
    }

    @Override
    public Tipo getTipoRuta() {
        return tipoRuta;
    }

    @Override
    public void setIdEstadoOrigen(int idEstadoOrigen) {
        super.idEstadoOrigen = idEstadoOrigen;
    }

    @Override
    public void setIdEstadoDestino(int idEstadoDestino) {
        super.idEstadoDestino = idEstadoDestino;
    }


    @Override
    public void setTiempo(int tiempo) {
        super.tiempo = tiempo;
    }

    @Override
    public void setDistancia(int distancia) {
        super.distancia = distancia;
    }

}
