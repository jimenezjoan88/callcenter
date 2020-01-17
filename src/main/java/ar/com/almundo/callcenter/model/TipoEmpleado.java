package ar.com.almundo.callcenter.model;

public enum TipoEmpleado {
	OPERADOR(3), SUPERVISOR(2), DIRECTOR(1);
	
	private int nivel;

	TipoEmpleado(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
	
}
