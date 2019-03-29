package Rutas.Model;


public enum Tipo {
    CAMION(900,125), AVION(7200,1200);


    private final int costoHora;
    private final int tiempoEspera;

    Tipo(int tiempo, int costoHora){
        this.tiempoEspera = tiempo;
        this.costoHora = costoHora;
    }


    public int getTiempoEspera(){return tiempoEspera;}
    public int getCostoHora(){return costoHora;}

}
