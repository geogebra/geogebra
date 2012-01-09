package geogebra.common.gui.view.properties;

import geogebra.common.kernel.View;
import geogebra.common.main.GeoElementSelectionListener;

public interface PropertiesView extends View, GeoElementSelectionListener{
	public void updateSelection();
}
