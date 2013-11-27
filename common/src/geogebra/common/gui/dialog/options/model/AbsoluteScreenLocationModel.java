package geogebra.common.gui.dialog.options.model;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.Furniture;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;

public class AbsoluteScreenLocationModel extends BooleanOptionModel {
	private App app;
	public AbsoluteScreenLocationModel(App app, IBooleanOptionListener listener) {
		super(listener);
		this.app = app;
		// TODO Auto-generated constructor stub
	}

	private AbsoluteScreenLocateable getAbsoluteScreenLocateable(int index) {
		return (AbsoluteScreenLocateable)getObjectAt(index);
	}
	@Override
	public boolean getValueAt(int index) {
		// TODO Auto-generated method stub
		return getGeoAt(index).isPinned();
	}

	@Override
	public void apply(int index, boolean value) {
		if (getObjectAt(index) instanceof AbsoluteScreenLocateable) {

			AbsoluteScreenLocateable geo = getAbsoluteScreenLocateable(index);
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (value) {
				// convert real world to screen coords
				int x = ev.toScreenCoordX(geo.getRealWorldLocX());
				int y = ev.toScreenCoordY(geo.getRealWorldLocY());
				if (!geo.isAbsoluteScreenLocActive())
					geo.setAbsoluteScreenLoc(x, y);
			} else {
				// convert screen coords to real world
				double x = ev.toRealWorldCoordX(geo
						.getAbsoluteScreenLocX());
				double y = ev.toRealWorldCoordY(geo
						.getAbsoluteScreenLocY());
				if (geo.isAbsoluteScreenLocActive())
					geo.setRealWorldLoc(x, y);
			}
			geo.setAbsoluteScreenLocActive(value);
			geo.toGeoElement().updateRepaint();
		} else if (getGeoAt(index).isPinnable()) {
			GeoElement geo = getGeoAt(index);
			ArrayList<GeoElement> al = new ArrayList<GeoElement>();
			al.add(getGeoAt(index));

			// geo could be redefined, so need to change geos[i] to
			// new geo
			geo = EuclidianStyleBarStatic.applyFixPosition(al,
					value, app.getActiveEuclidianView());
		}

	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (geo instanceof AbsoluteScreenLocateable) {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
			if (!absLoc.isAbsoluteScreenLocateable()
					|| geo.isGeoBoolean() || geo instanceof Furniture)
				return false;
		} else if (!geo.isPinnable()) {
			return false;
		}
	
		return true;
	}

}
