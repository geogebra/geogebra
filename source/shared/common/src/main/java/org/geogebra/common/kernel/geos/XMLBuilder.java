package org.geogebra.common.kernel.geos;

import java.util.Locale;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

/**
 * Helper class for XML export
 *
 */
public class XMLBuilder {
	/**
	 * Appends visual tags to string builder
	 * 
	 * @param geo
	 *            geo
	 * 
	 * @param sb
	 *            string builder
	 * @param withLabelOffset
	 *            true to include label offsets
	 */
	protected static void getXMLvisualTags(GeoElement geo,
			final StringBuilder sb, final boolean withLabelOffset) {
		final boolean isDrawable = geo.isDrawable();

		// show object and/or label in EuclidianView
		// don't save this for simple dependent numbers (e.g. in spreadsheet)
		if (isDrawable) {
			sb.append("\t<show object=\"");
			sb.append(geo.isSetEuclidianVisible());
			sb.append("\" label=\"");
			sb.append(geo.getLabelVisible());
			sb.append("\"");

			// default:
			// showing in EV1
			// hidden in EV2
			int EVs = 0;

			if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN)) {
				// bit 0 is opposite to bit 1
				// 0 = showing
				// 1 = hidden
				EVs += 1; // bit 0
			}

			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN2)) {
				// 0 = hidden
				// 2 = showing
				EVs += 2; // bit 1
			}

			if (geo.hasDrawable3D()) {
				switch (geo.visibleInView3D) {
				case TRUE:
					EVs += 4;
					break;
				case FALSE:
					EVs += 8; // we have to store it to distinguish from not set
					break;
				case UNKNOWN:
					break;
				}

				switch (geo.getVisibleInViewForPlane()) {
				case TRUE:
					EVs += 16;
					break;
				case FALSE:
					EVs += 32; // we have to store it to distinguish from not
								// set
					break;
				case UNKNOWN:
					break;
				}
			}

			if (EVs != 0) {
				sb.append(" ev=\"");
				sb.append(EVs);
				sb.append("\"");
			}

			sb.append("/>\n");
		}

		if (geo.getShowTrimmedIntersectionLines()) {
			sb.append("\t<showTrimmed val=\"true\"/>\n");
		}

		// conditional visibility
		geo.getShowObjectConditionXML(sb);

		// if (isDrawable) removed - want to be able to color objects in
		// AlgebraView, Spreadsheet

		geo.appendObjectColorXML(sb);
		if (geo instanceof GeoEvaluatable
				&& ((GeoEvaluatable) geo).getTableColumn() >= 0) {
			sb.append("\t<tableview column=\"")
					.append(((GeoEvaluatable) geo).getTableColumn())
					.append("\" points=\"")
					.append(((GeoEvaluatable) geo).isPointsVisible())
					.append("\"/>\n");
		}

		if (geo.bgColor != null) {
			sb.append("\t<bgColor");
			XMLBuilder.appendRGB(sb, geo.bgColor);
			sb.append(" alpha=\"");
			sb.append(geo.bgColor.getAlpha());
			sb.append("\"/>\n");
		}

		// don't remove layer 0 information
		// we always need it in case an earlier element has higher layer eg 1
		if (isDrawable) {
			sb.append("\t<layer val=\"");
			sb.append(geo.getLayer());
			sb.append("\"/>\n");

			if (!Double.isNaN(geo.getOrdering())) {
				sb.append("\t<ordering val=\"");
				sb.append(geo.getOrdering());
				sb.append("\"/>\n");
			}
		}

		if (geo.isDefaultGeo()) {
			sb.append("\t<autocolor val=\"");
			sb.append(geo.isAutoColor());
			sb.append("\"/>\n");
		}

		if (withLabelOffset
				&& ((geo.labelOffsetX != 0) || (geo.labelOffsetY != 0))) {
			sb.append("\t<labelOffset x=\"");
			sb.append(geo.labelOffsetX);
			sb.append("\" y=\"");
			sb.append(geo.labelOffsetY);
			sb.append("\"/>\n");
		}

		if (geo.isDrawable()) {
			sb.append("\t<labelMode val=\"");
			sb.append(geo.labelMode);
			sb.append("\"/>\n");

			if (geo.getTooltipMode() != GeoElementND.TOOLTIP_ALGEBRAVIEW_SHOWING) {
				sb.append("\t<tooltipMode val=\"");
				sb.append(geo.getTooltipMode());
				sb.append("\"/>\n");
			}
		}

		// trace on or off
		if (geo.isTraceable()) {
			final Traceable t = (Traceable) geo;
			if (t.getTrace()) {
				sb.append("\t<trace val=\"true\"/>\n");
			}
		}

		// G.Sturr 2010-5-29
		// Get spreadsheet trace XML from the trace manager

		// trace to spreadsheet
		if (geo.getKernel().getApplication().isUsingFullGui()
				&& geo.isSpreadsheetTraceable()
				&& geo.getSpreadsheetTrace()) {
			sb.append(geo.getKernel().getApplication().getTraceXML(geo));
		}

		// decoration type
		if (geo.getDecorationType() != GeoElementND.DECORATION_NONE) {
			sb.append("\t<decoration type=\"");
			sb.append(geo.getDecorationType());
			sb.append("\"/>\n");
		}

		if (!geo.isAlgebraLabelVisible()) {
			sb.append("\t<algebra labelVisible=\"false\"/>\n");
		}
	}

	/**
	 * @param sb
	 *            string builder
	 * @param toStringMode
	 *            line/plane equation mode
	 * @param parameter
	 *            parameter name
	 */
	public static void appendEquationTypeLine(StringBuilder sb, int toStringMode,
			String parameter) {
		switch (toStringMode) {
		case LinearEquationRepresentable.Form.CONST_PARAMETRIC:
			sb.append("\t<eqnStyle style=\"parametric\" parameter=\"");
			sb.append(parameter);
			sb.append("\"/>\n");
			break;
		case LinearEquationRepresentable.Form.CONST_IMPLICIT:
			appendType(sb, "implicit");
			break;
		case LinearEquationRepresentable.Form.CONST_EXPLICIT:
			appendType(sb, "explicit");
			break;
		case LinearEquationRepresentable.Form.CONST_GENERAL:
			appendType(sb, "general");
			break;
		case LinearEquationRepresentable.Form.CONST_USER:
			appendType(sb, "user");
			break;
		case LinearEquationRepresentable.Form.CONST_IMPLICIT_NON_CANONICAL:
			// don't want anything here
			break;
		default:
			break;
		}

	}

	/**
	 * @param sb
	 *            xml builder
	 * @param string
	 *            equation style
	 */
	private static void appendType(StringBuilder sb, String string) {
		sb.append("\t<eqnStyle style=\"");
		sb.append(string);
		sb.append("\"/>\n");
	}

	/**
	 * @param sb
	 *            string builder
	 * @param toStringMode
	 *            conic/quadric equation mode
	 * @param parameter
	 *            parameter name
	 */
	public static void appendEquationTypeConic(StringBuilder sb,
			int toStringMode, String parameter) {
		// implicit or specific mode
		switch (toStringMode) {
		case QuadraticEquationRepresentable.Form.CONST_SPECIFIC:
			XMLBuilder.appendType(sb, "specific");
			break;
		case QuadraticEquationRepresentable.Form.CONST_EXPLICIT:
			XMLBuilder.appendType(sb, "explicit");
			break;
		case QuadraticEquationRepresentable.Form.CONST_USER:
			XMLBuilder.appendType(sb, "user");
			break;
		case QuadraticEquationRepresentable.Form.CONST_VERTEX:
			XMLBuilder.appendType(sb, "vertex");
			break;
		case QuadraticEquationRepresentable.Form.CONST_CONICFORM:
			XMLBuilder.appendType(sb, "conic");
			break;
		case QuadraticEquationRepresentable.Form.CONST_PARAMETRIC:
			sb.append("\t<eqnStyle style=\"parametric\"/>\n");
			break;

		default:
			XMLBuilder.appendType(sb, "implicit");
		}
	}

	/**
	 * @param sb
	 *            xml builder
	 * @param number
	 *            corner index
	 * @param corners
	 *            corners
	 * @param isAbsolute whether the position is in screen pixels
	 */
	public static void getCornerPointXML(StringBuilder sb, int number, GeoPointND[] corners,
			boolean isAbsolute) {
		if (corners[number] == null) {
			return;
		}
		sb.append("\t<startPoint number=\"");
		sb.append(number);
		sb.append("\"");

		if (corners[number].isAbsoluteStartPoint()) {
			sb.append(" x=\"").append(corners[number].getInhomX());
			sb.append("\" y=\"").append(corners[number].getInhomY());
			sb.append("\" z=\"1\"");
		} else {
			sb.append(" exp=\"");
			StringUtil.encodeXML(sb, corners[number].getLabel(StringTemplate.xmlTemplate));
			sb.append("\"");
		}
		if (isAbsolute) {
			sb.append(" absolute=\"true\"");
		}
		sb.append("/>\n");
	}

	/**
	 * Add the &lt;dimension&gt; tag
	 * 
	 * @param sb
	 *            XML builder
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public static void dimension(StringBuilder sb, String width, String height) {
		sb.append("\t<dimensions width=\"").append(width)
				.append("\" height=\"").append(height).append("\" />\n");
	}

	/**
	 * @param sb
	 *            string builder
	 * @param color
	 *            color
	 */
	public static void appendRGB(StringBuilder sb, GColor color) {
		sb.append(" r=\"");
		sb.append(color.getRed());
		sb.append("\"");
		sb.append(" g=\"");
		sb.append(color.getGreen());
		sb.append("\"");
		sb.append(" b=\"");
		sb.append(color.getBlue());
		sb.append("\"");
	}

	/**
	 * @param sb string builder
	 * @param point element with point properties
	 */
	public static void appendPointProperties(StringBuilder sb, PointProperties point) {
		// point size
		sb.append("\t<pointSize val=\"");
		sb.append(point.getPointSize());
		sb.append("\"/>\n");

		// point style
		if (point.getPointStyle() >= 0) {
			sb.append("\t<pointStyle val=\"");
			sb.append(point.getPointStyle());
			sb.append("\"/>\n");
		}
	}

	/**
	 * Appends properties related to the symbolic mode.
	 *
	 * @param builder string builder
	 * @param symbolicMode element with symbolic mode
	 * @param defaultMode the default symbolic mode
	 */
	public static void appendSymbolicMode(StringBuilder builder, HasSymbolicMode symbolicMode,
			boolean defaultMode) {
		boolean isSymbolicMode = symbolicMode.isSymbolicMode();
		if (isSymbolicMode && !defaultMode) {
			builder.append("\t<symbolic val=\"true\" />\n");
		} else if (!isSymbolicMode && defaultMode) {
			builder.append("\t<symbolic val=\"false\" />\n");
		}
	}

	/**
	 * @param sb
	 *            string builder
	 * @param angleStyle
	 *            angle style
	 * @param emphasizeRightAngle
	 *            whether to show special symbol for right angle
	 */
	public static void appendAngleStyle(StringBuilder sb,
			AngleStyle angleStyle, boolean emphasizeRightAngle) {
		sb.append("\t<angleStyle val=\"");
		sb.append(angleStyle.getXmlVal());
		sb.append("\"/>\n");
		if (!emphasizeRightAngle) {
			// only store emphasizeRightAngle if "false"
			sb.append("\t<emphasizeRightAngle val=\"false\"/>\n");
		}
	}

	/**
	 * Adds position and dimension for inline texts and formulas
	 * @param sb XML builder
	 * @param inline inline text or formula
	 */
	public static void appendPosition(StringBuilder sb, RectangleTransformable inline) {
		GPoint2D location = inline.getLocation();
		if (location != null) {
			sb.append("\t<startPoint x=\"");
			sb.append(location.getX());
			sb.append("\" y=\"");
			sb.append(location.getY());
			sb.append("\"/>\n");
		}
		double width;
		double height;
		boolean convertToRw = !(inline instanceof GeoAudio);
		if (convertToRw) {
			width = inline.getWidth() / inline.getKernel().getApplication()
					.getActiveEuclidianView().getXscale();
			height = inline.getHeight() / inline.getKernel().getApplication()
					.getActiveEuclidianView().getYscale();
		} else {
			width = inline.getWidth();
			height = inline.getHeight();
		}
		sb.append("\t<dimensions width=\"");
		sb.append(width);
		sb.append("\" height=\"");
		sb.append(height);
		if (convertToRw) {
			sb.append("\" unscaled=\"true");
		}
		sb.append("\" angle=\"");
		sb.append(inline.getAngle());
		sb.append("\"/>\n");
	}

	/**
	 * Adds border color for inline texts
	 * @param sb XML builder
	 * @param text inline text
	 * @param alignment vertical alignment
	 */
	public static void appendBorderAndAlignment(StringBuilder sb, GeoInline text,
			VerticalAlignment alignment) {
		GColor borderColor = text.getBorderColor();
		if (borderColor != null) {
			sb.append("\t<borderColor");
			appendRGB(sb, borderColor);
			sb.append("/>\n");
		}
		if (alignment != VerticalAlignment.TOP) {
			sb.append("\t<verticalAlign val=\"");
			sb.append(alignment.name().toLowerCase(Locale.ROOT));
			sb.append("\"/>\n");
		}
	}

	/**
	 * @param sb builder
	 * @param node parent node
	 * @param alignment alignment
	 */
	public static void appendParent(StringBuilder sb, GeoMindMapNode node,
			GeoMindMapNode.NodeAlignment alignment) {
		if (node != null) {
			sb.append("\t<parent val=\"");
			sb.append(node.getLabel(StringTemplate.xmlTemplate));
			sb.append("\" align=\"");
			sb.append(alignment.toString());
			sb.append("\"/>\n");
		}
	}

	/**
	 * @param sb builder
	 * @param verticalIncrement vertical increment
	 */
	public static void appendVerticalIncrement(StringBuilder sb, NumberValue verticalIncrement) {
		sb.append("<incrementY val=\"");
		StringUtil.encodeXML(sb, verticalIncrement.getLabel(StringTemplate.xmlTemplate));
		sb.append("\"/>");
	}
}
