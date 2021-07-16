package org.geogebra.desktop.gui.dialog.options;

import javax.swing.JComboBox;

import org.geogebra.common.plugin.EuclidianStyleConstants;

public class EuclidianStyleConstantsD extends EuclidianStyleConstants {

	public static JComboBox getLineOptionsCombobox() {
		final Integer[] iconArray = new Integer[getLineStyleOptionsLength()];
		for (int i = 0; i < iconArray.length; i++) {
			iconArray[i] = getLineStyleOptions(i);
		}
		return new JComboBox(iconArray);
	}

}
