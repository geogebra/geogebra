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

package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.StringUtil;

public class ShowConditionModel extends OptionsModel {
	private IShowConditionListener listener;

	public interface IShowConditionListener extends PropertyListener {
		@MissingDoc
		void setText(String text);

		@MissingDoc
		void updateSelection(Object[] geos);

	}

	public ShowConditionModel(App app, IShowConditionListener listener) {
		super(app);
		this.listener = listener;
	}

	@Override
	public void updateProperties() {

		// take condition of first geo
		String strCond = "";
		GeoElement geo0 = getGeoAt(0);
		GeoBoolean cond = geo0.getShowObjectCondition();
		if (cond != null) {
			strCond = cond.getLabel(StringTemplate.editTemplate);
		}

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			cond = geo.getShowObjectCondition();
			if (cond != null) {
				String strCondGeo = cond.getLabel(StringTemplate.editTemplate);
				if (!strCond.equals(strCondGeo)) {
					strCond = "";
				}
			}
		}

		listener.setText(strCond);

	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isEuclidianShowable();
	}

	/**
	 * @param strCond
	 *            string value of the condition
	 * @param handler
	 *            takes care of errors
	 */
	public void applyChanges(String strCond, ErrorHandler handler) {
		// processed = true;
		GeoBoolean cond;
		if (StringUtil.emptyTrim(strCond)) {
			cond = null;
		} else {

			cond = app.getKernel().getAlgebraProcessor()
					.evaluateToBoolean(strCond, handler);
		}

		if (cond != null || StringUtil.emptyTrim(strCond)) {
			// set condition
			try {
				for (int i = 0; i < getGeosLength(); i++) {
					GeoElement geo = getGeoAt(i);
					geo.setShowObjectCondition(cond);

					// make sure object shown when condition removed
					if (cond == null) {
						geo.updateRepaint();
					}
				}

			} catch (CircularDefinitionException e) {
				listener.setText("");
				ErrorHelper.handleException(e, app, handler);
			}

			if (cond != null) {
				cond.updateRepaint();
			}

			// to update "showObject" as well
			listener.updateSelection(getGeos());
		} else {
			// put back faulty condition (for editing)
			listener.setText(strCond);
		}
		storeUndoInfo();
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
