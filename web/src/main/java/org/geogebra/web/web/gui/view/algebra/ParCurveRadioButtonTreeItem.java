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
public class ParCurveRadioButtonTreeItem extends RadioButtonTreeItem {

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public ParCurveRadioButtonTreeItem(GeoElement ge, SafeUri showUrl,
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
					"Curve[t,t*2,t*3,t,0,1]", false);
		} else {
			ret = kern.getAlgebraProcessor().processAlgebraCommand(
					"Curve[t,t*2,t,0,1]", false);
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
