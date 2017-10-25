package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;

import com.google.gwt.user.client.ui.IsWidget;

public interface AlgebraPanelInterface extends IsWidget {

	void scrollAVToBottom();
	// void setAlgebraView(final AlgebraViewW av);
	//
	// void scrollTo(int position);
	//
	// /**
	// * scrolls to the bottom of the panel
	// */
	// void scrollToBottom();
	//
	// /**
	// * Scroll to the item that is selected.
	// */
	// void scrollToActiveItem();
	//
	// /**
	// * Saves the current scroll position of the dock panel.
	// */
	// void saveScrollPosition();

	DockSplitPaneW getParentSplitPane();

	void saveAVScrollPosition();

	void deferredOnResize();

	int getInnerWidth();

	boolean isToolMode();

	void scrollToActiveItem();

	MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener mathKeyboardListener);

	int getOffsetHeight();
}
