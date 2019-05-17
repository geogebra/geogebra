package org.geogebra.web.full.gui.toolbarpanel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.MockApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class ToolbarPanelTest {

	private ToolbarPanel toolbarPanel;

	@Before
	public void setUp() {
		toolbarPanel = spy(new ToolbarPanel(mockApp()));
	}

	private AppW mockApp() {
		AppW app = MockApp.mockApplet(getClass());
		app.setShowToolBar(true);
		return app;
	}

	@Test
	public void testDispatchEvent() {
		EventDispatcher eventDispatcher = mock(EventDispatcher.class);
		toolbarPanel.setEventDispatcher(eventDispatcher);

		toolbarPanel.dispatchEvent(null);
		verify(eventDispatcher, times(1)).dispatchEvent((Event) any());
	}

	@Test
	public void close() {
		toolbarPanel.close();
		verifyDispatchEventCalled(EventType.LEFT_PANEL_CLOSED);
	}

	private void verifyDispatchEventCalled(EventType eventType) {
		verify(toolbarPanel, times(1)).dispatchEvent(eventType);
	}

	@Test
	public void open() {
		toolbarPanel.close();
		toolbarPanel.open();
		verifyDispatchEventCalled(EventType.LEFT_PANEL_OPENED);
	}

	@Test
	public void openAlgebra() {
		toolbarPanel.openAlgebra(true);
		verifyDispatchEventCalled(EventType.AV_PANEL_SELECTED);
	}

	@Test
	public void openTools() {
		toolbarPanel.openTools(true);
		verifyDispatchEventCalled(EventType.TOOLS_PANEL_SELECTED);
	}

	@Test
	public void openTableView() {
		toolbarPanel.openTableView(true);
		verifyDispatchEventCalled(EventType.TV_PANEL_SELECTED);
	}
}