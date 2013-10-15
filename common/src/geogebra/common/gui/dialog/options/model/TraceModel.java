package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.Traceable;

public class TraceModel extends BooleanOptionModel {
	private static final long serialVersionUID = 1L;
	
	public TraceModel(IBooleanOptionListener listener) {
		super(listener);
	}
	
	public void updateProperties() {
		
		// check if properties have same values
		Traceable temp, geo0 = (Traceable) getGeoAt(0);
		boolean equalTrace = true;

		for (int i = 1; i < getGeosLength(); i++) {
			temp = (Traceable) getGeoAt(i);
			// same object visible value
			if (geo0.getTrace() != temp.getTrace())
				equalTrace = false;
		}

		// set trace checkbox
		getListener().updateCheckbox(equalTrace ? geo0.getTrace(): false);	
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!(getGeoAt(i) instanceof Traceable)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public void applyChanges(boolean value) {
		Traceable geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = (Traceable) getGeoAt(i);
			geo.setTrace(value);
			geo.updateRepaint();
		}
	}
}
