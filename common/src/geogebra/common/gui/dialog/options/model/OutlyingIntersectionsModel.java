package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.LimitedPath;

public class OutlyingIntersectionsModel extends BooleanOptionModel {

	public OutlyingIntersectionsModel(IBooleanOptionListener listener) {
		super(listener);
	}

	@Override
	public boolean isValidAt(int index) {
		return (getObjectAt(index) instanceof LimitedPath);
	}

	@Override
	public boolean getValueAt(int index) {
		return ((LimitedPath)getObjectAt(index)).allowOutlyingIntersections();
	}

	@Override
	public void apply(int index, boolean value) {
		LimitedPath geo = (LimitedPath) getObjectAt(index);
		geo.setAllowOutlyingIntersections(value);
		geo.toGeoElement().updateRepaint();		
	}
}

