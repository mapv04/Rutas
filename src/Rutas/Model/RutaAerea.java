package Rutas.Model;

public class RutaAerea extends Ruta {

    public RutaAerea (int idOrigen, int idDestino){
        super.idEstadoOrigen = idOrigen;
        super.idEstadoDestino = idDestino;
        super.tipoRuta = Tipo.AVION;
        super.nombreEstado = getNombreEstado();
        super.nombreCapital = getNombreCapital();
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
        return super.precio;
    }

    @Override
    public String getTiempo() {
        int segundos = super.tiempo;
        int horas = segundos / 3600;
        int minutos = ((segundos-horas*3600)/60);
        return horas+"h "+minutos+"min";
    }

    @Override
    public Tipo getTipoRuta() {
        return tipoRuta;
    }

    @Override
    public String getNombreEstado() {
        return getEstado(0);
    }

    @Override
    public String getNombreCapital() {
        return getEstado(1);
    }

    @Override
    public String getEstado(int opcion) {
        String[] estado = ReadDatabase.getEstado(super.idEstadoDestino);
        return estado[opcion];
    }

    @Override
    public int getDistancia() {
        return super.distancia;
    }

    @Override
    public int getTiempoEnSegundos() {
        return super.tiempo;
    }


    @Override
    public void setTiempo(int tiempo) {
        super.tiempo = tiempo;
    }

    @Override
    public void setDistancia(int distancia) {
        super.distancia = distancia;
    }

    @Override
    public void setPrecio(int precio) {
        super.precio = precio;
    }

}
