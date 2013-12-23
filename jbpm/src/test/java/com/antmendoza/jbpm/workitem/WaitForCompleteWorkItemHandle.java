package com.antmendoza.jbpm.workitem;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class WaitForCompleteWorkItemHandle implements WorkItemHandler {

	private Long workItemId = null;

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		System.out.println("Params ->"+workItem.getParameters());
		
		workItemId = workItem.getId();

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

	}

	public Long getWorkItemId() {
		return workItemId;
	}

}
