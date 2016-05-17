package org.geogebra.common.jre.gui;

import org.geogebra.common.awt.MyImage;

public interface MyImageJre extends MyImage {

	public String getSVG();

	public boolean hasNonNullImplementation();
}
