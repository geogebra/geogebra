/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.geos;

import geogebra.common.awt.GFont;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentText;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoSequence;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;



/**
 * Geometrical element for holding text
 *
 */
public class GeoText extends GeoElement implements Locateable,
		AbsoluteScreenLocateable, TextValue, TextProperties, SpreadsheetTraceable {

	private String str;
	private GeoPointND startPoint; // location of Text on screen
	private boolean isLaTeX; // text is a LaTeX formula
	// corners of the text Michael Borcherds 2007-11-26, see AlgoTextCorner
	private GRectangle2D boundingBox;
	private boolean needsUpdatedBoundingBox = false;

	// font options
	private boolean serifFont;
	private int fontStyle;
	//private int fontSize = 0; // must be zero, as that is the value NOT saved to
	//							// XML
	
	// changed to a multiplier from ggb42
	private double fontSizeD = 1; 
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	
	/** index of exra small  modifier */
	final public static int FONTSIZE_EXTRA_SMALL = 0;
	/** index of very small  modifier */
	final public static int FONTSIZE_VERY_SMALL = 1;
	/** index of small  modifier */
	final public static int FONTSIZE_SMALL = 2;
	/** index of medium  modifier */
	final public static int FONTSIZE_MEDIUM = 3;
	/** index of large  modifier */
	final public static int FONTSIZE_LARGE = 4;
	/** index of very large  modifier */
	final public static int FONTSIZE_VERY_LARGE = 5;
	/** index of exra large  modifier */
	final public static int FONTSIZE_EXTRA_LARGE = 6;

	// for absolute screen location
	private boolean hasAbsoluteScreenLocation = false;

	/**
	 * Creates new text
	 * 
	 * @param c
	 *            construction
	 */
	public GeoText(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		// don't show in algebra view
		// setAlgebraVisible(false);
		setAuxiliaryObject(true);
	}
	/**
	 * Creates new geo text
	 * @param c constructino
	 * @param label label
	 * @param value value of thisforrmula
	 */
	public GeoText(Construction c, String label, String value) {
		this(c, value);
		setLabel(label);
	}

	/**
	 * Creates new GeoText
	 * @param c construction
	 * @param value text
	 */
	public GeoText(Construction c, String value) {
		this(c);
		setTextString(value);
	}

	/**
	 * Copy constructor
	 * @param text text to copy
	 */
	public GeoText(GeoText text) {
		this(text.cons);
		set(text);
	}

	@Override
	public GeoElement copy() {
		return new GeoText(this);
	}

	@Override
	public void set(GeoElement geo) {
		if(!geo.isGeoText())
			return;
		GeoText gt = (GeoText) geo;
		// macro output: don't set start point
		// but update to desired number format
		if (cons != geo.cons && isAlgoMacroOutput()) {
			if (!useSignificantFigures)
				gt.setPrintDecimals(
						printDecimals > -1 ? printDecimals : kernel
								.getPrintDecimals(), true);
			else
				gt.setPrintFigures(
						printFigures > -1 ? printFigures : kernel
								.getPrintFigures(), true);
			str = gt.str;
			isLaTeX = gt.isLaTeX;
			updateTemplate();
			return;
		}

		str = gt.str;
		isLaTeX = gt.isLaTeX;

		// needed for Corner[Element[text
		boundingBox = gt.getBoundingBox();

		try {
			if (gt.startPoint != null) {
				if (gt.hasAbsoluteLocation()) {
					// create new location point
					setStartPoint(gt.startPoint.copy());
				} else {
					// take existing location point
					setStartPoint(gt.startPoint);
				}
			}
		} catch (CircularDefinitionException e) {
			App
					.debug("set GeoText: CircularDefinitionException");
		}
		updateTemplate();
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		if (!geo.isGeoText())
			return;

		GeoText text = (GeoText) geo;
		serifFont = text.serifFont;
		fontStyle = text.fontStyle;
		fontSizeD = text.fontSizeD;
		printDecimals = text.printDecimals;
		printFigures = text.printFigures;
		useSignificantFigures = text.useSignificantFigures;
		updateTemplate();
	}

	/**
	 * Sets the text contained in this object
	 * @param text2 text
	 */
	final public void setTextString(String text2) {
		String text = text2;
		// Michael Borcherds 2008-05-11
		// remove trailing linefeeds (FreeHEP EMF export doesn't like them)
		while (text.length() > 1 && text.charAt(text.length() - 1) == '\n') {
			text = text.substring(0, text.length() - 1);
		}

		if (isLaTeX) {
			// TODO: check greek letters of latex string
			str = StringUtil.toLaTeXString(text, false);
		} else {
			// replace "\\n" with a proper newline
			// for eg Text["Hello\\nWorld",(1,1)]
			str = text.replaceAll("\\\\\\\\n", "\n");
		}

	}
	/**
	 * Returns the string wrapped in this text
	 * @return the string wrapped in this text
	 */
	final public String getTextString() {
		return str;
	}

	/**
	 * Sets the startpoint without performing any checks. This is needed for
	 * macros.
	 */
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;
	}

	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setStartPoint(p);
	}

	public void removeStartPoint(GeoPointND p) {
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch (Exception e) {
				//cannot happen
			}
		}
	}

	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {
		// don't allow this if it's eg Text["hello",(2,3)]
		if (alwaysFixed)
			return;
		// macro output uses initStartPoint() only
		// if (isAlgoMacroOutput()) return;

		// check for circular definition
		if (isParentOf((GeoElement) p))
			throw new CircularDefinitionException();

		// remove old dependencies
		if (startPoint != null)
			startPoint.getLocateableList().unregisterLocateable(this);

		// set new location
		if (p == null) {
			if (startPoint != null) // copy old startPoint
				startPoint = startPoint.copy();
			else
				startPoint = null;
			labelOffsetX = 0;
			labelOffsetY = 0;
		} else {
			startPoint = p;
			// add new dependencies
			startPoint.getLocateableList().registerLocateable(this);

			// absolute screen position should be deactivated
			setAbsoluteScreenLocActive(false);
		}
	}

	@Override
	public void doRemove() {
		super.doRemove();
		// tell startPoint
		if (startPoint != null)
			startPoint.getLocateableList().unregisterLocateable(this);
	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public GeoPointND[] getStartPoints() {
		if (startPoint == null)
			return null;

		GeoPointND[] ret = new GeoPointND[1];
		ret[0] = startPoint;
		return ret;
	}

	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	public void setWaitForStartPoint() {
		// this can be ignored for a text
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}

	@Override
	public void update() {

		super.update();

		// if (needsUpdatedBoundingBox) {
		// kernel.notifyUpdate(this);
		// }

	}

	/**
	 * always returns true
	 */
	@Override
	public boolean isDefined() {
		return str != null && (startPoint == null || startPoint.isDefined());
	}

	/**
	 * doesn't do anything
	 */
	@Override
	public void setUndefined() {
		str = null;
	}

	@Override
	public String toValueString(StringTemplate tpl1) {
		// http://www.geogebra.org/forum/viewtopic.php?f=8&t=26139
		return str == null ? "" : str;
	}

	/**
	 * Returns quoted text value string.
	 */
	@Override
	public String toOutputValueString(StringTemplate tpl1) {
		StringType printForm = tpl1.getStringType();

		sbToString.setLength(0);
		if (printForm.equals(StringType.LATEX))
			sbToString.append("\\text{``");
		else
			sbToString.append('\"');
		if (str != null)
			sbToString.append(str);
		if (printForm.equals(StringType.LATEX))
			sbToString.append("''}");
		else
			sbToString.append('\"');
		return sbToString.toString();
	}

	@Override
	public String toString(StringTemplate tpl1) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append('\"');
		if (str != null)
			sbToString.append(str);
		sbToString.append('\"');
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(80);

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public String getClassName() {
		return "GeoText";
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXT;
	}

	@Override
	public String getTypeString() {
		return "Text";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TEXT;
	}

	@Override
	public boolean isMoveable() {

		if (alwaysFixed)
			return false;

		return !isFixed();
	}

	/**
	 * used for eg Text["text",(1,2)] to stop it being editable
	 */
	public boolean isTextCommand = false;

	/**
	 * 
	 * @param isCommand
	 *            new value of isTextCommand
	 */
	public void setIsTextCommand(boolean isCommand) {
		this.isTextCommand = isCommand;
	}

	@Override
	public boolean isTextCommand() {

		// check for eg If[ a==1 , "hello", "bye"] first
		if (!(getParentAlgorithm() == null)
				&& !(getParentAlgorithm() instanceof AlgoDependentText))
			return true;

		return isTextCommand;
	}

	/**
	 * @return true if this text was produced by algo with LaTeX output
	 */
	public boolean isLaTeXTextCommand() {

		if (!isTextCommand || getParentAlgorithm() == null)
			return false;

		return getParentAlgorithm().isLaTeXTextCommand();
	}

	@Override
	public void setAlgoMacroOutput(boolean isAlgoMacroOutput) {
		super.setAlgoMacroOutput(true);
		setIsTextCommand(true);
	}

	/**
	 * used for eg Text["text",(1,2)] to stop it being draggable
	 */
	boolean alwaysFixed = false;
	private StringTemplate tpl = StringTemplate.defaultTemplate;

	/**
	 * 
	 * @param alwaysFixed
	 *            flag to prevent movement of Text["whee",(1,2)]
	 */
	public void setAlwaysFixed(boolean alwaysFixed) {
		this.alwaysFixed = alwaysFixed;
	}

	@Override
	public boolean isFixable() {

		// workaround for Text["text",(1,2)]
		if (alwaysFixed)
			return false;

		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}

	@Override
	public boolean isTextValue() {
		return true;
	}

	@Override
	public boolean isGeoText() {
		return true;
	}

	public MyStringBuffer getText() {
		if (str != null)
			return new MyStringBuffer(kernel, str);
		return new MyStringBuffer(kernel, "");
	}

	/**
	 * save object in XML format
	 */
	@Override
	public final void getXML(StringBuilder sb) {

		// an independent text needs to add
		// its expression itself
		// e.g. text0 = "Circle"
		if (isIndependent() && getDefaultGeoType() < 0) {
			sb.append("<expression");
			sb.append(" label=\"");
			StringUtil.encodeXML(sb, label);
			sb.append("\" exp=\"");
			StringUtil
					.encodeXML(sb, toOutputValueString(StringTemplate.xmlTemplate));
			// expression
			sb.append("\"/>\n");
		}

		sb.append("<element");
		sb.append(" type=\"text\"");
		sb.append(" label=\"");
		StringUtil.encodeXML(sb, label);
		if (getDefaultGeoType() >= 0) {
			sb.append("\" default=\"");
			sb.append(getDefaultGeoType());
		}
		sb.append("\">\n");
		getXMLtags(sb);
		sb.append("</element>\n");

	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		getXMLvisualTags(sb, false);

		getXMLfixedTag(sb);

		if (isLaTeX) {
			sb.append("\t<isLaTeX val=\"true\"/>\n");
		}

		appendFontTag(sb, serifFont, fontSizeD, fontStyle, isLaTeX, app);

		// print decimals
		if (printDecimals >= 0 && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}

		// print significant figures
		if (printFigures >= 0 && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}

		getBreakpointXML(sb);

		getAuxiliaryXML(sb);

		// store location of text (and possible labelOffset)
		sb.append(getXMLlocation());
		getScriptTags(sb);

	}

	/**
	 * Returns startPoint of this text in XML notation.
	 */
	private String getXMLlocation() {
		StringBuilder sb = new StringBuilder();

		if (hasAbsoluteScreenLocation) {
			sb.append("\t<absoluteScreenLocation ");
			sb.append(" x=\"");
			sb.append(labelOffsetX);
			sb.append("\"");
			sb.append(" y=\"");
			sb.append(labelOffsetY);
			sb.append("\"");
			sb.append("/>\n");
		} else {
			// location of text
			if (startPoint != null) {
				sb.append(startPoint.getStartPointXML());

				if (labelOffsetX != 0 || labelOffsetY != 0) {
					sb.append("\t<labelOffset");
					sb.append(" x=\"");
					sb.append(labelOffsetX);
					sb.append("\"");
					sb.append(" y=\"");
					sb.append(labelOffsetY);
					sb.append("\"");
					sb.append("/>\n");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void setAllVisualProperties(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualProperties(geo, keepAdvanced);

		// start point of text
		if (geo instanceof GeoText) {
			GeoText text = (GeoText) geo;
			setSameLocation(text);
			setLaTeX(text.isLaTeX, true);
		}
	}

	private void setSameLocation(GeoText text) {
		if (text.hasAbsoluteScreenLocation) {
			setAbsoluteScreenLocActive(true);
			setAbsoluteScreenLoc(text.getAbsoluteScreenLocX(),
					text.getAbsoluteScreenLocY());
		} else {
			if (text.startPoint != null) {
				try {
					setStartPoint(text.startPoint);
				} catch (Exception e) {
					//Circular definition, do nothing
				}
			}
		}
	}
	/**
	 * Returns true for LaTeX texts
	 * @return true for LaTeX texts
	 */
	public boolean isLaTeX() {
		return isLaTeX;
	}

	/**
	 * Changes type of this object to math rendering type (LaTeX or MATHML)
	 * @param b true for math rendering
	 * @param updateParentAlgo when true, parent is recomputed
	 */
	public void setLaTeX(boolean b, boolean updateParentAlgo) {
		if (b == isLaTeX)
			return;

		isLaTeX = b;
		updateTemplate();
		// update parent algorithm if it's not a sequence
		if (updateParentAlgo) {
			AlgoElement parent = getParentAlgorithm();
			if (parent != null && !(parent instanceof AlgoSequence)) {
				parent.update();
			}
		}
	}

	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
	}

	public int getAbsoluteScreenLocX() {
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {
		return labelOffsetY;
	}

	public double getRealWorldLocX() {
		if (startPoint == null)
			return 0;
		return startPoint.getInhomCoords().getX();
	}

	public double getRealWorldLocY() {
		if (startPoint == null)
			return 0;
		return startPoint.getInhomCoords().getY();
	}

	public void setRealWorldLoc(double x, double y) {
		GeoPoint loc = (GeoPoint) getStartPoint();
		if (loc == null) {
			loc = new GeoPoint(cons);
			try {
				setStartPoint(loc);
			} catch (Exception e) {
				//circular definition, do nothing
			}
		}
		loc.setCoords(x, y, 1.0);
		labelOffsetX = 0;
		labelOffsetY = 0;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		if (flag == hasAbsoluteScreenLocation)
			return;

		hasAbsoluteScreenLocation = flag;
		if (flag) {
			// remove startpoint
			if (startPoint != null) {
				startPoint.getLocateableList().unregisterLocateable(this);
				startPoint = null;
			}
		} else {
			labelOffsetX = 0;
			labelOffsetY = 0;
		}
	}

	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	//public int getFontSize() {
	//	return fontSize;
	//}
	
	public double getFontSizeMultiplier() {
		return fontSizeD;
	}
	
	/**
	 * 
	 * @param index index of size in the settings
	 * @return additive size modifier
	 */
	public static double getRelativeFontSize(int index) {
		switch (index) {
		case FONTSIZE_EXTRA_SMALL: // extra small
			return 0.5;
		case FONTSIZE_VERY_SMALL: // very small
			return 0.7;
		case FONTSIZE_SMALL: // small
			return 1;
		default:
		case FONTSIZE_MEDIUM: // medium
			return 1.4;
		case FONTSIZE_LARGE: // large
			return 2;
		case FONTSIZE_VERY_LARGE: // very large
			return 4;
		case FONTSIZE_EXTRA_LARGE: // extra large
			return 8;
		}
	}

	/**
	 * 
	 * @param d font size  modifier 
	 * @return corresponding index
	 */
	public static int getFontSizeIndex(double d) {
		if (d <= 0.5) {
			return FONTSIZE_EXTRA_SMALL;
		}
		if (d <= 0.8) {
			return FONTSIZE_VERY_SMALL;
		}
		if (d <= 1) {
			return FONTSIZE_SMALL;
		}
		if (d <= 1.5) {
			return FONTSIZE_MEDIUM;
		}
		if (d <= 2) {
			return FONTSIZE_LARGE;
		}
		if (d <= 4) {
			return FONTSIZE_VERY_LARGE;
		}
		return FONTSIZE_EXTRA_LARGE;

	}

	//public void setFontSize(int size) {
	//	fontSize = size;
	//}

	public void setFontSizeMultiplier(double d) {
		fontSizeD = d;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;

		// needed for eg \sqrt in latex
		if ((fontStyle & GFont.BOLD) != 0)
			lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS * 2;
		else
			lineThickness = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;

	}

	final public int getPrintDecimals() {
		return printDecimals;
	}

	final public int getPrintFigures() {
		return printFigures;
	}

	public void setPrintDecimals(int printDecimals, boolean update) {
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printDecimals = printDecimals;
			printFigures = -1;
			useSignificantFigures = false;
			updateTemplate();
			algo.update();
		}
	}

	public void setPrintFigures(int printFigures, boolean update) {
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printFigures = printFigures;
			printDecimals = -1;
			useSignificantFigures = true;
			updateTemplate();
			algo.update();
		}
	}

	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}

	public boolean isSerifFont() {
		return serifFont;
	}

	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}

	/**
	 * @param result point for storing result
	 * @param n index of corner (1 for lower left, then anticlockwise)
	 */
	public void calculateCornerPoint(GeoPoint result, int n) {
		// adapted from GeoImage by Michael Borcherds 2007-11-26
		if (hasAbsoluteScreenLocation || boundingBox == null) {
			result.setUndefined();
			return;
		}

		switch (n) {
		case 4: // top left
			result.setCoords(boundingBox.getX(), boundingBox.getY(), 1.0);
			break;

		case 3: // top right
			result.setCoords(boundingBox.getX() + boundingBox.getWidth(),
					boundingBox.getY(), 1.0);
			break;

		case 2: // bottom right
			result.setCoords(boundingBox.getX() + boundingBox.getWidth(),
					boundingBox.getY() + boundingBox.getHeight(), 1.0);
			break;

		case 1: // bottom left
			result.setCoords(boundingBox.getX(), boundingBox.getY()
					+ boundingBox.getHeight(), 1.0);
			break;

		default:
			result.setUndefined();
		}
	}

	/**
	 * @return Bounding box of this text
	 */
	public GRectangle2D getBoundingBox() {
		return boundingBox;
	}

	/**
	 * @param x x coord
	 * @param y y coord
	 * @param w width
	 * @param h height
	 */
	public void setBoundingBox(double x, double y, double w, double h) {

		boolean firstTime = boundingBox == null;
		if (firstTime) {
			boundingBox = geogebra.common.factories.AwtFactory.prototype
					.newRectangle2D();
		}

		boundingBox.setRect(x, y, w, h);
	}

	/**
	 * @return tue if bounding box is not  correct anymore
	 */
	public final boolean isNeedsUpdatedBoundingBox() {
		return needsUpdatedBoundingBox;
	}

	/**
	 * @param needsUpdatedBoundingBox true to make sure this object upates itself
	 */
	public final void setNeedsUpdatedBoundingBox(boolean needsUpdatedBoundingBox) {
		this.needsUpdatedBoundingBox = needsUpdatedBoundingBox;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (str == null)
			return false;
		if (geo.isGeoText())
			return str.equals(((GeoText) geo).str);
		return false;
	}

	@Override
	public void setZero() {
		str = "";
	}

	/**
	 * Returns a comparator for GeoText objects. If equal, doesn't return zero
	 * (otherwise TreeSet deletes duplicates)
	 * @return comparator
	 */
	public static Comparator<GeoText> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<GeoText>() {
				public int compare(GeoText itemA, GeoText itemB) {
					
					NormalizerMinimal noramlizer = itemA.getKernel().getApplication().getNormalizer();
					
					// remove accents etc
					String strA = noramlizer.transform(itemA.getTextString());
					String strB = noramlizer.transform(itemB.getTextString());

					// do comparison without accents etc
					int comp = strA.compareTo(strB);
					
					if (comp == 0) {
						// try compare with accents
						comp = itemA.getTextString().compareTo(itemB.getTextString());
					}

					if (comp == 0)
						// if we return 0 for equal strings, the TreeSet deletes
						// the equal one
						return itemA.getConstructionIndex() > itemB
								.getConstructionIndex() ? -1 : 1;
					return comp;
				}
			};
		}

		return comparator;
	}

	private static Comparator<GeoText> comparator;

	private void updateTemplate() {
		StringType type = isLaTeX ? app.getFormulaRenderingType()
				: StringType.GEOGEBRA;

		if (useSignificantFigures() && printFigures > -1) {
			tpl = StringTemplate.printFigures(type, printFigures, false);
		} else if (!useSignificantFigures && printDecimals > -1) {
			tpl = StringTemplate.printDecimals(type, printDecimals, false);
		} else {
			tpl = StringTemplate.get(type);
		}
	}

	public boolean isAlwaysFixed() {
		return alwaysFixed;
	}

	@Override
	final public boolean isAuxiliaryObjectByDefault() {
		return true;
	}

	public boolean justFontSize() {
		return false;
	}

	@Override
	public boolean isRedefineable() {
		return true;
	}

	@Override
	public boolean isLaTeXDrawableGeo(String latexStr) {

		return isLaTeX()
				|| (getTextString() != null && getTextString().indexOf('_') != -1);
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public boolean hasBackgroundColor() {
		return true;
	}

	/**
	 * String template; contains both string type and precision
	 * 
	 * @return template
	 */
	public StringTemplate getStringTemplate() {
		return tpl;
	}
	
	
	private static enum SpreadsheetTraceableCase {
		SPREADSHEET_TRACEABLE_NOT_TESTED,
		SPREADSHEET_TRACEABLE_TRUE,
		SPREADSHEET_TRACEABLE_FALSE
	}
	
	
	private SpreadsheetTraceableCase spreadsheetTraceableCase = SpreadsheetTraceableCase.SPREADSHEET_TRACEABLE_NOT_TESTED;
	private ExpressionValue spreadsheetTraceableValue;
	private ExpressionNode spreadsheetTraceableLeftTree;
	
	/**
	 * set objects for trace to spreadsheet
	 * @param leftTree tree for column heading
	 * @param value value to trace
	 */
	public void setSpreadsheetTraceable(ExpressionNode leftTree, ExpressionValue value){
		this.spreadsheetTraceableLeftTree = leftTree;
		this.spreadsheetTraceableValue = value;
	}
	
	/**
	 * init case for spreadsheet traceable case
	 */
	public void initSpreadsheetTraceableCase(){
		spreadsheetTraceableCase = SpreadsheetTraceableCase.SPREADSHEET_TRACEABLE_NOT_TESTED;
	}
	
	@Override
	public boolean isSpreadsheetTraceable() {
		
		//App.printStacktrace("\n"+this+"\n"+spreadsheetTraceableCase);
		
		switch(spreadsheetTraceableCase){
		case SPREADSHEET_TRACEABLE_TRUE:
			return true;
		case SPREADSHEET_TRACEABLE_FALSE:
			return false;
		case SPREADSHEET_TRACEABLE_NOT_TESTED:
			AlgoElement algo = getParentAlgorithm();
			if (algo!=null && (algo instanceof AlgoDependentText)){
				((AlgoDependentText) algo).setSpreadsheetTraceableText();
				if (spreadsheetTraceableLeftTree!=null){
					spreadsheetTraceableCase = SpreadsheetTraceableCase.SPREADSHEET_TRACEABLE_TRUE;
					//if no traceable value, only copy possible
					if (spreadsheetTraceableValue==null)
						traceModes = TraceModesEnum.ONLY_COPY;
					else
						traceModes = TraceModesEnum.ONE_VALUE_OR_COPY;
					return true;
				}
			}
			//spreadsheetTraceableCase = SpreadsheetTraceableCase.SPREADSHEET_TRACEABLE_FALSE;
			//return false;
			spreadsheetTraceableCase = SpreadsheetTraceableCase.SPREADSHEET_TRACEABLE_TRUE;
			traceModes = TraceModesEnum.ONLY_COPY;
			return true;
		default:
			return false;
		}
	}
	

	
	@Override
	public void updateColumnHeadingsForTraceValues(){
		
		resetSpreadsheetColumnHeadings();
		
		GeoText text = getColumnHeadingText(spreadsheetTraceableLeftTree);
		text.setLaTeX(this.isLaTeX, false);
		spreadsheetColumnHeadings.add(text);
		
	}


	@Override
	public void addToSpreadsheetTraceList(ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric numeric = new GeoNumeric(cons, spreadsheetTraceableValue.evaluateNum().getDouble());
		spreadsheetTraceList.add(numeric);

	}
	
	private TraceModesEnum traceModes;

	@Override
	public TraceModesEnum getTraceModes(){
		return traceModes;
	}
	
	/**
	 * @param sb string builder for appending the tag
	 * @param serifFont serif flag
	 * @param fontSizeD font size
	 * @param fontStyle font style
	 * @param isLaTeX latex flag
	 * @param app application
	 */
	public static void appendFontTag(StringBuilder sb, boolean serifFont,
			double fontSizeD, int fontStyle, boolean isLaTeX, App app) {
		// font settings
		if (serifFont || fontSizeD != 1 || fontStyle != 0 || isLaTeX) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			
			// multiplier
			sb.append("\" sizeM=\"");
			sb.append(fontSizeD);
			
			// work out an estimate (can't guarantee exact)
			double oldFontSize = app.getFontSize() * fontSizeD - app.getFontSize();
			
			if (oldFontSize > 0) {
				oldFontSize = Math.ceil(oldFontSize);
			} else {
				oldFontSize = Math.floor(oldFontSize);				
			}
			// still write this (for ggb40 compatibility)
			sb.append("\" size=\"");
			sb.append((int)oldFontSize);
			
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}

		
	}


}
