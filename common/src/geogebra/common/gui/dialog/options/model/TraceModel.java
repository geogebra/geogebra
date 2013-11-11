package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.Traceable;

public class TraceModel extends BooleanOptionModel {
	private static final long serialVersionUID = 1L;
	
	public TraceModel(IBooleanOptionListener listener) {
		super(listener);
	}
	
	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index) instanceof Traceable);
	}

	@Override
	public boolean getValueAt(int index) {
		return ((Traceable)getGeoAt(index)).getTrace();
	}

	@Override
	public void apply(int index, boolean value) {
		Traceable geo = (Traceable) getGeoAt(index);
		geo.setTrace(value);
		geo.updateRepaint();
	
	}
}
