/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoSequence;
import org.geogebra.common.kernel.algos.AlgoTakeString;
import org.geogebra.common.kernel.algos.AlgoTextCorner;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.SerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * Geometrical element for holding text
 *
 */
public class GeoText extends GeoElement
		implements Locateable, AbsoluteScreenLocateable, TextValue,
		TextProperties, SpreadsheetTraceable, HasSymbolicMode, HasAuralText {
	public static final String NEW_LINE = "\\\\n";
	private static Comparator<GeoText> comparator;

	private String str;
	private GeoPointND startPoint; // location of Text on screen

	private boolean isLaTeX;

	// corners of the text Michael Borcherds 2007-11-26, see AlgoTextCorner
	private GRectangle2D boundingBox;
	private boolean needsUpdatedBoundingBox = false;

	// font options
	private boolean serifFont;
	private int fontStyle;
	// private int fontSize = 0; // must be zero, as that is the value NOT saved
	// to
	// // XML

	// changed to a multiplier from ggb42
	private double fontSizeD = 1;
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	/**
	 * used for eg Text["text",(1,2)] to stop it being editable
	 */
	public boolean isTextCommand = false;
	private final StringBuilder sbToString = new StringBuilder(80);

	private SpreadsheetTraceCase spreadsheetTraceableCase = SpreadsheetTraceCase.NOT_TESTED;
	private ExpressionValue spreadsheetTraceableValue;
	private ExpressionNode spreadsheetTraceableLeftTree;

	/** index of exra small modifier */
	final public static int FONTSIZE_EXTRA_SMALL = 0;
	/** index of very small modifier */
	final public static int FONTSIZE_VERY_SMALL = 1;
	/** index of small modifier */
	final public static int FONTSIZE_SMALL = 2;
	/** index of medium modifier */
	final public static int FONTSIZE_MEDIUM = 3;
	/** index of large modifier */
	final public static int FONTSIZE_LARGE = 4;
	/** index of very large modifier */
	final public static int FONTSIZE_VERY_LARGE = 5;
	/** index of exra large modifier */
	final public static int FONTSIZE_EXTRA_LARGE = 6;

	// for absolute screen location
	private boolean hasAbsoluteScreenLocation = false;

	private Integer verticalAlignment;
	private Integer horizontalAlignment;

	/**
	 */
	boolean alwaysFixed = false;
	private StringTemplate tpl = StringTemplate.defaultTemplate;
	private GeoText linkedText;

	private TraceModesEnum traceModes;
	private boolean symbolicMode;
	private int totalHeight;
	private int totalWidth;
	private final List<GeoElement> updateListeners;
	private boolean hasSpreadsheetError = false;

	/**
	 * Creates a new GeoText.
	 *
	 * Note: This will set construction defaults.
	 *
	 * @param construction
	 *            construction
	 */
	public GeoText(Construction construction) {
		this(construction, true);
	}

	/**
	 * Creates a new GeoText.
	 *
	 * @param construction
	 *            construction
	 * @param setDefaults
	 *            if true, will set construction defaults.
	 */
	public GeoText(Construction construction, boolean setDefaults) {
		super(construction);

		if (setDefaults) {
			setConstructionDefaults(); // init visual settings
		}
		updateListeners = new ArrayList<>();
	}

	/**
	 * Creates a new GeoText.
	 *
	 * Note: This will set construction defaults.
	 *
	 * @param construction
	 *            construction
	 * @param value
	 *            text
	 */
	public GeoText(Construction construction, String value) {
		this(construction);
		setTextString(value);
	}

	/**
	 * Creates a new GeoText.
	 *
	 * @param construction
	 *            construction
	 * @param setDefaults
	 *            If true, set construction defaults
	 * @param value
	 *            text
	 */
	public GeoText(Construction construction, String value, boolean setDefaults) {
		this(construction, setDefaults);
		setTextString(value);
	}

	/**
	 * Copy constructor
	 * 
	 * @param text
	 *            text to copy
	 */
	public GeoText(GeoText text) {
		this(text.cons);
		set(text);
	}

	@Override
	public GeoText copy() {
		return new GeoText(this);
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoText()) {
			return;
		}
		GeoText gt = (GeoText) geo;
		// macro output: don't set start point
		// but update to desired number format
		if (cons != geo.getConstruction() && isAlgoMacroOutput()) {
			if (!useSignificantFigures) {
				gt.setPrintDecimals(printDecimals > -1 ? printDecimals
						: kernel.getPrintDecimals(), true);
			} else {
				gt.setPrintFigures(printFigures > -1 ? printFigures
						: kernel.getPrintFigures(), true);
			}
			str = gt.str;
			isLaTeX = gt.isLaTeX;
			updateTemplate();
			return;
		}

		str = gt.str;
		isLaTeX = gt.isLaTeX;

		// needed for Corner[Element[text
		boundingBox = gt.getBoundingBox();

		if (gt.getHorizontalAlignment() != null) {
			setHorizontalAlignment(gt.getHorizontalAlignment());
			if (gt.getVerticalAlignment() != null) {
				setVerticalAlignment(gt.getVerticalAlignment());
			}
		}
		try {
			if (gt.startPoint != null) {
				if (gt.hasStaticLocation()) {
					if (this.startPoint != null && this.hasStaticLocation()) {
						// just use the value
						this.startPoint.set(gt.startPoint);
					} else {
						// create new location point
						setStartPoint(gt.startPoint.copy());
					}
				} else {
					// take existing location point
					setStartPoint(gt.startPoint);
				}
			}
		} catch (CircularDefinitionException e) {
			Log.debug("set GeoText: CircularDefinitionException");
		}
		updateTemplate();
	}

	@Override
	public void setBasicVisualStyle(GeoElement geo) {
		super.setBasicVisualStyle(geo);
		if (!geo.isGeoText()) {
			return;
		}

		GeoText text = (GeoText) geo;
		serifFont = text.serifFont;
		fontStyle = text.fontStyle;
		fontSizeD = text.fontSizeD;
		printDecimals = text.printDecimals;
		printFigures = text.printFigures;
		useSignificantFigures = text.useSignificantFigures;
		isLaTeX = text.isLaTeX;
		updateTemplate();
	}

	/**
	 * Sets the text contained in this object
	 * 
	 * @param text2
	 *            text
	 */
	final public void setTextString(String text2) {
		String text = text2 == null ? "" : text2;
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
			str = text.replace(NEW_LINE, "\n");
		}

	}

	/**
	 * Returns the string wrapped in this text
	 * 
	 * @return the string wrapped in this text
	 */
	@Override
	@CheckForNull
	final public String getTextString() {
		return str;
	}

	final public String getTextStringSafe() {
		return str == null ? "" : str;
	}

	/**
	 * Sets the startpoint without performing any checks. This is needed for
	 * macros.
	 */
	@Override
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;
	}

	@Override
	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setStartPoint(p);
	}

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {
		// don't allow this if it's eg Text["hello",(2,3)]
		if (alwaysFixed) {
			return;
		// macro output uses initStartPoint() only
		// if (isAlgoMacroOutput()) return;
		}

		// check for circular definition
		if (isParentOf(p)) {
			app.showError(MyError.Errors.CircularDefinition);
			throw new CircularDefinitionException();
		}

		// remove old dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}

		// set new location
		if (p == null) {
			if (startPoint != null) {
				startPoint = startPoint.copy();
			}

			labelOffsetX = 0;
			labelOffsetY = 0;
		} else {
			startPoint = p;

			// add new dependencies
			startPoint.getLocateableList().registerLocateable(this);
		}
	}

	@Override
	public void doRemove() {
		List<GeoElement> listenersCopy = new ArrayList<>(updateListeners);
		updateListeners.clear();

		for (GeoElement geo : listenersCopy) {
			geo.removeDynamicCaption();
			kernel.notifyUpdate(geo);
		}

		super.doRemove();
		// tell startPoint
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}
	}

	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	@Override
	public boolean hasStaticLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	@Override
	public void update(boolean drag) {

		super.update(drag);
		if (!cons.isFileLoading() && getLabelSimple() != null
				&& getLabelSimple().startsWith("altText")) {
			kernel.getApplication().setAltText(this);
		}

		notifyListeners();
	}

	private void notifyListeners() {
		for (GeoElement geo : updateListeners) {
			geo.notifyUpdate();
			if (geo.isGeoNumeric()) {
				((GeoNumeric) geo).notifyScreenReader();
			}
		}
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
		// https://help.geogebra.org/topic/fixed-list-list-with-text-objects
		if (tpl1.hasType(StringType.SCREEN_READER_ASCII)) {
			return getAuralText();
		}
		return getTextStringSafe();
	}

	/**
	 * Returns quoted text value string.
	 */
	@Override
	public String toOutputValueString(StringTemplate tpl1) {
		StringType printForm = tpl1.getStringType();

		sbToString.setLength(0);

		if (printForm.equals(StringType.LATEX)) {

			if (!StringUtil.containsLaTeX(str)) {
				sbToString.append("\\text{");
			}

			sbToString.append(Unicode.OPEN_DOUBLE_QUOTE);

		} else {
			sbToString.append(tpl1.getOpenQuote());
		}

		if (str != null) {
			sbToString.append(str);
		}

		if (printForm.equals(StringType.LATEX)) {
			sbToString.append(Unicode.CLOSE_DOUBLE_QUOTE);
			if (!StringUtil.containsLaTeX(str)) {
				sbToString.append("}");
			}
		} else {
			sbToString.append(tpl1.getCloseQuote());
		}
		return sbToString.toString();
	}

	@Override
	public String toString(StringTemplate tpl1) {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append(" = ");
		}

		sbToString.append(tpl1.getOpenQuote());
		if (str != null) {
			sbToString.append(tpl1.escapeString(str));
		}
		sbToString.append(tpl1.getCloseQuote());

		return sbToString.toString();
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXT;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TEXT;
	}

	@Override
	public boolean isMoveable() {
		if (alwaysFixed) {
			return false;
		}

		return !isLocked();
	}

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
		if ((getParentAlgorithm() != null)
				&& !(getParentAlgorithm() instanceof AlgoDependentText)) {
			return true;
		}

		return isTextCommand;
	}

	/**
	 * @return true if this text was produced by algo with LaTeX output
	 */
	@Override
	public boolean isLaTeXTextCommand() {

		if (!isTextCommand || getParentAlgorithm() == null) {
			return false;
		}

		return getParentAlgorithm().isLaTeXTextCommand();
	}

	@Override
	public void setAlgoMacroOutput(boolean isAlgoMacroOutput) {
		super.setAlgoMacroOutput(true);
		setIsTextCommand(true);
	}

	/**
	 * For Text[Text[a]] the inner text must use template of the outer
	 * 
	 * @param text
	 *            descendant whose string template may be used
	 */
	public void addTextDescendant(GeoText text) {
		if (isLabelSet()) {
			return;
		}
		linkedText = text;
	}

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
		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean evaluatesToText() {
		return true;
	}

	@Override
	public boolean isGeoText() {
		return true;
	}

	@Override
	public MyStringBuffer getText() {
		if (str != null) {
			return new MyStringBuffer(kernel, str);
		}
		return new MyStringBuffer(kernel, "");
	}

	/**
	 * save object in XML format
	 */
	@Override
	public final void getExpressionXML(StringBuilder sb) {

		// an independent text needs to add
		// its expression itself
		// e.g. text0 = "Circle"
		if (isIndependent() && getDefaultGeoType() < 0) {
			sb.append("<expression label=\"");
			StringUtil.encodeXML(sb, label);
			sb.append("\" exp=\"");
			StringUtil.encodeXML(sb,
					toOutputValueString(StringTemplate.xmlTemplate));
			// expression
			sb.append("\"/>\n");
		}
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getStyleXML(StringBuilder sb) {
		if (isSymbolicMode()) {
			sb.append("\t<symbolic val=\"true\" />\n");
		}
		XMLBuilder.getXMLvisualTags(this, sb, false);

		getXMLfixedTag(sb);

		if (isLaTeX) {
			sb.append("\t<isLaTeX val=\"true\"/>\n");
		}

		appendFontTag(sb, serifFont, fontSizeD, fontStyle, isLaTeX,
				kernel.getApplication());

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
		getSpreadsheetHasErrorXML(sb);

		// store location of text (and possible labelOffset)
		sb.append(getXMLlocation());
		getScriptTags(sb);
	}

	private void getSpreadsheetHasErrorXML(StringBuilder sb) {
		sb.append("\t<hasSpreadsheetError val=\"");
		sb.append(hasSpreadsheetError);
		sb.append("\"/>\n");
	}

	/**
	 * Returns startPoint of this text in XML notation.
	 */
	private String getXMLlocation() {
		StringBuilder sb = new StringBuilder();

		if (hasAbsoluteScreenLocation && startPoint == null) {
			sb.append("\t<absoluteScreenLocation x=\"");
			sb.append(labelOffsetX);
			sb.append("\" y=\"");
			sb.append(labelOffsetY);
			sb.append("\"/>\n");
		} else {
			// location of text
			if (startPoint != null) {
				startPoint.appendStartPointXML(sb, isAbsoluteScreenLocActive());

				if (labelOffsetX != 0 || labelOffsetY != 0) {
					sb.append("\t<labelOffset");
					sb.append(" x=\"");
					sb.append(labelOffsetX);
					sb.append("\" y=\"");
					sb.append(labelOffsetY);
					sb.append("\"/>\n");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);

		// start point of text
		if (geo instanceof GeoText) {
			GeoText text = (GeoText) geo;
			setSameLocation(text);
			setLaTeX(text.isLaTeX, true);
		}
	}

	private void setSameLocation(GeoText text) {
		if (text.isAbsoluteScreenLocActive()) {
			if (text.startPoint == null) {
				hasAbsoluteScreenLocation = true;
				setAbsoluteScreenLoc(text.getAbsoluteScreenLocX(), text.getAbsoluteScreenLocY());
			} else {
				setAbsoluteStartPoint(text.startPoint, true);
			}
		} else {
			if (text.startPoint != null) {
				setAbsoluteStartPoint(text.startPoint, false);
			}
		}
	}

	private void setAbsoluteStartPoint(GeoPointND oldStartPoint, boolean isAbsolute) {
		hasAbsoluteScreenLocation = isAbsolute;
		try {
			setStartPoint(oldStartPoint);
		} catch (CircularDefinitionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns true for LaTeX texts
	 * 
	 * @return true for LaTeX texts
	 */
	public boolean isLaTeX() {
		return isLaTeX;
	}

	/**
	 * Changes type of this object to math rendering type (LaTeX)
	 * 
	 * @param b
	 *            true for math rendering
	 * @param updateParentAlgo
	 *            when true, parent is recomputed
	 */
	public void setLaTeX(boolean b, boolean updateParentAlgo) {
		if (b == isLaTeX) {
			return;
		}

		isLaTeX = b;

		updateTemplate();
		// update parent algorithm if it's not a sequence
		if (updateParentAlgo) {
			updateParent();
		}
	}

	private void updateParent() {
		AlgoElement parent = getParentAlgorithm();
		if (parent != null && !(parent instanceof AlgoSequence)) {
			parent.update();
		}

	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
			startPoint = null;
		}
		if (!hasScreenLocation() && (x != 0 && y != 0)) {
			setScreenLocation(x, y);
		}
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return startPoint == null ? labelOffsetX : (int) startPoint.getInhomX();
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return startPoint == null ? labelOffsetY : (int) startPoint.getInhomY();
	}

	@Override
	public double getRealWorldLocX() {
		if (startPoint == null) {
			return 0;
		}
		return startPoint.getInhomCoords().getX();
	}

	@Override
	public double getRealWorldLocY() {
		if (startPoint == null) {
			return 0;
		}
		return startPoint.getInhomCoords().getY();
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		GeoPointND locPoint = getStartPoint();
		if (locPoint == null || hasAbsoluteScreenLocation) {
			locPoint = new GeoPoint(cons);
			try {
				setStartPoint(locPoint);
			} catch (Exception e) {
				// circular definition, do nothing
			}
		}
		locPoint.setCoords(x, y, 1.0);
		labelOffsetX = 0;
		labelOffsetY = 0;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		if (flag == hasAbsoluteScreenLocation) {
			return;
		}

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

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	// public int getFontSize() {
	// return fontSize;
	// }

	@Override
	public double getFontSizeMultiplier() {
		return fontSizeD;
	}

	/**
	 * 
	 * @param index
	 *            index of size in the settings
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
	 * @param d
	 *            font size modifier
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

	@Override
	public void setFontSizeMultiplier(double d) {
		fontSizeD = d;
	}

	@Override
	public int getFontStyle() {
		return fontStyle;
	}

	@Override
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;

		// needed for eg \sqrt in latex
		if ((fontStyle & GFont.BOLD) != 0) {
			setLineThickness(
					EuclidianStyleConstants.DEFAULT_LINE_THICKNESS * 2);
		} else {
			setLineThickness(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS);
		}
	}

	@Override
	final public int getPrintDecimals() {
		return printDecimals;
	}

	@Override
	final public int getPrintFigures() {
		return printFigures;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printDecimals = printDecimals;
			printFigures = -1;
			useSignificantFigures = false;
			updateTemplate();
			updateTemplateAlgos(algo);
		}
	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printFigures = printFigures;
			printDecimals = -1;
			useSignificantFigures = true;
			updateTemplate();
			updateTemplateAlgos(algo);
		}
	}

	private void updateTemplateAlgos(AlgoElement algo) {
		if (algo == null) {
			return;
		}
		for (int i = 0; i < algo.getInput().length; i++) {
			if (algo.getInput()[i].isGeoText()) {
				updateTemplateAlgos(algo.getInput()[i].getParentAlgorithm());
			}
		}
		algo.update();

	}

	@Override
	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}

	@Override
	public boolean isSerifFont() {
		return serifFont;
	}

	@Override
	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}

	/**
	 * @param result
	 *            point for storing result
	 * @param n
	 *            index of corner (1 for lower left, then anticlockwise)
	 */
	@Override
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
			result.setCoords(boundingBox.getX(),
					boundingBox.getY() + boundingBox.getHeight(), 1.0);
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
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setBoundingBox(double x, double y, double w, double h) {

		boolean firstTime = boundingBox == null;
		if (firstTime) {
			boundingBox = AwtFactory.getPrototype().newRectangle2D();
		}

		boundingBox.setRect(x, y, w, h);
	}

	@Override
	public final boolean needsUpdatedBoundingBox() {
		return needsUpdatedBoundingBox;
	}

	/**
	 * @param needsUpdatedBoundingBox
	 *            true to make sure this object updates itself
	 */
	@Override
	public final void setNeedsUpdatedBoundingBox(
			boolean needsUpdatedBoundingBox) {
		this.needsUpdatedBoundingBox = needsUpdatedBoundingBox;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		// return false if it's a different type
		if (str == null) {
			return ExtendedBoolean.FALSE;
		}
		if (geo.isGeoText()) {
			return ExtendedBoolean.newExtendedBoolean(str.equals(((GeoText) geo).str));
		}
		return ExtendedBoolean.FALSE;
	}

	/**
	 * Returns a comparator for GeoText objects. If equal, doesn't return zero
	 * (otherwise TreeSet deletes duplicates)
	 * 
	 * @return comparator
	 */
	public static Comparator<GeoText> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<GeoText>() {
				@Override
				public int compare(GeoText itemA, GeoText itemB) {

					NormalizerMinimal noramlizer = itemA.getKernel()
							.getApplication().getNormalizer();

					// remove accents etc
					String strA = noramlizer.transform(itemA.getTextStringSafe());
					String strB = noramlizer.transform(itemB.getTextStringSafe());

					// do comparison without accents etc
					int comp = strA.compareTo(strB);

					if (comp == 0) {
						// try compare with accents
						comp = itemA.getTextStringSafe()
								.compareTo(itemB.getTextStringSafe());
					}

					if (comp == 0) {
						// if we return 0 for equal strings, the TreeSet deletes
						// the equal one
						return itemA.getConstructionIndex() > itemB
								.getConstructionIndex() ? -1 : 1;
					}
					return comp;
				}
			};
		}

		return comparator;
	}

	private void updateTemplate() {
		StringType type = isLaTeX ? StringType.LATEX : StringType.GEOGEBRA;

		if (useSignificantFigures() && printFigures > -1) {
			tpl = StringTemplate.printFigures(type, printFigures, false);
		} else if (!useSignificantFigures && printDecimals > -1) {
			tpl = StringTemplate.printDecimals(type, printDecimals, false);
		} else {
			tpl = StringTemplate.get(type);
		}
		tpl = tpl.deriveWithFractions(this.symbolicMode);
	}

	@Override
	public boolean isAlwaysFixed() {
		return alwaysFixed;
	}

	@Override
	public boolean isRedefineable() {
		return true;
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return isLaTeX() || (str != null
				&& str.indexOf('_') != -1);
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
		if (linkedText == null) {
			return tpl;
		}
		return linkedText.getStringTemplate();
	}

	private enum SpreadsheetTraceCase {
		NOT_TESTED, TRUE, FALSE
	}

	/**
	 * set objects for trace to spreadsheet
	 * 
	 * @param leftTree
	 *            tree for column heading
	 * @param value
	 *            value to trace
	 */
	public void setSpreadsheetTraceable(ExpressionNode leftTree,
			ExpressionValue value) {
		this.spreadsheetTraceableLeftTree = leftTree;
		this.spreadsheetTraceableValue = value;
	}

	/**
	 * init case for spreadsheet traceable case
	 */
	public void initSpreadsheetTraceableCase() {
		spreadsheetTraceableCase = SpreadsheetTraceCase.NOT_TESTED;
	}

	@Override
	public boolean isSpreadsheetTraceable() {
		switch (spreadsheetTraceableCase) {
		case TRUE:
			return true;
		case FALSE:
			return false;
		case NOT_TESTED:
			AlgoElement algo = getParentAlgorithm();
			if (algo != null && (algo instanceof AlgoDependentText)) {
				((AlgoDependentText) algo).setSpreadsheetTraceableText();
				if (spreadsheetTraceableLeftTree != null) {
					spreadsheetTraceableCase = SpreadsheetTraceCase.TRUE;
					// if no traceable value, only copy possible
					if (spreadsheetTraceableValue == null) {
						traceModes = TraceModesEnum.ONLY_COPY;
					} else {
						traceModes = TraceModesEnum.ONE_VALUE_OR_COPY;
					}
					return true;
				}
			}
			// spreadsheetTraceableCase =
			// SpreadsheetTraceableCase.FALSE;
			// return false;
			spreadsheetTraceableCase = SpreadsheetTraceCase.TRUE;
			traceModes = TraceModesEnum.ONLY_COPY;
			return true;
		default:
			return false;
		}
	}

	@Override
	public void updateColumnHeadingsForTraceValues() {

		resetSpreadsheetColumnHeadings();

		GeoText text = getColumnHeadingText(spreadsheetTraceableLeftTree);
		text.setLaTeX(this.isLaTeX, false);
		spreadsheetColumnHeadings.add(text);
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric numeric = new GeoNumeric(cons,
				spreadsheetTraceableValue.evaluateDouble());
		spreadsheetTraceList.add(numeric);
	}

	@Override
	public TraceModesEnum getTraceModes() {
		return traceModes;
	}

	/**
	 * @param sb
	 *            string builder for appending the tag
	 * @param serifFont
	 *            serif flag
	 * @param fontSizeD
	 *            font size
	 * @param fontStyle
	 *            font style
	 * @param isLaTeX
	 *            latex flag
	 * @param app
	 *            application
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
			double oldFontSize = app.getFontSize() * fontSizeD
					- app.getFontSize();

			if (oldFontSize > 0) {
				oldFontSize = Math.ceil(oldFontSize);
			} else {
				oldFontSize = Math.floor(oldFontSize);
			}
			// still write this (for ggb40 compatibility)
			sb.append("\" size=\"");
			sb.append((int) oldFontSize);

			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	public void updateLocation() {
		updateGeo(false);
		kernel.notifyUpdateLocation(this);
	}

	@Override
	public void updateVisualStyle(GProperty prop) {
		super.updateVisualStyle(prop);
		if (prop == GProperty.FONT) {
			ArrayList<AlgoElement> algosTextCorner = new ArrayList<>();
			for (AlgoElement algo : getAlgorithmList()) {
				if (algo instanceof AlgoTextCorner) {
					algosTextCorner.add(algo);
				}
			}
			AlgoElement.updateCascadeAlgos(algosTextCorner);
		}
		notifyListeners();
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	protected boolean isVisibleInView3DNotSet() {
		if (isVisibleInView(App.VIEW_EUCLIDIAN) && !hasStaticLocation()) {
			// visible: we set it
			visibleInView3D = ExtendedBoolean.TRUE;
			return true;
		}

		if (kernel.getApplication().getActiveEuclidianView()
				.isEuclidianView3D() && hasStaticLocation()) {
			// visible only in 3D view
			try {
				kernel.getApplication().removeFromEuclidianView(this);
			} catch (Exception e) {
				// in case EV is null
			}
			visibleInView3D = ExtendedBoolean.TRUE;
			return true;
		}

		// not visible: we don't set it
		visibleInView3D = ExtendedBoolean.FALSE;
		return false;
	}

	/**
	 * may need to be added to 3D view after creation
	 */
	public void checkVisibleIn3DViewNeeded() {
		if (isVisibleInView(App.VIEW_EUCLIDIAN)) {
			// we need to force visibility in 3D view and views
			// for plane
			addViews3D();
			if (kernel.getApplication().isEuclidianView3Dinited()) {
				kernel.getApplication().getEuclidianView3D().add(this);
			}
			setVisibleInViewForPlane(true);
			kernel.getApplication().addToViewsForPlane(this);
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.TEXT;

	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (isTextCommand && getParentAlgorithm() instanceof AlgoTakeString) {
			return DescriptionMode.DEFINITION_VALUE;
		}
		return DescriptionMode.VALUE;
	}

	@Override
	public void initSymbolicMode() {
		setSymbolicMode(true, true);
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		if (mode != this.symbolicMode) {
			this.symbolicMode = mode;
			updateTemplate();
			if (updateParent) {
				updateParent();
			}
		}
	}

	@Override
	public boolean isSymbolicMode() {
		return this.symbolicMode;
	}

	/**
	 * Sets the total width of the geo.
	 * 
	 * @param width
	 *            to set.
	 */
	public void setTotalWidth(int width) {
		totalWidth = width;
	}

	/**
	 * Sets the total height of the geo.
	 * 
	 * @param height
	 *            to set.
	 */
	public void setTotalHeight(int height) {
		totalHeight = height;
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return totalWidth;
	}

	@Override
	public boolean isFurniture() {
		return false;
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return totalHeight;
	}

	@Override
	public String getDefinitionForEditor() {
		if (!this.isIndependent() || isLaTeX()) {
			return super.getDefinitionForEditor();
		}

		if (isDefinitionValid()) {
			return StringTemplate.editorTemplate.escapeString(str);
		}

		return "?";
	}

	/**
	 * 
	 * @param builder
	 *            .
	 * @return the editable text itself, without label and ""-s.
	 */
	final public String getDescriptionForAV(
			IndexHTMLBuilder builder) {
		String txt = getDefinitionForEditor();
		builder.clear();
		builder.append(txt);
		return txt;
	}

	@Override
	public void addAuralContent(Localization loc, ScreenReaderBuilder sb) {
		sb.appendDegreeIfNeeded(this, getAuralText());
		sb.endSentence();
	}

	@Override
	public boolean isSingularValue() {
		String valueString = toValueString(StringTemplate.defaultTemplate);
		if (!valueString.contains(Unicode.DEGREE_STRING)) {
			return false;
		}
		int idx = valueString.indexOf(Unicode.DEGREE_STRING) - 1;
		StringBuilder sb = new StringBuilder();
		while (idx > 0 && StringUtil.isDigitOrDot(valueString.charAt(idx))) {
			sb.append(valueString.charAt(idx));
			idx--;
		}

		if (sb.length() == 0) {
			return true;
		}

		int maxi = Character.digit(sb.reverse().toString().charAt(0), 10);
		return maxi == 1;
	}

	/**
	 * 
	 * @return text to read out (LaTeX is converted to "nice" form)
	 */
	@Override
	public String getAuralText() {
		String ret;
		if (isLaTeX()) {
			ret = getAuralTextLaTeX();
		} else {
			ret = ScreenReader.convertToReadable(getTextString(), app);
		}
		return ret;
	}

	/**
	 * @return aural text assuming this is LaTeX
	 */
	public String getAuralTextLaTeX() {
		kernel.getApplication().getDrawEquation()
				.checkFirstCall();
		// TeXAtomSerializer makes formula human-readable.
		TeXFormula tf = getTeXFormula();
		SerializationAdapter adapter = ScreenReader.getSerializationAdapter(app);
		return new TeXAtomSerializer(adapter).serialize(tf.root);
	}

	private TeXFormula getTeXFormula() {
		String textString = getTextString();
		try {
			return new TeXFormula(textString);
		} catch (Exception e) {
			return TeXFormula.getPartialTeXFormula(textString);
		}
	}

	@Override
	public void addAuralName(ScreenReaderBuilder sb) {
		// only read content, no prefix
	}

	/**
	 * @param geo element using this as dynamic caption
	 */
	public void registerUpdateListener(GeoElement geo) {
		if (!updateListeners.contains(geo)) {
			updateListeners.add(geo);
		}
	}

	public void unregisterUpdateListener(GeoElement geo) {
		updateListeners.remove(geo);
	}

	@Override
	public void moveDependencies(GeoElement oldGeo) {
		if (!oldGeo.isGeoText()) {
			return;
		}

		GeoText text = (GeoText) oldGeo;
		List<GeoElement> listenersCopy = new ArrayList<>(text.updateListeners);
		updateListeners.clear();
		text.updateListeners.clear();
		for (GeoElement geo: listenersCopy) {
			geo.setDynamicCaption(this);
			registerUpdateListener(geo);
		}
	}

	public void setHorizontalAlignment(Integer horizAlign) {
		horizontalAlignment = horizAlign;
	}

	public Integer getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setVerticalAlignment(Integer vertAlign) {
		verticalAlignment = vertAlign;
	}

	public Integer getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 *
	 * @return if text is aligned either horizontally or vertically.
	 */
	public boolean hasAlignment() {
		return horizontalAlignment != null || verticalAlignment != null;
	}

	/**
	 * @return original string with extra \ in order to escape special characters
	 *       e.g. "cos(x)" will return "cos\\(x\\)"
	 */
	public String getEscapedSpecialCharsString() {
		StringBuilder b = new StringBuilder();
		if (str != null && !str.isEmpty()) {
			for (int i = 0; i < str.length(); ++i) {
				char ch = str.charAt(i);
				if ("\\.^$|?*+[]{}()".indexOf(ch) != -1) {
					b.append('\\').append(ch);
				} else {
					b.append(ch);
				}
			}
		}
		return b.toString();
	}

	public boolean hasSpreadsheetError() {
		return hasSpreadsheetError;
	}

	public void setSpreadsheetError(boolean hasSpreadsheetError) {
		this.hasSpreadsheetError = hasSpreadsheetError;
	}
}