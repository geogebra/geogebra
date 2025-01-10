package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author laszlo
 *
 */
public class NameValueModel extends ShowLabelModel {
	private ObjectNameModel nameModel;
	private boolean forceCaption = false;

	public interface INameValueListener extends IObjectNameListener, IShowLabelListener {
		// concat two interfaces.
	}
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
		setForceCaption(!getGeoAt(0)
				.getLabel(StringTemplate.defaultTemplate)
				.equals(getGeoAt(0).getCaption(StringTemplate.defaultTemplate)));

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
		if (shouldNameChange(name)) {
			nameModel.applyNameChange(name, handler);
		} else {
			nameModel.applyCaptionChange(name);
			setForceCaption(!StringUtil.emptyTrim(name));
		}
	}

	private boolean shouldNameChange(String name) {
		return "".equals(name) || (!isForceCaption() && !isUsedForOtherGeo(name)
				&& LabelManager.isValidLabel(name, kernel, null));
	}

	private boolean isUsedForOtherGeo(String name) {
		return kernel.lookupLabel(name) != null
				&& kernel.lookupLabel(name) != nameModel.getCurrentGeo();
	}

	/**
	 *
	 * @param label
	 * 				the new label
	 *
	 * @return if label should change to the new one.
	 */
	public boolean noLabelUpdateNeeded(String label) {
		return nameModel.noLabelUpdateNeeded(label);
	}

	/**
	 *
	 * @return caption should be changed or not.
	 */
	public boolean isForceCaption() {
		return forceCaption;
	}

	/**
	 * 
	 * @param forceCaption
	 *            caption should be changed or not.
	 */
	public void setForceCaption(boolean forceCaption) {
		this.forceCaption = forceCaption;
	}

	/**
	 * 
	 * @return if label is visible at all.
	 */
	public boolean isLabelVisible() {
		return getGeoAt(0).isLabelVisible();
	}
}
