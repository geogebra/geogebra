package org.geogebra.common.euclidian.draw;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoMindMapNode.NodeAlignment;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.kernel.matrix.Coords;

public class DrawMindMap extends DrawInlineText {

	private static final int BORDER_RADIUS = 8;
	private static final GBasicStroke connection = AwtFactory.getPrototype().newBasicStroke(2f,
			GBasicStroke.CAP_BUTT, GBasicStroke.JOIN_MITER);

	private static final Comparator<DrawMindMap> verticalComparator
			= Comparator.comparing(mindMap -> mindMap.rectangle.getBottom());

	private static final Comparator<DrawMindMap> horizontalComparator
			= Comparator.comparing(mindMap -> mindMap.rectangle.getRight());

	// default distance from the root node
	private static final int DISTANCE_TO_ROOT = 64;

	// we try to keep this minimum distance between nodes on different sides
	private static final int MIN_DISTANCE_BETWEEN_NODES = 16;

	// vertical distance between two nodes on the left or the right
	private static final int VERTICAL_DISTANCE_2 = 64;
	// vertical distance between three nodes on the left or the right
	private static final int VERTICAL_DISTANCE_3 = 32;
	// vertical distance between four or more nodes on the left or the right
	private static final int VERTICAL_DISTANCE_4 = 16;

	// horizontal distance between two nodes on the top or bottom
	private static final int HORIZONTAL_DISTANCE_2 = 32;
	// horizontal distance between three or more nodes on the top or bottom
	private static final int HORIZONTAL_DISTANCE_3 = 16;

	private final GeoMindMapNode node;
	private MindMapEdge mindMapEdge;

	private static class MindMapEdge {
		private final double x0;
		private final double x1;
		private final double y0;
		private final double y1;

		public MindMapEdge(DrawMindMap parent, DrawMindMap child, NodeAlignment alignment) {
			x0 = parent.rectangle.getLeft() + alignment.dx0 * parent.rectangle.getWidth();
			y0 = parent.rectangle.getTop() + alignment.dy0 * parent.rectangle.getHeight();
			x1 = child.rectangle.getLeft() + alignment.dx1 * child.rectangle.getWidth();
			y1 = child.rectangle.getTop() + alignment.dy1 * child.rectangle.getHeight();
		}

		private boolean isIntersecting(NodeAlignment alignment) {
			return ((alignment.dx0 - 0.5) * (x0 - x1) > 0)
					|| ((alignment.dy0 - 0.5) * (y0 - y1) > 0);
		}

		private GGeneralPath getConnectionPath(GeoMindMapNode node) {
			GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
			path.moveTo(x0, y0);
			double w0 = 1.0 / 4;
			if (isIntersecting(node.getAlignment())) {
				w0 = 2;
			}
			double w1 = 1 - w0;
			if (node.getAlignment() == NodeAlignment.TOP
					|| node.getAlignment() == NodeAlignment.BOTTOM) {
				path.curveTo(x0, w0 * y0 + w1 * y1, x1,
						w1 * y0 + w0 * y1, x1, y1);
			} else {
				path.curveTo(w0 * x0 + w1 * x1, y0, w1 * x0 + w0 * x1, y1, x1, y1);
			}
			return path;
		}

		public double getLength() {
			return (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
		}
	}

	/**
	 * @param view view
	 * @param node mind-map node
	 */
	public DrawMindMap(EuclidianView view, GeoMindMapNode node) {
		super(view, node);
		this.node = node;
	}

	@Override
	public void update() {
		super.update();
		GeoMindMapNode parentGeo = node.getParent();
		DrawMindMap parent = (DrawMindMap) view.getDrawableFor(parentGeo);
		if (parent == null) {
			return;
		}
		NodeAlignment alignment = node.getAlignment();
		if (mindMapEdge == null) {
			parent.rectangle.update();
			mindMapEdge = new MindMapEdge(parent, this, alignment);
		}
		if (!rootPending(node)) {
			if (mindMapEdge.isIntersecting(alignment)
					|| alignment.isOpposite(parent.node.getAlignment())) {
				updateAlignment(parent);
			} else {
				mindMapEdge = new MindMapEdge(parent, this, alignment);
			}
		}
	}

	private boolean rootPending(GeoMindMapNode node) {
		return node.isParentPending()
				|| (node.getParent() != null && rootPending(node.getParent()));
	}

	@Override
	public void draw(GGraphics2D g2) {
		for (GeoMindMapNode childGeo : node.getChildren()) {
			DrawMindMap child = (DrawMindMap) view.getDrawableFor(childGeo);
			if (child == null) {
				continue;
			}
			GGeneralPath path = child.mindMapEdge.getConnectionPath(childGeo);
			g2.setStroke(connection);
			g2.setColor(GColor.MIND_MAP_CONNECTION);
			g2.draw(path);
		}
		draw(g2, BORDER_RADIUS);
	}

	private void updateAlignment(DrawMindMap parent) {
		double length = Double.POSITIVE_INFINITY;
		boolean intersect = true;
		for (NodeAlignment alignment : NodeAlignment.values()) {
			if (alignment.isOpposite(parent.node.getAlignment())) {
				continue;
			}
			MindMapEdge connection = new MindMapEdge(parent, this, alignment);
			double newLength = connection.getLength();
			boolean newIntersect = connection.isIntersecting(alignment);
			if ((!newIntersect && intersect) || ((newIntersect == intersect)
					&& newLength < length)) {
				mindMapEdge = connection;
				node.setAlignment(alignment);
				intersect = newIntersect;
				length = newLength;
			}
		}
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

	/**
	 * Add new child node to the mind-map
	 * @param addHandler clicked handler
	 * @return added node
	 */
	public GeoMindMapNode addChildNode(EuclidianBoundingBoxHandler addHandler) {
		NodeAlignment newAlignment = toAlignment(addHandler);

		GPoint2D newLocation = computeNewLocation(newAlignment);
		GeoMindMapNode child = new GeoMindMapNode(node.getConstruction(), newLocation);
		child.setContentHeight(GeoMindMapNode.CHILD_HEIGHT);
		child.setSize(GeoMindMapNode.DEFAULT_WIDTH, GeoMindMapNode.CHILD_HEIGHT);
		child.setParent(node, newAlignment);
		child.setVerticalAlignment(VerticalAlignment.MIDDLE);
		child.setBackgroundColor(child.getKernel().getApplication().isMebis()
				? GColor.MOW_MIND_MAP_CHILD_BG_COLOR : GColor.MIND_MAP_CHILD_BG_COLOR);
		child.setBorderColor(child.getKernel().getApplication().isMebis()
				? GColor.MOW_MIND_MAP_CHILD_BORDER_COLOR : GColor.MIND_MAP_CHILD_BORDER_COLOR);
		child.setLabel(null);
		return child;
	}

	private GPoint2D computeNewLocation(NodeAlignment newAlignment) {
		Comparator<DrawMindMap> comparator = newAlignment.isVertical()
				? horizontalComparator : verticalComparator;

		List<GeoMindMapNode> childGeos = node.getChildren().stream()
				.filter(node -> node.getAlignment() == newAlignment)
				.collect(Collectors.toList());

		List<DrawMindMap> children = childGeos.stream()
				.map(node -> (DrawMindMap) view.getDrawableFor(node))
				.sorted(comparator)
				.collect(Collectors.toList());

		boolean correctlyAligned = correctlyAligned(newAlignment, children);
		if (correctlyAligned) {
			moveSiblings(newAlignment, childGeos, children);
		}

		double left = 0;
		double top = 0;
		if (children.isEmpty()) {
			left = rectangle.getLeft() + newAlignment.dx0 * rectangle.getWidth()
				+ (1 - 2 * newAlignment.dx1) * DISTANCE_TO_ROOT;
			top = rectangle.getTop() + newAlignment.dy0 * rectangle.getHeight()
				+ (1 - 2 * newAlignment.dy1) * DISTANCE_TO_ROOT;

			if (newAlignment.isVertical()) {
				left -= GeoMindMapNode.DEFAULT_WIDTH / 2;
			} else {
				top -= GeoMindMapNode.CHILD_HEIGHT / 2;
			}
		} else {
			Stream<DrawMindMap> stream = children.stream();
			DrawMindMap last = children.get(children.size() - 1);

			switch (newAlignment) {
			case BOTTOM:
				left = last.rectangle.getRight();
				top = stream.mapToInt(mindMap -> mindMap.rectangle.getTop()).min().orElse(0);
				break;
			case LEFT:
				left = stream.mapToInt(mindMap -> mindMap.rectangle.getRight()).max().orElse(0);
				top = last.rectangle.getBottom();
				break;
			case TOP:
				left = last.rectangle.getRight();
				top = stream.mapToInt(mindMap -> mindMap.rectangle.getBottom()).max().orElse(0);
				break;
			case RIGHT:
				left = stream.mapToInt(mindMap -> mindMap.rectangle.getLeft()).min().orElse(0);
				top = last.rectangle.getBottom();
				break;
			}

			left += marginLeft(newAlignment, children.size());
			top += marginTop(newAlignment, children.size());
		}

		double extraMovement = calculateExtraMovement(newAlignment, left, top);
		if (extraMovement != 0 && correctlyAligned) {
			if (newAlignment.isVertical()) {
				MoveGeos.moveObjects(childGeos,
						new Coords(0, -view.getInvYscale() * extraMovement, 0),
						null, null, view);
			} else {
				MoveGeos.moveObjects(childGeos,
						new Coords(view.getInvXscale() * extraMovement, 0, 0),
						null, null, view);
			}
		}

		switch (newAlignment) {
		case TOP:
			top -= GeoMindMapNode.CHILD_HEIGHT;
			break;
		case LEFT:
			left -= GeoMindMapNode.DEFAULT_WIDTH;
			break;
		default:
			break;
		}

		if (newAlignment.isVertical()) {
			top += extraMovement;
		} else {
			left += extraMovement;
		}

		return new GPoint2D(view.toRealWorldCoordX(left), view.toRealWorldCoordY(top));
	}

	private double calculateExtraMovement(NodeAlignment newAlignment, double left, double top) {
		Comparator<DrawMindMap> intersectionComparator = newAlignment.isVertical()
				? verticalComparator : horizontalComparator;

		if (newAlignment == NodeAlignment.BOTTOM || newAlignment == NodeAlignment.RIGHT) {
			intersectionComparator = intersectionComparator.reversed();
		}

		List<DrawMindMap> intersectableChildren = node.getChildren().stream()
				.filter(node -> node.getAlignment() != newAlignment)
				.map(node -> (DrawMindMap) view.getDrawableFor(node))
				.filter(e -> e != null)
				.sorted(intersectionComparator)
				.collect(Collectors.toList());

		for (DrawMindMap intersectableChild : intersectableChildren) {
			TransformableRectangle rect = intersectableChild.rectangle;

			if (newAlignment.isVertical()) {
				if (rect.getLeft() < left + GeoMindMapNode.DEFAULT_WIDTH
						&& left < rect.getRight()) {
					if (newAlignment == NodeAlignment.BOTTOM
							&& rect.getBottom() + MIN_DISTANCE_BETWEEN_NODES > top) {
						return rect.getBottom() + MIN_DISTANCE_BETWEEN_NODES - top;
					} else if (newAlignment == NodeAlignment.TOP
							&& rect.getTop() < top + MIN_DISTANCE_BETWEEN_NODES) {
						return rect.getTop() - MIN_DISTANCE_BETWEEN_NODES - top;
					}
				}
			} else {
				if (rect.getTop() < top + GeoMindMapNode.CHILD_HEIGHT && top < rect.getBottom()) {
					if (newAlignment == NodeAlignment.RIGHT
							&& rect.getRight() + MIN_DISTANCE_BETWEEN_NODES > left) {
						return rect.getRight() + MIN_DISTANCE_BETWEEN_NODES - left;
					} else if (newAlignment == NodeAlignment.LEFT
							&& rect.getLeft() < left + MIN_DISTANCE_BETWEEN_NODES) {
						return rect.getLeft() - MIN_DISTANCE_BETWEEN_NODES - left;
					}
				}
			}
		}

		return 0;
	}

	/**
	 * Check if the children on this side are aligned like they were aligned when inserted
	 * (only relative to each other, doesn't check alignment relative to the root)
	 */
	private boolean correctlyAligned(NodeAlignment newAlignment,
			List<DrawMindMap> children) {
		if (newAlignment == NodeAlignment.TOP || newAlignment == NodeAlignment.BOTTOM) {
			for (int i = 1; i < children.size(); i++) {
				int rightOfLeft = children.get(i - 1).rectangle.getRight();
				int leftOfRight = children.get(i).rectangle.getLeft();

				int distance = leftOfRight - rightOfLeft;
				if (Math.abs(distance - marginLeft(newAlignment, children.size() - 1)) > 3) {
					return false;
				}
			}
		} else {
			for (int i = 1; i < children.size(); i++) {
				int bottomOfTop = children.get(i - 1).rectangle.getBottom();
				int topOfBottom = children.get(i).rectangle.getTop();

				int distance = topOfBottom - bottomOfTop;
				if (Math.abs(distance - marginTop(newAlignment, children.size() - 1)) > 3) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * If the nodes on this side are correctly aligned, then we first move the siblings
	 * of the currently inserted child to the top or the left
	 */
	private void moveSiblings(NodeAlignment newAlignment, List<GeoMindMapNode> childGeos,
			List<DrawMindMap> children) {
		int spaceGained = decreaseDistanceBetweenChildren(newAlignment, children);

		if (newAlignment.isVertical()) {
			double toMove = marginLeft(newAlignment, children.size())
					+ GeoMindMapNode.DEFAULT_WIDTH - spaceGained;
			MoveGeos.moveObjects(childGeos, new Coords(-view.getInvXscale() * toMove / 2, 0, 0),
					null, null, view);
		} else {
			double toMove = marginTop(newAlignment, children.size())
					+ GeoMindMapNode.CHILD_HEIGHT - spaceGained;
			MoveGeos.moveObjects(childGeos, new Coords(0, view.getInvYscale() * toMove / 2,  0),
					null, null, view);
		}
	}

	/**
	 * Decreases the distance between the existing siblings of the newly inserted node to
	 * save some space
	 */
	private int decreaseDistanceBetweenChildren(NodeAlignment newAlignment,
			List<DrawMindMap> children) {
		if (children.size() == 1) {
			return 0;
		}

		if (newAlignment.isVertical()) {
			if (children.size() == 2) {
				double toMove = -view.getInvXscale() * HORIZONTAL_DISTANCE_3;
				MoveGeos.moveObjects(Collections.singletonList(children.get(1).node),
						new Coords(toMove, 0, 0), null, null, view);
				return HORIZONTAL_DISTANCE_3;
			}
		} else {
			if (children.size() == 2) {
				double toMove = view.getInvYscale() * VERTICAL_DISTANCE_3;
				MoveGeos.moveObjects(Collections.singletonList(children.get(1).node),
						new Coords(0, toMove, 0), null, null, view);
				return VERTICAL_DISTANCE_3;
			} else if (children.size() == 3) {
				double toMove1 = view.getInvYscale() * VERTICAL_DISTANCE_4;
				double toMove2 = view.getInvYscale() * 2 * VERTICAL_DISTANCE_4;

				MoveGeos.moveObjects(Collections.singletonList(children.get(1).node),
						new Coords(0, toMove1, 0), null, null, view);
				MoveGeos.moveObjects(Collections.singletonList(children.get(2).node),
						new Coords(0, toMove2, 0), null, null, view);
				return 2 * VERTICAL_DISTANCE_4;
			}
		}

		return 0;
	}

	private int marginLeft(NodeAlignment newAlignment, int size) {
		if (newAlignment.isVertical()) {
			if (size == 1) {
				return HORIZONTAL_DISTANCE_2;
			} else {
				return HORIZONTAL_DISTANCE_3;
			}
		}

		return 0;
	}

	private double marginTop(NodeAlignment newAlignment, int size) {
		if (!newAlignment.isVertical()) {
			if (size == 1) {
				return VERTICAL_DISTANCE_2;
			} else if (size == 2) {
				return VERTICAL_DISTANCE_3;
			} else {
				return VERTICAL_DISTANCE_4;
			}
		}

		return 0;
	}

	/**
	 * @param parentNode override parent node
	 */
	public void fixPosition(GeoMindMapNode parentNode) {
		if (parentNode == null) {
			double centerX = (view.getXmin() + view.getXmax()) / 2;
			double centerY = (view.getYmin() + view.getYmax()) / 2;
			Coords coords = new Coords(centerX - node.getLocation().x
					- rectangle.getWidth() * view.getInvXscale() / 2,
					centerY - node.getLocation().y
					+ rectangle.getHeight() * view.getInvYscale() / 2);
			translateSubtree(node, coords);
			return;
		}

		if (node.getAlignment().isOpposite(parentNode.getAlignment())) {
			fixPosition(parentNode.getAlignment(), parentNode);
		} else if (node.isParentPending() || overlapsChild(parentNode)) {
			fixPosition(node.getAlignment(), parentNode);
		}
	}

	private void fixPosition(NodeAlignment alignment, GeoMindMapNode parentNode) {
		DrawMindMap parent = (DrawMindMap) view.getDrawableFor(parentNode);
		if (parent != null) {
			GPoint2D newLocation = parent.computeNewLocation(alignment);
			node.setAlignment(alignment);
			Coords coords = new Coords(newLocation.x - node.getLocation().x,
					newLocation.y - node.getLocation().y);
			translateSubtree(node, coords);
		}
	}

	private boolean overlapsChild(GeoMindMapNode parentNode) {
		return parentNode.getChildren().stream().anyMatch(child ->
				child != node && node.getLocation().distance(child.getLocation())
						< view.getInvXscale());
	}

	private void translateSubtree(GeoMindMapNode node, Coords shift) {
		node.translate(shift);
		Group group = node.getParentGroup();
		if (group != null) {
			group.stream().filter(this::translateFiler)
					.forEach(geo -> ((Translateable) geo).translate(shift));
		}
		node.updateCascade(false);
		for (GeoMindMapNode child: node.getChildren()) {
			translateSubtree(child, shift);
		}
	}

	private boolean translateFiler(GeoElement geoElement) {
		return geoElement instanceof Translateable && !(geoElement instanceof GeoMindMapNode);
	}
}
