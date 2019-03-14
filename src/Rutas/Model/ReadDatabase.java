package Rutas.Model;

import Rutas.database.ConnectionMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReadDatabase {
    private static ResultSet rs;
    private static Connection conn = ConnectionMysql.getInstance().getConnection();
    private static PreparedStatement preparedStatement;

    public static List<Estado> getAllEstados(){
        String sql = "SELECT * FROM estados;";
        List<Estado> listEstados = new ArrayList<>();
        try{
            preparedStatement = conn.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                Estado estado = new Estado();
                estado.setIdEstado(rs.getInt(1));
                estado.setNombre(rs.getString(2));
                estado.setCapital(rs.getString(3));
                estado.setAbreviatura(rs.getString(4));
                listEstados.add(estado);
            }
        }catch(SQLException e){
            System.out.println("ERROR in sql statment. method getAllEstados  error: "+e);
        }
        return listEstados;
    }

    public static int[][] getMatrizM() {
        String sql ="select * from matriz_distancia;";
        int[][] matriz = new int[32][32];
        try {
            preparedStatement = conn.prepareStatement(sql);

            rs = preparedStatement.executeQuery();
            int cont = 1;
            for(int i =0; i<matriz.length; i++){
                matriz[0][i] = i;
            }
            for(int i = 0;i<matriz.length;i++){
                matriz[i][0] = i;
            }
            while (rs.next()) {

                for (int j = 1; j < matriz.length; j++) {

                    matriz[cont][j] = rs.getInt(j+1);

                }
                cont++;
            }
        } catch (SQLException e) {
            System.out.println("ERROR in sql statement. method getMatrizM  error: "+e);
        }
        return matriz;
    }

    public static int[][] getMatrizMAerea() {
        String sql ="select * from matriz_distancia_avion;";
        int[][] matriz = new int[32][32];
        try {
            preparedStatement = conn.prepareStatement(sql);

            rs = preparedStatement.executeQuery();
            int cont = 1;
            for(int i =0; i<matriz.length; i++){
                matriz[0][i] = i;
            }
            for(int i = 0;i<matriz.length;i++){
                matriz[i][0] = i;
            }
            while (rs.next()) {

                for (int j = 1; j < matriz.length; j++) {

                    matriz[cont][j] = rs.getInt(j+1);

                }
                cont++;
            }
        } catch (SQLException e) {
            System.out.println("ERROR in sql statement. method getMatrizM  error: "+e);
        }
        return matriz;
    }


}
