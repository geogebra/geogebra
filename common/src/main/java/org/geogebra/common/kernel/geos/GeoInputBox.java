package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPointInRegion;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;

import javax.annotation.Nonnull;

/**
 * Input box for user input
 *
 * @author Michael
 *
 */
public class GeoInputBox extends GeoButton implements HasSymbolicMode, HasAlignment {

	private static final int defaultLength = 20;

	private int length = defaultLength;
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	private StringTemplate tpl = StringTemplate.defaultTemplate;

	private boolean symbolicMode = false;
	private boolean editing = false;

	private StringTemplate stringTemplateForLaTeX;

	private TextAlignment textAlignment = TextAlignment.LEFT;

	private @Nonnull GeoElementND linkedGeo;
	private @Nonnull InputBoxProcessor inputBoxProcessor;

	/**
	 * Creates new text field
	 *
	 * @param cons
	 *            construction
	 */
	public GeoInputBox(Construction cons) {
		super(cons);
		setLinkedGeo(new GeoText(cons, ""));
	}

	/**
	 * @param cons
	 *            construction
	 * @param labelOffsetX
	 *            x offset
	 * @param labelOffsetY
	 *            y offset
	 */
	public GeoInputBox(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	@Override
	public boolean isChangeable() {
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TEXTFIELD;
	}

	/**
	 * @param geo
	 *            new linked geo
	 */
	public void setLinkedGeo(GeoElementND geo) {
		linkedGeo = geo;
		inputBoxProcessor = new InputBoxProcessor(this, geo);
	}

	/**
	 *
	 * @return text to edit.
	 */
	public String getTextForEditor() {
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextString();
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			GeoNumeric numeric = (GeoNumeric) linkedGeo;

			if (!numeric.isDefined() || isSymbolicMode() && numeric.isSymbolicMode()) {
				linkedGeoText = numeric.getRedefineString(true, true);
			} else if (numeric.isSymbolicMode()) {
				linkedGeoText = numeric.getValueForInputBar();
			} else {
				linkedGeoText = numeric.toValueString(tpl);
			}
		} else if (isSymbolicMode()) {
			linkedGeoText = linkedGeo.getRedefineString(true, true);
		} else {
			linkedGeoText = linkedGeo.getValueForInputBar();
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	/**
	 * Get the text (used for scripting)
	 *
	 * @return the text
	 */
	public String getText() {
		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextString();
		}

		String linkedGeoText;

		if (linkedGeo.isGeoNumeric()) {
			if (symbolicMode && !((GeoNumeric) linkedGeo).isSimple()) {
				linkedGeoText = toLaTex(linkedGeo);
			} else {
				linkedGeoText = getStringForNumeric((GeoNumeric) linkedGeo);
			}
		} else if (isSymbolicMode()) {
			linkedGeoText = toLaTex(linkedGeo);
		} else {
			linkedGeoText = linkedGeo.getValueForInputBar();
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	private String getStringForNumeric(GeoNumeric numeric) {
		if (linkedGeo.isDefined()) {
			return numeric.toValueString(tpl);
		} else {
			return numeric.getRedefineString(true, true);
		}
	}

	private String toLaTex(GeoElementND geo) {
		if (geo.isGeoFunction()) {
			return geo.getRedefineString(true, true,
					getStringtemplateForLaTeX());
		}
		return geo.toLaTeXString(true, StringTemplate.latexTemplate);
	}

	private StringTemplate getStringtemplateForLaTeX() {
		if (stringTemplateForLaTeX == null) {
			stringTemplateForLaTeX = StringTemplate.latexTemplate.makeStrTemplateForEditing();
		}
		return stringTemplateForLaTeX;
	}

	/**
	 * Returns the linked geo
	 *
	 * @return linked geo
	 */
	public GeoElementND getLinkedGeo() {
		return linkedGeo;
	}

	@Override
	public String toValueString(StringTemplate tpl1) {
		return getText();
	}

	/**
	 * Set the text
	 *
	 * @param newText
	 *            new text value
	 */
	public void setText(String newText) {
		updateLinkedGeo(newText);
	}

	@Override
	public boolean isGeoInputBox() {
		return true;
	}

	/**
	 * Sets length of the input box
	 *
	 * @param len
	 *            new length
	 */
	public void setLength(int len) {
		length = len;
		this.updateVisualStyle(GProperty.LENGTH);
	}

	/**
	 * @return length of the input box
	 */
	public int getLength() {
		return length;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		sb.append("\t<linkedGeo exp=\"");
		StringUtil.encodeXML(sb,
				linkedGeo.getLabel(StringTemplate.xmlTemplate));
		sb.append("\"");
		sb.append("/>\n");

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

		if (isSymbolicMode()) {
			sb.append("\t<symbolic val=\"true\" />\n");
		}

		if (getLength() != defaultLength) {
			sb.append("\t<length val=\"");
			sb.append(getLength());
			sb.append("\"");
			sb.append("/>\n");
		}
		if (getAlignment() != TextAlignment.LEFT) {
			sb.append("\t<textAlign val=\"");
			sb.append(getAlignment().toString());
			sb.append("\"/>\n");
		}
	}

	@Override
	public GeoElement copy() {
		return new GeoInputBox(cons, labelOffsetX, labelOffsetY);
	}

	/**
	 * @param inputText
	 *            new value for linkedGeo
	 */
	public void updateLinkedGeo(String inputText) {
		inputBoxProcessor.updateLinkedGeo(inputText, tpl, printDecimals > -1 || printFigures > -1);
	}

	/**
	 * Called by a Drawable for this object when it is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void updateText(TextObject textFieldToUpdate) {
		String linkedText;

		if (linkedGeo.isGeoText()) {
			linkedText = ((GeoText) linkedGeo).getTextString();
		} else if (linkedGeo.getParentAlgorithm() instanceof AlgoPointOnPath
				|| linkedGeo.getParentAlgorithm() instanceof AlgoPointInRegion) {
			linkedText = linkedGeo.toValueString(tpl);
		} else {

			// want just a number for eg a=3 but we want variables for eg
			// y=m x + c
			boolean substituteNos = linkedGeo.isGeoNumeric()
					&& linkedGeo.isIndependent();

			if (linkedGeo.isGeoFunction()) {
				linkedText = linkedGeo.getRedefineString(true, true);
			} else {
				linkedText = linkedGeo.getFormulaString(tpl, substituteNos);
			}
		}

		if (linkedText == null || "?".equals(linkedText) && !linkedGeo.isGeoText()) {
			linkedText = "";
		}

		if (linkedGeo.isGeoText()) {
			linkedText = linkedText.replaceAll("\n", "\\\\\\\\n");
		}

		// avoid redraw error
		if (!textFieldToUpdate.getText().equals(linkedText)) {
			textFieldToUpdate.setText(linkedText);
		}
	}

	/**
	 * Called by a Drawable when its text object is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void textObjectUpdated(TextObject textFieldToUpdate) {
		updateLinkedGeo(textFieldToUpdate.getText());
		updateText(textFieldToUpdate);
	}

	/**
	 * Called by a Drawable when the input is submitted (e.g. by pressing ENTER)
	 */
	public void textSubmitted() {
		runClickScripts(getText());
	}

	private void updateTemplate() {
		if (useSignificantFigures() && printFigures > -1) {
			tpl = StringTemplate.printFigures(StringType.GEOGEBRA, printFigures,
					false);
		} else if (!useSignificantFigures && printDecimals > -1) {
			tpl = StringTemplate.printDecimals(StringType.GEOGEBRA,
					printDecimals, false);
		} else {
			tpl = StringTemplate.get(StringType.GEOGEBRA);
		}
	}

	@Override
	public int getPrintDecimals() {
		return printDecimals;
	}

	@Override
	public int getPrintFigures() {
		return printFigures;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		this.printDecimals = printDecimals;
		printFigures = -1;
		useSignificantFigures = false;
		updateTemplate();
	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		this.printFigures = printFigures;
		printDecimals = -1;
		useSignificantFigures = true;
		updateTemplate();
	}

	@Override
	public boolean useSignificantFigures() {
		return useSignificantFigures;
	}

	@Override
	public void setBackgroundColor(final GColor bgCol) {
		if (bgCol == null) {
			// transparent
			bgColor = null;
			return;
		}

		// default in case alpha = 0 (not allowed for Input Boxes)
		int red = 255, green = 255, blue = 255;

		// fix for files saved with alpha = 0
		if (bgCol.getAlpha() != 0) {

			red = bgCol.getRed();
			green = bgCol.getGreen();
			blue = bgCol.getBlue();
		}

		bgColor = GColor.newColor(red, green, blue);
	}

	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		DrawableND draw = ev.getDrawableFor(this);
		if (draw instanceof DrawInputBox) {
			return ((DrawInputBox) draw).getTotalSize().getWidth();
		}
		return getWidth();
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		DrawableND draw = ev.getDrawableFor(this);
		if (draw instanceof DrawInputBox) {
			return ((DrawInputBox) draw).getTotalSize().getHeight();
		}
		return getHeight();
	}

	@Override
	public DescriptionMode needToShowBothRowsInAV() {
		return DescriptionMode.DEFINITION;
	}

	@Override
	public GColor getBackgroundColor() {
		return bgColor;
	}

	/**
	 * @return description for the screen reader
	 */
	public String getAuralText() {
		ScreenReaderBuilder sb = new ScreenReaderBuilder();
		sb.append(getKernel().getLocalization().getMenu("Text Field"));
		sb.appendSpace();
		sb.append(getCaption(StringTemplate.screenReader));
		return sb.toString();
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		this.symbolicMode = mode;
	}

	@Override
	public boolean isSymbolicMode() {
		return canBeSymbolic() && symbolicMode;
	}

	/**
	 *
	 * @return if linked object can be a symbolic one.
	 */
	public boolean canBeSymbolic() {
		return canBeSymbolicNumber() || linkedGeo.isGeoFunction() || linkedGeo.isGeoPoint() || linkedGeo.isGeoVector();
	}

	private boolean canBeSymbolicNumber() {
		if (!linkedGeo.isGeoNumeric()) {
			return false;
		}

		GeoNumeric number = (GeoNumeric) linkedGeo;
		return !number.isAngle();
	}

	/**
	 *
	 * @return if the GeoInputBox is under editing.
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Set this true if an editor is active for this input box
	 * or false if it is not.
	 *
	 * @param editing
	 * 			to set.
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	@Override
	public void setAlignment(TextAlignment alignment) {
		textAlignment = alignment;
	}

	@Override
	public TextAlignment getAlignment() {
		return textAlignment;
	}
}
