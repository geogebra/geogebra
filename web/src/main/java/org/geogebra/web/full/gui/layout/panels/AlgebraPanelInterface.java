package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.user.client.ui.IsWidget;

public interface AlgebraPanelInterface extends IsWidget {

	void scrollAVToBottom();

	DockSplitPaneW getParentSplitPane();

	void saveAVScrollPosition();

	void deferredOnResize();

	int getInnerWidth();

	DockPanelData.TabIds getTabId();

	void scrollToActiveItem();

	MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener mathKeyboardListener);

	int getOffsetHeight();

	int getAbsoluteTop();

	void showStyleBarPanel(boolean show);

	boolean hasLongStyleBar();

	boolean isStyleBarPanelShown();

	boolean isStyleBarVisible();
}
