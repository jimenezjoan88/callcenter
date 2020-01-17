package ar.com.almundo.callcenter.dispatcher;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import ar.com.almundo.callcenter.model.Empleado;
import ar.com.almundo.callcenter.model.EmpleadoEstado;
import ar.com.almundo.callcenter.model.Llamada;

public class Dispatcher implements Runnable {

	private static final Logger logger = Logger.getLogger(Dispatcher.class);

	private Boolean active;
	private ExecutorService executorService;
	private ConcurrentLinkedDeque<Empleado> empleados;
	/**
	 * Para resolver cuando no hay un empleado libre se crea una lista del tipo ConcurrentLinkedDeque 
	 * Se guardan las llamadas en espera para que puedan ser tomadas por el proximo empleado libre
	 */
	public static ConcurrentLinkedDeque<Llamada> llamadasEnEspera = new ConcurrentLinkedDeque<Llamada>();

	public Dispatcher(List<Empleado> empleados) {
		this.empleados = new ConcurrentLinkedDeque<Empleado>(empleados);
		this.executorService = Executors.newFixedThreadPool(empleados.size() + 1);
	}

	public static synchronized void dispatchCall(Llamada llamada) {
		logger.info("Nueva llamada. Identificador #" + llamada.getIdLlamada());
		llamadasEnEspera.add(llamada);
	}

	public synchronized void start() {
		this.active = true;

		// Creamos un thread por cada empleado
		for (Empleado empleado : this.empleados) {
			this.executorService.execute(empleado);
		}
	}

	public synchronized void stop() {
		this.active = false;
		this.executorService.shutdown();
	}

	public synchronized Boolean getActivo() {
		return active;
	}

	public void run() {
		while (getActivo()) {
			if (llamadasEnEspera.isEmpty()) {
				continue;
			} else {
				Empleado empleado = null;
				
				Comparator<Empleado> porNivelEmpleado = (Empleado empleado1, Empleado empleado2) -> Integer
						.compare(empleado2.getTipoEmpleado().getNivel(), empleado1.getTipoEmpleado().getNivel());

				// Buscamos los empleados que estan disponibles, y ordenamos por nivel: operador, supervisor, director
				/**
				 * Con esto se resuelve si todos los empleados estan ocupados en alguna llamada, el proceso dispatcher asignara una llamada al proximo
				 * empleado que este disponible
				 */
				Optional<Empleado> empleadoSeleccionado = this.empleados.stream().filter(e -> e.getEmpleadoEstado() == EmpleadoEstado.DISPONIBLE)
						.sorted(porNivelEmpleado).findFirst();

				if (!empleadoSeleccionado.isPresent()) {
					logger.debug("No hay empleados disponibles.");
					continue;
				} else {
					empleado = empleadoSeleccionado.get();
				}

				// Si hay un empleado libre se le asigna la llamada
				try {
					Llamada llamada = llamadasEnEspera.pollFirst();
					empleado.asginarLlamada(llamada);
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
		}
	}

}
