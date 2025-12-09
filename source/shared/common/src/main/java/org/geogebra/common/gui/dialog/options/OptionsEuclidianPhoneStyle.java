/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.dialog.options;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.main.App;

/**
 * Class for managing EV options in phone style
 * 
 * @author mathieu
 *
 */
public class OptionsEuclidianPhoneStyle extends OptionsEuclidian {
	private static final String[] EDIT_TEXT_DEFAULT = { "x", "y", "z" };

	private AxisModel[] mAxisModel;
	private EuclidianView view;
	private int dimension;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            euclidian view
	 */
	public OptionsEuclidianPhoneStyle(App app, EuclidianView view) {

        this.view = view;

        if (view.isEuclidianView3D()) {
			dimension = 3;
		} else {
			dimension = 2;
		}

        // axes
		mAxisModel = new AxisModel[dimension];
		for (int i = 0; i < dimension; i++) {
			mAxisModel[i] = new AxisModel(app, view, i, null);
        }

    }

	public int getDimension() {
		return dimension;
	}

	/**
	 * 
	 * @return related euclidian view
	 */
	public EuclidianView getView() {
		return view;
	}

	@Override
	public void updateGUI() {
		// TODO implement if needed
	}

	@Override
	public void updateBounds() {
		// TODO implement if needed
	}

	/**
	 * 
	 * @return true if at least 1 axis label is shown
	 */
	public boolean getLabelShown() {
		String[] labels = view.getAxesLabels(true);
		for (int i = 0; i < labels.length; i++) {
			if (labels[i] != null && labels[i].length() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return false if at least one axis hasn't auto distance
	 */
	public boolean isAutoDistance() {
		boolean[] auto = view.isAutomaticAxesNumberingDistance();
		for (int i = 0; i < auto.length; i++) {
			if (!auto[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * perform "show all objects" on view
	 */
	public void setViewShowAllObjects() {
		view.setViewShowAllObjects(true, false);
	}

	/**
	 * perform "set standard view" on view
	 */
	public void setStandardView() {
		view.setStandardView(true);
	}

	/**
	 * 
	 * @param i
	 *            index
	 * @return i-th axis model
	 */
	public AxisModel getAxisModel(int i) {
		return mAxisModel[i];
	}

	public void updateAxesLabels(String[] labels) {
		boolean changed = false;
		if (labels == null) {
			for (int i = 0; i < mAxisModel.length; i++) {
				changed = mAxisModel[i].applyAxisLabel("", false) || changed;
			}
		} else {
			for (int i = 0; i < mAxisModel.length; i++) {
				changed = mAxisModel[i].applyAxisLabel(labels[i], false) || changed;
			}
		}
		if (changed) {
			view.settingsChanged(view.getSettings());
			view.updateBounds(true, true);
			view.updateBackground();
			view.repaintView();
		}
	}

	public String getAxisLabel(int i) {
		String text = getView().getAxisLabel(i, true);
		if (text == null || text.equals("")) {
			return EDIT_TEXT_DEFAULT[i];
		}
		return text;
	}

}
