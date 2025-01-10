package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.MyPoint;

public class GeneralPathMock extends GeneralPathClipped {
	/**
	 * Creates new clipped general path
	 * @param view view
	 */
	public GeneralPathMock(EuclidianViewInterfaceSlim view) {
		super(view);
	}

	@Override
	public MyPoint firstPoint() {
		return super.firstPoint();
	}
}
