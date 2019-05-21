package org.geogebra.common.plugin;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class EventDispatcherTest extends BaseUnitTest {

	private EventDispatcher eventDispatcher;
	private ScriptManager scriptManager;

	@Before
	public void setUp() {
		eventDispatcher = getApp().getEventDispatcher();
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

	private void verifyClientListenersNotified(EventType eventType) {
		Event event = new Event(eventType);
		eventDispatcher.dispatchEvent(event);
		verify(scriptManager, times(1))
				.callClientListeners(scriptManager.clientListeners, event);
	}
}