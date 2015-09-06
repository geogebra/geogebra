package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * ParCurveRadioButtonTreeItem for creating parametric curves (GeoCurveCartesian
 * objects) in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class ParCurveTreeItem extends RadioTreeItem {

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public ParCurveTreeItem(GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);
	}

	public static GeoCurveCartesianND createBasic(Kernel kern) {
		boolean oldVal = kern.isUsingInternalCommandNames();
		kern.setUseInternalCommandNames(true);
		GeoElement[] ret;

		if (kern.getApplication().is3D()
				&& kern.getApplication().isEuclidianView3Dinited()
				&& kern.getApplication().hasEuclidianView3D()
				&& kern.getApplication().getEuclidianView3D().isShowing()) {
			ret = kern.getAlgebraProcessor().processAlgebraCommand(
					"Curve[t^2,t*(t-1)*(t+1),t,t,-5,5]", false);
		} else {
			ret = kern.getAlgebraProcessor().processAlgebraCommand(
					"Curve[t^2,t*(t-1)*(t+1),t,-5,5]", false);
		}

		kern.setUseInternalCommandNames(oldVal);
		if ((ret != null) && (ret.length > 0) && (ret[0] != null)
				&& (ret[0] instanceof GeoCurveCartesianND)
				&& (ret[0].isGeoCurveCartesian())) {
			return (GeoCurveCartesianND) ret[0];
		}
		return null;
	}
}
