package org.geogebra.web.geogebra3D.web.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;

public class EuclidianStyleBarForPlaneW extends EuclidianStyleBarW {

	public EuclidianStyleBarForPlaneW(EuclidianView ev, int viewID) {
		super(ev, viewID);
	}

	@Override
	protected void setOptionType() {
		optionType = OptionType.EUCLIDIAN_FOR_PLANE;
	}
}
