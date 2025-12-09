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

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.impl.objects.AnimationStepProperty;
import org.geogebra.common.util.DoubleUtil;

public class AnimationStepModel extends TextPropertyModel {

	private boolean partOfSlider;

	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;

	/**
	 * Constructor
	 *
	 * @param app application
	 */
	public AnimationStepModel(App app) {
		super(app);
	}

	@Override
	public String getText() {

		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (!DoubleUtil.isEqual(geo0.getAnimationStep(),
					temp.getAnimationStep())) {
				equalStep = false;
			}
			if (!temp.isGeoAngle()) {
				onlyAngles = false;
			}
		}

		// int oldDigits = kernel.getMaximumFractionDigits();
		// kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		StringTemplate highPrecision = StringTemplate.printDecimals(
				StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS, false);

		if (equalStep) {
			NumberValue step = geo0.getAnimationStepObject();
			GeoElement stepGeo = GeoElement.as(step);
			if (onlyAngles && (stepGeo == null
					|| (!stepGeo.isLabelSet() && stepGeo.isIndependent()))) {
				return app.getKernel()
						.formatAngle(geo0.getAnimationStep(), highPrecision,
								((GeoAngle) geo0)
										.getAngleStyle() == AngleStyle.UNBOUNDED)
						.toString();
			} else {
				boolean autostep = false;
				if (geo0.isGeoNumeric()) {
					autostep = ((GeoNumeric) geo0).isAutoStep();
				}
				return autostep ? "" : step.getLabel(highPrecision);
			}
		} else {
			return "";
		}

	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return AnimationStepProperty.isValid(geo, partOfSlider);
	}

	@Override
	public void applyChanges(GeoNumberValue value, String str) {
		boolean notDefined = value == null || Double.isNaN(value.getDouble());

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isGeoNumeric()) {
				((GeoNumeric) geo).setAutoStep(notDefined);
				if (!notDefined) {
					geo.setAnimationStep(value);
				}
			} else if (value != null) {
				geo.setAnimationStep(value);
			}
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "AnimationStep";
	}

	public void setPartOfSlider(boolean partOfSlider) {
		this.partOfSlider = partOfSlider;
	}

}
