package com.antmendoza.jbpm.workitem;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class AutoCompleteWorkItemHandle implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		System.out.println("Params ->"+workItem.getParameters());
		manager.completeWorkItem(workItem.getId(), null);

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

	}

}
