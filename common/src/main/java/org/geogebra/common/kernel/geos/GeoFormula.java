package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

public class GeoFormula extends GeoElement implements GeoInline, Translateable, PointRotateable {

	public static final int DEFAULT_WIDTH = 250;
	public static final int DEFAULT_HEIGHT = 48;

	private static final Parser parser = new Parser(new MetaModel());
	private static final TeXSerializer texSerializer = new TeXSerializer();

	private GPoint2D position;
	private boolean defined = true;
	private String formula;

	private double width;
	private double height;

	private double minWidth;
	private double minHeight;

	private double angle = 0;
	private String latex = "";

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 * @param location initial location in RW coordinates
	 */
	public GeoFormula(Construction c, GPoint2D location) {
		super(c);
		this.position = location;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.FORMULA;
	}

	@Override
	public GeoElement copy() {
		GeoFormula copy = new GeoFormula(cons, position);
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
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isAlgebraViewEditable() {
		return false;
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<content val=\"");
		StringUtil.encodeXML(sb, formula);
		sb.append("\"/>\n");
		XMLBuilder.appendPosition(sb, this);
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public GPoint2D getLocation() {
		return position;
	}

	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void setLocation(GPoint2D startPoint) {
		this.position = startPoint;
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

	@Override
	public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		position.setLocation(position.getX() + v.getX(), position.getY() + v.getY());
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		angle -= r.getDouble();
		GeoInlineText.rotate(position, r, S);
	}

	@Override
	public void rotate(NumberValue r) {
		angle -= r.getDouble();
	}

	/**
	 * @return formula, parseable by editor
	 */
	public String getContent() {
		return formula;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
}
