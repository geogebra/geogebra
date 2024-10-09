package org.geogebra.web.html5.main;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbarpanel.AlgebraTab;
import org.geogebra.web.full.gui.toolbarpanel.TabContainer;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.full.gui.toolbarpanel.ToolsTab;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyProbabilityTable;
import org.geogebra.web.full.gui.toolbarpanel.tableview.TableTab;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.FocusUtil;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(FocusUtil.class)
public class GgbAPIWTest {

	private GgbAPIW api;
	private ToolbarPanel toolbarPanel;

	@Before
	public void setUp() {
		AppWFull app = spy(AppMocker.mockGraphing());
		toolbarPanel = spyToolbarPanel(app);
		api = new GgbAPIW(app);
	}

	private ToolbarPanel spyToolbarPanel(AppWFull app) {
		final GuiManagerW guiManager = spy(app.getGuiManager());
		final ToolbarPanel toolbarPanel = spy(guiManager.getUnbundledToolbar());
		spyTabs(toolbarPanel);
		when(guiManager.getUnbundledToolbar()).thenReturn(toolbarPanel);
		when(app.getGuiManager()).thenReturn(guiManager);
		return toolbarPanel;
	}

	private void spyTabs(ToolbarPanel toolbarPanel) {
		final AlgebraTab algebraTab = spy(new AlgebraTab(toolbarPanel));
		final ToolsTab toolsTab = spy(new ToolsTab(toolbarPanel));
		final TableTab tableTab = spy(new TableTab(toolbarPanel, new StickyProbabilityTable()));
		when(toolbarPanel.getTab(anyObject())).thenAnswer((Answer<ToolbarTab>) invocation -> {
			DockPanelData.TabIds tabID = invocation.getArgumentAt(0, DockPanelData.TabIds.class);
			switch (tabID) {
			case TABLE: return tableTab;
			case TOOLS: return toolsTab;
			case ALGEBRA: return algebraTab;
			}
			return null;
		});
		final TabContainer tabContainer = spy(new TabContainer(toolbarPanel));
		when(toolbarPanel.getTabContainer()).thenReturn(tabContainer);
	}

	@Test
	public void setPerspective() {
		api.setPerspective("-SP");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+SP");
		Assert.assertTrue(toolbarPanel.isOpen());

		api.setPerspective("-Algebra");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+Algebra");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openAlgebra(anyBoolean());

		api.setPerspective("-Tools");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+Tools");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openTools(anyBoolean());

		api.setPerspective("-Table");
		Assert.assertTrue(toolbarPanel.isClosed());
		api.setPerspective("+Table");
		Assert.assertTrue(toolbarPanel.isOpen());
		verify(toolbarPanel, times(1)).openTableView(anyBoolean());
	}
}