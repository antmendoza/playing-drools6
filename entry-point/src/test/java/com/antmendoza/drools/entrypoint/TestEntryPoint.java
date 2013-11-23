package com.antmendoza.drools.entrypoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import com.antmendoza.model.Signal;

/**
 * Entendiendo el funcionamiento de los EntryPoint
 * 
 * @author antmendoza
 * 
 */
public class TestEntryPoint {

	private KieSession ksession;

	@Before
	public void configure() {
		KieServices ks = KieServices.Factory.get();

		KieContainer kc = ks.getKieClasspathContainer();

		// Creamos las sesi—n definida en el fichero kmodule.xml.
		ksession = kc.newKieSession("EntryPointKS");

		// Listener
		ksession.addEventListener(new DebugAgendaEventListener());
		ksession.addEventListener(new DebugRuleRuntimeEventListener());
	}

	@Test
	public void execute() {

		int floor1 = 1;
		int floor2 = 2;

		// Insertamos las se–ales.
		Signal signal1 = new Signal(1, floor1, 33, 71);
		insertObject(ksession, signal1);

		Signal signal2 = new Signal(2, floor1, 55, 90);
		insertObject(ksession, signal2);

		Signal signal3 = new Signal(3, floor2, 20, 40);
		insertObject(ksession, signal3);

		Signal signal4 = new Signal(4, floor2, 55, 40);
		insertObject(ksession, signal4);

		Signal signal5 = new Signal(5, floor1, 20, 40);
		insertObject(ksession, signal5);

		ksession.fireAllRules();

		// Obtenemos los entryPoint de cada uno de los pisos.
		EntryPoint entryPointFloor1 = getEntryPoint(ksession, floor1);
		EntryPoint entryPointFloor2 = getEntryPoint(ksession, floor2);

		Assert.assertEquals(3, entryPointFloor1.getFactCount());
		Assert.assertEquals(2, entryPointFloor2.getFactCount());

		// En la regla "Signal from floor2 with temperature > 50" insertamos el
		// objeto en el entryPoint 'alert'.
		EntryPoint entryPointAlert = ksession.getEntryPoint("alert");
		Assert.assertEquals(1, entryPointAlert.getFactCount());

	}

	private EntryPoint getEntryPoint(KieSession ksession2, int floor) {

		return ksession.getEntryPoint("floor" + floor);
	}

	private FactHandle insertObject(KieSession ksession, Signal signal) {

		return getEntryPoint(ksession, signal.getFloor()).insert(signal);

	}

	@After
	public void dispose() {

		ksession.dispose();
	}

}
