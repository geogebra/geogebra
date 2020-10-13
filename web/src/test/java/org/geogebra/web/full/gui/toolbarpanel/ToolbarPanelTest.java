package org.geogebra.web.full.gui.toolbarpanel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CountDownLatch;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventDispatcher;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({ResizeComposite.class})
public class ToolbarPanelTest {

	private ToolbarPanel toolbarPanel;
	private AppWFull app;

	@Before
	public void setUp() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
		toolbarPanel = spy(new ToolbarPanel(mockApp()));
	}

	private AppWFull mockApp() {
		AppMocker.useProviderForSchedulerImpl();
		app = AppMocker.mockGraphing(getClass());
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
		Assert.assertTrue(toolbarPanel.isOpen());

		toolbarPanel.close();
		verifyDispatchEventCalled(EventType.SIDE_PANEL_CLOSED);
	}

	private void verifyDispatchEventCalled(EventType eventType) {
		verify(toolbarPanel, times(1)).dispatchEvent(eventType);
	}

	@Test
	public void open() {
		toolbarPanel.close();
		toolbarPanel.open();
		verifyDispatchEventCalled(EventType.SIDE_PANEL_OPENED);
	}

	@Test
	public void openAlgebra() {
		toolbarPanel.openAlgebra(true);
		verifyDispatchEventCalled(EventType.ALGEBRA_PANEL_SELECTED);
	}

	@Test
	public void openTools() {
		toolbarPanel.openTools(true);
		verifyDispatchEventCalled(EventType.TOOLS_PANEL_SELECTED);
	}

	@Test
	public void openTableView() {
		toolbarPanel.openTableView(true);
		verifyDispatchEventCalled(EventType.TABLE_PANEL_SELECTED);
	}

	@Test
	public void testSaveToolbarState() {
		openPanel();
		closePanel();
	}

	private void openPanel() {
		toolbarPanel.open();
		Assert.assertTrue(toolbarPanel.isOpen());
		String algebraViewVisible = "view id=\"" + App.VIEW_ALGEBRA + "\" visible=\"true\"";
		Assert.assertTrue(app.getXML().contains(algebraViewVisible));
	}

	private void closePanel() {
		toolbarPanel.close();
		Assert.assertTrue(toolbarPanel.isClosed());
		checkPanelNotVisibleInXml();
	}

	private void checkPanelNotVisibleInXml() {
		final CountDownLatch latch = new CountDownLatch(1);
		app.invokeLater(new Runnable() {
			@Override
			public void run() {
				String algebraViewNotVisible =
						"view id=\"" + App.VIEW_ALGEBRA + "\" visible=\"false\"";
				Assert.assertTrue(app.getXML().contains(algebraViewNotVisible));
				latch.countDown();
			}
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}