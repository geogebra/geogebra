package geogebra.html5.util;

import geogebra.common.util.StringUtil;
import geogebra.html5.gui.tooltip.ToolTipManagerW;

public abstract class SaveCallback {
	
	public abstract void onSaved();

	public void onError(String errorMessage) {
		ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(errorMessage) + "</html>", true);
    }
}
