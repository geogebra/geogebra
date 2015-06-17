package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoArcLength;
import org.geogebra.common.kernel.algos.AlgoClosestPoint;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class TextDispatcher {
	protected Localization l10n;
	protected Kernel kernel;
	private EuclidianView view;

	public TextDispatcher(Kernel kernel, EuclidianView view) {
		this.kernel = kernel;
		this.view = view;
		this.l10n = kernel.getLocalization();
	}

	protected static String removeUnderscores(String label) {
		// remove all indices
		return label.replaceAll("_", "");
	}

	protected void setNoPointLoc(GeoText text, GPoint loc) {
		text.setAbsoluteScreenLocActive(true);
		text.setAbsoluteScreenLoc(loc.x, loc.y);
	}

	public GeoElement[] getAreaText(GeoElement conic, GeoNumberValue area,
			GPoint loc) {
		// text
		GeoText text = createDynamicTextForMouseLoc("AreaOfA", conic, area, loc);
		if (conic.isLabelSet()) {
			if (!area.isLabelSet()) {
				area.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n
						.getCommand("Area")) + conic.getLabelSimple()));
			}
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ conic.getLabelSimple()));
		}
		return new GeoElement[] { text };
	}

	private String descriptionPoints(String type, GeoPolygon poly) {
		// build description text including point labels
		StringBuilder descText = new StringBuilder();

		// use points for polygon with static points (i.e. no list of points)
		GeoPointND[] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}

		if (points != null) {
			descText.append(" \"");
			boolean allLabelsSet = true;
			for (int i = 0; i < points.length; i++) {
				if (points[i].isLabelSet()) {
					descText.append(" + Name["
							+ points[i]
									.getLabel(StringTemplate.defaultTemplate)
							+ "]");
				} else {
					allLabelsSet = false;
					i = points.length;
				}
			}

			if (allLabelsSet) {
				descText.append(" + \"");
				for (int i = 0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else {
				return l10n.getPlain(
						type,
						"\" + Name["
								+ poly.getLabel(StringTemplate.defaultTemplate)
								+ "] + \"");
			}
		} else {
			return l10n.getPlain(
					type,
					"\" + Name["
							+ poly.getLabel(StringTemplate.defaultTemplate)
							+ "] + \"");
		}

		return l10n.getPlain(type, descText.toString());
	}

	/**
	 * Creates a text that shows a number value of geo.
	 */
	private GeoText createDynamicText(String type, GeoElement object,
			GeoElementND value) {
		// create text that shows length
		try {

			// type might be eg "Area of %0" or "XXX %0 YYY"

			String descText;

			if (object.isGeoPolygon()) {
				descText = descriptionPoints(type, (GeoPolygon) object);
			} else {
				descText = l10n
						.getPlain(
								type,
								"\" + Name["
										+ object.getLabel(StringTemplate.defaultTemplate)
										+ "] + \"");
			}

			// create dynamic text
			String dynText = "\"" + descText + " = \" + "
					+ value.getLabel(StringTemplate.defaultTemplate);

			// checkZooming();

			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText,
					true, true);
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a text that shows a number value of geo at the current mouse
	 * position.
	 */
	protected GeoText createDynamicTextForMouseLoc(String type,
			GeoElement object, GeoElementND value, GPoint loc) {

		GeoText text = createDynamicText(type, object, value);
		if (text != null) {
			GeoPointND P = null;
			if (object.isRegion()) {
				P = getPointForDynamicText((Region) object, loc);
			} else if (object.isPath()) {
				P = getPointForDynamicText((Path) object, loc);
			} else {
				P = getPointForDynamicText(loc);
			}

			if (P != null) {
				((GeoElement) P).setAuxiliaryObject(true);
				P.setEuclidianVisible(false);
				P.updateRepaint();
				try {
					text.setStartPoint(P);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				setNoPointLoc(text, loc);
			}

			text.checkVisibleIn3DViewNeeded();
			text.setBackgroundColor(GColor.WHITE);
			text.updateRepaint();
		}

		return text;

	}

	protected GeoPointND getPointForDynamicText(Region object, GPoint loc) {
		double rwx = 0, rwy = 0;
		if (loc != null) {
			rwx = view.toRealWorldCoordX(loc.x);
			rwy = view.toRealWorldCoordY(loc.y);
		} else if (object instanceof GeoPolygon) {
			GeoPointND[] pts = ((GeoPolygon) object).getPointsND();
			for (GeoPointND pt : pts) {
				rwx += pt.getCoordsInD2().getX();
				rwy += pt.getCoordsInD2().getY();
			}
			rwx = rwx / pts.length;
			rwy = rwy / pts.length;
		} else if (object instanceof GeoConicND) {
			rwx = ((GeoConicND) object).getTranslationVector().getX();
			rwy = ((GeoConicND) object).getTranslationVector().getY();
		}
		return view.getEuclidianController().createNewPoint(
				removeUnderscores(l10n.getPlain("Point")
						+ object.getLabel(StringTemplate.defaultTemplate)),
				false, object, rwx, rwy, 0, false, false);
	}

	protected GeoPointND getPointForDynamicText(Path object, GPoint loc) {

		return view
				.getEuclidianController()
				.getCompanion()
				.createNewPoint(
						removeUnderscores(l10n.getPlain("Point")
								+ object.getLabel(StringTemplate.defaultTemplate)),
						false, object, view.toRealWorldCoordX(loc.x),
						view.toRealWorldCoordY(loc.y), 0, false, false);
	}

	protected GeoPointND getPointForDynamicText(GPoint loc) {

		return null;
	}

	/**
	 * Creates a text that shows the distance length between geoA and geoB at
	 * the given startpoint.
	 */
	public GeoText createDistanceText(GeoElementND geoA, GeoElementND geoB,
			GeoPointND textCorner, GeoNumeric length) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		// create text that shows length
		try {
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {
				length.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n
						.getCommand("Distance"))
				// .toLowerCase(Locale.US)
						+ geoA.getLabel(tpl) + geoB.getLabel(tpl)));
				// strText = "\"\\overline{\" + Name["+ geoA.getLabel()
				// + "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
				// + length.getLabel();

				// DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
				// or
				// DistanceAB=%0+%1+" \\, = \\, "+%2
				strText = "Name[" + geoA.getLabel(tpl) + "] + Name["
						+ geoB.getLabel(tpl) + "] + \" = \" + "
						+ length.getLabel(tpl);
				// Application.debug(strText);
				makeLabelNameVisible(geoA);
				makeLabelNameVisible(geoB);
				geoA.updateRepaint();
				geoB.updateRepaint();
			} else {
				length.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n
						.getCommand("Distance"))));
				// .toLowerCase(Locale.US)));
				strText = "\"\"" + length.getLabel(tpl);
			}

			// create dynamic text
			// checkZooming();

			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText,
					true, true);
			if (useLabels) {
				text.setLabel(removeUnderscores(l10n.getPlain("Text")
						+ geoA.getLabel(tpl) + geoB.getLabel(tpl)));
			}

			text.checkVisibleIn3DViewNeeded();
			text.setStartPoint(textCorner);
			text.setBackgroundColor(GColor.WHITE);
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void makeLabelNameVisible(GeoElementND geo) {
		// make sure that name of the geo will be visible
		if (!geo.isLabelVisible()) {
			if (geo.getLabelMode() != GeoElement.LABEL_NAME_VALUE)
				geo.setLabelMode(GeoElement.LABEL_NAME);
			geo.setLabelVisible(true);
		} else {
			if (geo.getLabelMode() == GeoElement.LABEL_VALUE)
				geo.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		}
	}

	public GeoElement[] createCircumferenceText(GeoConicND conic, GPoint loc) {
		if (conic.isGeoConicPart()) {

			Construction cons = kernel.getConstruction();
			AlgoArcLength algo = new AlgoArcLength(cons, null,
					(GeoConicPart) conic);
			// cons.removeFromConstructionList(algo);
			GeoNumeric arcLength = algo.getArcLength();

			GeoText text = createDynamicTextForMouseLoc("ArcLengthOfA", conic,
					arcLength, loc);
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ conic.getLabelSimple()));
			GeoElement[] ret = { text };
			return ret;

		}

		// standard case: conic
		// checkZooming();

		GeoNumeric circumFerence = kernel.getAlgoDispatcher().Circumference(
				null, conic);

		// text
		GeoText text = createDynamicTextForMouseLoc("CircumferenceOfA", conic,
				circumFerence, loc);
		if (conic.isLabelSet()) {
			circumFerence.setLabel(removeUnderscores(StringUtil
					.toLowerCase(l10n.getCommand("Circumference"))
					+ conic.getLabel(StringTemplate.defaultTemplate)));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ conic.getLabel(StringTemplate.defaultTemplate)));
		}
		GeoElement[] ret = { text };
		return ret;
	}

	public GeoElement[] createPerimeterText(GeoPolygon poly, GPoint mouseLoc) {
		GeoNumeric perimeter = kernel.getAlgoDispatcher().Perimeter(null, poly);

		// text
		GeoText text = createDynamicTextForMouseLoc("PerimeterOfA", poly,
				perimeter, mouseLoc);

		if (poly.isLabelSet()) {
			perimeter.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n
					.getCommand("Perimeter")) + poly.getLabelSimple()));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ poly.getLabelSimple()));
		}
		text.checkVisibleIn3DViewNeeded();
		GeoElement[] ret = { text };
		return ret;
	}

	public GeoElement[] createSlopeText(GeoLine line, GPoint mouseLoc) {
		GeoNumeric slope;
		/*
		 * if (strLocale.equals("de_AT")) { slope = kernel.Slope("k", line); }
		 * else { slope = kernel.Slope("m", line); }
		 */

		String label = l10n.getPlain("ExplicitLineGradient");

		// make sure automatic naming goes m, m_1, m_2, ..., m_{10}, m_{11}
		// etc
		if (kernel.lookupLabel(label) != null) {
			int i = 1;
			while (kernel.lookupLabel(i > 9 ? label + "_{" + i + "}" : label
					+ "_" + i) != null) {
				i++;
			}
			label = i > 9 ? label + "_{" + i + "}" : label + "_" + i;
		}

		// checkZooming();

		slope = kernel.getAlgoDispatcher().Slope(label, line);

		// show value
		if (slope.isLabelVisible()) {
			slope.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		} else {
			slope.setLabelMode(GeoElement.LABEL_VALUE);
		}
		slope.setLabelVisible(true);
		slope.updateRepaint();
		GeoElement[] ret = { slope };
		return ret;
	}

	public GeoElement createDistanceText(GeoPointND point1, GeoPointND point2) {
		GeoNumeric length = kernel.getAlgoDispatcher().Distance(null, point1,
				point2);

		// set startpoint of text to midpoint of two points
		GeoPointND midPoint = MidpointForDistance(point1, point2);
		return this.createDistanceText(point1, point2, midPoint, length);
	}

	/**
	 * Creates Midpoint M = (P + Q)/2 without label (for use as e.g. start
	 * point)
	 */
	private final GeoPointND MidpointForDistance(GeoPointND P, GeoPointND Q) {

		return (GeoPointND) view.getEuclidianController().getCompanion()
				.midpoint(P, Q);
	}

	public GeoElement createDistanceText(GeoPointND point, GeoLineND line) {
		GeoNumeric length = kernel.getAlgoDispatcher().Distance(null, point,
				(GeoElement) line);

		// set startpoint of text to midpoint between point and line
		GeoPointND midPoint = MidpointForDistance(point,
				ClosestPoint(point, (Path) line));
		return this.createDistanceText(point, line, midPoint, length);
	}

	/**
	 * Returns the projected point of P on line g (or nearest for a Segment)
	 */
	final private GeoPointND ClosestPoint(GeoPointND P, Path g) {

		Construction cons = kernel.getConstruction();

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		AlgoClosestPoint cp = kernel.getAlgoDispatcher()
				.getNewAlgoClosestPoint(cons, g, P);

		cons.setSuppressLabelCreation(oldMacroMode);
		return cp.getP();
	}
}
