package ar.com.almundo.callcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ar.com.almundo.callcenter.dispatcher.Dispatcher;

public class Empleado implements Runnable {

	private static final Logger logger = Logger.getLogger(Empleado.class);
	public String nombreEmpleado;
	private TipoEmpleado tipoEmpleado;
	/**
	 * Si ingresan mas de 10 llamadas el empleado queda con estado ocupado
	 * El proceso queda esperando a que haya algun empleado libre para tomar la llamada
	 */
	private EmpleadoEstado empleadoEstado;
	private Llamada llamada;
	private ConcurrentLinkedDeque<Llamada> llamadasAtendidas;

	public Empleado(String nombreEmpleado, TipoEmpleado tipoEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
		this.tipoEmpleado = tipoEmpleado;
		this.empleadoEstado = EmpleadoEstado.DISPONIBLE;
		llamadasAtendidas = new ConcurrentLinkedDeque<Llamada>();
	}

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public TipoEmpleado getTipoEmpleado() {
		return tipoEmpleado;
	}

	public synchronized EmpleadoEstado getEmpleadoEstado() {
		return empleadoEstado;
	}

	private synchronized void setEmpleadoEstado(EmpleadoEstado empleadoEstado) {
		this.empleadoEstado = empleadoEstado;
	}

	public synchronized List<Llamada> getLlamadasAtendidas() {
		return new ArrayList<>(llamadasAtendidas);
	}

	/**
	 * Se le asigna una llamada a un empleado
	 *
	 */
	public synchronized void asginarLlamada(Llamada llamada) {
		this.llamada = llamada;
		this.setEmpleadoEstado(EmpleadoEstado.OCUPADO);
		logger.info(getNombreEmpleado() + " toma la llamada #" + llamada.getIdLlamada());
		logger.info(Dispatcher.llamadasEnEspera.size() + " llamadas en espera");
	}

	/**
	 * El empleado atiende la llamada. La llamada ya contiene la duracion
	 */
	public void run() {
		while (true) {
			if (getEmpleadoEstado() == EmpleadoEstado.OCUPADO) {
				logger.info(getNombreEmpleado() + " atiende la llamada #" + this.llamada.getIdLlamada());
				try {
					TimeUnit.SECONDS.sleep(this.llamada.getDuracionLlamadaSegundos());
				} catch (InterruptedException ie) {
					Dispatcher.llamadasEnEspera.addFirst(this.llamada);
					logger.debug("Hubo un error en la llamada #" + this.llamada.getIdLlamada());
				} finally {
					this.setEmpleadoEstado(EmpleadoEstado.DISPONIBLE);
					logger.info(getTipoEmpleado() + " " + getNombreEmpleado() + " finalizo la llamada #"
							+ this.llamada.getIdLlamada() + ". Duracion de la llamada: "
							+ this.llamada.getDuracionLlamadaSegundos() + " seg.");
				}
				this.llamadasAtendidas.add(llamada);
			}
		}
	}

}