package ar.com.almundo.callcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ar.com.almundo.callcenter.dispatcher.Dispatcher;
import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.model.TipoEmpleado;

public class CallCenter {
	private static final Logger logger = Logger.getLogger(CallCenter.class);
	private static final int MINIMO_LLAMADAS = 10;
	private static final int MAXIMO_LLAMADAS = 15;
    private static final int MINIMO_DURACION_LLAMADA = 5;
    private static final int MAXIMO_DURACION_LLAMADA = 10;
	
	public static void main(String[] args) {
		
		List<Empleado> empleados = new ArrayList<Empleado>();
		
		empleados.add(new Empleado("Director 1", TipoEmpleado.DIRECTOR));
		empleados.add(new Empleado("Director 2", TipoEmpleado.DIRECTOR));
		empleados.add(new Empleado("Director 3", TipoEmpleado.DIRECTOR));

		empleados.add(new Empleado("Supervisor 1", TipoEmpleado.SUPERVISOR));
		empleados.add(new Empleado("Supervisor 2", TipoEmpleado.SUPERVISOR));
		empleados.add(new Empleado("Supervisor 3", TipoEmpleado.SUPERVISOR));

		empleados.add(new Empleado("Operador 1", TipoEmpleado.OPERADOR));
		empleados.add(new Empleado("Operador 2", TipoEmpleado.OPERADOR));
		empleados.add(new Empleado("Operador 3", TipoEmpleado.OPERADOR));
		
		Dispatcher dispatcher = new Dispatcher(empleados);
        dispatcher.start();
        
        try {
        	// Se ejecuta el dispatcher
			TimeUnit.SECONDS.sleep(1);
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(dispatcher);
			TimeUnit.SECONDS.sleep(1);
			
			// Random de llamadas
			int totalCallGenerator = ThreadLocalRandom.current().nextInt(MINIMO_LLAMADAS, MAXIMO_LLAMADAS);
			
			List<Llamada> llamadas = new ArrayList<Llamada>();
	        for (int i = 0; i < totalCallGenerator; i++) {
	        	Llamada llamada = new Llamada(ThreadLocalRandom.current().nextInt(MINIMO_DURACION_LLAMADA, MAXIMO_DURACION_LLAMADA + 1));
	        	llamadas.add(llamada);
	        }

	        // Se agregan las llamadas al dispatcher
			for(Llamada llamadaDispatcher : llamadas) {
				Dispatcher.dispatchCall(llamadaDispatcher);
			}
        } catch (InterruptedException ex) {
        	logger.error("Error al generar llamadas " + ex.getMessage());
		}
        
       
	}
}
