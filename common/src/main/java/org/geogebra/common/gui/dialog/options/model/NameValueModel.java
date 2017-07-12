package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;

/**
 * 
 * @author laszlo
 *
 */
public class NameValueModel extends ShowLabelModel {
	private static final int MODE_CAPTION = 3;

	public interface INameValueListener extends IObjectNameListener, IShowLabelListener {
		// concat two interfaces.
	}
	private ObjectNameModel nameModel;
	private boolean forceCaption = false;
	/**
	 * 
	 * @param app
	 *            the app.
	 * @param listener
	 *            to update the gui.
	 */
	public NameValueModel(App app, INameValueListener listener) {
		super(app, listener);
		nameModel = new ObjectNameModel(app, listener);
	}

	@Override
	public void setGeos(Object[] geos) {
		super.setGeos(geos);
		nameModel.setGeos(geos);
		forceCaption = ""
				.equals(getGeoAt(0).getCaption(StringTemplate.defaultTemplate));

	}

	@Override
	public void updateProperties() {
		super.updateProperties();
		nameModel.updateProperties();
	}

	/**
	 * Apply name changes
	 * 
	 * @param name
	 *            to set.
	 * @param handler
	 *            to report error.
	 * 
	 */
	public void applyNameChange(final String name, ErrorHandler handler) {

		if (forceCaption || kernel.lookupLabel(name) != null) {
			applyModeChanges(MODE_CAPTION, true);
			nameModel.applyCaptionChange(name);
			forceCaption = true;
		} else {
			nameModel.applyNameChange(name, handler);
		}
	}

}
