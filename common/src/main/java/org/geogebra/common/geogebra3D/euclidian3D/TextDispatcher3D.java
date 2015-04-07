package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.TextDispatcher;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.util.StringUtil;

public class TextDispatcher3D extends TextDispatcher {

	private EuclidianView3D view3D;

	public TextDispatcher3D(Kernel kernel, EuclidianView3D view) {
		super(kernel, view);
		view3D = view;
	}

	@Override
	protected GeoPointND getPointForDynamicText(Region object, GPoint loc) {

		Coords coords = view3D.getCursor3D().getCoords();

		return view3D.getEuclidianController().createNewPoint(
				removeUnderscores(l10n.getPlain("Point")
						+ object.getLabel(StringTemplate.defaultTemplate)),
				false, object, coords.getX(), coords.getY(), coords.getZ(),
				false, false);
	}

	@Override
	protected GeoPointND getPointForDynamicText(Path object, GPoint loc) {

		Coords coords = view3D.getCursor3D().getCoords();

		return view3D
				.getEuclidianController()
				.getCompanion()
				.createNewPoint(
						removeUnderscores(l10n.getPlain("Point")
								+ object.getLabel(StringTemplate.defaultTemplate)),
						false, object, coords.getX(), coords.getY(),
						coords.getZ(), false, false);
	}

	@Override
	protected GeoPointND getPointForDynamicText(GPoint loc) {

		GeoPoint3D cursor = view3D.getCursor3D();

		if (cursor.hasRegion())
			return getPointForDynamicText(cursor.getRegion(), loc);

		if (cursor.hasPath())
			return getPointForDynamicText(cursor.getPath(), loc);

		return super.getPointForDynamicText(loc);
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

	public void createVolumeText(GeoElement hasVolume, GPoint loc) {
		GeoNumeric volume = kernel.getManager3D().Volume(null,
				(HasVolume) hasVolume);

		// text
		GeoText text = createDynamicTextForMouseLoc("VolumeOfA", hasVolume,
				volume, loc);
		if (hasVolume.isLabelSet()) {
			volume.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n
					.getCommand("Volume")) + hasVolume.getLabelSimple()));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ hasVolume.getLabelSimple()));
		}

	}

}
