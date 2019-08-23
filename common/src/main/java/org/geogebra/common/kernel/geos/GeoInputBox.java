package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPointInRegion;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Input box for user input
 *
 * @author Michael
 *
 */
public class GeoInputBox extends GeoButton implements HasSymbolicMode {
	private static int defaultLength = 20;
	private int length;
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	private StringTemplate tpl = StringTemplate.defaultTemplate;
	private GeoElementND linkedGeo = null;

	private String text = null;
	private boolean symbolicMode = false;
	private boolean editing = false;

	/**
	 * Creates new text field
	 *
	 * @param c
	 *            construction
	 */
	public GeoInputBox(Construction c) {
		super(c);
		length = defaultLength;
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

	@Override
	public boolean isTextField() {
		return true;
	}

	/**
	 * @param geo
	 *            new linked geo
	 */
	public void setLinkedGeo(GeoElementND geo) {
		linkedGeo = geo;
		text = getLinkedGeoText();

		// remove quotes from start and end
		if (text.length() > 0 && text.charAt(0) == '"') {
			text = text.substring(1);
		}
		if (text.length() > 0 && text.charAt(text.length() - 1) == '"') {
			text = text.substring(0, text.length() - 1);
		}
	}

	private String getLinkedGeoText() {
		if (linkedGeo.isGeoNumeric()) {
			return getSymbolicNumberText();
		} else if (isSymbolicMode()) {
			return toLaTex(linkedGeo);
		}
		return linkedGeo.getValueForInputBar();
	}

	private String toLaTex(GeoElementND geo) {
		return geo.toLaTeXString(true, StringTemplate.latexTemplate);
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
		if (linkedGeo == null) {
			return "";
		}
		return text;
	}

	/**
	 * Set the text
	 *
	 * @param newText
	 *            new text value
	 */
	public void setText(String newText) {
		text = newText;
	}

	/**
	 * Get the text (used for scripting)
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
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
		if (linkedGeo != null) {

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
		String defineText = inputText;
		boolean imaginaryAdded = false;
		if (linkedGeo.isGeoLine()) {

			// not y=
			// and not Line[A,B]
			if ((defineText.indexOf('=') == -1)
					&& (defineText.indexOf('[') == -1)) {
				// x + 1 changed to
				// y = x + 1
				defineText = "y=" + defineText;
			}

			String prefix = linkedGeo.getLabel(tpl) + ":";
			// need a: in front of
			// X = (-0.69, 0) + \lambda (1, -2)
			if (!defineText.startsWith(prefix)) {
				defineText = prefix + defineText;
			}
		} else if (linkedGeo.isGeoText()) {
			defineText = "\"" + defineText + "\"";
		} else if (linkedGeo.isGeoPoint()) {
			if (((GeoPointND) linkedGeo)
					.getToStringMode() == Kernel.COORD_COMPLEX) {

				// make sure user can enter regular "i"
				defineText = defineText.replace('i', Unicode.IMAGINARY);

				// z=2 doesn't work for complex numbers (parses to
				// GeoNumeric)
				defineText = defineText + "+0" + Unicode.IMAGINARY;
				imaginaryAdded = true;
			}
		} else if (linkedGeo instanceof FunctionalNVar) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((FunctionalNVar) linkedGeo).getVarString(tpl) + ")="
					+ defineText;
		}

		if ("".equals(defineText.trim())) {
			return;
		}

		double num = Double.NaN;
		ExpressionNode parsed = null;

		if (linkedGeo.isGeoNumeric()) {
			try {
				parsed = kernel.getParser().parseExpression(inputText);

			} catch (Throwable e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		// for a simple number, round it to the textfield setting (if set)
		if (parsed != null && parsed.isConstant()
				&& !linkedGeo.isGeoAngle()
				&& (printDecimals > -1 || printFigures > -1)) {
			try {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				num = kernel.getAlgebraProcessor().evaluateToDouble(inputText,
						false, null);
				defineText = kernel.format(num, tpl);

			} catch (Exception e) {
				// user has entered eg 33+
				// do nothing
				e.printStackTrace();
			}
		}

		try {
			if (linkedGeo instanceof GeoNumeric && linkedGeo.isIndependent()
					&& parsed != null && parsed.isConstant()) {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				kernel.getAlgebraProcessor().evaluateToDouble(defineText, false,
						(GeoNumeric) linkedGeo);

				// setValue -> avoid slider range changing

				linkedGeo.updateRepaint();

			} else {
				final boolean imaginary = imaginaryAdded;
				EvalInfo info = new EvalInfo(!cons.isSuppressLabelsActive(),
						linkedGeo.isIndependent(), false).withSliders(false);

				// TRAC-5294 make sure user input gives the correct type
				// so that eg construction isn't killed by entering "y"
				// in a box linked to a number

				// kernel.setSilentMode(true);
				// try {
				// ValidExpression exp = kernel.getParser()
				// .parseGeoGebraExpression(defineText);
				// GeoElementND[] geos = kernel.getAlgebraProcessor()
				// .processValidExpression(exp);
				//
				// if (!(geos[0].getGeoClassType()
				// .equals(linkedGeo.getGeoClassType()))) {
				// showError();
				// return;
				//
				// }
				//
				// } catch (Throwable t) {
				// showError();
				// return;
				// } finally {
				// kernel.setSilentMode(false);
				// }

				kernel.getAlgebraProcessor()
						.changeGeoElementNoExceptionHandling(linkedGeo,
								defineText, info, true,
								new AsyncOperation<GeoElementND>() {

									@Override
									public void callback(GeoElementND obj) {
										if (imaginary) {
											ExpressionNode def = obj
													.getDefinition();
											if (def != null
													&& def.getOperation() == Operation.PLUS
													&& def.getRight()
															.toString(
																	StringTemplate.defaultTemplate)
															.equals("0"
																	+ Unicode.IMAGINARY)) {
												obj.setDefinition(
														def.getLeftTree());
												setLinkedGeo(obj);
												obj.updateRepaint();
												return;
											}

										}
										setLinkedGeo(obj);
									}
								}, kernel.getApplication().getErrorHandler());
				return;
			}
		} catch (MyError e1) {
			kernel.getApplication().showError(e1);
			return;
		} catch (Exception e1) {
			Log.error(e1.getMessage());
			showError();
			return;
		}
		this.setLinkedGeo(linkedGeo);

	}

	private boolean isLinkedNumberValueNotChanged(String text) {
		if (isSymbolicMode() && canBeSymbolicNumber()) {
			GeoNumeric evaluatedNumber = new GeoNumeric(kernel.getConstruction());
			kernel.getAlgebraProcessor().evaluateToDouble(text, true, evaluatedNumber);
			String linkedNonSymbolic = getNonSymbolicNumberValue(linkedGeo);
			return linkedNonSymbolic != null && linkedNonSymbolic
					.equals(getNonSymbolicNumberValue(evaluatedNumber));
		}

		return false;
	}

	private String getNonSymbolicNumberValue(GeoElementND geo) {
		if (!geo.isGeoNumeric()) {
			return null;
		}
		return getFormattedDouble((GeoNumeric) geo);
	}

	private void showError() {
		kernel.getApplication().showError(Errors.InvalidInput);
	}

	/**
	 * Called by a Drawable for this object when it is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void updateText(TextObject textFieldToUpdate) {

		if (linkedGeo != null) {

			String linkedText;

			if (linkedGeo.isGeoText()) {
				linkedText = ((GeoText) linkedGeo).getTextString();
			} else if (linkedGeo.getParentAlgorithm() instanceof AlgoPointOnPath
					|| linkedGeo
							.getParentAlgorithm() instanceof AlgoPointInRegion) {
				linkedText = linkedGeo.toValueString(tpl);
			} else {

				// want just a number for eg a=3 but we want variables for eg
				// y=m x + c
				boolean substituteNos = linkedGeo.isGeoNumeric()
						&& linkedGeo.isIndependent();
				linkedText = linkedGeo.getFormulaString(tpl, substituteNos);
			}

			if (linkedText == null) {
				linkedText = "";
			}

			if (linkedGeo.isGeoText() && (linkedText.indexOf("\n") > -1)) {
				// replace linefeed with \\n
				while (linkedText.indexOf("\n") > -1) {
					linkedText = linkedText.replaceAll("\n", "\\\\\\\\n");
				}
			}
			if (!textFieldToUpdate.getText().equals(linkedText)) { // avoid
																	// redraw
																	// error
				textFieldToUpdate.setText(linkedText);
			}

		} else {
			textFieldToUpdate.setText(text);
		}

		if (isSymbolicMode()) {
			setText(getLinkedGeoText());
		} else if (isLinkedNumberValueNotChanged(text)) {
			setText(getNonSymbolicNumberValue(linkedGeo));
		} else {
			setText(textFieldToUpdate.getText());
		}
	}

	/**
	 * Called by a Drawable when its text object is updated
	 *
	 * @param textFieldToUpdate
	 *            the Drawable's text field
	 */
	public void textObjectUpdated(TextObject textFieldToUpdate) {
		if (linkedGeo != null) {
			updateLinkedGeo(textFieldToUpdate.getText());
			updateText(textFieldToUpdate);
		} else {
			setText(textFieldToUpdate.getText());
		}
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
		if (linkedGeo == null) {
			return;
		}

		this.symbolicMode = mode;
		setText(getLinkedGeoText());
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
		return linkedGeo != null && ((canBeSymbolicNumber()) || linkedGeo.isGeoFunction());
	}

	private boolean canBeSymbolicNumber() {
		if (!linkedGeo.isGeoNumeric()) {
			return false;
		}

		GeoNumeric number = (GeoNumeric)linkedGeo;
		return !number.isAngle();
	}

	/**
	 *
	 * @return text to edit.
	 */
	public String getTextForEditor() {
		if (!isSymbolicMode()) {
			return getText();
		}

		if (linkedGeo.isGeoNumeric()) {
			return getLinkedSymbolicNumberForEditor();
		}

		return getLinkedGeoTextForEditor();
	}

	private String getLinkedSymbolicNumberForEditor() {
		if (!linkedGeo.isDefined()) {
			return "?";
		}
		GeoNumeric number = (GeoNumeric) linkedGeo;
		return isLatexNeededFor(number) ? number.toLaTeXString(true, tpl)
				: getFormattedDouble(number);
	}

	private String getFormattedDouble(GeoNumeric number) {
		return kernel.format(number.getValue(), tpl);
	}

	private String getSymbolicNumberText() {
		if (!linkedGeo.isDefined()) {
			return "?";
		}
		GeoNumeric number = (GeoNumeric) linkedGeo;
		return isLatexNeededFor(number) ? toLaTex(number) : getFormattedDouble(number);
	}

	private boolean isLatexNeededFor(GeoNumeric number) {
		return symbolicMode && !number.isSimple();
	}

	private String getLinkedGeoTextForEditor() {
		return linkedGeo.getValueForInputBar();
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
}
