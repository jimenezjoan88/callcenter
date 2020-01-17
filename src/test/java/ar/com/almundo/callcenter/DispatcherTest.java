package ar.com.almundo.callcenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ar.com.almundo.callcenter.dispatcher.Dispatcher;
import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.Llamada;
import ar.com.almundo.callcenter.model.TipoEmpleado;

public class DispatcherTest {

	private static final int CANTIDAD_LLAMADAS = 10;
	private static final int DURACION_MINIMA_LLAMADA= 5;
	private static final int DURACION_MAXIMA_LLAMADA = 10;

	@Test
	public void testDispatchCallsToEmployees() throws InterruptedException {

		Empleado operador1 = new Empleado("operador 1", TipoEmpleado.OPERADOR);
		Empleado operador2 = new Empleado("operador 2", TipoEmpleado.OPERADOR);
		Empleado operador3 = new Empleado("operador 3", TipoEmpleado.OPERADOR);
		Empleado supervisor1 = new Empleado("supervisor 1", TipoEmpleado.SUPERVISOR);
		Empleado supervisor2 = new Empleado("supervisor 2", TipoEmpleado.SUPERVISOR);
		Empleado director = new Empleado("director", TipoEmpleado.DIRECTOR);
		
		List<Empleado> empleados = Arrays.asList(operador1, operador2, operador3, supervisor1, supervisor2, director);
				
		Dispatcher dispatcher = new Dispatcher(empleados);
		dispatcher.start();
		TimeUnit.SECONDS.sleep(1);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(dispatcher);
		TimeUnit.SECONDS.sleep(1);
		
		List<Llamada> llamadas = new ArrayList<Llamada>();
        for (int i = 0; i < CANTIDAD_LLAMADAS; i++) {
        	Llamada llamada = new Llamada(ThreadLocalRandom.current().nextInt(DURACION_MINIMA_LLAMADA, DURACION_MAXIMA_LLAMADA + 1));
        	llamadas.add(llamada);
        }

        llamadas.stream().forEach(call -> {
			Dispatcher.dispatchCall(call);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				fail();
			}
		});

		executorService.awaitTermination(DURACION_MAXIMA_LLAMADA * 2, TimeUnit.SECONDS);
		assertEquals(CANTIDAD_LLAMADAS, empleados.stream().mapToInt(empleado -> empleado.getLlamadasAtendidas().size()).sum());
	}

}