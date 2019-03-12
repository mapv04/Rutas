package Rutas.Model;


public abstract class Ruta {
    protected int idEstadoOrigen;
    protected int idEstadoDestino;
    protected String nombreRuta;
    protected int precio;
    protected String tiempo;
    protected Tipo tipoRuta;

    public abstract int getIdEstadoOrigen();
    public abstract int getIdEstadoDestino();
    public abstract String getNombreRuta();
    public abstract int getPrecio();
    public abstract String getTiempo();
    public abstract Tipo getTipoRuta();

    public abstract void setIdEstadoOrigen(int idEstadoOrigen);

    public abstract void setIdEstadoDestino(int idEstadoDestino);

    public abstract void setNombreRuta(String nombreRuta);

    public abstract void setPrecio(int precio);

    public abstract void setTiempo(String tiempo);

    public abstract void setTipoRuta(Tipo tipoRuta);
}
