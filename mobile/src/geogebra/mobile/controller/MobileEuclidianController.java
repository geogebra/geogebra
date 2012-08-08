package geogebra.mobile.controller;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;

public class MobileEuclidianController extends EuclidianController
{

	@Override
	public void setApplication(App app)
	{
		this.app = app;
	}

	@Override
	protected void initToolTipManager()
	{
	}

	@Override
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1)
	{
		return null;
	}

	@Override
	protected void resetToolTipManager()
	{
	}

}
