package Rutas.Model;


public abstract class Ruta {
    protected int idEstadoOrigen;
    protected int idEstadoDestino;
    protected String nombreRuta;
    protected int tiempo;
    protected Tipo tipoRuta;
    protected int distancia;

    public abstract int getIdEstadoOrigen();
    public abstract int getIdEstadoDestino();
    public abstract String getNombreRuta();
    public abstract int getPrecio();
    public abstract String getTiempo();
    public abstract Tipo getTipoRuta();

    public abstract void setIdEstadoOrigen(int idEstadoOrigen);

    public abstract void setIdEstadoDestino(int idEstadoDestino);

    public abstract void setTiempo(int tiempo);
    public abstract void setDistancia(int distancia);

}
