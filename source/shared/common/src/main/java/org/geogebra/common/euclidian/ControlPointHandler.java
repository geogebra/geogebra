package org.geogebra.common.euclidian;

public class ControlPointHandler implements ShapeManipulationHandler {
	public final int id;

	public ControlPointHandler(int hit) {
		this.id = hit;
	}

	@Override
	public boolean isAddHandler() {
		return false;
	}
}
