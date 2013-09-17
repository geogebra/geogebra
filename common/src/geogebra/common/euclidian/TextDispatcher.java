package geogebra.common.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoArcLength;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.Localization;
import geogebra.common.util.StringUtil;

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
	
	protected void setNoPointLoc(GeoText text, GPoint loc){
		text.setAbsoluteScreenLocActive(true);
		text.setAbsoluteScreenLoc(loc.x, loc.y);
	}

	public GeoElement[] getAreaText(GeoElement conic, GeoNumberValue area, GPoint loc) {
		// text
		GeoText text = createDynamicTextForMouseLoc("AreaOfA", conic, area, loc);
		if (conic.isLabelSet()) {
			area.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n.getCommand("Area"))
					+ conic.getLabelSimple()));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ conic.getLabelSimple()));
		}
		return  new GeoElement[] { text };
	}
	protected String descriptionPoints(String type, GeoPolygon poly) {
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
					descText.append(" + Name[" + points[i].getLabel(StringTemplate.defaultTemplate)
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
				return l10n.getPlain(type, "\" + Name[" + poly.getLabel(StringTemplate.defaultTemplate) + "] + \"");
			}
		}
		return l10n.getPlain(type,  descText.toString() );
	}
	
	/**
	 * Creates a text that shows a number value of geo.
	 */
	protected GeoText createDynamicText(String type, GeoElement object, GeoElementND value) {
		// create text that shows length
		try {
			
			// type might be eg "Area of %0" or "XXX %0 YYY"
			
			String descText;
			
			if (object.isGeoPolygon()) {
				descText = descriptionPoints(type, (GeoPolygon) object);
			} else {
				descText = l10n.getPlain(type, "\" + Name[" + object.getLabel(StringTemplate.defaultTemplate) + "] + \"");
			}
			
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel(StringTemplate.defaultTemplate);
	
			//checkZooming(); 
			
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
	protected GeoText createDynamicTextForMouseLoc(String type, GeoElement object, GeoElementND value, GPoint loc) {
		
		GeoText text = createDynamicText(type, object, value);
		if (text!=null){
			GeoPointND P = null;
			if (object.isRegion()){
				P = getPointForDynamicText((Region) object, loc);
			}else if (object.isPath()){
				P = getPointForDynamicText((Path) object, loc);
			}else{
				P = getPointForDynamicText(loc);
			}
			

			if (P!=null){
				((GeoElement) P).setAuxiliaryObject(true);
				P.setEuclidianVisible(false);
				P.updateRepaint();
				try {
					text.setStartPoint(P);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}else{
				setNoPointLoc(text, loc);
			}
			
			text.setBackgroundColor(GColor.WHITE);
			text.updateRepaint();	
		}
		
		return text;
		
	}
	
	protected GeoPointND getPointForDynamicText(Region object, GPoint loc){

		return view.getEuclidianController().createNewPoint(removeUnderscores(l10n.getPlain("Point")+ object.getLabel(StringTemplate.defaultTemplate)),
				false, 
				object, 
				view.toRealWorldCoordX(loc.x), view.toRealWorldCoordY(loc.y), 0, 
				false, false); 
	}
	
	protected GeoPointND getPointForDynamicText(Path object, GPoint loc){

		return view.getEuclidianController().createNewPoint(removeUnderscores(l10n.getPlain("Point")+ object.getLabel(StringTemplate.defaultTemplate)),
				false, 
				object, 
				view.toRealWorldCoordX(loc.x), view.toRealWorldCoordY(loc.y), 0, 
				false, false); 
	}
	
	protected GeoPointND getPointForDynamicText(GPoint loc){

		return null; 
	}
	
	/**
	 * Creates a text that shows the distance length between geoA and geoB at
	 * the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, GeoPointND textCorner,
			GeoNumeric length) {
				StringTemplate tpl = StringTemplate.defaultTemplate;
				// create text that shows length
				try {
					String strText = "";
					boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
					if (useLabels) {
						length.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n.getCommand("Distance"))
								//.toLowerCase(Locale.US)
								+ geoA.getLabel(tpl)
								+ geoB.getLabel(tpl)));
						// strText = "\"\\overline{\" + Name["+ geoA.getLabel()
						// + "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
						// + length.getLabel();
			
						// DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
						// or
						// DistanceAB=%0+%1+" \\, = \\, "+%2
						strText = l10n.getPlain("DistanceAB.LaTeX",
								"Name[" + geoA.getLabel(tpl) + "]",
								"Name[" + geoB.getLabel(tpl) + "]", length.getLabel(tpl));
						// Application.debug(strText);
						makeLabelNameVisible(geoA);
						makeLabelNameVisible(geoB);
						geoA.updateRepaint();
						geoB.updateRepaint();
					} else {
						length.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n.getCommand("Distance"))));
								//.toLowerCase(Locale.US)));
						strText = "\"\"" + length.getLabel(tpl);
					}
			
					// create dynamic text
					//checkZooming(); 
					
					GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText,
							true, true);
					if (useLabels) {
						text.setLabel(removeUnderscores(l10n.getPlain("Text")
								+ geoA.getLabel(tpl) + geoB.getLabel(tpl)));
						text.setLaTeX(useLabels, true);
					}
			
					text.setStartPoint(textCorner);
					text.setBackgroundColor(GColor.WHITE);
					text.updateRepaint();
					return text;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
	
	private static void makeLabelNameVisible(GeoElement geo){
		//make sure that name of the geo will be visible
		if (!geo.isLabelVisible()){
			if (geo.getLabelMode()!=GeoElement.LABEL_NAME_VALUE)
				geo.setLabelMode(GeoElement.LABEL_NAME);
			geo.setLabelVisible(true);
		}else{
			if (geo.getLabelMode()==GeoElement.LABEL_VALUE)
				geo.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		}
	}
	public GeoElement[] createCircumferenceText(GeoConicND conic, GPoint loc) {
		if (conic.isGeoConicPart()) {
			
			Construction cons = kernel.getConstruction();
			AlgoArcLength algo = new AlgoArcLength(cons, null, (GeoConicPart) conic);
			//cons.removeFromConstructionList(algo);
			GeoNumeric arcLength = algo.getArcLength();
			
			GeoText text = createDynamicTextForMouseLoc("ArcLengthOfA", conic, arcLength, loc);
				text.setLabel(removeUnderscores(l10n.getPlain("Text")
						+ conic.getLabelSimple()));
			GeoElement[] ret = { text };
			return ret;

			
		}

		// standard case: conic
		//checkZooming(); 
		
		GeoNumeric circumFerence = kernel.getAlgoDispatcher().Circumference(null, conic);

		// text
		GeoText text = createDynamicTextForMouseLoc("CircumferenceOfA", conic,
				circumFerence, loc);
		if (conic.isLabelSet()) {
			circumFerence.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n.getCommand(
					"Circumference"))
					+ conic.getLabel(StringTemplate.defaultTemplate)));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ conic.getLabel(StringTemplate.defaultTemplate)));
		}
		GeoElement[] ret = { text };
		return ret;
	}
	public GeoElement[] createPerimeterText(GeoPolygon[] poly, GPoint mouseLoc) {
		GeoNumeric perimeter = kernel.getAlgoDispatcher().Perimeter(null, poly[0]);
		
		// text
		GeoText text = createDynamicTextForMouseLoc("PerimeterOfA", poly[0],
				perimeter, mouseLoc);

		if (poly[0].isLabelSet()) {
			perimeter.setLabel(removeUnderscores(StringUtil.toLowerCase(l10n.getCommand("Perimeter"))
					+ poly[0].getLabelSimple()));
			text.setLabel(removeUnderscores(l10n.getPlain("Text")
					+ poly[0].getLabelSimple()));
		}
		GeoElement[] ret = { text };
		return ret;
	}
}
