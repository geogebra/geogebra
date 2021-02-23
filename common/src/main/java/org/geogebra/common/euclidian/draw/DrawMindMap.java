package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoMindMapNode.NodeAlignment;

public class DrawMindMap extends DrawInlineText {

	private static final int BORDER_RADIUS = 8;

	private final GeoMindMapNode node;

	public DrawMindMap(EuclidianView view, GeoInline text) {
		super(view, text);
		this.node = (GeoMindMapNode) text;
	}

	@Override
	public void update() {
		super.update();
		if (node != null) {
			for (GeoMindMapNode child : node.getChildren()) {
				view.getDrawableFor(child).update();
			}
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		for (GeoMindMapNode childGeo : node.getChildren()) {
			DrawMindMap child = (DrawMindMap) view.getDrawableFor(childGeo);
			if (child == null) {
				continue;
			}

			NodeAlignment alignment = childGeo.getAlignment();

			double x0 = rectangle.getLeft() + alignment.dx0 * rectangle.getWidth();
			double y0 = rectangle.getTop() + alignment.dy0 * rectangle.getHeight();
			double x1 = child.rectangle.getLeft() + alignment.dx1 * child.rectangle.getWidth();
			double y1 = child.rectangle.getTop() + alignment.dy1 * child.rectangle.getHeight();

			GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
			path.moveTo(x0, y0);
			if (alignment == NodeAlignment.TOP || alignment == NodeAlignment.BOTTOM) {
				path.curveTo(x0, y1, x1, y0, x1, y1);
			} else {
				path.curveTo(x1, y0, x0, y1, x1, y1);
			}

			g2.setStroke(getBorderStroke());
			g2.setColor(GColor.BLACK);
			g2.draw(path);
		}

		draw(g2, BORDER_RADIUS);
	}

	private NodeAlignment toAlignment(EuclidianBoundingBoxHandler addHandler) {
		switch (addHandler) {
		case ADD_TOP:
			return NodeAlignment.TOP;
		case ADD_RIGHT:
			return NodeAlignment.RIGHT;
		case ADD_BOTTOM:
			return NodeAlignment.BOTTOM;
		case ADD_LEFT:
			return NodeAlignment.LEFT;
		default:
			return null;
		}
	}

	public GeoMindMapNode addChildNode(EuclidianBoundingBoxHandler addHandler) {
		GPoint2D newLocation = new GPoint2D(node.getLocation().x, node.getLocation().y);
		GeoMindMapNode child = new GeoMindMapNode(node, toAlignment(addHandler), newLocation);
		node.addChild(child);
		return child;
	}
}
