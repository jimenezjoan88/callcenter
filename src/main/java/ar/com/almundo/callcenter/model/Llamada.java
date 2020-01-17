package ar.com.almundo.callcenter.model;

/**
 * Models the Call Domain Objects
 */
public class Llamada {

	private Integer idLlamada;
    private Integer duracionLlamadaSegundos;
    public static int total = 0; 

    public Llamada(Integer duracionLlamadaSegundos) { 
        total++;
        this.idLlamada = total;
        this.duracionLlamadaSegundos = duracionLlamadaSegundos;
    }   
    
    public Integer getIdLlamada() {
        return this.idLlamada;
    }
    
    public Integer getDuracionLlamadaSegundos() {
        return duracionLlamadaSegundos;
    }
    
    public void setDuracionLlamadaSegundos(Integer duracionLlamadaSegundos){
    	this.duracionLlamadaSegundos = duracionLlamadaSegundos;
    }

}