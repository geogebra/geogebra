package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelDecorator;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.ViewCounter;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * @author Laszlo Gal
 *
 */
public class ToolbarDockPanelW extends DockPanelW
		implements AlgebraPanelInterface {

	private ToolbarPanel toolbar;
	private DockPanelData.TabIds tabId;
	private static final int MIN_ROWS_WITHOUT_KEYBOARD = 5;
	private static final int MIN_ROWS_WITH_KEYBOARD = 3;
	private final DockPanelDecorator decorator;

	/**
	 * New panel with AV and tools
	 */
	public ToolbarDockPanelW(DockPanelDecorator decorator) {
		super(App.VIEW_ALGEBRA, null, false);
		this.decorator = decorator;
	}
	
	@Override
	protected Widget loadComponent() {
		toolbar = new ToolbarPanel(app, decorator);
		setTabId(tabId);
		return toolbar;
	}

	@Override
	public void setAlone(boolean alone) {
		if (toolbar != null) {
			toolbar.setAlone(alone);
		}
		tryBuildZoomPanel();
		// call super last to make sure we rebuild *after* zoom panel init
		super.setAlone(alone);
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
		Scheduler.get().scheduleDeferred(() -> toolbar.resize());
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return toolbar.getKeyboardListener();
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
		if (toolbar.getSelectedTabId() == DockPanelData.TabIds.TABLE) {
			return ml;
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
			} else if (tabId == DockPanelData.TabIds.DISTRIBUTION) {
				toolbar.openDistributionView(false);
			} else if (tabId == DockPanelData.TabIds.SPREADSHEET) {
				toolbar.openSpreadsheetView(false);
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
		if (decorator != null) {
			decorator.setLabels();
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

	@Override
	public int getNavigationRailWidth() {
		return toolbar == null ? 0 : toolbar.getNavigationRailWidth();
	}

	@Override
	public double getMinVHeight(boolean keyboard) {
		int rows = keyboard ? MIN_ROWS_WITH_KEYBOARD
				: MIN_ROWS_WITHOUT_KEYBOARD;
		return rows * ToolbarPanel.CLOSED_HEIGHT_PORTRAIT;
	}

	@Override
	public void onOrientationChange() {
		if (toolbar != null) {
			toolbar.onOrientationChange(isAlone());
		}
	}

	/**
	 * Hide view opposite to AV
	 */
	public void hideOppositeView() {
		if (toolbar != null) {
			toolbar.hideOppositeView();
		}
	}

	/**
	 * Close the toolbar panel
	 */
	public void hideToolbar() {
		if (toolbar != null) {
			toolbar.hideToolbar();
		}
	}

	@Override
	public void paintToCanvas(CanvasRenderingContext2D context2d,
			ViewCounter counter, int left, int top) {
		if (toolbar != null) {
			drawWhiteBackground(context2d, left, top);
			toolbar.paintToCanvas(context2d, counter, left, top);
		} else if (counter != null) {
			counter.decrement();
		}
	}
}
