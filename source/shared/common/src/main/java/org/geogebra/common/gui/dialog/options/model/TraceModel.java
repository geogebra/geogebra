package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.main.App;

public class TraceModel extends BooleanOptionModel {

	public TraceModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	protected Traceable getTraceableAt(int index) {
		return (Traceable) getGeoAt(index);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index) instanceof Traceable;
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

	@Override
	public String getTitle() {
		return "ShowTrace";
	}
}
