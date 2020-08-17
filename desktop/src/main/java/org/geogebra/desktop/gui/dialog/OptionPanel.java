package org.geogebra.desktop.gui.dialog;

import javax.swing.JPanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;

abstract class OptionPanel extends JPanel implements
		SetLabels, UpdateFonts, UpdateablePropertiesPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected OptionPanel() {
		// make this protected
	}

}
