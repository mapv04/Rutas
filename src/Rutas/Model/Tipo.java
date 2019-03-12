package Rutas.Model;


public enum Tipo {
    CAMION(90,900,1200), AVION(940,7200,125);

    private final int velocidad;
    private final int tiempo;
    private final int costoHora;

    Tipo(int velocidad, int tiempo, int costoHora){
        this.velocidad = velocidad;
        this.tiempo = tiempo;
        this.costoHora = costoHora;
    }

    public int getVelocidad(){return velocidad;}
    public int getTiempo(){return tiempo;}
    public int getCostoHora(){return costoHora;}

}
