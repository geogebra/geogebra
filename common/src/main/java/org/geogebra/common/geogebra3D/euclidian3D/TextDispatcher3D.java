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
			e.printStackTrace();
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
