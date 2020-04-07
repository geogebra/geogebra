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
import com.himamis.retex.editor.share.util.Unicode;

public class GeoFormula extends GeoElement implements GeoInline, Translateable, PointRotateable {
	private static final String[] STRINGS = {"e^(" + Unicode.PI_STRING + " i)+1=0", "E=mc^2",
			"nroot(3,3)>nroot(5,5)"};
	private static Parser parser = new Parser(new MetaModel());
	private static TeXSerializer texSerializer = new TeXSerializer();

	private GPoint2D position;
	private boolean defined = true;
	private String formula;
	private double width = 250;
	private double height = 48;
	private double angle = 0;
	private String latex;

	/**
	 * Creates new GeoElement for given construction
	 *
	 * @param c Construction
	 * @param initPoint initial location in RW coordinates
	 */
	public GeoFormula(Construction c, GPoint2D initPoint) {
		super(c);
		setContent(STRINGS[(int) (Math.random() * STRINGS.length)]);
		this.position = initPoint;
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
	public ValueType getValueType() {
		return ValueType.TEXT;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
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
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
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
	public double getMinHeight() {
		return 50;
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
}
