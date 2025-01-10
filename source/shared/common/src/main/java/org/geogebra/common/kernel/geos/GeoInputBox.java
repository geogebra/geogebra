package org.geogebra.common.kernel.geos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.SymbolicEditor;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.inputbox.EditorContent;
import org.geogebra.common.kernel.geos.inputbox.InputBoxProcessor;
import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextObject;

import com.himamis.retex.editor.share.util.Unicode;

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
	StringTemplate tpl = StringTemplate.defaultTemplate;

	protected boolean symbolicMode = true;

	private HorizontalAlignment textAlignment = HorizontalAlignment.LEFT;

	private @Nonnull
	GeoElementND linkedGeo;

	private @Nonnull
	InputBoxProcessor inputBoxProcessor;
	private @Nonnull
	InputBoxRenderer inputBoxRenderer;

	private String tempUserEvalInput;
	private String tempUserDisplayInput;

	private boolean serifContent = true;

	/**
	 * Creates new text field
	 * @param cons construction
	 */
	public GeoInputBox(Construction cons, GeoElement linkedGeo) {
		super(cons);
		if (linkedGeo == null) {
			this.linkedGeo = new GeoText(cons, "");
		} else {
			this.linkedGeo = linkedGeo;
		}
		inputBoxRenderer = new InputBoxRenderer(this);
		inputBoxProcessor = new InputBoxProcessor(this, this.linkedGeo);
	}

	/**
	 * @param cons construction
	 * @param labelOffsetX x offset
	 * @param labelOffsetY y offset
	 */
	public GeoInputBox(Construction cons, GeoElement linkedGeo,
			int labelOffsetX, int labelOffsetY) {
		this(cons, linkedGeo);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	/**
	 *
	 * @param geo to check.
	 * @return if geo is linkable to inputBox.
	 */
	public static boolean isGeoLinkable(GeoElement geo) {
		if (geo.isGeoImage() || geo.isGeoButton() || geo.isGeoBoolean()) {
			return false;
		}

		if (hasCommand(geo.getDefinition())) {
			return false;
		}

		return !geo.isCommandOutput();
	}

	private static boolean hasCommand(ExpressionNode node) {
		return node != null && node.inspect(
			t -> {
				return t.isGeoElement() && ((GeoElement) t).isCommandOutput();
			}
		);
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
	 * @param geo new linked geo
	 */
	public void setLinkedGeo(GeoElementND geo) {
		if (geo == null) {
			linkedGeo = new GeoText(cons, "");
		} else {
			linkedGeo = geo;
		}

		inputBoxRenderer.setLinkedGeo(geo);
		inputBoxProcessor = new InputBoxProcessor(this, linkedGeo);
	}

	/**
	 * @return text to edit with the symbolic editor
	 */
	public String getTextForEditor() {
		String textForEditor = getTextForEditor(StringTemplate.inputBoxTemplate);
		return textForEditor.replace(Unicode.IMAGINARY, 'i');
	}

	/**
	 * @return editor state on edit, inputbox flat string otherwise
	 */
	public String getInputBoxState() {
		if (app.getActiveEuclidianView().getSymbolicEditor() != null) {
			SymbolicEditor editor = app.getActiveEuclidianView().getSymbolicEditor();
			if (editor.getGeoInputBox() == this) {
				return editor.getEditorState();
			}
		}
		return getTextForEditor();
	}

	/**
	 * set inputbox state
	 * @param input flat string
	 */
	public void setInputBoxState(String input) {
		SymbolicEditor editor = app.getActiveEuclidianView().initSymbolicEditor();
		if (editor != null) {
			String latex = editor.getLatexInput(input);
			updateLinkedGeo(input, latex);
		}
	}

	private String getTextForEditor(StringTemplate tpl) {
		if (tempUserEvalInput != null) {
			return tempUserEvalInput;
		}

		if (linkedGeo.isGeoText()) {
			return ((GeoText) linkedGeo).getTextStringSafe().replace("\n", GeoText.NEW_LINE);
		}

		if (isSymbolicMode() && isListEditor()) {
			return inputBoxRenderer.getStringForFlatList(tpl);
		}

		String linkedGeoText;
		if (hasLaTeXEditableVector()) {
			linkedGeoText = ((GeoVectorND) linkedGeo).toValueStringAsColumnVector(this.tpl);
		} else if (linkedGeo.isPointInRegion() || linkedGeo.isPointOnPath()) {
			linkedGeoText = linkedGeo.toValueString(StringTemplate.inputBoxTemplate);
		} else {
			linkedGeoText = linkedGeo.getRedefineString(true, true, tpl);
		}

		if ("?".equals(linkedGeoText)) {
			return "";
		}

		return linkedGeoText;
	}

	/**
	 * Get the string that should be displayed by the renderer.
	 * @return editor display string
	 */
	public String getDisplayText() {
		return isSymbolicMode() && tempUserDisplayInput != null
				? tempUserDisplayInput
				: getText();
	}

	private boolean hasLaTeXEditableVector() {
		return linkedGeo instanceof GeoVectorND
				&& linkedGeo.hasSpecialEditor();
	}

	/**
	 * Get the text to display and edit with the non symbolic editor
	 * @return the text
	 */
	public String getText() {
		if (tempUserEvalInput != null) {
			return tempUserEvalInput;
		}
		return inputBoxRenderer.getText();
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return super.isLaTeXTextCommand();
	}

	/**
	 * Returns the linked geo
	 * @return linked geo
	 */
	public @Nonnull
	GeoElementND getLinkedGeo() {
		return linkedGeo;
	}

	@Override
	public String toValueString(StringTemplate tpl1) {
		if (isSymbolicMode() && tpl1.getStringType() != StringType.LATEX) {
			return getTextForEditor(tpl1.isScreenReader() ? tpl1 : StringTemplate.editTemplate);
		}
		return getText();
	}

	@Override
	public boolean isGeoInputBox() {
		return true;
	}

	/**
	 * Sets length of the input box
	 * @param len new length
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
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);

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
			sb.append("\t<contentSerif val=\"").append(serifContent).append("\" />\n");
		}

		if (getLength() != defaultLength) {
			sb.append("\t<length val=\"");
			sb.append(getLength());
			sb.append("\"/>\n");
		}
		if (getAlignment() != HorizontalAlignment.LEFT) {
			sb.append("\t<textAlign val=\"");
			sb.append(getAlignment().toString());
			sb.append("\"/>\n");
		}

		if (tempUserDisplayInput != null
				&& tempUserEvalInput != null) {
			sb.append("\t<tempUserInput display=\"");
			StringUtil.encodeXML(sb, tempUserDisplayInput);
			sb.append("\" eval=\"");
			StringUtil.encodeXML(sb, tempUserEvalInput);
			sb.append("\"/>\n");
		}

		// for input boxes created without linked object save the
		// input in the tempUserInput
		if (linkedGeo.isGeoText() && !linkedGeo.isLabelSet()) {
			sb.append("\t<tempUserInput eval=\"");
			StringUtil.encodeXML(sb, ((GeoText) linkedGeo).getTextStringSafe());
			sb.append("\"/>\n");
		}
	}

	@Override
	public GeoElement copy() {
		return new GeoInputBox(cons, null, labelOffsetX, labelOffsetY);
	}

	/**
	 * @param inputText new value for linkedGeo
	 * @param latex editor content as LaTeX
	 * @param entries matrix entries
	 */
	public void updateLinkedGeo(String inputText, String latex, String... entries) {
		inputBoxProcessor.updateLinkedGeo(new EditorContent(inputText, latex,
				entries, getRows()), tpl);
		getKernel().getApplication().storeUndoInfo();
	}

	/**
	 * @param inputText plain text input
	 */
	public void updateLinkedGeo(String inputText) {
		updateLinkedGeo(inputText, null);
	}

	/**
	 * Called by a Drawable for this object when it is updated
	 * @param textFieldToUpdate the Drawable's text field
	 */
	public void updateText(TextObject textFieldToUpdate) {
		// avoid redraw error
		String linkedText = getText();
		if (!textFieldToUpdate.getText().equals(linkedText)) {
			textFieldToUpdate.setText(linkedText);
		}
	}

	/**
	 * Called by a Drawable when its text object is updated
	 * @param textFieldToUpdate the Drawable's text field
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
		inputBoxRenderer.updateLatexTemplate();
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
	public DescriptionMode getDescriptionMode() {
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
		ScreenReaderBuilder sb = new ScreenReaderBuilder(getKernel().getLocalization());
		sb.append(getKernel().getLocalization().getMenu("TextField"));
		sb.appendSpace();
		if (!addAuralCaption(sb) && isLabelVisible()) {
			addAuralLabel(sb);
		}
		return sb.toString();
	}

	@Override
	public void initSymbolicMode() {
		this.symbolicMode = false;
	}

	/**
	 * Sets the symbolic mode.
	 * @param symbolicMode True for symbolic mode
	 */
	public void setSymbolicMode(boolean symbolicMode) {
		setSymbolicMode(symbolicMode, false);
	}

	@Override
	public void setSymbolicMode(boolean mode, boolean updateParent) {
		this.symbolicMode = mode;
	}

	@Override
	public boolean isSymbolicMode() {
		return symbolicMode;
	}

	@Override
	public boolean supportsEngineeringNotation() {
		return false;
	}

	@Override
	public void setEngineeringNotationMode(boolean mode) {
		// Not needed
	}

	@Override
	public boolean isEngineeringNotationMode() {
		return false;
	}

	boolean hasSymbolicFunction() {
		return linkedGeo instanceof GeoFunction || linkedGeo instanceof GeoFunctionNVar;
	}

	@Override
	public void setAlignment(HorizontalAlignment alignment) {
		textAlignment = alignment;
	}

	@Override
	public HorizontalAlignment getAlignment() {
		return textAlignment;
	}

	/**
	 * @return whether the alpha button should be shown
	 */
	public boolean needsSymbolButton() {
		return getLength() >= EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH
				&& !(linkedGeo instanceof GeoText && linkedGeo.isLabelSet());
	}

	/**
	 * Get the temporary user evaluation input. This input is
	 * in ASCII math format and can be evaluated.
	 * @return user eval input
	 */
	public String getTempUserEvalInput() {
		return tempUserEvalInput;
	}

	/**
	 * Set the temporary user evaluation input. This input
	 * must be in ASCII math format.
	 * @param eval temporary user eval input
	 */
	public void setTempUserInput(String eval, String display) {
		this.tempUserEvalInput = eval;
		this.tempUserDisplayInput = display;
	}

	/**
	 * Get the temporary user display input. This input
	 * can be in ASCII or LaTeX format.
	 * @return temporary display user input
	 */
	public String getTempUserDisplayInput() {
		return this.tempUserDisplayInput;
	}

	/**
	 * Clears the temp user inputs.
	 */
	public void clearTempUserInput() {
		this.tempUserDisplayInput = null;
		this.tempUserEvalInput = null;
	}

	public boolean isSerifContent() {
		return serifContent;
	}

	public void setSerifContent(boolean serifContent) {
		this.serifContent = serifContent;
	}

	public boolean hasError() {
		return !StringUtil.emptyTrim(getTempUserEvalInput());
	}

	private int getRows() {
		return linkedGeo instanceof GeoList  ? ((GeoList) linkedGeo).size()
				: (linkedGeo instanceof GeoVectorND ? ((GeoVectorND) linkedGeo).getDimension() : 1);
	}

	/**
	 * linked geo type (needed for input box specific keyboard)
	 * @return input box type
	 */
	public InputBoxType getInputBoxType() {
		if (linkedGeo instanceof GeoFunction) {
			return ((GeoFunction) linkedGeo).isInequality()
					? InputBoxType.INEQ_BOOL : InputBoxType.FUNCTION;
		} else if (linkedGeo instanceof GeoSurfaceCartesianND
			|| linkedGeo instanceof GeoCurveCartesianND) {
			return InputBoxType.FUNCTION;
		} else if (linkedGeo instanceof GeoFunctionNVar) {
			return ((GeoFunctionNVar) linkedGeo).isInequality()
					? InputBoxType.INEQ_BOOL : InputBoxType.FUNCTION;
		} else if (linkedGeo instanceof GeoBoolean) {
			return InputBoxType.INEQ_BOOL;
		} else if (linkedGeo instanceof GeoList || linkedGeo instanceof GeoVectorND) {
			return InputBoxType.VECTOR_MATRIX;
		} else {
			return InputBoxType.DEFAULT;
		}
	}

	/**
	 * variables of linked geo if it is a function.
	 *
	 * @return the list of variable names.
	 */
	public List<String> getFunctionVars() {
		FunctionVariable[] functionVariables = linkedGeo instanceof VarString
			? ((VarString) linkedGeo).getFunctionVariables()
			: null;

		if (functionVariables == null) {
			return Collections.emptyList();
		}

		return Arrays.stream(functionVariables).map(this::getVariableName)
				.collect(Collectors.toList());
	}

	private String getVariableName(FunctionVariable functionVariable) {
		String name = functionVariable.getSetVarString();
		return name.replaceAll("[{}]", "");
	}

	/**
	 * @return whether to use the special editor for lists (no braces)
	 */
	public boolean isListEditor() {
		return linkedGeo instanceof GeoList && linkedGeo.hasSpecialEditor()
				&& !((GeoList) linkedGeo).isMatrix();
	}

	/**
	 * @return whether it is a symbolic input box linked to a geo with special editor
	 */
	public boolean isSymbolicModeWithSpecialEditor() {
		return isSymbolicMode() && linkedGeo.hasSpecialEditor();
	}

	public boolean validate(EditorContent editorState, StringBuilder sb) {
		return inputBoxProcessor.validate(editorState, sb);
	}
}