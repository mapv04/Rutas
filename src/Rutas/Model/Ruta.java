package Rutas.Model;


public abstract class Ruta {
    protected int idEstadoOrigen;
    protected int idEstadoDestino;
    protected String nombreRuta;
    protected String nombreEstado;
    protected String nombreCapital;
    protected int tiempo;
    protected Tipo tipoRuta;
    protected int distancia;
    protected int precio;

    public abstract int getIdEstadoOrigen();
    public abstract int getIdEstadoDestino();
    public abstract String getNombreRuta();
    public abstract int getPrecio();
    public abstract String getTiempo();
    public abstract Tipo getTipoRuta();
    public abstract String getNombreEstado();
    public abstract String getNombreCapital();
    public abstract String getEstado(int opcion);
    public abstract int getDistancia();
    public abstract int getTiempoEnSegundos();

    public abstract void setTiempo(int tiempo);
    public abstract void setDistancia(int distancia);
    public abstract void setPrecio(int precio);
}
