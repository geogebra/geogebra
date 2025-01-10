package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.main.App;

public abstract class IconOptionsModel extends NumberOptionsModel {
	public IconOptionsModel(App app) {
		super(app);
	}

	public abstract void setListener(IComboListener listener);

	public abstract String getTitle();
}
