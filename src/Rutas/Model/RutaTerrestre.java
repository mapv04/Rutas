package Rutas.Model;

public class RutaTerrestre extends Ruta {
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
        return nombreRuta;
    }

    @Override
    public int getPrecio() {
        return precio;
    }

    @Override
    public String getTiempo() {
        return tiempo;
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
    public void setNombreRuta(String nombreRuta) {
        super.nombreRuta = nombreRuta;
    }

    @Override
    public void setPrecio(int precio) {
        super.precio = precio;
    }

    @Override
    public void setTiempo(String tiempo) {
        super.tiempo = tiempo;
    }

    @Override
    public void setTipoRuta(Tipo tipoRuta) {
        super.tipoRuta = tipoRuta;
    }
}
