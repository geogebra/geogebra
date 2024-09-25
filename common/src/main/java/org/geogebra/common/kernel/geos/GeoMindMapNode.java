package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class GeoMindMapNode extends GeoInline implements TextStyle, HasTextFormatter,
		HasVerticalAlignment {

	public static final double DEFAULT_WIDTH = 200;
	public static final double ROOT_HEIGHT = 72;
	public static final double CHILD_HEIGHT = 48;
	private boolean parentPending = false;
	private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

	public enum NodeAlignment {
		// The order is intentionally inverted
		BOTTOM(0.5, 1, 0.5, 0),
		RIGHT(1, 0.5, 0, 0.5),
		TOP(0.5, 0, 0.5, 1),
		LEFT(0, 0.5, 1, 0.5);

		public final double dx0;
		public final double dy0;
		public final double dx1;
		public final double dy1;

		NodeAlignment(double dx0, double dy0, double dx1, double dy1) {
			this.dx0 = dx0;
			this.dy0 = dy0;
			this.dx1 = dx1;
			this.dy1 = dy1;
		}

		public boolean isVertical() {
			return this == TOP || this == BOTTOM;
		}

		public boolean isOpposite(NodeAlignment alignment) {
			return alignment != null && Math.abs(ordinal() - alignment.ordinal()) == 2;
		}
	}

	private GeoMindMapNode parent;
	private NodeAlignment nodeAlignment;

	private final ArrayList<GeoMindMapNode> children = new ArrayList<>();

	private String content;
	private boolean defined = true;
	private double minHeight;

	/**
	 * @param cons construction
	 * @param location real world location
	 */
	public GeoMindMapNode(Construction cons, GPoint2D location) {
		super(cons);
		setLocation(location);
		setLineThickness(1);
		setContentWidth(DEFAULT_WIDTH);
		setContentHeight(ROOT_HEIGHT);
		setContent("[{\"text\":\"\\n\",\"align\":\"center\"}]");
	}

	@Override
	public void update(boolean dragging) {
		super.update(dragging);
		for (GeoMindMapNode child : children) {
			child.update(dragging);
		}
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.MIND_MAP;
	}

	@Override
	public GeoMindMapNode copy() {
		GeoMindMapNode copy = new GeoMindMapNode(cons, null);
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoMindMapNode) {
			setLocation(new GPoint2D(((GeoMindMapNode) geo).getLocation().x,
					((GeoMindMapNode) geo).getLocation().y));
		}
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public void doRemove() {
		if (parent != null) {
			parent.getChildren().remove(this);
		}
		removeWithChildren();
	}

	/**
	 * doRemove cannot be recursive, because breaking the link between parent
	 * and current node would cause ConcurrentModificationException
	 */
	private void removeWithChildren() {
		super.doRemove();
		for (GeoMindMapNode child : children) {
			child.removeWithChildren();
		}
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}

	@Override
	public double getMinWidth() {
		return GeoInlineText.MIN_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, GeoInlineText.MIN_HEIGHT);
	}

	@Override
	public int getFontStyle() {
		return GeoInlineText.getFontStyle(getFormatter());
	}

	@Override
	public double getFontSizeMultiplier() {
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}

	@Override
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		XMLBuilder.appendBorderAndAlignment(sb, this, verticalAlignment);
		XMLBuilder.appendParent(sb, parent, nodeAlignment);
		if (getLineThickness() != 0) {
			getLineStyleXML(sb);
		}
	}

	/**
	 * Build XML with parent label replaced
	 * @param sb builder
	 */
	public void getXMLNoParent(StringBuilder sb) {
		if (parent == null) {
			getXML(false, sb);
			return;
		}
		String oldParent = parent.getLabelSimple();
		parent.setLabelSimple("_");
		getXML(false, sb);
		parent.setLabelSimple(oldParent);
	}

	public GeoMindMapNode getParent() {
		return parent;
	}

	/**
	 * @param parent parent node
	 * @param nodeAlignment alignment
	 */
	public void setParent(GeoMindMapNode parent, NodeAlignment nodeAlignment) {
		setParent(parent);
		this.nodeAlignment = nodeAlignment;
	}

	/**
	 * @param parent parent node
	 */
	public void resolvePendingParent(GeoMindMapNode parent) {
		setParent(parent);
		parentPending = false;
	}

	private void setParent(GeoMindMapNode parent) {
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		} else {
			parentPending = true;
		}
	}

	public List<GeoMindMapNode> getChildren() {
		return children;
	}

	public void addChild(GeoMindMapNode child) {
		children.add(child);
	}

	public NodeAlignment getAlignment() {
		return nodeAlignment;
	}

	public void setAlignment(NodeAlignment alignment) {
		this.nodeAlignment = alignment;
	}

	public boolean isParentPending() {
		return this.parentPending;
	}

	@Override
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignment valign) {
		verticalAlignment = valign;
	}
}
