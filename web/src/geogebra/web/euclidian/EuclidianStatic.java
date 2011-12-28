package geogebra.web.euclidian;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Shape;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

public class EuclidianStatic extends geogebra.common.euclidian.EuclidianStatic {

	@Override
	protected Rectangle doDrawMultilineLaTeX(AbstractApplication app,
	        Graphics2D tempGraphics, GeoElement geo, Graphics2D g2, Font font,
	        Color fgColor, Color bgColor, String labelDesc, int xLabel,
	        int yLabel, boolean serif) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Point doDrawIndexedString(AbstractApplication app, Graphics2D g3,
	        String str, float xPos, float yPos, boolean serif) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doFillWithValueStrokePure(Shape shape, Graphics2D g3) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Rectangle doDrawMultiLineText(AbstractApplication app,
	        String labelDesc, int xLabel, int yLabel, Graphics2D g2,
	        boolean serif) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doDrawWithValueStrokePure(Shape shape, Graphics2D g2) {
		// TODO Auto-generated method stub

	}

}
