package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.user.client.ui.IsWidget;

/**
 * Algebra view wrapping panel.
 */
public interface AlgebraPanelInterface extends IsWidget {

	/**
	 * Scroll to the bottom.
	 */
	void scrollAVToBottom();

	/**
	 * @return parent split pane
	 */
	DockSplitPaneW getParentSplitPane();

	/**
	 * Save scroll position.
	 */
	void saveAVScrollPosition();

	/**
	 * Fit content to size (deferred).
	 */
	void deferredOnResize();

	/**
	 * @return inner width in pixels
	 */
	int getInnerWidth();

	/**
	 * @return ID of the active tab (ALGEBRA for Classic dock panel)
	 */
	DockPanelData.TabIds getTabId();

	/**
	 * Scroll to the active item.
	 */
	void scrollToActiveItem();

	/**
	 * @param mathKeyboardListener current keyboard listener
	 * @return new keyboard listener
	 */
	MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener mathKeyboardListener);

	/**
	 * @return height in pixels
	 */
	int getOffsetHeight();

	/**
	 * @return vertical offset in pixels
	 */
	int getAbsoluteTop();

	/**
	 * Show or hide style bar.
	 * @param show whether to show it
	 */
	void showStyleBarPanel(boolean show);

	/**
	 * @return whether style bar takes the whole width
	 */
	boolean hasLongStyleBar();

	/**
	 * @return whether style bar is visible in DOM
	 */
	boolean isStyleBarPanelShown();

	/**
	 * @return whether style bar should be visible based on preferences
	 */
	boolean isStyleBarVisible();

	/**
	 * @return navigation rail width (in landscape)
	 */
	int getNavigationRailWidth();
}
