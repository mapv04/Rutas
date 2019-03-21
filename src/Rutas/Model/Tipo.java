package Rutas.Model;


public enum Tipo {
    CAMION(900,125), AVION(7200,1200);


    private final int costoHora;
    private final int tiempo;

    Tipo(int tiempo, int costoHora){
        this.tiempo = tiempo;
        this.costoHora = costoHora;
    }


    public int getTiempo(){return tiempo;}
    public int getCostoHora(){return costoHora;}

}
