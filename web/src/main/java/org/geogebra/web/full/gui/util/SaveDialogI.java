package org.geogebra.web.full.gui.util;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;

import com.google.gwt.user.client.ui.Widget;

public interface SaveDialogI {

	void show();

	void setTitle();

	void setSaveType(MaterialType type);

	void setLabels();

	void hide();

	void showIfNeeded(AsyncOperation<Boolean> callback, boolean needed,
			Widget anchor);

	void showIfNeeded(AsyncOperation<Boolean> runnable);
}
