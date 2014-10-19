package com.antmendoza.um.solicitudes;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antmendoza.eventlistener.NodeEventListener;
import com.antmendoza.um.solicitudes.Estado;
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

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);

			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			WaitForCompleteWorkItemHandler humanWorkItemHandler = (WaitForCompleteWorkItemHandler) getWorkItemHandler("Human Task");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_output", Estado.VALIDADO.name());
			humanWorkItemHandler.completeWorkItemByTaskName(
					"Valida vacaciones", result);

			// Comprobación de datos
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Vacaciones validadas"));

			Assert.assertEquals(processInstance.getState(),
					ProcessInstance.STATE_COMPLETED);

			ksession.dispose();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testVacacionesNoValidadas() {

		try {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);

			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			WaitForCompleteWorkItemHandler humanWorkItemHandler = (WaitForCompleteWorkItemHandler) getWorkItemHandler("Human Task");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_output", Estado.NO_VALIDADO.name());
			humanWorkItemHandler.completeWorkItemByTaskName(
					"Valida vacaciones", result);

			// Comprobación de datos
			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Vacaciones no validadas"));

			Assert.assertEquals(processInstance.getState(),
					ProcessInstance.STATE_COMPLETED);

			ksession.dispose();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testVacacionesRevisaYValida() {

		try {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("solicitante", "antmendoza");
			params.put("num_dias", 2);

			ProcessInstance processInstance = ksession.startProcess(PROCESS_ID,
					params);

			// Completando tarea de usuario
			WaitForCompleteWorkItemHandler humanWorkItemHandler = (WaitForCompleteWorkItemHandler) getWorkItemHandler("Human Task");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("estado_output", Estado.REVISAR.name());
			result.put("comentario_output", "Sólo un día");
			humanWorkItemHandler.completeWorkItemByTaskName(
					"Valida vacaciones", result);

			Assert.assertTrue(nodeEventListener.getNodesName().contains(
					"Revisa solicitud"));

			
			// Comprobamos los valor de la variable comentario
			String comentario = (String) getVariableValue(processInstance,
					"comentario");
			Assert.assertEquals("Sólo un día", comentario);


			
			// Revisa solicitud y cambia el número de días.
			result = new HashMap<String, Object>();
			result.put("num_dias_output", 1);
			humanWorkItemHandler.completeWorkItemByTaskName("Revisa solicitud",
					result);

			// Acepta/valida solicitud
			result = new HashMap<String, Object>();
			result.put("estado_output", Estado.VALIDADO.name());
			humanWorkItemHandler.completeWorkItemByTaskName(
					"Valida vacaciones", result);

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
		} catch (Exception e) {
			Assert.fail(e.getMessage());
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
}
