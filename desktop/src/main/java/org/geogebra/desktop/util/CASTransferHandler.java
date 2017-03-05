package org.geogebra.desktop.util;

import java.awt.datatransfer.DataFlavor;

public class CASTransferHandler {

	// supported data flavors
	public static final DataFlavor casTableFlavor = new DataFlavor(
			Integer.class, "cell reference");

}
