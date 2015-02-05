package com.antmendoza.um.workitem;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActualizaDiasWorkItem implements WorkItemHandler {

	private Logger log = LoggerFactory.getLogger(ActualizaDiasWorkItem.class);

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Object num_dias_input = workItem.getParameter("num_dias_input");

		Object solicitante_input = workItem.getParameter("solicitante_input");

		log.info("Actualizando num_dias_input " + num_dias_input);

		log.info("Actualizando solicitante_input " + solicitante_input);

		// Actualizamos días, DDBB, WS...

		Map<String, Object> result = new HashMap<String, Object>();
		// Parámetros de salida
		// result.put(key, value)

		manager.completeWorkItem(workItem.getId(), result);

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

	}

}
