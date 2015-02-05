package com.antmendoza.um.solicitudes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antmendoza.eventlistener.NodeEventListener;
import com.antmendoza.workitem.mock.Task;
import com.antmendoza.workitem.mock.WaitForCompleteWorkItemHandler;

public class TestPeticionVacaciones {

	private static String PROCESS_ID = "com.antmendoza.um.solicitudes.vacaciones";

	private KieSession ksession;

	private Logger log = LoggerFactory.getLogger(TestPeticionVacaciones.class);

	private NodeEventListener nodeEventListener = null;

	@Before
	public void createKSession() {
		nodeEventListener = new NodeEventListener();

		KieServices ks = KieServices.Factory.get();
		KieContainer kc = ks.getKieClasspathContainer();
		ksession = kc.newKieSession("ks_umu_test");
		ksession.addEventListener(nodeEventListener);
	}

	@Test
	public void testVacacionesValidadas() {

		try {

			
			printInicioTest("testVacacionesValidadas");
			
			// Iniciamos un proceso, creamos una instancia del proceso
			// 'com.antmendoza.um.solicitudes.vacaciones'
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);
			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			// Obtenemos la lista de tareas pendientes para el usuario
			// super_pepe, completamos la primera, la que se le acaba de asignar
			List<Task> tasks = getTaskByActorId("super_pepe");
			Task task = tasks.get(0);
			// Completamos con el parámetro estado_form = VALIDADO
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_form", Estado.VALIDADO.name());
			completeTaskTaskName(task.getId(), result);

			// Comprobamos si ha pasado por la actividad..
			// "Vacaciones validadas"
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Vacaciones validadas"));

			Assert.assertEquals(processInstance.getState(),
					ProcessInstance.STATE_COMPLETED);

			ksession.dispose();

			
			printFinTest("testVacacionesValidadas");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testVacacionesNoValidadas() {

		try {

			
			printInicioTest("testVacacionesNoValidadas");
			
			// Iniciamos un proceso, creamos una instancia del proceso
			// 'com.antmendoza.um.solicitudes.vacaciones'
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);
			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			// Obtenemos la lista de tareas pendientes para el usuario
			// super_pepe, completamos la primera, la que se le acaba de asignar
			List<Task> tasks = getTaskByActorId("super_pepe");
			Task task = tasks.get(0);
			// Completamos con el parámetro estado_form = NO_VALIDADO
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_form", Estado.NO_VALIDADO.name());
			completeTaskTaskName(task.getId(), result);

			// Comprobamos si ha pasado por el nodo 'Vacaciones no validadas'
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Vacaciones no validadas"));

			Assert.assertEquals(processInstance.getState(),
					ProcessInstance.STATE_COMPLETED);

			ksession.dispose();
			
			
			printFinTest("testVacacionesNoValidadas");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testVacacionesRevisaYValida() {

		try {

			printInicioTest("testVacacionesRevisaYValida");

			// Iniciamos un proceso, creamos una instancia del proceso
			// 'com.antmendoza.um.solicitudes.vacaciones'
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);

			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			// Obtenemos la lista de tareas pendientes para el usuario
			// super_pepe, completamos la primera, la que se le acaba de asignar
			List<Task> tasks = getTaskByActorId("super_pepe");
			Task task = tasks.get(0);
			// Completamos con el parámetro estado_form = REVISAR, añadimos un
			// comentario.
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_form", Estado.REVISAR.name());
			result.put("comentario_form", "Sólo un día");
			completeTaskTaskName(task.getId(), result);

			// Comprobamos que ha pasado por 'Revisa solicitud'
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Revisa solicitud"));

			// Comprobamos los valor de la variable comentario dentro de la
			// instancia del proceso.
			String comentario = (String) getVariableValue(processInstance,
					"comentario");
			Assert.assertEquals("Sólo un día", comentario);

			// Completando tarea de usuario 'Revisa solicitud', asignada al
			// solicitante en tiempo de ejecución.
			// Obtenemos la lista de tareas pendientes para el usuario
			// antmendoza, completamos la primera, la que se le acaba de asignar
			tasks = getTaskByActorId("antmendoza");
			task = tasks.get(0);
			result = new HashMap<String, Object>();
			result.put("num_dias_output", 1);
			completeTaskTaskName(task.getId(), result);

			// Acepta/valida solicitud
			// El usuario super_pepe vuelve a tener una tarea asignada, la
			// completamos
			tasks = getTaskByActorId("super_pepe");
			task = tasks.get(0);
			// En con estado VALIDADO, el proceso finaliza.
			result = new HashMap<String, Object>();
			result.put("estado_form", Estado.VALIDADO.name());
			completeTaskTaskName(task.getId(), result);

			// Comprobación de datos
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Vacaciones validadas"));

			// Comprobamos los varlores de las variables
			Integer num_dias = (Integer) getVariableValue(processInstance,
					"num_dias");
			Assert.assertEquals(new Integer(1), num_dias);

			Assert.assertEquals(processInstance.getState(),
					ProcessInstance.STATE_COMPLETED);

			ksession.dispose();
			
			printFinTest("testVacacionesRevisaYValida");

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private org.kie.api.runtime.process.WorkItemHandler getWorkItemHandler(
			String workItemName) {
		return ((DefaultWorkItemManager) ((StatefulKnowledgeSession) ksession)
				.getWorkItemManager()).getWorkItemHandler(workItemName);

	}

	private Object getVariableValue(ProcessInstance processInstance,
			String variableId) {
		return ((WorkflowProcessInstance) processInstance)
				.getVariable(variableId);

	}

	private void completeTaskTaskName(long taskId, Map<String, Object> paramWI) {

		WaitForCompleteWorkItemHandler humanWorkItemHandler = getWaitForCompleteWorkItemHandler();
		humanWorkItemHandler.completeTask(taskId, paramWI);

	}

	private List<Task> getTaskByActorId(String actorId) {
		WaitForCompleteWorkItemHandler humanWorkItemHandler = getWaitForCompleteWorkItemHandler();
		return humanWorkItemHandler.getTaskByActorId(actorId);
	}

	private WaitForCompleteWorkItemHandler getWaitForCompleteWorkItemHandler() {
		return (WaitForCompleteWorkItemHandler) getWorkItemHandler("Human Task");
	}

	private void printInicioTest(String methodName) {
		System.out.print("------------------------------------------------------------------------------------------");
		System.out.println("------------------------------------------------------------------------------------------");
		log.debug("Inicio test " + methodName);
	}

	private void printFinTest(String methodName) {

		log.debug("Fin test " + methodName);
		System.out.println("");

	}

}
