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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class ReflexAngleModel extends MultipleOptionsModel {
	private boolean hasOrientation;
	private boolean isDrawable;
	private boolean isDefaults;

	public interface IReflexAngleListener extends IComboListener {
		@MissingDoc
		void setComboLabels();
	}

	public ReflexAngleModel(App app, boolean isDefaults) {
		super(app);
		this.isDefaults = isDefaults;
	}

	private AngleProperties getAnglePropertiesAt(int index) {
		return (AngleProperties) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		AngleProperties temp, geo0 = getAnglePropertiesAt(0);
		boolean equalangleStyle = true;
		boolean hasOrientationOld = hasOrientation;
		boolean isDrawableOld = isDrawable;
		hasOrientation = true;
		isDrawable = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getAnglePropertiesAt(i);
			if (!temp.hasOrientation()) {
				hasOrientation = false;
			}
			if (!temp.isDrawable()) {
				isDrawable = false;
			}
			if (geo0.getAngleStyle() != temp.getAngleStyle()) {
				equalangleStyle = false;
			}

		}

		if (hasOrientation != hasOrientationOld
				|| isDrawableOld != isDrawable) {
			((IReflexAngleListener) getListener()).setComboLabels();
		}

		if (equalangleStyle) {
			getListener().setSelectedIndex(geo0.getAngleStyle().getXmlVal());
		}

	}

	@Override
	public String getTitle() {
		return "AngleBetween";
	}

	@Override
	public List<String> getChoices(Localization loc) {
		List<GeoAngle.AngleStyle> angleStyles;
		if (hasOrientation) {
			angleStyles = Arrays.asList(GeoAngle.AngleStyle.values());
			if (isDrawable) {
				// don't want to allow (-inf, +inf)
				angleStyles.remove(GeoAngle.AngleStyle.UNBOUNDED);
			}
		} else {
			// only 180degree wide interval are possible
			angleStyles = List.of(GeoAngle.AngleStyle.NOTREFLEX, GeoAngle.AngleStyle.ISREFLEX);
		}

		return angleStyles.stream()
				.map(style -> loc.getPlain("AandB", style.getMin(), style.getMax()))
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);

		return (!(geo.isIndependent() && !isDefaults)
				&& (geo instanceof AngleProperties) && !geo.isGeoList())
				|| isAngleList(geo);

	}

	@Override
	protected void apply(int index, int value) {
		AngleProperties geo = getAnglePropertiesAt(index);
		geo.setAngleStyle(value);
		geo.updateVisualStyleRepaint(GProperty.ANGLE_INTERVAL);
	}

	public boolean hasOrientation() {
		return hasOrientation;
	}

	@Override
	public int getValueAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}
