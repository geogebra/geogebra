package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.util.debug.Log;

public class AdjustInputBox extends AdjustButton {
	private static final int MARGIN_X = 15;
	private static final int MARGIN_Y = 5;
	private GeoInputBox input;
	private int viewWidth;
	private int viewHeight;

	public AdjustInputBox(GeoInputBox input, EuclidianView view) {
		super(input, view);
		this.input = input;

		DrawInputBox di = (DrawInputBox) view.getDrawableFor(input);
		di.update();
		GDimension gd = di.getTotalSize();
		width = gd.getWidth();
		height = gd.getHeight();

		Log.debug(input.getLabelSimple() + "[AS] Input w: " + width + " h: "
				+ height);
	}


}
