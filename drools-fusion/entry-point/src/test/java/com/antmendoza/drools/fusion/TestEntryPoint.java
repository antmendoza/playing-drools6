package com.antmendoza.drools.fusion;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.antmendoza.model.Signal;

public class TestEntryPoint {

	private StatefulKnowledgeSession ksession;

	@Before
	public void configure() {

		// Creamos la
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		Resource ruleRes = ResourceFactory.newClassPathResource("rules.drl",
				getClass());

		kbuilder.add(ruleRes, ResourceType.DRL);

		if (kbuilder.hasErrors()) {
			if (kbuilder.getErrors().size() > 0) {
				for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
					System.err.println(kerror);
				}
			}
		}

		KnowledgeBaseConfiguration config = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration();
		config.setOption(EventProcessingOption.STREAM);

		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);

		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

		ksession = kbase.newStatefulKnowledgeSession();

	}

	@Test
	public void execute() {

		// Insertamos un par de objetos.

		int idFloor1 = 1;
		int idFloor2 = 2;

		Signal signal1 = new Signal(1, "sensor1", idFloor1, 33, 71);
		insertEvent(ksession, signal1);

		Signal signal2 = new Signal(2, "sensor2", idFloor1, 55, 90);
		insertEvent(ksession, signal2);

		Signal signal3 = new Signal(3, "sensor3", idFloor2, 20, 40);
		insertEvent(ksession, signal3);

		Signal signal4 = new Signal(4, "sensor4", idFloor2, 55, 40);
		FactHandle factSignal4 = insertEvent(ksession, signal4);

		Signal signal5 = new Signal(5, "sensor1", idFloor1, 20, 40);
		insertEvent(ksession, signal5);

		ksession.fireAllRules();

		WorkingMemoryEntryPoint entryPointFloor1 = getEntryPoint(ksession, 1);
		WorkingMemoryEntryPoint entryPointFloor2 = getEntryPoint(ksession, 2);

		Assert.assertEquals(3, entryPointFloor1.getFactCount());
		Assert.assertEquals(2, entryPointFloor2.getFactCount());

		
		WorkingMemoryEntryPoint entryPointAlert = ksession.getWorkingMemoryEntryPoint("alert");
		Assert.assertEquals(1, entryPointAlert.getFactCount());
		
		
		//
		// Assert.assertEquals(factSignal4,
		// entryPointStream2.getFactHandle(signal4));

	}

	@After
	public void dispose() {

		ksession.dispose();
	}

	private FactHandle insertEvent(StatefulKnowledgeSession ksession,
			Signal signal) {
		return getEntryPoint(ksession, signal.getIdFloor()).insert(signal);

	}

	private String getNameEntryPoint(int idFloor) {

		StringBuilder builder = new StringBuilder();
		builder.append("floor").append(idFloor);

		return builder.toString();

	}

	private WorkingMemoryEntryPoint getEntryPoint(
			StatefulKnowledgeSession ksession, int idFloor) {

		String name = getNameEntryPoint(idFloor);

		WorkingMemoryEntryPoint entryPoint = ksession
				.getWorkingMemoryEntryPoint(name);

		return entryPoint;

	}

}
