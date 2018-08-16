package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;

import com.google.gwt.user.client.ui.Widget;

public interface SaveDialogI {

	public void show();

	public void setTitle();

	public void setSaveType(MaterialType type);

	public void setLabels();

	public SaveDialogI setDefaultVisibility(MaterialVisibility visibility);

	public void hide();

	public void showIfNeeded(Runnable runnable, boolean needed, Widget anchor);

	public void showIfNeeded(Runnable runnable);
}
