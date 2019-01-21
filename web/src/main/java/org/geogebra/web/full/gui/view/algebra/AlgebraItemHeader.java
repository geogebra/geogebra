package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.html5.gui.view.button.MyToggleButton;

import com.google.gwt.user.client.ui.IsWidget;

public interface AlgebraItemHeader extends IsWidget {

	int getOffsetWidth();

	void updateIcons(boolean warning);

	void setLabels();

	void update();

	void setHighlighted(boolean selected);

	MyToggleButton getBtnHelpToggle();

	MyToggleButton getBtnPlus();

	boolean isHit(int x, int y);

	void setIndex(int itemCount);

}
