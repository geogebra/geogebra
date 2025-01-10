package org.geogebra.web.geogebra3D.web.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;

/**
 * Stylebar for view for plane
 * 
 * @author Mathieu
 */
public class EuclidianStyleBarForPlaneW extends EuclidianStyleBarW {

	/**
	 * @param ev
	 *            view
	 * @param viewID
	 *            view ID
	 */
	public EuclidianStyleBarForPlaneW(EuclidianView ev, int viewID) {
		super(ev, viewID);
	}

	@Override
	protected void setOptionType() {
		optionType = OptionType.EUCLIDIAN_FOR_PLANE;
	}

	@Override
	protected void setEvStandardView() {
		EuclidianViewForPlaneCompanion companion = (EuclidianViewForPlaneCompanion) getView()
				.getCompanion();
		companion.updateCenterAndOrientationRegardingView();
		companion.updateScaleRegardingView();
	}
}
