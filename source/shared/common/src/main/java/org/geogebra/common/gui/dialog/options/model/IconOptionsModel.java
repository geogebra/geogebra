package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.main.App;

public abstract class IconOptionsModel extends NumberOptionsModel {
	public IconOptionsModel(App app) {
		super(app);
	}

	@MissingDoc
	public abstract void setListener(IComboListener listener);

	@MissingDoc
	public abstract String getTitle();
}
