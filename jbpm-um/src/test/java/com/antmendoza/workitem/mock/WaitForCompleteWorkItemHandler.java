package com.antmendoza.workitem.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class WaitForCompleteWorkItemHandler implements WorkItemHandler {

	private List<WorkItem> items = new ArrayList<WorkItem>();
	private WorkItemManager manager = null;
	private Map<String, Object> inputParameters;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		inputParameters = new HashMap<String, Object>();
		this.items.add(workItem);
		this.manager = manager;
		for (Map.Entry<String, Object> entry : workItem.getParameters()
				.entrySet()) {
			inputParameters.put(entry.getKey(), entry.getValue());
		}

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}

	public Map<String, Object> getInputParameters() {
		return inputParameters;
	}

	public Object getInputParameter(String parameterName) {
		return inputParameters.get(parameterName);
	}

	private WorkItem getItemByTaskName(String taskName) {
		WorkItem result = null;
		for (WorkItem item : items) {
			// nombre de la tarea.
			String name = (String) item.getParameter("TaskName");
			if (taskName.equals(name)) {
				result = item;
			}

		}
		items.remove(result);

		return result;
	}

	public void completeWorkItemByTaskName(String taskName,
			Map<String, Object> paramWI) {
		manager.completeWorkItem(getItemByTaskName(taskName).getId(), paramWI);

	}

}