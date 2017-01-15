package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.debug.Log;

public class AdjustInputBox extends AdjustButton {
	private GeoInputBox input;

	public AdjustInputBox(GeoInputBox input, EuclidianView view) {
		super(input, view);
		this.input = input;
		calculateSize();
	}

	private boolean calculateSize() {
		DrawInputBox di = (DrawInputBox) view.getDrawableFor(input);
		if (di == null) {
			Log.debug(
					"[AS] " + input.getLabelSimple() + " DrawInputBox is null");
			return false;
		}

		di.update();
		GDimension gd = di.getTotalSize();
		width = gd.getWidth();
		height = gd.getHeight();

		// Log.debug(input.getLabelSimple() + "[AS] Input w: " + width + " h: "
		// + height);
		return true;
	}

	@Override
	public void apply() {
		if (calculateSize()) {
			super.apply();
			Log.debug("[AS] " + input.getLabelSimple() + " apply() succeeded.");
		} else {
			Log.debug("[AS] " + input.getLabelSimple() + " apply() FAILED!");

		}
	}
}
