package org.geogebra.desktop.gui.dialog.options;

import javax.swing.JComboBox;

import org.geogebra.common.plugin.EuclidianStyleConstants;

public class EuclidianStyleConstantsD extends EuclidianStyleConstants {

	public static JComboBox getLineOptionsCombobox() {
		return new JComboBox(lineStyleOptions);
	}

}
