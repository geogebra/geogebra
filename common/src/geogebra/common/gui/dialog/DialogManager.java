package geogebra.common.gui.dialog;

import java.util.ArrayList;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

public abstract class DialogManager {

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showRedefineDialog(GeoElement geoElement, boolean b);

}
