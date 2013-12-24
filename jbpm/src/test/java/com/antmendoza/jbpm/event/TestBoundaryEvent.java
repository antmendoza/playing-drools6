package com.antmendoza.jbpm.event;

import org.drools.core.event.DebugProcessEventListener;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;

import com.antmendoza.jbpm.workitem.AutoCompleteWorkItemHandle;
import com.antmendoza.jbpm.workitem.WaitForCompleteWorkItemHandle;

/**
 * 
 * 
 * @author antmendoza
 * 
 */
public class TestBoundaryEvent {

	private KieSession ksession;
	private WaitForCompleteWorkItemHandle wiTask1 = null;
	private WaitForCompleteWorkItemHandle wiTask2 = null;

	@Before
	public void configure() {
		KieServices ks = KieServices.Factory.get();

		KieContainer kc = ks.getKieClasspathContainer();

		// Creamos las sesi—n definida en el fichero kmodule.xml.
		ksession = kc.newKieSession("KSBoundaryEvent");

		wiTask1 = new WaitForCompleteWorkItemHandle();
		wiTask2 = new WaitForCompleteWorkItemHandle();

		ksession.getWorkItemManager().registerWorkItemHandler("Task1", wiTask1);

		ksession.getWorkItemManager().registerWorkItemHandler("Task2", wiTask2);

		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new AutoCompleteWorkItemHandle());

		ksession.addEventListener(new ProcessEventListener() {

			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				// TODO Auto-generated method stub

			}

			public void beforeProcessStarted(ProcessStartedEvent event) {
				// TODO Auto-generated method stub

			}

			public void beforeProcessCompleted(ProcessCompletedEvent event) {
				// TODO Auto-generated method stub

			}

			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
				System.out.println("beforeNodeTriggered-> "
						+ event.getNodeInstance().getNodeName());

			}

			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
				System.out.println("beforeNodeLeft-> "
						+ event.getNodeInstance().getNodeName());

			}

			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				// TODO Auto-generated method stub

			}

			public void afterProcessStarted(ProcessStartedEvent event) {
				// TODO Auto-generated method stub

			}

			public void afterProcessCompleted(ProcessCompletedEvent event) {
				// TODO Auto-generated method stub

			}

			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				// TODO Auto-generated method stub

			}

			public void afterNodeLeft(ProcessNodeLeftEvent event) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Test
	public void executeTimerCancelSubProcess() {

		ksession.startProcess("boundary_event_cancel");

		org.kie.api.runtime.process.ProcessInstance processInstance = ksession
				.getProcessInstances().iterator().next();

		RuleFlowProcessInstance flowProcessInstance = (RuleFlowProcessInstance) processInstance;

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		NodeInstance nodeInstanceImpl = flowProcessInstance.getNodeInstances()
				.iterator().next();

		Assert.assertEquals("Sub Process 1", nodeInstanceImpl.getNodeName());

		try {
			// El timer salta a los 500ms
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		ksession.getWorkItemManager().completeWorkItem(wiTask2.getWorkItemId(),
				null);

		Assert.assertEquals(0, ksession.getProcessInstances().size());

	}

	@Test
	public void executeTimerNoCancelSubProcess() {

		ksession.startProcess("boundary_event_no_cancel");

		org.kie.api.runtime.process.ProcessInstance processInstance = ksession
				.getProcessInstances().iterator().next();

		RuleFlowProcessInstance flowProcessInstance = (RuleFlowProcessInstance) processInstance;

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		NodeInstance nodeInstanceImpl = flowProcessInstance.getNodeInstances()
				.iterator().next();

		Assert.assertEquals("Sub Process 1", nodeInstanceImpl.getNodeName());

		try {
			// El timer salta a los 500ms
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(2, flowProcessInstance.getNodeInstances().size());

		ksession.getWorkItemManager().completeWorkItem(wiTask2.getWorkItemId(),
				null);
		CompositeContextNodeInstance contextNodeInstance = (CompositeContextNodeInstance) nodeInstanceImpl;

		Assert.assertEquals("Sub Process 1", contextNodeInstance.getNodeName());

		ksession.getWorkItemManager().completeWorkItem(wiTask1.getWorkItemId(),
				null);

		Assert.assertEquals(0, ksession.getProcessInstances().size());

	}

	@Test
	public void executeSignalNoCancelSubProcess() {

		ksession.startProcess("boundary_event_no_cancel");

		org.kie.api.runtime.process.ProcessInstance processInstance = ksession
				.getProcessInstances().iterator().next();

		RuleFlowProcessInstance flowProcessInstance = (RuleFlowProcessInstance) processInstance;

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		NodeInstance nodeInstanceImpl = flowProcessInstance.getNodeInstances()
				.iterator().next();
		Assert.assertEquals("Sub Process 1", nodeInstanceImpl.getNodeName());

		CompositeContextNodeInstance contextNodeInstance = (CompositeContextNodeInstance) nodeInstanceImpl;

		ksession.signalEvent("Signal_1", null);

		Assert.assertEquals(2, flowProcessInstance.getNodeInstances().size());

		ksession.getWorkItemManager().completeWorkItem(wiTask2.getWorkItemId(),
				null);

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		Assert.assertEquals("Sub Process 1", contextNodeInstance.getNodeName());

		ksession.getWorkItemManager().completeWorkItem(wiTask1.getWorkItemId(),
				null);

		Assert.assertEquals(0, ksession.getProcessInstances().size());

	}

	@Test
	public void executeSignalCancelSubProcess() {

		ksession.startProcess("boundary_event_cancel");

		org.kie.api.runtime.process.ProcessInstance processInstance = ksession
				.getProcessInstances().iterator().next();

		RuleFlowProcessInstance flowProcessInstance = (RuleFlowProcessInstance) processInstance;

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		NodeInstance nodeInstanceImpl = flowProcessInstance.getNodeInstances()
				.iterator().next();
		Assert.assertEquals("Sub Process 1", nodeInstanceImpl.getNodeName());

		ksession.signalEvent("Signal_1", null);

		Assert.assertEquals(1, flowProcessInstance.getNodeInstances().size());

		Assert.assertEquals("Task 2", flowProcessInstance.getNodeInstances()
				.iterator().next().getNodeName());

		ksession.getWorkItemManager().completeWorkItem(wiTask2.getWorkItemId(),
				null);

		Assert.assertEquals(0, ksession.getProcessInstances().size());

	}

	@After
	public void dispose() {

		ksession.dispose();
	}

}
