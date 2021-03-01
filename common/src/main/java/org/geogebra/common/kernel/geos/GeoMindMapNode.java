package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;

public class GeoMindMapNode extends GeoInline implements TextStyle, HasTextFormatter {

	public static final double MIN_WIDTH = 200;

	public static final double ROOT_HEIGHT = 72;
	public static final double CHILD_HEIGHT = 48;

	public enum NodeAlignment {
		TOP(0.5, 0, 0.5, 1),
		RIGHT(0, 0.5, 1, 0.5),
		BOTTOM(0.5, 1, 0.5, 0),
		LEFT(1, 0.5, 0, 0.5);

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
	}

	private GeoMindMapNode parent;
	private NodeAlignment nodeAlignment;

	private final ArrayList<GeoMindMapNode> children = new ArrayList<>();

	private String content;
	private boolean defined = true;
	private double minHeight;

	public GeoMindMapNode(Construction cons, GPoint2D location) {
		super(cons);
		setLocation(location);
		setLineThickness(1);
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
	public GeoElement copy() {
		GeoElement copy = new GeoMindMapNode(cons, null);
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
		return MIN_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, parent == null ? ROOT_HEIGHT : CHILD_HEIGHT);
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
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		XMLBuilder.appendBorder(sb, this);
		XMLBuilder.appendParent(sb, parent, nodeAlignment);
		if (getLineThickness() != 0) {
			getLineStyleXML(sb);
		}
	}

	@Override
	public void translate(Coords v) {
		super.translate(v);
		for (GeoMindMapNode child : children) {
			child.translate(v);
		}
	}

	public GeoMindMapNode getParent() {
		return parent;
	}

	public void setParent(GeoMindMapNode parent, NodeAlignment nodeAlignment) {
		this.parent = parent;
		this.nodeAlignment = nodeAlignment;
		parent.addChild(this);
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
}
