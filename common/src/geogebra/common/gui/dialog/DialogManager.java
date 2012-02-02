package geogebra.common.gui.dialog;

import java.util.ArrayList;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
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

	public abstract boolean showSliderCreationDialog(int x, int y);

	public abstract void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPoint2[] selectedPoints,
			GeoElement[] selGeos);

	public abstract void showNumberInputDialogRegularPolygon(String menu,
			GeoPoint2 geoPoint2, GeoPoint2 geoPoint22);

	public abstract void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool);

	public abstract void showNumberInputDialogCirclePointRadius(String menu,
			GeoPointND geoPointND, AbstractEuclidianView view);

	public abstract NumberValue showNumberInputDialog(String title, String message,
			String initText);

	public abstract Object[] showAngleInputDialog(String title, String message,
			String initText);

	public abstract boolean showButtonCreationDialog(int x, int y, boolean textfield);

}
