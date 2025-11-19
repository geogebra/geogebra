package org.geogebra.common.kernel.geos;

import java.util.Locale;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.io.XMLStringBuilder;
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
			final XMLStringBuilder sb, final boolean withLabelOffset) {
		final boolean isDrawable = geo.isDrawable();

		// show object and/or label in EuclidianView
		// don't save this for simple dependent numbers (e.g. in spreadsheet)
		if (isDrawable) {
			sb.startTag("show");
			sb.attr("object", geo.isSetEuclidianVisible());
			sb.attr("label", geo.getLabelVisible());

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
				sb.attr("ev", EVs);
			}

			sb.endTag();
		}

		if (geo.getShowTrimmedIntersectionLines()) {
			sb.startTag("showTrimmed").attr("val", true).endTag();
		}

		// conditional visibility
		geo.getShowObjectConditionXML(sb);

		// if (isDrawable) removed - want to be able to color objects in
		// AlgebraView, Spreadsheet

		geo.appendObjectColorXML(sb);
		if (geo instanceof GeoEvaluatable
				&& ((GeoEvaluatable) geo).getTableColumn() >= 0) {
			sb.startTag("tableview")
					.attr("column", ((GeoEvaluatable) geo).getTableColumn())
					.attr("points", ((GeoEvaluatable) geo).isPointsVisible())
					.endTag();
		}

		if (geo.bgColor != null) {
			sb.startTag("bgColor");
			XMLBuilder.appendRGB(sb, geo.bgColor);
			sb.attr("alpha", geo.bgColor.getAlpha());
			sb.endTag();
		}

		// don't remove layer 0 information
		// we always need it in case an earlier element has higher layer eg 1
		if (isDrawable) {
			sb.startTag("layer").attr("val", geo.getLayer()).endTag();

			if (!Double.isNaN(geo.getOrdering())) {
				sb.startTag("ordering").attr("val", geo.getOrdering()).endTag();
			}
		}

		if (geo.isDefaultGeo()) {
			sb.startTag("autocolor").attr("val", geo.isAutoColor()).endTag();
		}

		if (withLabelOffset
				&& ((geo.labelOffsetX != 0) || (geo.labelOffsetY != 0))) {
			sb.startTag("labelOffset");
			sb.attr("x", geo.labelOffsetX);
			sb.attr("y", geo.labelOffsetY);
			sb.endTag();
		}

		if (geo.isDrawable()) {
			sb.startTag("labelMode").attr("val", geo.labelMode).endTag();

			if (geo.getTooltipMode() != GeoElementND.TOOLTIP_ALGEBRAVIEW_SHOWING) {
				sb.startTag("tooltipMode").attr("val", geo.getTooltipMode()).endTag();
			}
		}

		// trace on or off
		if (geo.isTraceable()) {
			final Traceable t = (Traceable) geo;
			if (t.getTrace()) {
				sb.startTag("trace").attr("val", true).endTag();
			}
		}

		// Get spreadsheet trace XML from the trace manager

		// trace to spreadsheet
		if (geo.getKernel().getApplication().isUsingFullGui()
				&& geo.isSpreadsheetTraceable()
				&& geo.getSpreadsheetTrace()) {
			geo.getKernel().getApplication().getTraceXML(geo, sb);
		}

		// decoration type
		if (geo.getDecorationType() != GeoElementND.DECORATION_NONE) {
			sb.startTag("decoration").attr("type", geo.getDecorationType()).endTag();
		}

		if (!geo.isAlgebraLabelVisible()) {
			sb.startTag("algebra").attr("labelVisible", false).endTag();
		}
	}

	/**
	 * Add coordStyle tag to XML.
	 * @param sb XML string builder
	 * @param xmlName name of the coordinate type for XML
	 */
	public static void coordStyle(XMLStringBuilder sb, String xmlName) {
		sb.startTag("coordStyle").attrRaw("style", xmlName).endTag();
	}

	/**
	 * @param sb
	 *            string builder
	 * @param equationForm
	 *            line/plane equation mode
	 * @param parameter
	 *            parameter name
	 */
	public static void appendEquationTypeLine(XMLStringBuilder sb,
			LinearEquationRepresentable.Form equationForm,
			String parameter) {
		if (equationForm == null) {
			return;
		}
		switch (equationForm) {
		case PARAMETRIC:
			sb.startTag("eqnStyle").attrRaw("style", "parametric")
					.attr("parameter", parameter).endTag();
			break;
		case IMPLICIT:
			appendType(sb, "implicit");
			break;
		case EXPLICIT:
			appendType(sb, "explicit");
			break;
		case GENERAL:
			appendType(sb, "general");
			break;
		case USER:
			appendType(sb, "user");
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
	private static void appendType(XMLStringBuilder sb, String string) {
		sb.startTag("eqnStyle").attrRaw("style", string).endTag();
	}

	/**
	 * @param sb
	 *            string builder
	 * @param equationForm
	 *            conic/quadric equation mode
	 * @param parameter
	 *            parameter name
	 */
	public static void appendEquationTypeConic(XMLStringBuilder sb,
			QuadraticEquationRepresentable.Form equationForm, String parameter) {
		if (equationForm == null) { // null handled the same as default branch for compatibility
			XMLBuilder.appendType(sb, "implicit");
			return;
		}
		switch (equationForm) {
		case SPECIFIC:
			XMLBuilder.appendType(sb, "specific");
			break;
		case EXPLICIT:
			XMLBuilder.appendType(sb, "explicit");
			break;
		case USER:
			XMLBuilder.appendType(sb, "user");
			break;
		case VERTEX:
			XMLBuilder.appendType(sb, "vertex");
			break;
		case CONICFORM:
			XMLBuilder.appendType(sb, "conic");
			break;
		case PARAMETRIC:
			XMLBuilder.appendType(sb, "parametric");
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
	public static void getCornerPointXML(XMLStringBuilder sb, int number, GeoPointND[] corners,
			boolean isAbsolute) {
		if (corners[number] == null) {
			return;
		}
		sb.startTag("startPoint").attr("number", number);

		if (corners[number].isAbsoluteStartPoint()) {
			sb.attr("x", corners[number].getInhomX());
			sb.attr("y", corners[number].getInhomY());
			sb.attr("z", 1);
		} else {
			sb.attr("exp", corners[number].getLabel(StringTemplate.xmlTemplate));
		}
		if (isAbsolute) {
			sb.attr("absolute", true);
		}
		sb.endTag();
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
	public static void dimension(XMLStringBuilder sb, String width, String height) {
		sb.startTag("dimensions").attr("width", width)
				.attr("height", height).endTag();
	}

	/**
	 * @param sb
	 *            string builder
	 * @param color
	 *            color
	 */
	public static void appendRGB(XMLStringBuilder sb, GColor color) {
		sb.attr("r", color.getRed());
		sb.attr("g", color.getGreen());
		sb.attr("b", color.getBlue());
	}

	/**
	 * @param sb string builder
	 * @param point element with point properties
	 */
	public static void appendPointProperties(XMLStringBuilder sb, PointProperties point) {
		// point size
		sb.startTag("pointSize").attr("val", point.getPointSize()).endTag();

		// point style
		if (point.getPointStyle() >= 0) {
			sb.startTag("pointStyle").attr("val", point.getPointStyle()).endTag();
		}
	}

	/**
	 * Appends properties related to the symbolic mode.
	 *
	 * @param builder string builder
	 * @param symbolicMode element with symbolic mode
	 * @param defaultMode the default symbolic mode
	 */
	public static void appendSymbolicMode(XMLStringBuilder builder, HasSymbolicMode symbolicMode,
			boolean defaultMode) {
		boolean isSymbolicMode = symbolicMode.isSymbolicMode();
		if (isSymbolicMode != defaultMode) {
			builder.startTag("symbolic").attr("val", isSymbolicMode).endTag();
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
	public static void appendAngleStyle(XMLStringBuilder sb,
			AngleStyle angleStyle, boolean emphasizeRightAngle) {
		sb.startTag("angleStyle").attr("val", angleStyle.getXmlVal()).endTag();
		if (!emphasizeRightAngle) {
			// only store emphasizeRightAngle if "false"
			sb.startTag("emphasizeRightAngle").attr("val", false).endTag();
		}
	}

	/**
	 * Adds position and dimension for inline texts and formulas
	 * @param sb XML builder
	 * @param inline inline text or formula
	 */
	public static void appendPosition(XMLStringBuilder sb, RectangleTransformable inline) {
		GPoint2D location = inline.getLocation();
		if (location != null) {
			sb.startTag("startPoint");
			sb.attr("x", location.getX());
			sb.attr("y", location.getY());
			sb.endTag();
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
		sb.startTag("dimensions");
		sb.attr("width", width);
		sb.attr("height", height);
		if (convertToRw) {
			sb.attr("unscaled", true);
		}
		sb.attr("angle", inline.getAngle());
		sb.endTag();
	}

	/**
	 * Adds border color for inline texts
	 * @param sb XML builder
	 * @param text inline text
	 * @param alignment vertical alignment
	 */
	public static void appendBorderAndAlignment(XMLStringBuilder sb, GeoInline text,
			VerticalAlignment alignment) {
		GColor borderColor = text.getBorderColor();
		if (borderColor != null) {
			sb.startTag("borderColor");
			appendRGB(sb, borderColor);
			sb.endTag();
		}
		if (alignment != VerticalAlignment.TOP) {
			sb.startTag("verticalAlign");
			sb.attr("val", alignment.name().toLowerCase(Locale.ROOT));
			sb.endTag();
		}
	}

	/**
	 * @param sb builder
	 * @param node parent node
	 * @param alignment alignment
	 */
	public static void appendParent(XMLStringBuilder sb, GeoMindMapNode node,
			GeoMindMapNode.NodeAlignment alignment) {
		if (node != null) {
			sb.startTag("parent")
					.attr("val", node.getLabel(StringTemplate.xmlTemplate))
					.attr("align", alignment.toString()).endTag();
		}
	}

	/**
	 * @param sb builder
	 * @param verticalIncrement vertical increment
	 */
	public static void appendVerticalIncrement(XMLStringBuilder sb, NumberValue verticalIncrement) {
		sb.startTag("incrementY")
				.attr("val", verticalIncrement.getLabel(StringTemplate.xmlTemplate))
				.endTag();
	}
}
