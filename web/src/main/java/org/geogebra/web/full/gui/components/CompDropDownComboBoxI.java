package org.geogebra.web.full.gui.components;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface CompDropDownComboBoxI extends IsWidget {

	/**
	 * Expand/collapse the dropdown.
	 */
	void toggleExpanded();

	void updateSelectionText(String text);

	@Override
	Widget asWidget();
}
