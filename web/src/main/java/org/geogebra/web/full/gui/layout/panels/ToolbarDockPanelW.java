package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Laszlo Gal
 *
 */
public class ToolbarDockPanelW extends DockPanelW
		implements AlgebraPanelInterface {

	private ToolbarPanel toolbar;
	private DockPanelData.TabIds tabId;

	/**
	 * New panel with AV and tools
	 */
	public ToolbarDockPanelW() {
		super(
				App.VIEW_ALGEBRA, // view id
				"ToolbarWindow", 			// view title phrase
				null,						// toolbar string
				false,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
	}
	
	@Override
	protected Widget loadComponent() {
		toolbar = new ToolbarPanel(app);
		setTabId(tabId);
		return toolbar;
	}

	@Override
	public void onResize() {
		super.onResize();

		if (toolbar != null) {
			toolbar.resize();
		}

	}

	@Override
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				toolbar.resize();
			}
		});
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		if (toolbar.isAlgebraViewActive()) {
			return toolbar.getKeyboardListener();
		}

		return super.getKeyboardListener();
	}

	/**
	 * Delegating to toolbar.
	 * 
	 * @param ml
	 *            the litstener
	 * @return the updated listener;
	 */
	@Override
	public MathKeyboardListener updateKeyboardListener(MathKeyboardListener ml) {
		if (toolbar.isAlgebraViewActive()) {
			return toolbar.updateKeyboardListener(ml);
		}
		return null;
	}

	/**
	 * 
	 * @return the tabbed toolbar.
	 */
	public ToolbarPanel getToolbar() {
		return toolbar;
	}

	/**
	 * Saves the scroll position of algebra view
	 */
	@Override
	public void saveAVScrollPosition() {
		toolbar.saveAVScrollPosition();
	}

	/**
	 * Scrolls Algebra View to the bottom.
	 */
	@Override
	public void scrollAVToBottom() {
		if (toolbar != null) {
			toolbar.scrollAVToBottom();
		}
	}

	@Override
	public void setTabId(DockPanelData.TabIds tabId) {
		if (toolbar != null) {
			// open with false: no fading here.
			if (tabId == DockPanelData.TabIds.TOOLS) {
				toolbar.openTools(false);
			} else if (tabId == DockPanelData.TabIds.TABLE) {
				toolbar.openTableView(false);
			} else {
				toolbar.openAlgebra(false);
			}
			toolbar.updateHeader();
		}
		doSetTabId(tabId);
	}

	/**
	 * simple setter
	 * @param tabId active tab ID
	 */
	public void doSetTabId(DockPanelData.TabIds tabId) {
		this.tabId = tabId;
	}

	@Override
	public DockPanelData createInfo() {
		return super.createInfo().setTabId(tabId);
	}

	@Override
	public DockPanelData.TabIds getTabId() {
		return tabId;
	}

	@Override
	public void setLabels() {
		if (toolbar != null) {
			toolbar.setLabels();
		}
	}

	@Override
	public int getInnerWidth() {
		return getOffsetWidth();
	}

	@Override
	public void scrollToActiveItem() {
		if (toolbar != null) {
			toolbar.scrollToActiveItem();
		}
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}
