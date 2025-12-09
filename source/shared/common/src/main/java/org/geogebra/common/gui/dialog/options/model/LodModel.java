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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class LodModel extends MultipleOptionsModel {

	private boolean isDefaults;

	public LodModel(App app, boolean isDefaults) {
		super(app);
		this.isDefaults = isDefaults;
	}

	private SurfaceEvaluable getSurfaceAt(int index) {
		return (SurfaceEvaluable) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		SurfaceEvaluable temp, geo0 = getSurfaceAt(0);
		boolean equalLevelOfDetail = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getSurfaceAt(i);
			if (geo0.getLevelOfDetail() != temp.getLevelOfDetail()) {
				equalLevelOfDetail = false;
			}

		}

		if (equalLevelOfDetail) {
			getListener().setSelectedIndex(
					geo0.getLevelOfDetail() == LevelOfDetail.SPEED ? 0 : 1);
		}

	}

	@Override
	public String getTitle() {
		return "LevelOfDetail";
	}

	@Override
	public List<String> getChoices(Localization loc) {
		List<String> result = new ArrayList<>();
		result.add(loc.getMenu("Speed"));
		result.add(loc.getMenu("Quality"));
		return result;
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return !isDefaults && geo.hasLevelOfDetail();
	}

	@Override
	protected void apply(int index, int value) {
		SurfaceEvaluable geo = getSurfaceAt(index);
		geo.setLevelOfDetail(
				value == 0 ? LevelOfDetail.SPEED : LevelOfDetail.QUALITY);
		((GeoElementND) geo).updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public int getValueAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}
