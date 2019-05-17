package org.geogebra.web.html5.main;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbarpanel.AlgebraTab;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolsTab;
import org.geogebra.web.full.gui.toolbarpanel.tableview.TableTab;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.MockApp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class GgbAPIWTest {

	private GgbAPIW api;
	private ToolbarPanel toolbarPanel;

	@Before
	public void setUp() {
		AppWFull app = spy(MockApp.mockApplet(getClass()));
		toolbarPanel = spyToolbarPanel(app);
		api = new GgbAPIW(app);
	}

	private ToolbarPanel spyToolbarPanel(AppWFull app) {
		final GuiManagerW guiManager = spy(app.getGuiManager());
		final ToolbarPanel toolbarPanel = spy(guiManager.getUnbundledToolbar());
		spyTabs(toolbarPanel);
		when(guiManager.getUnbundledToolbar()).then(new Answer<ToolbarPanel>() {
			@Override
			public ToolbarPanel answer(InvocationOnMock invocation) {
				return toolbarPanel;
			}
		});
		when(app.getGuiManager()).then(new Answer<GuiManagerW>() {
			@Override
			public GuiManagerW answer(InvocationOnMock invocation) {
				return guiManager;
			}
		});
		return toolbarPanel;
	}

	private void spyTabs(ToolbarPanel toolbarPanel) {
		final AlgebraTab algebraTab = spy(new AlgebraTab(toolbarPanel));
		when(toolbarPanel.getAlgebraTab()).then(new Answer<AlgebraTab>() {
			@Override
			public AlgebraTab answer(InvocationOnMock invocation) {
				return algebraTab;
			}
		});
		final ToolsTab toolsTab = spy(new ToolsTab(toolbarPanel));
		when(toolbarPanel.getToolsTab()).then(new Answer<ToolsTab>() {
			@Override
			public ToolsTab answer(InvocationOnMock invocation) {
				return toolsTab;
			}
		});
		final TableTab tableTab = spy(new TableTab(toolbarPanel));
		when(toolbarPanel.getTableTab()).then(new Answer<TableTab>() {
			@Override
			public TableTab answer(InvocationOnMock invocation) {
				return tableTab;
			}
		});
	}

	@Test
	public void setPerspective() {
		api.setPerspective("-LP");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+LP");
		Assert.assertTrue(toolbarPanel.isOpen());

		api.setPerspective("-A");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+A");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openAlgebra(anyBoolean());

		api.setPerspective("-Tools");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+Tools");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openTools(anyBoolean());

		api.setPerspective("-TV");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+TV");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openTableView(anyBoolean());
	}
}