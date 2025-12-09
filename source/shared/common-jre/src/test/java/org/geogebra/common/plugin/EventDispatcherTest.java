/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.test.EventAccumulator;
import org.junit.Before;
import org.junit.Test;

public class EventDispatcherTest extends BaseUnitTest implements EventListener {

	private EventDispatcher eventDispatcher;
	private ScriptManager scriptManager;
	private boolean batch = false;
	private int objectsAdded = 0;

	@Before
	public void setUp() {
		eventDispatcher = getApp().getEventDispatcher();
		getApp().getEventDispatcher().removeEventListener(this);
	}

	@Test
	public void addEventListener() {
		ScriptManager scriptManager = getApp().getScriptManager();
		assertTrue(eventDispatcher.getListeners().contains(scriptManager));
	}

	@Test
	public void dispatchEvent() {
		scriptManager = spy(ScriptManager.class);
		eventDispatcher.addEventListener(scriptManager);

		verifyClientListenersNotified(EventType.SIDE_PANEL_CLOSED);
		verifyClientListenersNotified(EventType.SIDE_PANEL_OPENED);
		verifyClientListenersNotified(EventType.ALGEBRA_PANEL_SELECTED);
		verifyClientListenersNotified(EventType.TOOLS_PANEL_SELECTED);
		verifyClientListenersNotified(EventType.TABLE_PANEL_SELECTED);
	}

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Test
	public void cubeElementsShouldBeBatched() {
		add("A=(0,0,0)");
		add("B=(0,1,0)");
		add("C=(1,1,0)");
		prepareBatchingTest();
		add("cube=Cube(A,B,C)");
		assertEquals(24, objectsAdded);
	}

	@Test
	public void netElementsShouldBeBatched() {
		add("A=(0,0,0)");
		add("B=(0,1,0)");
		add("C=(1,1,0)");
		add("cube=Cube(A,B,C)");
		prepareBatchingTest();
		add("Net(cube, 1)");
		assertEquals(40, objectsAdded);
	}

	@Test
	public void prismNetElementsShouldBeBatched() {
		add("A=(0,0,0)");
		add("B=(0,1,0)");
		add("C=(1,1,0)");
		add("D=(1,0,0)");
		add("prism=Prism(A,B,C,D,(0,0,1))");
		prepareBatchingTest();
		add("Net(prism, 1)");
		assertEquals(40, objectsAdded);
	}

	@Test
	public void shouldNotNotifyAboutSpotlightUpdates() {
		EuclidianController ec = getApp().getActiveEuclidianView().getEuclidianController();
		EventAccumulator acc = new EventAccumulator();
		eventDispatcher.addEventListener(acc);
		ec.spotlightOn();
		ec.getSpotlight().notifyUpdate();
		ec.spotlightOff();
		assertEquals(Collections.singletonList("HIDE_SPOTLIGHT null"), acc.getEvents());
	}

	private void prepareBatchingTest() {
		getApp().getEventDispatcher().addEventListener(this);
		objectsAdded = 0;
		batch = false;
	}

	@Override
	public void sendEvent(Event evt) {
		if (evt.getType() == EventType.BATCH_ADD_STARTED) {
			batch = true;
		} else if (evt.getType() == EventType.BATCH_ADD_COMPLETE) {
			batch = false;
		} if (evt.getType() == EventType.ADD) {
			assertTrue(evt.getTarget() + "added outside of batch", batch);
			objectsAdded++;
		}
	}

	private void verifyClientListenersNotified(EventType eventType) {
		Event event = new Event(eventType);
		eventDispatcher.dispatchEvent(event);
		verify(scriptManager, times(1))
				.callClientListeners(scriptManager.clientListeners, event);
	}
}