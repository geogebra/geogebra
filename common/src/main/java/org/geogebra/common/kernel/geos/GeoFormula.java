package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

public class GeoFormula extends GeoInline {

	public static final int DEFAULT_WIDTH = 250;
	public static final int DEFAULT_HEIGHT = 48;

	private static final Parser parser = new Parser(new MetaModel());
	private static final TeXSerializer texSerializer = new TeXSerializer();

	static {
		texSerializer.setPlaceholderEnabled(false);
	}

	private boolean defined = true;
	private String formula;

	private double minWidth;
	private double minHeight;

	private String latex = "";

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 * @param location initial location in RW coordinates
	 */
	public GeoFormula(Construction c, GPoint2D location) {
		super(c);
		setLocation(location);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FORMULA;
	}

	@Override
	public GeoElement copy() {
		GeoFormula copy = new GeoFormula(cons, getLocation());
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoFormula) {
			this.formula = ((GeoFormula) geo).formula;
			this.defined = geo.isDefined();
		} else {
			setUndefined();
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
		return latex;
	}

	@Override
	public void setContent(String content) {
		formula = content;
		try {
			latex = texSerializer.serialize(parser.parse(formula));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getContent() {
		return formula;
	}

	@Override
	public double getMinWidth() {
		return Math.max(minWidth, DEFAULT_WIDTH);
	}

	@Override
	public double getMinHeight() {
		return Math.max(minHeight, DEFAULT_HEIGHT);
	}

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinHeight(double minHeight) {
		this.minHeight = minHeight;
	}
}
