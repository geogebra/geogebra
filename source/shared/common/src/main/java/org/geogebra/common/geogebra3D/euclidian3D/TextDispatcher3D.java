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

package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.TextDispatcher;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Dispatcher for Area / Length texts
 *
 */
public class TextDispatcher3D extends TextDispatcher {

	private EuclidianView3D view3D;

	/**
	 * @param kernel
	 *            kernel
	 * @param view
	 *            view
	 */
	public TextDispatcher3D(Kernel kernel, EuclidianView3D view) {
		super(kernel, view);
		view3D = view;
	}

	@Override
	protected GeoPointND getPointForDynamicText(Region object, GPoint loc0) {

		Coords coords = view3D.getCursor3D().getCoords();

		return view3D.getEuclidianController().createNewPoint(
				removeUnderscoresAndBraces(loc.getMenu("Point")
						+ object.getLabel(StringTemplate.defaultTemplate)),
				false, object, coords.getX(), coords.getY(), coords.getZ(),
				false, false);
	}

	@Override
	protected GeoPointND getPointForDynamicText(Path object, GPoint loc0) {

		Coords coords = view3D.getCursor3D().getCoords();

		return view3D.getEuclidianController().getCompanion().createNewPoint(
				removeUnderscoresAndBraces(loc.getMenu("Point")
						+ object.getLabel(StringTemplate.defaultTemplate)),
				false, object, coords.getX(), coords.getY(), coords.getZ(),
				false, false);
	}

	@Override
	protected GeoPointND getPointForDynamicText(GPoint corner) {

		GeoPoint3D cursor = view3D.getCursor3D();

		if (cursor.hasRegion()) {
			return getPointForDynamicText(cursor.getRegion(), corner);
		}

		if (cursor.isPointOnPath()) {
			return getPointForDynamicText(cursor.getPath(), corner);
		}

		return super.getPointForDynamicText(corner);
	}

	@Override
	protected void setNoPointLoc(GeoText text, GPoint loc) {
		try {
			GeoPoint3D p = new GeoPoint3D(kernel.getConstruction());
			p.setCoords(view3D.getCursor3D().getCoords());
			text.setStartPoint(p);
		} catch (CircularDefinitionException e) {
			Log.debug(e);
		}
	}

	/**
	 * @param hasVolume
	 *            element that has volume
	 * @param corner
	 *            text location
	 */
	public void createVolumeText(GeoElement hasVolume, GPoint corner) {
		GeoNumeric volume = kernel.getManager3D().volume(null,
				(HasVolume) hasVolume);

		// text
		GeoText text = createDynamicTextForMouseLoc("VolumeOfA", "Volume of %0",
				hasVolume,
				volume, corner);
		if (text != null && hasVolume.isLabelSet()) {
			volume.setLabel(removeUnderscoresAndBraces(
					StringUtil.toLowerCaseUS(loc.getCommand("Volume"))
							+ hasVolume.getLabelSimple()));
			text.setLabel(removeUnderscoresAndBraces(
					loc.getMenu("Text") + hasVolume.getLabelSimple()));
		}
	}
}
