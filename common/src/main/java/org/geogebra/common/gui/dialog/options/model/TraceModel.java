package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.Traceable;

public class TraceModel extends BooleanOptionModel {
	private static final long serialVersionUID = 1L;
	
	public TraceModel(IBooleanOptionListener listener) {
		super(listener);
	}

	protected Traceable getTraceableAt(int index) {
		return (Traceable)getGeoAt(index);
	}
	
	@Override
	public boolean isValidAt(int index) {
		return (getGeoAt(index) instanceof Traceable);
	}

	@Override
	public boolean getValueAt(int index) {
		return getTraceableAt(index).getTrace();
	}

	@Override
	public void apply(int index, boolean value) {
		Traceable geo = getTraceableAt(index);
		geo.setTrace(value);
		geo.updateRepaint();
	
	}
}
