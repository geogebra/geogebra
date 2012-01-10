package geogebra.common.gui.dialog;

import java.util.ArrayList;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;

public abstract class DialogManager {

	public abstract boolean showFunctionInspector(GeoFunction geoFunction);

	public abstract void showPropertiesDialog(ArrayList<GeoElement> geos);

	public abstract void showRedefineDialog(GeoElement geoElement, boolean b);

	public abstract void showNumberInputDialogSegmentFixed(String menu,
			GeoPoint2 geoPoint2);

	public abstract void showNumberInputDialogAngleFixed(String menu,
			GeoSegment[] selectedSegments, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showTextCreationDialog(GeoPointND loc);

}
