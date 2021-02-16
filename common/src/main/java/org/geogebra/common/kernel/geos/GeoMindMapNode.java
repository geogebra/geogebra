package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class GeoMindMapNode extends GeoInline implements TextStyle, HasTextFormatter {

	private static final double MIN_WIDTH = 200;
	private static final double MIN_HEIGHT = 72;

	private String content;
	private boolean defined = true;
	private double minHeight;

	public GeoMindMapNode(Construction cons, GPoint2D location) {
		super(cons);
		setLocation(location);
		setSize(MIN_WIDTH, MIN_HEIGHT);
		setLineThickness(1);
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
		return Math.max(minHeight, MIN_HEIGHT);
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
		if (getLineThickness() != 0) {
			getLineStyleXML(sb);
		}
	}
}
