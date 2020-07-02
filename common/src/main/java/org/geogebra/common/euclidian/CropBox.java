package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.debug.Log;

public class CropBox implements BoundingBoxDelegate {
	private static final int CROP_HANDLERS = 8;
	private GBasicStroke outlineStroke = AwtFactory.getPrototype().newBasicStroke(6.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);
	private GBasicStroke handlerStroke = AwtFactory.getPrototype().newBasicStroke(4.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);
	private MediaBoundingBox box;

	public CropBox(MediaBoundingBox box) {
		this.box = box;
	}

	@Override
	public void createHandlers() {
		box.initHandlers(CROP_HANDLERS);
	}

	@Override
	public void setHandlerFromCenter(int handlerIndex, double x, double y) {
		double tangent = 5 * Math.cos(box.geo.getAngle());
		double normal = 5 * Math.sin(box.geo.getAngle());
		switch (handlerIndex) {
		case 0:
			getHandler(0).moveTo(x - 2 * normal, y + 2 * tangent);
			getHandler(0).lineTo(x, y);
			getHandler(0).lineTo(x + 2 * tangent, y + 2 * normal);
			break;
		case 1:
			getHandler(1).moveTo(x + 2 * normal, y - 2 * tangent);
			getHandler(1).lineTo(x, y);
			getHandler(1).lineTo(x + 2 * tangent, y + 2 * normal);
			break;
		case 2:
			getHandler(2).moveTo(x - 2 * tangent, y - 2 * normal);
			getHandler(2).lineTo(x, y);
			getHandler(2).lineTo(x + 2 * normal, y - 2 * tangent);
			break;
		case 3:
			getHandler(3).moveTo(x - 2 * normal, y + 2 * tangent);
			getHandler(3).lineTo(x, y);
			getHandler(3).lineTo(x - 2 * tangent, y - 2 * normal);
			break;
		case 4:
			// side handlers
			getHandler(4).moveTo(x - tangent, y - normal);
			getHandler(4).lineTo(x + tangent, y + normal);
			break;
		case 5:
			getHandler(5).moveTo(x + normal, y - tangent);
			getHandler(5).lineTo(x - normal, y + tangent);
			break;
		case 6:
			getHandler(6).moveTo(x - tangent, y - normal);
			getHandler(6).lineTo(x + tangent, y + normal);
			break;
		case 7:
			getHandler(7).moveTo(x + normal, y - tangent);
			getHandler(7).lineTo(x - normal, y + tangent);
			break;
		default:
			Log.warn("illegal handler " + handlerIndex);
		}
	}

	private GGeneralPath getHandler(int handlerIndex) {
		return (GGeneralPath) box.handlers.get(handlerIndex);
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (!box.handlers.isEmpty()) {
			g2.setColor(GColor.WHITE);
			g2.setStroke(outlineStroke);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(getHandler(i));
			}
			g2.setStroke(handlerStroke);
			g2.setColor(GColor.BLACK);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(getHandler(i));
			}
		}
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return box.rectangle != null && box.hitRectangle(x, y, 2 * hitThreshold);
	}

	@Override
	public GGeneralPath createHandler() {
		return AwtFactory.getPrototype().newGeneralPath();
	}

}
