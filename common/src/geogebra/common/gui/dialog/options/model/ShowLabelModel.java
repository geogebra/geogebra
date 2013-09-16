package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

public class ShowLabelModel extends OptionsModel{
	public interface IShowLabelListener {
		void update(boolean isEqualVal, boolean isEqualMode);


	}
	
	private App app;
	private Kernel kernel;

	private static final long serialVersionUID = 1L;
	private boolean showNameValue;
	private IShowLabelListener listener;
	
	public ShowLabelModel(App app, IShowLabelListener listener) {
		this.app = app;
		kernel = app.getKernel();
		this.listener = listener;
	}

	
	@Override
	public void updateProperties() {
	
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalLabelVal = true;
		boolean equalLabelMode = true;
		showNameValue = geo0.isLabelValueShowable();

		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same label visible value
			if (geo0.isLabelVisible() != temp.isLabelVisible())
				equalLabelVal = false;
			// same label mode
			if (geo0.getLabelMode() != temp.getLabelMode())
				equalLabelMode = false;

			showNameValue = showNameValue
					&& temp.isLabelValueShowable();
		}

		// change "Show Label:" to "Show Label" if there's no menu
		// Michael Borcherds 2008-02-18
		listener.update(equalLabelVal, equalLabelMode);
		}

	// show everything but numbers (note: drawable angles are shown)
	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!geo.isLabelShowable()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public void applyShowChanges(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
				GeoElement geo = getGeoAt(i);
				geo.setLabelVisible(value);
				geo.updateRepaint();
			}
			updateProperties();
	}
	
	public void applyModeChanges(int mode) {
			GeoElement geo;
			for (int i = 0; i < getGeosLength(); i++) {
				geo = getGeoAt(i);
				geo.setLabelMode(mode);
				geo.updateVisualStyle();
			}
			kernel.notifyRepaint();
			app.storeUndoInfo();
		}
	
	public boolean isNameValueShown() {
		return showNameValue;
	}
}