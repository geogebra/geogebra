package org.geogebra.web.html5.gui;

import org.geogebra.common.kernel.ModeSetter;

public interface ToolBarInterface {

	public int setMode(int mode, ModeSetter m);

	public String getImageURL(int mode);

	public void setVisible(boolean show);


}
