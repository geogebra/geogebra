package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.AlgoCasCellInterface;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.GeoSurfaceReplacer;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.Inspecting.CommandFinder;
import org.geogebra.common.kernel.arithmetic.Inspecting.IneqFinder;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant.ArbconstReplacer;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandRemover;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.DummyVariableCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.cas.AlgoDependentCasCell;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Cell pair of input and output strings used in the CAS view. This needs to be
 * a GeoElement in order to handle dependencies between cells and other
 * GeoElements together with AlgoSymbolic.
 * 
 * @author Markus Hohenwarter
 */

public class GeoCasCell extends GeoElement
		implements VarString, TextProperties, GeoSymbolicI {
	private static final int TOOLTIP_SCREEN_WIDTH = 80;

	/**
	 * Symbol for static reference
	 */
	public static final char ROW_REFERENCE_STATIC = '#';

	/**
	 * Symbol for dynamic reference
	 */
	public static final char ROW_REFERENCE_DYNAMIC = '$';

	/**
	 * Assignment variable used when plotting with marble
	 */
	private static final String PLOT_VAR = "GgbmpvarPlot";

	private ValidExpression inputVE;
	private ValidExpression evalVE;
	private ValidExpression outputVE;
	private String input;
	private String prefix;
	private String postfix;
	private String error;
	private String latex;
	private String latexInput;
	private String localizedInput;
	private String currentLocaleStr;
	private boolean suppressOutput = false;
	private AssignmentType assignmentType = AssignmentType.NONE;

	private boolean keepInputUsed;

	// input variables of this cell
	private TreeSet<String> invars;
	private TreeSet<String> functionvars;
	// defined input GeoElements of this cell
	private TreeSet<GeoElement> inGeos;
	private boolean isCircularDefinition;

	// twin geo, e.g. GeoCasCell m := 8 creates GeoNumeric m = 8
	private GeoElement twinGeo;
	private boolean firstComputeOutput;
	private boolean ignoreTwinGeoUpdate;

	private String assignmentVar;
	private boolean includesRowReferences;
	private boolean includesNumericCommand;
	private boolean useGeoGebraFallback;

	private String evalCmd;
	private String evalComment;
	private int row = -1; // for CAS view, set by Construction
	private int preferredRowNumber = -1;
	// use this cell as text field
	private boolean useAsText;
	// for the future, is only holding font infos
	private GeoText commentText;
	private boolean nativeOutput;

	private ArrayList<Vector<String>> substList;

	private boolean nSolveCmdNeeded = false;
	// make sure we don't enter setAssignmentVar from itself
	private boolean ignoreSetAssignment = false;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	private ValidExpression expandedEvalVE;
	private boolean pointList;

	private String tooltip;

	/**
	 * Creates new CAS cell
	 * 
	 * @param c
	 *            construction
	 */

	public GeoCasCell(final Construction c) {
		super(c);
		input = "";
		localizedInput = "";
		setInputVE(null);
		outputVE = null;
		prefix = "";
		evalVE = null;
		postfix = "";
		evalCmd = "";
		evalComment = "";
		useAsText = false;
		commentText = new GeoText(c, "");
		twinGeo = null;
		// setGeoText(commentText);
		substList = new ArrayList<>();
	}

	@Override
	public void setAssignmentType(AssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	/**
	 * @return the current {@link AssignmentType}
	 */
	public AssignmentType getAssignmentType() {
		return assignmentType;
	}

	/**
	 * @return whether KeepInput command is part of this expression
	 */
	public boolean isKeepInputUsed() {
		return keepInputUsed;
	}

	/**
	 * Sets this GeoCasCell to the current state of geo which also needs to be a
	 * GeoCasCell. Note that twinGeo is kept null.
	 */
	@Override
	public void set(final GeoElementND geo) {
		// some dead code removed in r20927
	}

	/**
	 * Returns the input of this row. Command names are localized when
	 * kernel.isPrintLocalizedCommandNames() is true, otherwise internal command
	 * names are used.
	 * 
	 * @param tpl
	 *            string template
	 * @return input string
	 */
	public String getInput(final StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			// input with localized command names
			if (currentLocaleStr == null
					|| !currentLocaleStr.equals(getLoc().getLocaleStr())) {
				updateLocalizedInput(tpl, input);
			}
			return localizedInput;
		}
		// input with internal command names
		return input;
	}

	/**
	 * Returns the output of this row.
	 * 
	 * @param tpl
	 *            string template
	 * @return output string
	 */
	public String getOutput(StringTemplate tpl) {
		if (isError()) {
			return localizedError(tpl);
		}

		if (outputVE == null) {
			return "";
		}

		return outputVE.toAssignmentString(tpl, getAssignmentType());
	}

	private String localizedError(StringTemplate tpl) {
		if (tpl.isPrintLocalizedCommandNames()) {
			if (error.startsWith(Errors.UndefinedVariable.getKey())) {
				return Errors.UndefinedVariable.getError(getLoc())
						+ ": "
						+ error.substring(
								Errors.UndefinedVariable.getKey()
										.length());
			}
			return getLoc().getError(error);
		}
		return error;
	}

	/**
	 * Returns the output of this row without any definitions. where getOutput
	 * returns g: x+y=1, this returns only x+y=1
	 * 
	 * @param tpl
	 *            string template
	 * @return output string
	 */
	public String getOutputRHS(StringTemplate tpl) {
		if (isError()) {
			return localizedError(tpl);
		}

		if (outputVE == null) {
			return "";
		}

		return outputVE.toString(tpl);
	}

	/**
	 * @return prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the evaluation text (between prefix and postfix) of this row
	 * using internal command names. This method is important to process this
	 * row using GeoGebraCAS. XML template is used because we need both maximal
	 * precision and internal commands
	 * 
	 * @return the evaluation text
	 */
	public String getEvalText() {
		if (evalVE == null) {
			return "";
		}
		return evalVE.toString(StringTemplate.xmlTemplate);
	}

	/**
	 * Returns the evaluation expression (between prefix and postfix) of this
	 * row. This method is important to process this row using GeoGebraCAS.
	 * 
	 * @return the evaluation expression
	 */
	public ValidExpression getEvalVE() {
		return evalVE;
	}

	/**
	 * @return input expression
	 */
	public ValidExpression getInputVE() {
		return inputVE;
	}

	/**
	 * @return postfix
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * @return LaTeX representation of output
	 */
	public String getLaTeXOutput() {
		return getLaTeXOutput(true);
	}

	/**
	 * @param bold
	 *            bold?
	 * @return LaTeX representation of output
	 */
	public String getLaTeXOutput(boolean bold) {

		if (useAsText) {
			return "\\text{" + this.commentText.getTextString() + "}";
		}

		if (isError()) {
			return "";
		} else if (latex == null) {
			if (outputVE != null) {
				StringBuilder sb = new StringBuilder();

				// create LaTeX string
				if (nativeOutput || !(outputVE instanceof ExpressionNode)) {
					// #5119 use same rounding as in Algebra, but avoid 3.14 ->
					// pi hack
					sb.append(outputVE.toAssignmentLaTeXString(
							getLaTeXTemplate(),
							getAssignmentType()));
				} else {
					GeoElement geo = ((GeoElement) ((ExpressionNode) outputVE)
							.getLeft());
					appendLaTeXOutputGeo(sb, geo);

				}

				latex = sb.toString();
				// TODO Uncomment once support for latex line breaking is
				// implemented.
				// kernel.setInsertLineBreaks(oldLineBreaks);
			}
		}

		return bold ? "\\mathbf{" + latex + "}" : latex;
	}

	/**
	 * @return template used for LaTeX output
	 */
	public StringTemplate getLaTeXTemplate() {
		return includesNumericCommand() ? StringTemplate.numericLatex
				: StringTemplate.latexTemplateCAS;
	}

	private void appendLaTeXOutputGeo(StringBuilder sb, GeoElement geo) {
		if (isAssignmentVariableDefined()) {
			sb.append(getAssignmentLHS(StringTemplate.latexTemplateCAS));
			if (geo instanceof GeoFunction
					|| geo instanceof GeoSurfaceCartesianND) {
				sb.append('(');
				sb.append(((VarString) geo)
						.getVarString(StringTemplate.latexTemplateCAS));
				sb.append(')');
			}

			switch (getAssignmentType()) {
			case DEFAULT:
				sb.append(outputVE.getAssignmentOperator().trim());
				break;
			case DELAYED:
				sb.append(outputVE.getDelayedAssignmentOperator().trim());
				break;
			case NONE:
				break;

			}
		}
		if (!(geo instanceof GeoLocus)) {
			sb.append(geo.toValueString(StringTemplate.latexTemplateCAS));
		} else {
			// as GeoLocuses can not be converted to value strings
			sb.append(geo.algoParent
					.getDefinition(StringTemplate.latexTemplateCAS));
		}
	}

	/**
	 * @return whether this cell is used as comment
	 */
	public boolean isUseAsText() {
		return useAsText;
	}

	/**
	 * @param val
	 *            true to use this cell as comment only
	 */
	public void setUseAsText(final boolean val) {
		useAsText = val;
		// TODO: by expanding the GeoText functionality, this could become a
		// problem
		if (!val) {
			this.input = this.commentText.getTextString();
		} else {
			this.commentText.setTextString(input);
		}
		suppressOutput = useAsText;
		// recalc row height
		update();
	}

	/**
	 * @param val
	 *            true if we should set evalCmd to NSolve
	 */
	public void setNSolveCmdNeeded(boolean val) {
		this.nSolveCmdNeeded = val;
	}

	/**
	 * @return nSolveCmdNeeded
	 */
	public boolean getNSolveCmdNeeded() {
		return this.nSolveCmdNeeded;
	}

	/**
	 * @param ft
	 *            font
	 */
	public void setFont(GFont ft) {
		setFontSizeMultiplier((double) ft.getSize()
				/ (double) kernel.getApplication().getFontSize());
		setFontStyle(ft.getStyle());
	}

	/**
	 * @param style
	 *            font style
	 */
	@Override
	public void setFontStyle(int style) {
		commentText.setFontStyle(style);
	}

	/**
	 * @return font color
	 */
	public GColor getFontColor() {
		return this.getObjectColor();
	}

	/**
	 * @param c
	 *            font color
	 */
	public void setFontColor(GColor c) {
		this.setObjColor(c);
	}

	/**
	 * @return font style
	 */
	@Override
	public int getFontStyle() {
		return commentText.getFontStyle();
	}

	/**
	 * @param d
	 *            font size multiplier
	 */
	@Override
	public void setFontSizeMultiplier(double d) {
		commentText.setFontSizeMultiplier(d);
	}

	/**
	 * @return font size
	 */
	@Override
	public double getFontSizeMultiplier() {
		return commentText.getFontSizeMultiplier();
	}

	/**
	 * @param gt
	 *            comment text
	 */
	public void setGeoText(GeoText gt) {
		if (gt != null) {
			commentText = gt;
			// setInput(gt.toString());
		}
	}

	/**
	 * @return comment text
	 */
	public GeoText getGeoText() {
		return commentText;
	}

	/**
	 * @return whether input and output are empty
	 */
	public boolean isEmpty() {
		return isInputEmpty() && isOutputEmpty();
	}

	/**
	 * @return whether input is empty
	 */
	public boolean isInputEmpty() {
		return getInputVE() == null;
	}

	/**
	 * @return whether output is empty
	 */
	public boolean isOutputEmpty() {
		return outputVE == null && !isError();
	}

	/**
	 * @return true if output is not empty and can be shown
	 */
	public boolean showOutput() {
		return !isOutputEmpty() && !suppressOutput();
	}

	private boolean suppressOutput() {
		return suppressOutput && !isError();
	}

	/**
	 * Returns if this GeoCasCell has a twinGeo or not
	 * 
	 * @return if this GeoCasCell has a twinGeo or not
	 */
	public boolean hasTwinGeo() {
		return twinGeo != null;
	}

	/**
	 * Sets the input of this row using the current casTwinGeo.
	 * 
	 * @param force
	 *            force update (needed if twin geo is a slider)
	 * @param dragging
	 *            whether this was triggered by drag
	 */
	public void setInputFromTwinGeo(boolean force, boolean dragging) {
		if (ignoreTwinGeoUpdate && !force) {
			return;
		}

		if (twinGeo != null && twinGeo.isIndependent()
				&& twinGeo.isLabelSet()) {
			// Update ASSIGNMENT of twin geo
			// e.g. m = 8 changed in GeoGebra should set cell to m := 8
			String assignmentStr = twinGeo
					.toCasAssignment(StringTemplate.defaultTemplate);
			if (suppressOutput) {
				assignmentStr = assignmentStr + ";";
			}
			String evalCmd1 = evalCmd;
			if (setInput(assignmentStr)) {
				if ("Numeric".equals(evalCmd1)) {
					setProcessingInformation("",
							"Numeric["
									+ evalVE.toString(
											StringTemplate.defaultTemplate)
									+ "]",
							"");
				}
				setEvalCommand(evalCmd1);
				// GGB-1249 don't update the cell if dragging
				if (!dragging) {
					computeOutput(false, false);
				}
				update();
			}
		}
	}

	/**
	 * Sets the input of this row.
	 * 
	 * @param inValue
	 *            input value
	 * @return success
	 */
	public boolean setInput(String inValue) {
		return setInput(inValue, false);
	}

	/**
	 * Sets the input of this row.
	 * 
	 * @param inValue
	 *            input value
	 * @param internalInput
	 *            true if the input is in internal format, otherwise false (i.e.
	 *            user input)
	 * @return success
	 */
	public boolean setInput(String inValue, boolean internalInput) {
		String inNotNull = inValue != null ? inValue : "";
		// if the cell is used as comment, treat it as empty
		if (useAsText) {
			suppressOutput = true;
			setInputVE(null);
			this.commentText.setTextString(inNotNull);
		} else { // parse input into valid expression
			suppressOutput = inNotNull.endsWith(";");
			// with nSolve command do not update inputVE
			// only the input string
			if (!nSolveCmdNeeded) {
				setInputVE(parseGeoGebraCASInputAndResolveDummyVars(inNotNull));
			}
		}
		latexInput = null;
		input = inNotNull; // remember exact user input

		// APPS-428 avoid problem with " " changing to " *"
		while (input.indexOf("  ") > -1) {
			input = input.replace("  ", " ");
		}

		prefix = "";
		evalVE = getInputVE();
		postfix = "";
		setEvalCommand("");
		setEvalComment("");
		setError(null);

		// update input and output variables
		updateInputVariables(getInputVE());

		// input should have internal command names
		if (!internalInput) {
			internalizeInput();
		}

		// for efficiency: input with localized command names
		updateLocalizedInput(StringTemplate.defaultTemplate, input);

		// make sure computeOutput() knows that input has changed
		firstComputeOutput = true;

		if (!isEmpty()) {
			// make sure we put this casCell into the construction set
			cons.addToGeoSetWithCasCells(this);
		}
		return true;
	}

	private void updateLocalizedInput(final StringTemplate tpl,
			final String input1) {
		// for efficiency: localized input with local command names
		currentLocaleStr = getLoc().getLocaleStr();
		localizedInput = localizeInput(input1, tpl);
	}

	/**
	 * Sets row number for CAS view. This method should only be called by
	 * {@link Construction#updateCasCellRows()}
	 * 
	 * @param row
	 *            row number
	 */
	final public void setRowNumber(final int row) {
		this.row = row;
	}

	/***
	 * Returns position of the given GeoCasCell object (free or dependent) in
	 * the construction list. This is the row number used in the CAS view.
	 * 
	 * @return row number of casCell for CAS view or -1 if casCell is not in
	 *         construction list
	 */
	final public int getRowNumber() {
		return row;
	}

	/**
	 * Updates row references strings in input by setting input =
	 * inputVE.toString()
	 */
	public void updateInputStringWithRowReferences() {
		updateInputStringWithRowReferences(false);
	}

	/**
	 * Updates input strings row references
	 * 
	 * @param force
	 *            true if update variable names also
	 */
	public void updateInputStringWithRowReferences(boolean force) {
		if (!includesRowReferences && !force) {
			return;
		}

		// inputVE will print the correct label, e.g. $4 for
		// the row reference
		input = getInputVE().toAssignmentString(StringTemplate.noLocalDefault,
				getAssignmentType());

		// TODO this always translates input.
		updateLocalizedInput(StringTemplate.defaultTemplate,
				getInputVE().toAssignmentString(StringTemplate.defaultTemplate,
						getAssignmentType()));

		if (suppressOutput) { // append ; if output is suppressed
			input = input + ";";
			localizedInput = localizedInput + ";";
		}
	}

	/**
	 * Sets how this row should be evaluated. Note that the input is NOT changed
	 * by this method, so you need to call setInput() first. Make sure that
	 * input = prefix + eval without wrapper command + postfix.
	 * 
	 * @param prefix
	 *            beginning part that should NOT be evaluated, e.g. "25a +"
	 * @param evaluate
	 *            part of the input that needs to be evaluated, e.g.
	 *            "Expand[(a+b)^2]"
	 * @param postfix
	 *            end part that should NOT be evaluated, e.g. " + "5 (c+d)"
	 */
	public void setProcessingInformation(final String prefix,
			final String evaluate, final String postfix) {
		String eval = evaluate;
		// needed for TRAC-3081
		if (eval.contains("CLIPBOARDmagicSTRING")) {
			eval = eval.replaceAll("CLIPBOARDmagicSTRING", "");
		}
		String postfix1 = postfix;
		String prefix1 = prefix;
		setEvalCommand("");
		setEvalComment("");
		if (prefix1 == null) {
			prefix1 = "";
		}
		if (postfix1 == null) {
			postfix1 = "";
		}

		// stop if input is assignment
		if (isAssignmentVariableDefined()) {
			eval = prefix1 + eval + postfix1;
			prefix1 = "";
			postfix1 = "";
		}

		// commented since this causes mode changes to evaluate to be ignored
		// when the input remains the same.
		// see ticket #1620
		//
		// nothing to do
		// if ("".equals(prefix) && "".equals(postfix) &&
		// localizedInput.equals(eval))
		// return;

		// parse eval text into valid expression
		evalVE = parseGeoGebraCASInputAndResolveDummyVars(eval);
		if (inputVE != null && inputVE.getLabel() != null && evalVE != null) {
			evalVE.setLabel(inputVE.getLabel());
		}

		if (evalVE != null) {
			evalVE = resolveInputReferences(evalVE, inGeos);
			if (evalVE.isTopLevelCommand()) {
				// extract command from eval
				setEvalCommand(evalVE.getTopLevelCommand().getName());
			}
			this.prefix = prefix1;
			this.postfix = postfix1;
		} else {
			evalVE = getInputVE();
			this.prefix = "";
			this.postfix = "";
		}
	}

	// private boolean hasPrefixOrPostfix() {
	// return prefix.length() > 0 && postfix.length() > 0;
	// }

	/**
	 * Checks if newInput is structurally equal to the current input String.
	 * 
	 * a+b/c is equal to a+(b/c), but not to (a+b)/c
	 * 
	 * @param newInput
	 *            new input
	 * @return whether newInput and current input have same stucture
	 */
	public boolean isStructurallyEqualToLocalizedInput(final String newInput) {
		if (localizedInput != null && localizedInput.equals(newInput)) {
			return true;
		}

		if (!kernel.getGeoGebraCAS().isStructurallyEqual(getInputVE(), newInput,
				getKernel())) {
			setError("CAS.SelectionStructureError");
			return false;
		}
		return true;
	}

	/**
	 * Parses the given expression and resolves variables as GeoDummy objects.
	 * The result is returned as a ValidExpression.
	 */
	private ValidExpression parseGeoGebraCASInputAndResolveDummyVars(
			final String inValue) {
		return kernel.getGeoGebraCAS().parseOutput(inValue, this, kernel);
	}

	/**
	 * Updates the set of input variables and array of input GeoElements. For
	 * example, the input "b := a + 5" has the input variable "a"
	 */
	private void updateInputVariables(final ValidExpression ve) {
		// clear var sets
		clearInVars();

		if (ve == null || useAsText) {
			return;
		}

		// get all command names
		HashSet<Command> commands = new HashSet<>();
		ve.traverse(CommandCollector.getCollector(commands));
		if (commands.isEmpty()) {
			commands = null;
		} else {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand
						|| ("Numeric".equals(cmdName)
								&& cmd.getArgumentNumber() > 1)
						|| "ScientificText".equals(cmdName);

				// if command not known to CAS
				if (!kernel.getGeoGebraCAS().isCommandAvailable(cmd)) {
					if (kernel.lookupCasCellLabel(cmdName) != null
							|| kernel.lookupLabel(cmdName) != null) {
						// treat command name as defined user function name
						getInVars().add(cmdName);
					} else if (kernel.getAlgebraProcessor()
							.isCommandAvailable(cmdName)) {
						// command is known to GeoGebra: use possible fallback
						useGeoGebraFallback = true;
					} else {
						// treat command name as undefined user function name
						getInVars().add(cmdName);
					}
				}

			}
		}

		// TRAC-1523 GGB-2208
		// FromBase["101",2] should still go to CAS
		useGeoGebraFallback = useGeoGebraFallback
				|| (!input.contains("FromBase")
						&& ve.inspect(Inspecting.textFinder));

		// get all used GeoElement variables
		// check for function
		boolean isFunction = ve instanceof FunctionNVar;

		// get input vars. Do this *before* we set the assignment variable to
		// avoid name clash,
		// see #2599
		// f(x)=FitPoly[...] has no x on RHS, but we need it
		if (ve instanceof FunctionNVar) {
			for (FunctionVariable fv : ((FunctionNVar) ve)
					.getFunctionVariables()) {
				getFunctionVars()
						.add(fv.toString(StringTemplate.defaultTemplate));
			}
		}
		HashSet<GeoElement> geoVars = ve.getVariables(SymbolicMode.NONE);
		if (geoVars != null) {
			for (GeoElement geo : geoVars) {
				String var = geo.getLabel(StringTemplate.defaultTemplate);
				if (isFunction && ((FunctionNVar) ve).isFunctionVariable(var)) {
					// function variable, e.g. k in f(k) := k^2 + 3
					getFunctionVars().add(var);
				} else {
					// input variable, e.g. b in a + 3 b
					getInVars().add(var);
					cons.getCASdummies().addAll(invars);
				}
			}
		}

		switch (getAssignmentType()) {
		case NONE:
			setAssignmentVar(null);
			break;
		// do that only if the expression is an assignment
		case DEFAULT:
			// outvar of assignment b := a + 5 is "b"
			setAssignmentVar(ve.getLabel());
			break;
		case DELAYED:
			setAssignmentVar(ve.getLabel());
			break;
		}

		if (ve.getLabel() != null && getFunctionVars().isEmpty()) {
			String var = getFunctionVariable(ve, getKernel());
			if (var != null) {
				getFunctionVars().add(var);
			}
		}
		// create Array of defined input GeoElements
		inGeos = updateInputGeoElements(invars);

		// replace GeoDummyVariable objects in inputVE by the found inGeos
		// This is important for row references and renaming of inGeos to work
		setInputVE(resolveInputReferences(getInputVE(), inGeos));

		// check for circular definition
		isCircularDefinition = false;
		if (inGeos != null) {
			for (GeoElement inGeo : inGeos) {
				if (inGeo.isChildOf(this) || this.equals(inGeo)) {
					isCircularDefinition = true;
					setError("CircularDefinition");
				}
			}
		}

	}

	private static String getFunctionVariable(final ValidExpression ve,
			Kernel kernel) {
		if (!ve.isTopLevelCommand()) {
			return null;
		}
		Command cmd = ve.getTopLevelCommand();
		if ("Derivative".equals(cmd.getName())) {
			if (cmd.getArgumentNumber() > 1) {

				if (!cmd.getArgument(1).isLeaf() || !(cmd.getArgument(1)
						.getLeft() instanceof GeoDummyVariable)) {
					return null;
				}
				return ((GeoElement) cmd.getArgument(1).getLeft())
						.toString(StringTemplate.defaultTemplate);
			}

			Iterator<GeoElement> it = cmd.getArgument(0)
					.getVariables(SymbolicMode.NONE).iterator();
			while (it.hasNext()) {
				GeoElement em = it.next();
				if (kernel.lookupLabel(
						em.toString(StringTemplate.defaultTemplate)) == null) {
					if (em instanceof VarString) {
						return ((VarString) em)
								.getVarString(StringTemplate.defaultTemplate);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Sets input to use internal command names and translatedInput to use
	 * localized command names. As a side effect, all command names are added as
	 * input variables as they could be function names.
	 */
	private void internalizeInput() {
		// local commands -> internal commands
		input = GgbScript.localizedScript2Script(kernel.getApplication(),
				input);
	}

	/**
	 * Returns the input using command names in the current language.
	 */
	private String localizeInput(final String input1,
			final StringTemplate tpl) {
		// replace all internal command names in input by local command names
		if (tpl.isPrintLocalizedCommandNames()) {
			// internal commands -> local commands
			return GgbScript.script2LocalizedScript(kernel.getApplication(),
					input1);
		}
		// keep internal commands
		return input1;
	}

	/**
	 * Set assignment var of this cell. For example "b := a^2 + 3" has
	 * assignment var "b".
	 * 
	 * @param var
	 *            variable
	 */
	private void setAssignmentVar(final String var) {
		if (ignoreSetAssignment) {
			return;
		}
		if (assignmentVar != null && assignmentVar.equals(var)) {
			return;
		}

		if (assignmentVar != null) {
			// remove old label from construction
			cons.removeCasCellLabel(assignmentVar);
		}

		if (var == null) {
			assignmentVar = null;

			// make sure we are using an unused label
		} else if (cons.isFreeLabel(var)) {
			// check for invalid assignment variables like $, $$, $1, $2, ...,
			// $1$, $2$, ... which are dynamic references

			if (!LabelManager.validVar(var)) {
				setError("CAS.VariableIsDynamicReference");
			}

			assignmentVar = var;
		}
		// needed for GGB-450
		else if (cons.isFileLoading() && inputVE.getLabel().equals(var)) {
			if (!LabelManager.validVar(var)) {
				setError("CAS.VariableIsDynamicReference");
			}

			assignmentVar = var;
		} else {

			changeAssignmentVar(var, getPointVectorDefault(var));
		}

		// store label of this CAS cell in Construction
		if (assignmentVar != null) {
			if (twinGeo != null) {
				ignoreSetAssignment = true;
				twinGeo.rename(assignmentVar);
			}
			updateDependentCellInput();
			cons.putCasCellLabel(this, assignmentVar);
		} else {
			// remove twinGeo if we had one
			setTwinGeo(null);
		}
		ignoreSetAssignment = false;
	}

	/**
	 * Replace old assignment var in input, e.g. "m := 8" becomes "a := 8"
	 */
	private void changeAssignmentVar(final String oldLabel,
			final String newLabel) {
		if (newLabel.equals(oldLabel)) {
			return;
		}

		getInputVE().setLabel(newLabel);
		if (oldLabel != null) {
			input = input.replaceFirst(oldLabel, newLabel);
			if (latexInput != null && latexInput.indexOf(oldLabel) >= 0) {
				latexInput = latexInput.replaceFirst(oldLabel, newLabel);
			} else {
				latexInput = null;
			}
			localizedInput = localizedInput.replaceFirst(oldLabel, newLabel);
		}
		assignmentVar = newLabel;
	}

	private TreeSet<String> getInVars() {
		if (invars == null) {
			invars = new TreeSet<>();
		}
		return invars;
	}

	private TreeSet<String> getFunctionVars() {
		if (functionvars == null) {
			functionvars = new TreeSet<>();
		}
		return functionvars;
	}

	private void clearInVars() {
		invars = null;
		functionvars = null;
		includesRowReferences = false;
		includesNumericCommand = false;
		useGeoGebraFallback = false;
	}

	/**
	 * Returns the n-th input variable (in alphabetical order).
	 * 
	 * @param n
	 *            index
	 * @return n-th input variable
	 */
	public String getInVar(int n) {
		if (invars == null) {
			return null;
		}

		Iterator<String> it = invars.iterator();
		int pos = 0;
		while (it.hasNext()) {
			String var = it.next();
			if (pos == n) {
				return var;
			}
			pos++;
		}

		return null;
	}

	/**
	 * Returns all GeoElement input variables including GeoCasCell objects and
	 * row references in construction order.
	 * 
	 * @return input GeoElements including GeoCasCell objects
	 */
	public TreeSet<GeoElement> getGeoElementVariables() {
		if (inGeos == null) {
			inGeos = updateInputGeoElements(invars);
		}
		return inGeos;
	}

	private TreeSet<GeoElement> updateInputGeoElements(
			final TreeSet<String> inputVars) {
		if (inputVars == null || inputVars.isEmpty()) {
			return null;
		}

		// list to collect geo variables
		TreeSet<GeoElement> geoVars = new TreeSet<>();

		// go through all variables
		for (String varLabel : inputVars) {
			// lookup GeoCasCell first
			GeoElement geo = kernel.lookupCasCellLabel(varLabel);

			if (geo == null) {
				// try row reference lookup
				// $ for previous row
				if (varLabel.equals(
						ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX)) {
					geo = row > 0 ? cons.getCasCell(row - 1)
							: cons.getLastCasCell();
				} else {
					try {
						geo = kernel.lookupCasRowReference(varLabel);
					} catch (CASException ex) {
						this.setError(ex.getKey());
						return null;
					}
				}
				if (geo != null) {
					includesRowReferences = true;
				}
			}

			if (geo == null) {
				// now lookup other GeoElements
				geo = kernel.lookupLabel(varLabel);

				if (geo != null && geo.getCorrespondingCasCell() != null) {
					// this is a twin geo of a CAS cell
					// input will be set from CAS
					geo = geo.getCorrespondingCasCell();
				}
			}

			if (geo != null) {
				// add found GeoElement to variable list
				geoVars.add(geo);
			}
		}

		if (geoVars.size() == 0) {
			return null;
		}
		return geoVars;
	}

	/**
	 * Replaces GeoDummyVariable objects in inputVE by the found inGeos. This is
	 * important for row references and renaming of inGeos to work.
	 */
	private ValidExpression resolveInputReferences(final ValidExpression ve,
			final TreeSet<GeoElement> inputGeos) {
		if (ve == null) {
			return ve;
		}
		AssignmentType assign = getAssignmentType();
		ValidExpression ret;

		// make sure we have an expression node
		ExpressionNode node;
		if (ve.isTopLevelCommand() && getFunctionVars().iterator().hasNext()) {
			Log.warn("wrong function syntax");
			String[] labels = ve.getLabels();
			if (ve instanceof ExpressionNode) {
				node = (ExpressionNode) ve;
			} else {
				node = new ExpressionNode(kernel, ve);
			}
			ret = new Function(node, new FunctionVariable(kernel,
					getFunctionVars().iterator().next()));
			ret.setLabels(labels);
		} else if (ve instanceof FunctionNVar) {
			node = ((FunctionNVar) ve).getExpression();
			ret = ve; // make sure we return the Function
		} else if (ve instanceof ExpressionNode) {
			node = (ExpressionNode) ve;
			ret = ve; // return the original ExpressionNode
		} else {
			node = new ExpressionNode(kernel, ve);
			node.setLabel(ve.getLabel());
			ret = node; // return a new ExpressionNode
		}

		// replace GeoDummyVariable occurances for each geo
		if (inputGeos != null) {
			for (GeoElement inGeo : inputGeos) {
				// replacement uses default template
				GeoDummyReplacer ge = GeoDummyReplacer.getReplacer(
						inGeo.getLabel(StringTemplate.defaultTemplate), inGeo,
						false);
				node.traverse(ge);
				if (!ge.didReplacement()) {
					// try $ row reference
					ge = GeoDummyReplacer.getReplacer(
							ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX,
							inGeo, false);
					node.traverse(ge);

				}
			}
		}

		// handle GeoGebra Fallback
		if (useGeoGebraFallback) {
			if (!includesOnlyDefinedVariables(true)) {
				useGeoGebraFallback = false;
			}
		}
		setAssignmentType(assign);
		return ret;
	}

	/**
	 * Replaces GeoDummyVariable objects in outputVE by the function inGeos.
	 * This is important for row references and renaming of inGeos to work.
	 */
	private static void resolveFunctionVariableReferences(
			final ValidExpression outputVE) {
		if (!(outputVE instanceof FunctionNVar)) {
			return;
		}

		FunctionNVar fun = (FunctionNVar) outputVE;

		// replace function variables in tree
		for (FunctionVariable fVar : fun.getFunctionVariables()) {
			// look for GeoDummyVariable objects with name of function variable
			// and replace them
			fun.getExpression().replaceVariables(fVar.getSetVarString(), fVar);
		}
	}

	/**
	 * Replaces GeoDummyVariable objects in outputVE by GeoElements from kernel
	 * that are not GeoCasCells.
	 */
	private void resolveGeoElementReferences(final ValidExpression outVE) {
		if (invars == null || !(outVE instanceof FunctionNVar)) {
			return;
		}
		FunctionNVar fun = (FunctionNVar) outVE;

		// replace function variables in tree
		for (String varLabel : invars) {
			GeoElement geo = kernel.lookupLabel(varLabel);
			if (geo != null) {
				// look for GeoDummyVariable objects with name of function
				// variable and replace them
				GeoDummyReplacer ge = GeoDummyReplacer.getReplacer(varLabel,
						geo, false);
				fun.getExpression().traverse(ge);
			}
		}
	}

	/**
	 * Returns whether this object only depends on named GeoElements defined in
	 * the kernel.
	 * 
	 * @return whether this object only depends on named GeoElements
	 */
	final public boolean includesOnlyDefinedVariables() {
		return includesOnlyDefinedVariables(false);
	}

	/**
	 * Same as previous function, except ignoring the undefined variables x and
	 * y to provide definition of functions like: f: x+y=1
	 * 
	 * @param ignoreUndefinedXY
	 *            true to ignore x,y
	 * @return whether this object only depends on named GeoElements
	 */
	final public boolean includesOnlyDefinedVariables(
			final boolean ignoreUndefinedXY) {
		if (invars == null) {
			return true;
		}

		for (String varLabel : invars) {
			if (!(ignoreUndefinedXY
					&& ("x".equals(varLabel) || "y".equals(varLabel)))) {
				// definitions
																		// of
																		// funktions
																		// like
																		// f:
																		// x+y =
																		// 1
																		// //TODO:
																		// find
																		// a
																		// better
																		// way
				if (kernel.lookupLabel(varLabel) == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns whether var is an input variable of this cell. For example, "b"
	 * is an input variable of "c := a + b"
	 * 
	 * @param var
	 *            variable name
	 * @return whether var is an input variable of this cell
	 */
	final public boolean isInputVariable(final String var) {
		return invars != null && invars.contains(var);
	}

	/**
	 * Returns whether var is a function variable of this cell. For example, "y"
	 * is a function variable of "f(y) := 2y + b"
	 * 
	 * @param var
	 *            variable name
	 * @return whether var is a function variable of this cell
	 */
	final public boolean isFunctionVariable(final String var) {
		return functionvars != null && functionvars.contains(var);
	}

	/**
	 * Returns the function variable string if input is a function or null
	 * otherwise. For example, "m" is a function variable of "f(m) := 2m + b"
	 * 
	 * @return function variable string
	 */
	final public String getFunctionVariable() {
		if (functionvars != null && !functionvars.isEmpty()) {
			return functionvars.first();
		}
		return null;
	}

	/**
	 * Returns whether this cell includes row references like $2.
	 * 
	 * @return whether this cell includes row references like $2.
	 */
	final public boolean includesRowReferences() {
		return includesRowReferences;
	}

	/**
	 * Returns whether this cell includes any Numeric[] commands.
	 * 
	 * @return whether this cell includes any Numeric[] commands.
	 */
	final public boolean includesNumericCommand() {
		return includesNumericCommand;
	}

	/**
	 * Returns the assignment variable of this cell. For example, "c" is the
	 * assignment variable of "c := a + b"
	 * 
	 * @return may be null
	 */
	final public String getAssignmentVariable() {
		return assignmentVar;
	}

	/**
	 * @return true if assignment variable is defined
	 */
	final public boolean isAssignmentVariableDefined() {
		return assignmentVar != null;
	}

	/**
	 * @param cmd
	 *            command
	 */
	final public void setEvalCommand(final String cmd) {
		if ("Evaluate".equals(cmd)) {
			evalCmd = "";
			setKeepInputUsed(false);
			return;
		}
		if ("Substitute".equals(cmd)) {
			updateInputVariables(evalVE);
		}
		evalCmd = cmd == null ? "" : cmd;

		// includesNumericCommand = includesNumericCommand || evalCmd != null
		// && "Numeric".equals(evalCmd);
		setKeepInputUsed(
				evalCmd != null && evalCmd.toLowerCase().equals("keepinput"));
	}

	/**
	 * @param keepInputUsed
	 *            true if KeepInput was used
	 */
	public void setKeepInputUsed(final boolean keepInputUsed) {
		this.keepInputUsed = keepInputUsed;
	}

	/**
	 * @param comment
	 *            comment
	 */
	final public void setEvalComment(final String comment) {
		if (comment != null) {
			if (!"".equals(comment)) {
				setSubstList(getSubstListFromSubstComment(comment));
			}
			if (evalComment != null && !"".equals(evalComment)
					&& "".equals(comment)) {
				setSubstList(getSubstListFromSubstComment(evalComment));
			}
			evalComment = comment;
		}
	}

	/**
	 * @param output
	 *            output string (from CAS)
	 * @param prependLabel
	 *            whether f(x):= must be prepended to output before evaluation
	 */
	public void setOutput(final String output, boolean prependLabel) {
		error = null;
		clearStrings();

		// when input is a function declaration, output also needs to become a
		// function
		// so we need to add f(x,y) := if it is missing
		boolean isFunctionDeclaration = isAssignmentVariableDefined()
				&& functionvars != null && !functionvars.isEmpty();
		// note: MPReduce returns "f" for a function definition "f(x) := x^2"
		// && !output.startsWith(assignmentVar);
		if (nativeOutput) {
			String res = output;

			if (isFunctionDeclaration && prependLabel) {
				// removing y from expressions y = x! and
				outputVE = (ValidExpression) parseGeoGebraCASInputAndResolveDummyVars(
						res).traverse(Traversing.FunctionCreator.getCreator());

				StringBuilder sb = new StringBuilder();
				sb.append(getInputVE().getLabelForAssignment());

				switch (getAssignmentType()) {
				case DEFAULT:
					sb.append(getInputVE().getAssignmentOperator());
					break;
				case DELAYED:
					sb.append(getInputVE().getDelayedAssignmentOperator());
					break;
				case NONE:
					break;
				}
				// #5119 / TRAC-4213 make sure internally the result does not
				// depend on rounding
				sb.append(outputVE.toString(StringTemplate.numericNoLocal));
				res = sb.toString();
			}

			// parse output into valid expression
			ValidExpression parsed = parseGeoGebraCASInputAndResolveDummyVars(
					res);
			if ((evalCmd != null && "NSolve".equals(evalCmd))
					|| (inputVE != null && inputVE.getTopLevelCommand() != null
							&& inputVE.getTopLevelCommand().getName()
									.equals("NSolve"))) {
				parsed = removeComplexResults(parsed);
			}
			outputVE = parsed;
			// needed for GGB-810
			// replace geoDummys with constants
			if (arbconst != null) {
				ArrayList<GeoNumeric> constList = arbconst.getConstList();
				if (!constList.isEmpty()) {
					for (GeoNumeric geoNum : constList) {
						geoNum.setSendValueToCas(false);
						GeoDummyReplacer replacer = GeoDummyReplacer
								.getReplacer(geoNum.getLabelSimple(), geoNum,
										false);
						outputVE.traverse(replacer);
					}
				}
			}

			if (outputVE != null) {
				CommandReplacer cr = CommandReplacer.getReplacer(kernel, true);
				outputVE.traverse(cr);
				if (inputVE != null) {
					if (inputVE.isTopLevelCommand("Vector")) {
						ExpressionNode wrapped = outputVE.wrap();
						wrapped.setForceVector();
						outputVE = wrapped;
					}

				}
			} else {
				setError("CAS.GeneralErrorMessage");
			}
		}
		if (isFunctionDeclaration) {
			// replace GeoDummyVariable objects in outputVE by the function
			// variables
			resolveFunctionVariableReferences(outputVE);
			// replace GeoDummyVariable objects in outputVE by GeoElements from
			// kernel
			resolveGeoElementReferences(outputVE);
		} else if (isAssignmentVariableDefined()) {
			outputVE.setLabel(assignmentVar);
			if (StringUtil.isLowerCase(assignmentVar.charAt(0))) {
				ExpressionValue ve = outputVE.unwrap();
				if (ve instanceof MyVecNode) {
					MyVecNode node = (MyVecNode) ve;
					node.setupCASVector();
				} else if (ve instanceof MyVec3DNode) {
					MyVec3DNode node3d = (MyVec3DNode) ve;
					node3d.setupCASVector();
				}
			}
		}
	}

	private ValidExpression removeComplexResults(ValidExpression ve) {
		if (ve instanceof ExpressionNode
				&& ((ExpressionNode) ve).getLeft() instanceof MyList
				&& ((ExpressionNode) ve).getRight() == null) {
			ArrayList<ExpressionValue> results = new ArrayList<>();
			for (int i = 0; i < ((MyList) ((ExpressionNode) ve).getLeft())
					.getLength(); i++) {
				boolean isComplex = ((MyList) ((ExpressionNode) ve).getLeft())
						.getListElement(i)
						.inspect(Inspecting.ComplexChecker.INSTANCE);
				if (!isComplex) {
					results.add(((MyList) ((ExpressionNode) ve).getLeft())
							.getListElement(i));
				}
			}
			MyList filteredResultList = new MyList(kernel, results.size());
			if (!results.isEmpty()) {
				for (ExpressionValue ev : results) {
					filteredResultList.addListElement(ev);
				}
			}
			return new ExpressionNode(kernel, filteredResultList);
		}
		return ve;
	}

	/**
	 * Updates the given GeoElement using the given casExpression.
	 * 
	 * @param allowFunction
	 *            whether we can use eg x as function (false: x is just a dummy)
	 */
	public void updateTwinGeo(boolean allowFunction) {
		ignoreTwinGeoUpdate = true;

		if (firstComputeOutput && twinGeo == null) {
			// create twin geo
			createTwinGeo(allowFunction);
		} else {
			// input did not change: just do a simple update
			simpleUpdateTwinGeo(allowFunction);
		}

		ignoreTwinGeoUpdate = false;
	}

	/**
	 * Creates a twinGeo using the current output
	 */
	private void createTwinGeo(boolean allowFunction) {
		if (isError()) {
			return;
		}
		boolean isLine = false;
		// case we have 3DLine
		if (inputVE != null && inputVE.getTopLevelCommand() != null
				&& inputVE.getTopLevelCommand().getName().equals("Line")
				&& outputVE instanceof Equation
				&& ((Equation) outputVE).getLHS().getLeft()
						.toString(StringTemplate.defaultTemplate).equals("X")
				&& ((Equation) outputVE).getRHS().getLeft()
						.evaluatesTo3DVector()) {
			isLine = true;
		}
		if (!isAssignmentVariableDefined() || outputVE == null) {
			return;
		}
		if (isNative() && (getInputVE() instanceof Function)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new Function((ExpressionNode) outputVE,
					((Function) getInputVE()).getFunctionVariable());
			outputVE.setLabels(labels);
		} else if (isNative() && (getInputVE() instanceof FunctionNVar)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new FunctionNVar((ExpressionNode) outputVE,
					((FunctionNVar) getInputVE()).getFunctionVariables());
			outputVE.setLabels(labels);
		}

		// check that assignment variable is not a reserved name in GeoGebra
		if (!isLine && kernel.getApplication().getParserFunctions()
				.isReserved(assignmentVar)) {
			return;
		}

		// needed for GGB-450
		GeoElement geo = null;
		// if file is loading check for already existent
		// twingeo with assignmentVar label
		if (cons.isFileLoading() && assignmentVar != null) {
			geo = kernel.lookupLabel(assignmentVar);
		}

		// try to create twin geo for assignment, e.g. m := c + 3
		ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
		arbconst.reset();
		outputVE.traverse(repl);
		setEquationMode();

		GeoElement newTwinGeo = null;
		if (isLine && outputVE instanceof Equation) {

			try {
				boolean old = kernel.getConstruction().isSuppressLabelsActive();
				kernel.getConstruction().setSuppressLabelCreation(true);
				GeoElement[] line = kernel.getAlgebraProcessor()
						.doProcessValidExpression(outputVE,
								new EvalInfo(false));
				kernel.getConstruction().setSuppressLabelCreation(old);
				newTwinGeo = line[0];
			} catch (MyError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CircularDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			HashSet<FunctionVariable> fVarSet = new HashSet<>();
			if (isFunctionProducingCommand()) {
				((ExpressionNode) outputVE).setForceFunction();
				TreeSet<String> varSet = new TreeSet<>(
						new Comparator<String>() {
							@Override
							public int compare(String o1, String o2) {
								return o2.compareTo(o1);
							}
						});
				evalVE.traverse(
						Traversing.DummyVariableCollector.getCollector(varSet));
				Iterator<String> it = varSet.iterator();
				// collect function variables
				while (it.hasNext() && varSet.size() != 1) {
					String curFVar = it.next();
					if ("y".equals(curFVar)) {
						FunctionVariable fv = new FunctionVariable(kernel,
								curFVar);
						fVarSet.add(fv);
					}
				}
			}
			newTwinGeo = silentEvalInGeoGebra(outputVE, allowFunction);
			// twingeo exists
			// change newTwinGeo
			if (geo != null) {
				newTwinGeo = geo;
			}
			// update newTwinGeo as multivariable function
			if (isFunctionProducingCommand() && !fVarSet.isEmpty()
					&& newTwinGeo instanceof GeoFunction) {
				FunctionVariable[] funcVars = ((GeoFunction) newTwinGeo)
						.getFunctionVariables();
				FunctionVariable[] newFuncVars = new FunctionVariable[funcVars.length
						+ fVarSet.size()];
				Iterator<FunctionVariable> it = fVarSet.iterator();
				while (it.hasNext()) {
					FunctionVariable curFV = it.next();
					int i;
					for (i = 0; i < funcVars.length; i++) {
						newFuncVars[i] = funcVars[i];
					}
					newFuncVars[i] = curFV;
					i++;
				}

				FunctionNVar newFNV = new FunctionNVar(
						((GeoFunction) newTwinGeo).getFunctionExpression(),
						newFuncVars);
				newTwinGeo = new GeoFunctionNVar(cons, newFNV);
			}
			if (uniformListCommand() && newTwinGeo instanceof GeoList) {
				makePlotable((GeoList) newTwinGeo);
			}
		}

		if (outputVE.unwrap() instanceof GeoElement
				&& ((GeoElement) outputVE.unwrap())
						.getDrawAlgorithm() instanceof DrawInformationAlgo) {
			newTwinGeo.setDrawAlgorithm(
					(DrawInformationAlgo) ((GeoElement) outputVE.unwrap())
							.getDrawAlgorithm());
		}
		if (newTwinGeo != null && !dependsOnDummy(newTwinGeo)) {
			setTwinGeo(newTwinGeo);
			if (twinGeo instanceof GeoImplicit) {
				((GeoImplicit) twinGeo).setToUser();
			}
			if (newTwinGeo instanceof GeoNumeric) {
				newTwinGeo.setLabelVisible(true);
			}
		}
	}

	private boolean uniformListCommand() {
		if (inputVE == null) {
			return false;
		}
		return inputVE.isTopLevelCommand("Sequence")
				|| inputVE.isTopLevelCommand("Zip")
				|| inputVE.isTopLevelCommand("KeepIf")
				|| inputVE.isTopLevelCommand("IterationList");
	}

	private static void makePlotable(GeoList list) {
		if (list.size() < 2) {
			return;
		}
		boolean hasFunction = false;
		for (int i = 0; i < list.size() && !hasFunction; i++) {
			if (list.get(i).isGeoFunction()) {
				hasFunction = true;
			}
		}
		if (hasFunction) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isGeoNumeric()) {
					list.setListElement(i,
							((GeoNumeric) list.get(i)).getGeoFunction());
				}
			}
		}

	}

	/**
	 * @return whether the output should be considered a function even if it is
	 *         just a number eg. plotting LeftSide[7=x] should produce f(x)=7
	 */
	private boolean isFunctionProducingCommand() {
		if (evalVE == null || evalVE.getTopLevelCommand() == null) {
			return false;
		}
		String name = evalVE.getTopLevelCommand().getName();
		if ("LeftSide".equals(name) || "RightSide".equals(name)) {
			return true;
		}
		return false;
	}

	private void setEquationMode() {
		if (this.inputVE != null && this.inputVE.unwrap() instanceof Equation
				&& this.inputVE.inspect(new Inspecting() {

					@Override
					public boolean check(ExpressionValue v) {
						return (v instanceof FunctionVariable
								|| v instanceof GeoDummyVariable)
								&& "z".equals(v.toString(
										StringTemplate.defaultTemplate));
					}
				})) {
			if (outputVE.unwrap() instanceof Equation) {
				((Equation) outputVE.unwrap()).setForcePlane();
			}
		}
	}

	/**
	 * Sets the label of twinGeo.
	 * 
	 * @return whether label was set
	 */
	public boolean setLabelOfTwinGeo() {
		if (twinGeo == null || twinGeo.isLabelSet()
				|| !isAssignmentVariableDefined()) {
			return false;
		}

		// allow GeoElement to get same label as CAS cell, so we temporarily
		// remove the label
		// but keep it in the underlying CAS
		cons.removeCasCellLabel(assignmentVar);
		// set Label of twinGeo
		twinGeo.setLabel(assignmentVar);
		// set back CAS cell label
		cons.putCasCellLabel(this, assignmentVar);
		if (cons.isFileLoading()) {
			updateConstructionDependencies();
		}
		return true;
	}

	// method to switch geoDummys with geoNumerics in outputVE and twinGeo
	// needed for undo
	private void updateConstructionDependencies() {
		if (this.getInputVE() != null && this.getInputVE() instanceof Function
				&& ((Function) this.getInputVE()).getFunctionExpression()
						.getTopLevelCommand() != null
				&& (((Function) this.getInputVE()).getFunctionExpression()
						.getTopLevelCommand().getName().equals("Integral")
						|| ((Function) this.getInputVE())
								.getFunctionExpression().getTopLevelCommand()
								.getName().equals("SolveODE"))) {
			MyArbitraryConstant myArbConst = cons.getArbitraryConsTable()
					.get(this.row);
			if (this.arbconst.getConstList().isEmpty() && myArbConst != null) {
				ArrayList<GeoNumeric> constList = myArbConst.getConstList();
				if (!constList.isEmpty()) {
					for (GeoNumeric geoNum : constList) {
						cons.addToConstructionList(geoNum, false);
						cons.putLabel(geoNum);
						this.arbconst.getConstList().add(geoNum);
						GeoDummyReplacer replacer = GeoDummyReplacer
								.getReplacer(
								geoNum.getLabelSimple(), geoNum, false);
						if (outputVE != null) {
							outputVE.traverse(replacer);
						}
						if (twinGeo instanceof GeoFunction
								&& ((GeoFunction) twinGeo).getFunction() != null
								&& ((GeoFunction) twinGeo)
										.getFunctionExpression() != null) {
							((GeoFunction) twinGeo).getFunctionExpression()
									.traverse(replacer);
						}

					}
				}
			}
		}
	}

	/**
	 * Sets twinGeo using current output
	 */
	private void simpleUpdateTwinGeo(boolean allowFunction) {
		if (twinGeo == null) {
			return;
		} else if (isError()) {
			twinGeo.setUndefined();
			return;
		}

		ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
		arbconst.reset();
		outputVE.traverse(repl);
		setEquationMode();

		// silent evaluation of output in GeoGebra
		GeoElement lastOutputEvaluationGeo = silentEvalInGeoGebra(outputVE,
				allowFunction);

		// Log.debug(lastOutputEvaluationGeo);

		if (lastOutputEvaluationGeo != null
				&& !dependsOnDummy(lastOutputEvaluationGeo)) {
			try {
				if (TestGeo.canSet(twinGeo, lastOutputEvaluationGeo)) {
					if (lastOutputEvaluationGeo instanceof GeoNumeric
							&& twinGeo instanceof GeoNumeric) {
						((GeoNumeric) twinGeo)
								.extendMinMax(lastOutputEvaluationGeo);
					}
					if (twinGeo instanceof GeoSurfaceCartesianND
							&& lastOutputEvaluationGeo instanceof GeoSurfaceCartesianND) {
						// when we replace twinGeo, dependent geos are also
						// deleted from cons
						twinGeo.doRemove();
						notifyRemove();
						twinGeo = lastOutputEvaluationGeo;
						cons.addToConstructionList(twinGeo, true);
						cons.putLabel(twinGeo);
						twinGeo.notifyAdd();
						twinGeo.setCorrespondingCasCell(this);
						// add to construction casCell and parentAlgo
						if (this.getParentAlgorithm() != null) {
							cons.addToConstructionList(
									this.getParentAlgorithm(), true);
						}
						cons.addToGeoSetWithCasCells(this);
						if (assignmentVar == null) {
							assignmentVar = twinGeo
									.getLabel(StringTemplate.defaultTemplate);
						}
					}
					// switch twinGeo with new evaluation
					// needed for undo->function wasn't draggable
					else {

						if (uniformListCommand() && lastOutputEvaluationGeo instanceof GeoList) {
							makePlotable((GeoList) lastOutputEvaluationGeo);
						}
						// if both geos are the same type we can use set safely
						twinGeo.set(lastOutputEvaluationGeo);
						// update constants references
						if (lastOutputEvaluationGeo instanceof GeoFunction) {
							ExpressionNode expr = ((GeoFunction) lastOutputEvaluationGeo)
									.getFunctionExpression();
							expr.inspect(new ArbconstAlgoFixer());

						}
					}
				} else if (!lastOutputEvaluationGeo.isDefined()) {
					// newly created GeoElement is undefined, we can set our
					// twin geo undefined
					twinGeo.setUndefined();
				} else {
					// different types:
					// needed for TRAC-2635
					// list wanted but we get line from giac
					if (inputVE != null && inputVE.isTopLevelCommand("Tangent")
							&& twinGeo instanceof GeoList
							&& !(lastOutputEvaluationGeo instanceof GeoList)
							&& (((Command) ((ExpressionNode) inputVE).getLeft())
									.getArgumentNumber() == 2)) {
						ExpressionNode[] args = ((Command) ((ExpressionNode) inputVE)
								.getLeft()).getArguments();
						// Tangent[Point, Conic]
						if (args[0].getLeft() instanceof GeoPoint
								&& args[1].getLeft() instanceof GeoConic) {
							((GeoList) twinGeo).clear();
							((GeoList) twinGeo).add(lastOutputEvaluationGeo);
						}

					} else {
						twinGeo = lastOutputEvaluationGeo;
						cons.replace(twinGeo, lastOutputEvaluationGeo);
					}

				}
				if (outputVE.unwrap() instanceof GeoElement
						&& ((GeoElement) outputVE.unwrap())
								.getDrawAlgorithm() instanceof DrawInformationAlgo) {
					twinGeo.setDrawAlgorithm(
							(DrawInformationAlgo) ((GeoElement) outputVE
									.unwrap()).getDrawAlgorithm());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// r2835: if the evaluation of outputVE returns null we have no twin
			// geo, we remove the old one and return
			// We only hide the geo from XML to avoid parsing eg 0=0 on next
			// file load
			twinGeo.setUndefined();
			twinGeo.setAlgebraVisible(false);
			cons.removeFromConstructionList(twinGeo);
			return;
		}
		if (isIndependent()) {
			twinGeo.update();
		} else {
			// AlgoDependentCasCell calls one more update; important to skip
			// this because of spreadsheet trace
			twinGeo.updateGeo(false);
		}
	}

	@Override
	public void updateCascade() {
		update();
		// Log.debug("updating"+getLabel(StringTemplate.defaultTemplate));
		if (twinGeo != null && !dependsOnDummy(twinGeo)) {
			ignoreTwinGeoUpdate = true;
			twinGeo.update();
			ignoreTwinGeoUpdate = false;
			updateAlgoUpdateSetWith(twinGeo);
		} else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			algoUpdateSet.updateAll();
		}
	}

	@Override
	public void update(boolean drag) {
		clearStrings();
		super.update(drag);
	}

	/**
	 * Evaluates ValidExpression in GeoGebra and returns one GeoElement or null.
	 * 
	 * @param ve
	 *            input
	 * @param allowFunction
	 *            whether to accept function as a result
	 * @return result GeoElement or null
	 */
	private GeoElement silentEvalInGeoGebra(final ValidExpression ve,
			boolean allowFunction) {
		if (!nativeOutput && outputVE.isExpressionNode()
				&& ((ExpressionNode) outputVE)
						.getLeft() instanceof GeoElement) {
			GeoElement ret = (GeoElement) ((ExpressionNode) outputVE).getLeft();
			return ret;
		}
		boolean wasFunction = outputVE instanceof FunctionNVar
				|| Equation.isFunctionEquation(outputVE);
		boolean wasCurve = twinGeo == null || twinGeo.isParametric();

		// replace variables x and y with a FunctionVariable object
		FunctionVariable fvX = new FunctionVariable(kernel, "x");
		Traversing variableReplacer = Traversing.VariableReplacer
				.getReplacer("x", fvX, kernel);
		ve.traverse(variableReplacer);
		FunctionVariable fvY = new FunctionVariable(kernel, "y");
		variableReplacer = Traversing.VariableReplacer.getReplacer("y", fvY,
				kernel);
		ve.traverse(variableReplacer);
		if (kernel.getApplication().is3D()) {
			FunctionVariable fvZ = new FunctionVariable(kernel, "z");
			variableReplacer = Traversing.VariableReplacer.getReplacer("z", fvZ,
					kernel);
			ve.traverse(variableReplacer);
		}

		boolean oldValue = kernel.isSilentMode();

		kernel.setSilentMode(true);

		try {
			// evaluate in GeoGebra
			ExpressionNode copy = ve.deepCopy(kernel).wrap();
			copy.setLabel(ve.getLabel());
			GeoElement[] ggbEval = kernel.getAlgebraProcessor()
					.doProcessValidExpression(copy,
							new EvalInfo(false).withSimplifying(false));
			if (ggbEval != null) {
				if (ggbEval[0] instanceof GeoLine) {
					((GeoLine) ggbEval[0]).setToUser();
				}
				if (!allowFunction && (ggbEval[0] instanceof FunctionalNVar)
						&& !wasFunction) {
					return null;
				}
				if (!allowFunction && (ggbEval[0].isParametric() && !wasCurve)) {
					return null;
				}

				return ggbEval[0];
			}
			return null;

		} catch (Throwable e) {
			Log.error("GeoCasCell.silentEvalInGeoGebra: " + ve + "\n\terror: "
					+ e.getMessage());
			return null;
		} finally {
			kernel.setSilentMode(oldValue);
		}
	}

	@Override
	final public void computeOutput() {
		// do not compute output if this cell is used as a text cell
		if (!useAsText) {
			// input VE is noll sometimes, ie if Solve is used on a=b+c,b
			if (getEvalVE() == null) {
				return;
			}
			computeOutput(getAssignmentType() != AssignmentType.DELAYED, false);
		}
	}

	/**
	 * @return whether top level command is Substitute
	 */
	public boolean isSubstitute() {
		Command cmd = evalVE.getTopLevelCommand();
		return cmd != null && "Substitute".equals(cmd.getName());
	}

	/**
	 * Computes the output of this CAS cell based on its current input settings.
	 * 
	 * @param doTwinGeoUpdate
	 *            whether twin geo should be updated or not
	 */
	private void computeOutput(final boolean doTwinGeoUpdate,
			final boolean allowFunction) {
		// check for circular definition before we do anything
		if (isCircularDefinition) {
			setError("CircularDefinition");
			if (doTwinGeoUpdate) {
				updateTwinGeo(allowFunction);
			}
			return;
		}

		if (input.contains("Surface")) {
			useGeoGebraFallback = true;
		}

		String result = null;
		boolean success = false;
		CASException ce = null;
		nativeOutput = true;
		if (inputVE != null && getAssignmentType() == AssignmentType.DELAYED) {
			result = ExpressionNode.getLabelOrDefinition(inputVE, StringTemplate.numericNoLocal);
			success = result != null;
		} else if (!useGeoGebraFallback) {
			// CAS EVALUATION
			try {
				if (evalVE == null) {
					throw new CASException("Invalid input (evalVE is null)");
				}

				boolean isSubstitute = isSubstitute();

				// wrap in Evaluate if it's an expression rather than a command
				// needed for Giac (for simplifying x+x to 2x)
				evalVE = wrapEvaluate(evalVE,
						isSubstitute && !isKeepInputUsed());

				// wrap in PointList if the top level command is Solutions
				// and the assignment variable is defined
				if (isAssignmentVariableDefined()) {
					adjustPointList(true);
				}

				expandedEvalVE = pointList ? wrapPointList(evalVE) : evalVE;
				if (expandedEvalVE.isTopLevelCommand()
						&& !expandedEvalVE.isTopLevelCommand("Evaluate")
						&& ((Command) expandedEvalVE
								.unwrap()).getArgumentNumber() != 1
						&& ((Command) expandedEvalVE
								.unwrap()).getArgument(0) != null) {
					ExpressionNode node = ((Command) expandedEvalVE.unwrap())
							.getArgument(0);
					if (!(node.getLeft() instanceof GeoSurfaceCartesianND)
							&& !(node.getRight() instanceof MyList)) {
						// needed for GGB-494
						// replace GeoSurfaceCartesian3D geos with MyVect3D with
						// expressions of surface
						expandedEvalVE = (ValidExpression) expandedEvalVE
								.traverse(GeoSurfaceReplacer.getInstance());
					}
				}

				if (!expandedEvalVE.isTopLevelCommand("Delete")
						&& !this.getNSolveCmdNeeded()) {
					FunctionExpander fex = FunctionExpander.getCollector();
					expandedEvalVE = (ValidExpression) expandedEvalVE.wrap()
							.getCopy(kernel).traverse(fex);
					expandedEvalVE = processSolveCommand(expandedEvalVE);
					// needed for GGB-955
					expandedEvalVE = processSolutionCommand(expandedEvalVE);
				}

				// make work NSolve with cell input
				if (expandedEvalVE.isTopLevelCommand("NSolve")
						&& ((Command) expandedEvalVE.unwrap()).getArgument(0)
										.getLeft() instanceof GeoCasCell) {
					GeoCasCell cellArg = ((GeoCasCell) ((Command) expandedEvalVE
							.unwrap()).getArgument(0).getLeft());
					ExpressionNode inputVEofGeoCasCell = (ExpressionNode) cellArg
							.getInputVE();
					((Command) expandedEvalVE.unwrap()).setArgument(0,
							inputVEofGeoCasCell);
				}

				// hack needed for GGB-494
				// Solve command with list of equs and list of vars
				if (expandedEvalVE instanceof ExpressionNode
						&& ((ExpressionNode) expandedEvalVE)
								.getLeft() instanceof Command
						&& "Solve"
								.equals(((Command) ((ExpressionNode) expandedEvalVE)
										.getLeft()).getName())
						&& ((Command) ((ExpressionNode) expandedEvalVE)
								.getLeft()).getArgumentNumber() == 2) {
					// get list of equations
					ExpressionValue equListV = ((Command) ((ExpressionNode) expandedEvalVE)
							.getLeft()).getArgument(0).unwrap();
					if (equListV instanceof MyList) {
						MyList equList = (MyList) equListV;
						// "x" geoDummy instead of functionVariable
						GeoDummyVariable x = new GeoDummyVariable(cons, "x");
						// "y" geoDummy instead of functionVariable
						GeoDummyVariable y = new GeoDummyVariable(cons, "y");
						for (int i = 0; i < equList.size(); i++) {
							if (equList
									.getListElement(i) instanceof ExpressionNode
									&& equList.getListElement(i)
											.unwrap() instanceof Equation) {
								// set Equation in list of equs instead of
								// ExpressionNode that contains Equation
								equList.setListElement(i,
										equList.getListElement(i).unwrap());
								// Equation contains "x" functionVariable
								// replace with simple GeoDummyVariable
								equList.getListElement(i)
										.traverse(GeoDummyReplacer
												.getReplacer("x", x, true));
								// Equation contains "y" functionVariable
								// replace with simple GeoDummyVariable
								equList.getListElement(i)
										.traverse(GeoDummyReplacer
												.getReplacer("y", y, true));
							}
						}
					}
				}

				// we need the row number of this row
				// to store the arbitrary constant in construction
				cons.updateCasCellRows();

				if (!cons.getArbitraryConsTable().isEmpty()) {
					// get abritraryConstant for this cell from construction
					MyArbitraryConstant myArbconst = cons
							.getArbitraryConsTable().get(this.row);
					// case we found an arbconst
					if (myArbconst != null && arbconst.getPosition() == 0) {
						// replace it
						arbconst = myArbconst;
						// replace geoCasCell for arbitrary constant
						if (arbconst.getCasCell() != this) {
							arbconst.setCasCell(this);
						}
						// hack needed for web with file loading
						if (cons.isFileLoading()) {
							ArrayList<GeoNumeric> constList = arbconst
									.getConstList();
							// switch geoNumerics created by xml reading
							// with geoNumerics created by cas evaluation
							if (constList != null && !constList.isEmpty()) {
								for (GeoNumeric geoNum : constList) {
									GeoElement geo = cons.lookupLabel(
											geoNum.getLabelSimple());
									if (geo instanceof GeoNumeric) {
										((GeoNumeric) geo)
												.setIsDependentConst(true);
										cons.removeLabel(geo);
										cons.addToConstructionList(geoNum,
												true);
										cons.putLabel(geoNum);
									}
								}
							}
						}
					}
				}

				arbconst.setSymbolic(hasSymbolicConstant());

				// compute the result using CAS
				result = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(
						expandedEvalVE, arbconst, StringTemplate.numericNoLocal,
						this, kernel);

				// if we had constants in expression
				// store arbconst in construction
				if (arbconst.getPosition() != 0) {
					cons.getArbitraryConsTable().put(this.row, arbconst);
				}

				// switch back the variable exchanges in result to command
				// SolveODE
				ArrayList<String> varSwaps = ((GeoGebraCAS) (kernel
						.getGeoGebraCAS())).getVarSwaps();
				if (!varSwaps.isEmpty()) {
					for (String currStr : varSwaps) {
						String[] swap = currStr.split("->");
						result = result.replaceAll(swap[1], swap[0]);
					}
					((GeoGebraCAS) (kernel.getGeoGebraCAS())).getVarSwaps()
							.clear();
				}
				// if KeepInput was used, return the input, except for the
				// Substitute command
				if (!isSubstitute && inputVE != null && isKeepInputUsed()) {
					result = inputVE.wrap()
							.toString(StringTemplate.numericNoLocal);
				}
				success = result != null;
			} catch (CASException e) {
				Log.error("GeoCasCell.computeOutput(), CAS eval: " + evalVE
						+ "\n\terror: " + e.getMessage());
				success = false;
				ce = e;
			} catch (Exception e) {
				Log.error("GeoCasCell.computeOutput(), CAS eval: " + evalVE
						+ "\n\t " + e);
				e.printStackTrace();
				success = false;
				ce = new CASException(e);
			}
		}

		// GEOGEBRA FALLBACK
		else {
			// EVALUATE evalVE in GeoGebra
			boolean oldValue = kernel.isSilentMode();
			kernel.setSilentMode(true);

			try {
				// process inputExp in GeoGebra *without* assignment (we need to
				// avoid redefinition)
				GeoElementND[] geos = kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(
								// we remove Numeric commands, since we are
								// using GeoGebra here
								evalVE.deepCopy(kernel)
										.traverse(Traversing.CommandRemover
												.getRemover("Numeric"))
										.toString(StringTemplate.maxPrecision),
								false);

				// GeoElement evalGeo = silentEvalInGeoGebra(evalVE);
				if (geos != null) {
					if (geos.length == 0 && evalVE.isTopLevelCommand()
							&& isScriptingCommand(
									evalVE.getTopLevelCommand().getName())) {
						geos = new GeoElement[] { new GeoBoolean(cons, true) };
					}
					success = true;
					/*
					 * Relation does not return any output, so it does not make
					 * sense to read off geos.
					 */
					if (!evalVE.isTopLevelCommand("Relation")) {
						result = geos[0]
								.toValueString(StringTemplate.numericNoLocal);
						AlgoElement parentAlgo = geos[0].getParentAlgorithm();
						if (parentAlgo != null) {
							parentAlgo.remove();
							// make sure fallback algos are synced with CAS, but
							// not
							// printed in XML (#2688)
							parentAlgo.setPrintedInXML(false);
						}
						outputVE = new ExpressionNode(kernel, geos[0]);
						// geos[0].addCasAlgoUser();
					}
					nativeOutput = false;
				}
			} catch (Throwable th2) {
				th2.printStackTrace();
				Log.warn("GeoCasCell.computeOutput(), GeoGebra eval: "
						+ evalVE + "\n error: " + th2.getMessage());
				success = false;
			} finally {
				kernel.setSilentMode(oldValue);
			}
		}

		// set Output
		finalizeComputation(success, result, ce, doTwinGeoUpdate,
				allowFunction);
	}

	// replace in Solutions[{h(s)=g(t)},{s,t}] vector nodes with equations
	private ValidExpression processSolutionCommand(ValidExpression ve) {
		if (ve.isTopLevelCommand("Solutions")) {
			Command cmd = (Command) ve.unwrap();
			if (cmd.getArgumentNumber() == 2) {
				ExpressionNode arg1 = cmd.getArgument(0);
				if (arg1.getLeft() instanceof MyList
						&& ((MyList) arg1.getLeft()).getListDepth() == 1
						&& ((MyList) arg1.getLeft())
								.getListElement(0) instanceof Equation) {
					expandEquation(cmd, ((Equation) ((MyList) arg1.getLeft())
							.getListElement(0)));
				} else if (arg1.unwrap() instanceof Equation) {
					expandEquation(cmd, (Equation) arg1.unwrap());
				}
			}
		}
		return ve;

	}

	private void expandEquation(Command cmd, Equation eqn) {
		ExpressionNode lhs = eqn.getLHS();
		ExpressionNode rhs = eqn.getRHS();
		if (lhs.getLeft() instanceof MyVecNode
				&& rhs.getLeft() instanceof MyVecNode) {
			ExpressionValue xLHS = ((MyVecNode) lhs.getLeft()).getX();
			ExpressionValue xRHS = ((MyVecNode) rhs.getLeft()).getX();
			Equation xEqu = new Equation(kernel, xLHS, xRHS);
			ExpressionValue yLHS = ((MyVecNode) lhs.getLeft()).getY();
			ExpressionValue yRHS = ((MyVecNode) rhs.getLeft()).getY();
			Equation yEqu = new Equation(kernel, yLHS, yRHS);
			MyList myList = new MyList(kernel, 2);
			myList.addListElement(new ExpressionNode(kernel, xEqu));
			myList.addListElement(new ExpressionNode(kernel, yEqu));
			ExpressionNode arg1 = new ExpressionNode(kernel, myList);
			cmd.setArgument(0, arg1);
		}

	}

	private static boolean isScriptingCommand(String name) {
		return "Delete".equals(name) || "StartAnimation".equals(name)
				|| (name != null && name.startsWith("Set"))
				|| (name != null && name.startsWith("Show"));
	}

	/**
	 * Wraps an expression in PointList command and copies the assignment
	 * 
	 * @param arg
	 *            expression to be wrapped
	 * @return point list command
	 */
	private ValidExpression wrapPointList(ValidExpression arg) {
		Command c = new Command(kernel, "PointList", false);
		c.addArgument(arg.wrap());
		ExpressionNode expr = c.wrap();
		expr.setLabel(arg.getLabel());
		return expr;
	}

	/*
	 * wrap eg x+x as Evaluate[x+x] so that it's simplified
	 */
	private ValidExpression wrapEvaluate(ValidExpression arg,
			boolean forceWrapping) {
		// don't want to wrap eg Integral[(x+1)^100] otherwise it will be
		// expanded
		if (arg.unwrap() instanceof Command && !forceWrapping) {
			return arg;
		}

		// To prevent recursion.
		if (isEvaluateCommand(arg))  {
			return arg;
		}

		// don't wrap if f'(x) is on top level (it is the same as
		// Derivative[f(x)])
		// but DO wrap f'(x+1) or f'(3) as it may simplify
		if (arg.unwrap() instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) arg.unwrap();
			if (en.getOperation() == Operation.EQUAL_BOOLEAN) {
				return arg;
			}
			if ((en.getOperation().equals(Operation.FUNCTION)
					|| en.getOperation().equals(Operation.FUNCTION_NVAR))
					&& en.getLeft() instanceof ExpressionNode) {
				ExpressionNode en2 = (ExpressionNode) en.getLeft();
				if (en2.getOperation().equals(Operation.DERIVATIVE)
						&& en.getRight().unwrap() instanceof GeoDummyVariable) {
					return arg;
				}
			}
		}

		ExpressionValue argUnwrapped = arg.unwrap();
		// wrap in ExpressionNode if necessary
		ExpressionNode en;
		if (arg.isExpressionNode()) {
			en = (ExpressionNode) arg;
		} else if (argUnwrapped.isExpressionNode()) {
			en = (ExpressionNode) argUnwrapped;
		} else {
			// eg f(x):=x+x
			// eg {x+x,y+y}
			// eg x+x=y+y
			en = new ExpressionNode(kernel, arg.unwrap(),
					Operation.NO_OPERATION, null);
		}
		Command c = new Command(kernel, "Evaluate", false);
		c.addArgument(en);
		ExpressionNode expr = c.wrap();
		expr.setLabel(arg.getLabel());
		return expr;
	}

	private boolean isEvaluateCommand(ValidExpression ve) {
		if (! (ve.unwrap() instanceof Function)) {
			return false;
		}

		ExpressionValue value = ((Function) ve.unwrap()).getExpression().unwrap();
		return  value instanceof Command
				&& ((Command) value).getName().equals("Evaluate");
	}

	private ValidExpression processSolveCommand(ValidExpression ve) {
		if ((!(ve.unwrap() instanceof Command))) {
			return ve;
		}
		if (((Command) ve.unwrap()).getName().equals("Numeric")) {
			((Command) ve.unwrap()).setArgument(0,
					processSolveCommand(((Command) ve.unwrap()).getArgument(0))
							.wrap());
			return ve;
		}
		if (!((Command) ve.unwrap()).getName().equals("Solve")) {
			return ve;
		}

		Command cmd = (Command) ve.unwrap();

		// Hack: collapse X=(a,b), X=(a+b,a-b+1) into one equation
		MyList arg = cmd.getArgument(0).unwrap() instanceof MyList
				? (MyList) cmd.getArgument(0).unwrap() : null;
		if (arg != null && arg.size() == 2) {
			String lhs1 = lhs(arg.getListElement(0), "@0");
			String lhs2 = lhs(arg.getListElement(1), "@1");

			if (lhs1.equals(lhs2)) {
				String test = null;
				try {
					test = kernel.getParser().parseLabel(lhs1);
				} catch (Throwable t) {
					// not a label
				}
				if (test != null && !((Equation) arg.getListElement(0).unwrap())
						.getRHS().evaluatesToNumber(true)) {
					Equation merge = new Equation(kernel,
							((Equation) arg.getListElement(0).unwrap())
									.getRHS(),
							((Equation) arg.getListElement(1).unwrap())
									.getRHS());
					cmd.setArgument(0, merge.wrap());
				}
			}
		}

		if (cmd.getArgumentNumber() >= 2) {
			if (cmd.getArgument(1).unwrap() instanceof MyList) {
				/* Modify solve in the following way: */
				/* Solve[expr, {var}] -> Solve[expr, var] */
				/* Ticket #697 */
				MyList argList = (MyList) cmd.getArgument(1).unwrap();
				if (argList.size() == 1) {
					cmd.setArgument(1, argList.getItem(0).wrap());
				}
			}
			return cmd.wrap();
		}
		if (cmd.getArgumentNumber() == 0) {
			return cmd.wrap();
		}
		ExpressionNode en = cmd.getArgument(0);
		/*
		 * Solve command has one argument which is an expression | equation |
		 * list
		 */
		/*
		 * We extract all the variables, order them, giving x y and z a priority
		 */
		/*
		 * Return the first n of them, where n is the number of
		 * equation/expression in the first parameter
		 */
		/* Ticket #3563 */
		Set<String> set = new TreeSet<>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.equals(o2)) {
					return 0;
				}
				if ("x".equals(o1)) {
					return -1;
				}
				if ("x".equals(o2)) {
					return 1;
				}
				if ("y".equals(o1)) {
					return -1;
				}
				if ("y".equals(o2)) {
					return 1;
				}
				if ("z".equals(o1)) {
					return -1;
				}
				if ("z".equals(o2)) {
					return 1;
				}
				return o1.compareTo(o2);
			}
		});
		cmd.getArgument(0).traverse(DummyVariableCollector.getCollector(set));
		int n = en.unwrap() instanceof MyList
				? ((MyList) en.unwrap()).getLength() : 1;
		// for equation (t,t) = (2s-1,3s+3)
		// make sure that we allow the correct number of variables
		// needed for #5332
		if (en.unwrap() instanceof Equation) {
			// 2DVector -> allow 2 variables
			if (((Equation) en.unwrap()).getLHS()
					.evaluatesToNonComplex2DVector()
					&& ((Equation) en.unwrap()).getRHS()
							.evaluatesToNonComplex2DVector()) {
				n = 2;
			}
			// 3DVector -> allow 3 variables
			if (((Equation) en.unwrap()).getLHS().evaluatesTo3DVector()
					&& ((Equation) en.unwrap()).getRHS()
							.evaluatesTo3DVector()) {
				n = 3;
			}
		}

		MyList variables = new MyList(kernel, n);
		int i = 0;
		Iterator<String> ite = set.iterator();
		if (n == 1) {
			if (ite.hasNext()) {
				cmd.addArgument(new GeoDummyVariable(cons, ite.next()).wrap());
			}
		} else {
			while (i < n && ite.hasNext()) {
				variables
						.addListElement(new GeoDummyVariable(cons, ite.next()));
				i++;
			}
			if (variables.size() > 0) {
				cmd.addArgument(variables.wrap());
			}
		}
		return cmd.wrap();
	}

	private static String lhs(ExpressionValue arg, String fallback) {
		return arg.unwrap() instanceof Equation ? ((Equation) arg.unwrap())
				.getLHS().toString(StringTemplate.defaultTemplate) : fallback;

	}

	private void finalizeComputation(final boolean success, final String result,
			final CASException ce, final boolean doTwinGeoUpdate,
			boolean allowFunction) {
		if (success) {
			if ((prefix.length() == 0 && postfix.length() == 0)
					// ignore selection with keep input
					|| (keepInputUsed)) {
				setOutput(result, true);
			} else {
				// make sure that evaluation is put into parentheses
				StringBuilder sb = new StringBuilder();
				sb.append(prefix);
				sb.append(" (");
				sb.append(result);
				sb.append(") ");
				sb.append(postfix);
				setOutput(sb.toString(), true);
			}
		} else {
			if (ce == null) {
				setError("CAS.GeneralErrorMessage");
			} else {
				setError(ce.getKey());
			}
		}

		// update twinGeo

		if (doTwinGeoUpdate) {
			updateTwinGeo(allowFunction);
		}

		if (outputVE != null && (!doTwinGeoUpdate || twinGeo == null)
				&& !getAssignmentType().equals(AssignmentType.DELAYED)) {
			ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
			arbconst.reset();

			// Bugfix for ticket: 2468
			// if outputVE is only a constant -> insert branch otherwise
			// traverse did not work correct
			outputVE.traverse(repl);
		}
		// set back firstComputeOutput, see setInput()
		firstComputeOutput = false;
		// invalidate latex
		clearStrings();

	}

	@Override
	public void setError(final String error) {
		this.error = error;
		clearStrings();
		outputVE = null;
	}

	/**
	 * @return true if this displays error
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * @return true if this displays circular definition error
	 */
	public boolean isCircularDefinition() {
		return isCircularDefinition;
	}

	/**
	 * Appends &lt;cascell caslabel="m"&gt; XML tag to StringBuilder.
	 */
	@Override
	protected void getElementOpenTagXML(StringBuilder sb) {
		sb.append("<cascell");
		if (assignmentVar != null) {
			sb.append(" caslabel=\"");
			StringUtil.encodeXML(sb, assignmentVar);
			sb.append("\" ");
		}
		sb.append(">\n");
	}

	/**
	 * Appends &lt;/cascell&gt; XML tag to StringBuilder.
	 */
	@Override
	protected void getElementCloseTagXML(StringBuilder sb) {
		sb.append("</cascell>\n");
	}

	/**
	 * Appends &lt;cellPair&gt; XML tag to StringBuilder.
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		// StringBuilder sb = new StringBuilder();
		sb.append("\t<cellPair>\n");

		// useAsText
		if (useAsText) {
			sb.append("\t\t<useAsText>\n");

			getFontXML(sb);

			sb.append("\t\t</useAsText>\n");
		}

		// inputCell
		if (!isInputEmpty() || useAsText
				|| (input != null && input.length() > 0)) {
			sb.append("\t\t<inputCell>\n");
			getInputExpressionXML(sb);
			sb.append("\t\t</inputCell>\n");
		}

		// outputCell
		if (!isOutputEmpty()) {
			sb.append("\t\t<outputCell>\n");
			getOutputExpressionXML(sb);
			sb.append("\t\t</outputCell>\n");
		}

		sb.append("\t</cellPair>\n");

		// return sb.toString();
	}

	private void getOutputExpressionXML(StringBuilder sb) {
		sb.append("\t\t\t<expression value=\"");
		StringUtil.encodeXML(sb, getOutput(StringTemplate.xmlTemplate));
		sb.append("\"");
		if (isError()) {
			sb.append(" error=\"true\"");
		}
		if (isNative()) {
			sb.append(" native=\"true\"");
		}
		if (!"".equals(evalCmd)) {
			sb.append(" evalCommand=\"");
			StringUtil.encodeXML(sb, evalCmd);
			sb.append("\" ");
		}

		if (!"".equals(evalComment)) {
			sb.append(" evalComment=\"");
			StringUtil.encodeXML(sb, evalComment);
			sb.append("\" ");
		}

		sb.append("/>\n");

	}

	private void getInputExpressionXML(StringBuilder sb) {
		sb.append("\t\t\t<expression value=\"");
		if (useAsText) {
			StringUtil.encodeXML(sb, commentText.getTextString());
			sb.append("\" ");
		} else {
			StringUtil.encodeXML(sb, input);
			sb.append("\" ");

			if (evalVE != getInputVE()) {
				if (!"".equals(prefix)) {
					sb.append(" prefix=\"");
					StringUtil.encodeXML(sb, prefix);
					sb.append("\" ");
				}

				sb.append(" eval=\"");
				StringUtil.encodeXML(sb, getEvalText());
				sb.append("\" ");

				if (!"".equals(postfix)) {
					sb.append(" postfix=\"");
					StringUtil.encodeXML(sb, postfix);
					sb.append("\" ");
				}

				sb.append("evalCmd=\"");
				StringUtil.encodeXML(sb, evalCmd);
				sb.append("\"");
			}

			if (pointList) {
				sb.append(" pointList=\"true\"");
			}
		}
		sb.append("/>\n");

	}

	private void getFontXML(StringBuilder sb) {
		sb.append("\t\t\t<FontStyle value=\"");
		sb.append(getFontStyle());
		sb.append("\" ");
		sb.append("/>\n");

		sb.append("\t\t\t<FontSizeM value=\"");
		sb.append(getFontSizeMultiplier());
		sb.append("\" ");
		sb.append("/>\n");

		sb.append("\t\t\t<FontColor");
		XMLBuilder.appendRGB(sb, getFontColor());
		sb.append("/>\n");
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CAS_CELL;
	}

	@Override
	public GeoElement copy() {
		GeoCasCell casCell = new GeoCasCell(cons);
		casCell.set(this);
		return casCell;
	}

	@Override
	public boolean isDefined() {
		return !isError();
	}

	@Override
	public void setUndefined() {
		setError("CAS.GeneralErrorMessage");
		if (twinGeo != null) {
			twinGeo.setUndefined();
		}
	}

	@Override
	public String toValueString(final StringTemplate tpl) {

		return outputVE != null ? outputVE.toValueString(tpl) : toString(tpl);
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	/**
	 * Returns assignment variable, e.g. "a" for "a := 5" or row reference, e.g.
	 * "$5$". Note that kernel.getCASPrintForm() is taken into account, e.g. row
	 * references return the output of this cell (instead of the label) for the
	 * underlying CAS.
	 */
	@Override
	public String getLabel(StringTemplate tpl) {
		// standard case: assignment
		if (assignmentVar != null) {
			return tpl.printVariableName(assignmentVar);
		}

		// row reference like $5
		StringBuilder sb = new StringBuilder();
		switch (tpl.getStringType()) {
		// send output to underlying CAS
		case GIAC:
			sb.append(" (");
			sb.append(outputVE == null ? "?" : outputVE.toString(tpl));
			sb.append(") ");
			break;

		default:
			// standard case: return current row, e.g. $5
			if (row >= 0) {
				if (tpl.hasType(StringType.LATEX)) {
					sb.append("\\$");
				} else {
					sb.append(ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX);
				}
				sb.append(row + 1);
			}
			break;
		}
		return sb.toString();
	}

	/**
	 * This might appear when we use KeepInput and display the result => we want
	 * to show symbolic version
	 */
	@Override
	public String toString(final StringTemplate tpl) {
		return getLabel(tpl);
	}

	@Override
	public String getAlgebraDescriptionDefault() {

		if (isDefined()) {
			return getOutput(StringTemplate.defaultTemplate);

		}
		final StringBuilder sbAlgebraDesc = new StringBuilder();
		sbAlgebraDesc.append(label);
		sbAlgebraDesc.append(' ');
		sbAlgebraDesc.append(getLoc().getMenu("Undefined"));
		return sbAlgebraDesc.toString();

	}

	@Override
	public boolean isGeoCasCell() {
		return true;
	}

	@Override
	public void doRemove() {
		if (assignmentVar != null) {
			// remove variable name from Construction
			cons.removeCasCellLabel(assignmentVar);
			assignmentVar = null;
		}

		super.doRemove();
		cons.removeFromGeoSetWithCasCells(this);

		setTwinGeo(null);
		if (this.isInConstructionList()) {
			cons.updateCasCells();
		}
	}

	/**
	 * @param newTwinGeo
	 *            new twin GeoElement
	 */
	private void setTwinGeo(final GeoElement newTwinGeo) {
		if (newTwinGeo == null && twinGeo != null) {
			GeoElement oldTwinGeo = twinGeo;
			twinGeo = null;
			oldTwinGeo.setCorrespondingCasCell(null);
			oldTwinGeo.doRemove();
		}

		twinGeo = newTwinGeo;
		if (twinGeo == null) {
			return;
		}
		twinGeo.setCorrespondingCasCell(this);
		twinGeo.setParentAlgorithm(getParentAlgorithm());
		if (twinGeo.isGeoNumeric() && inputVE != null
				&& (inputVE.isTopLevelCommand("Integral")
						|| inputVE.isTopLevelCommand("IntegralBetween"))) {
			((GeoNumeric) twinGeo).setDrawable(true, false);
		}
		if (dependsOnDummy(twinGeo)) {
			twinGeo.setUndefined();
			twinGeo.setAlgebraVisible(false);
		} else {
			twinGeo.setAlgebraVisible(true);
		}
	}

	private static boolean dependsOnDummy(final GeoElement geo) {
		if (geo instanceof GeoDummyVariable) {
			GeoElement subst = ((GeoDummyVariable) geo)
					.getElementWithSameName();
			// c_ check needed for GGB-810: skip constants
			if (subst != null
					&& (!subst.sendValueToCas || (subst.getLabelSimple() != null
							&& subst.getLabelSimple().startsWith("c_")))) {
				return false;
			} else if (subst == null
					&& ((GeoDummyVariable) geo).getVarName() != null
					&& ((GeoDummyVariable) geo).getVarName().startsWith("c_")) {
				return false;
			}
			return true;
		}
		if (geo.isGeoList()) {
			for (int i = 0; i < ((GeoList) geo).size(); i++) {
				if (dependsOnDummy(((GeoList) geo).get(i))) {
					return true;
				}
			}
		}

		AlgoElement algo = geo.getParentAlgorithm();
		if (algo == null || geo.getParentAlgorithm() == null) {
			return false;
		}

		for (int i = 0; i < algo.getInput().length; i++) {
			if (dependsOnDummy(algo.getInput()[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return twin element
	 */
	public GeoElement getTwinGeo() {
		return twinGeo;
	}

	/**
	 * Adds algorithm to update set of this GeoCasCell and also to the update
	 * set of an independent twinGeo.
	 */
	@Override
	public boolean addToUpdateSets(final AlgoElement algorithm) {
		final boolean added = super.addToUpdateSets(algorithm);
		if (twinGeo != null && twinGeo.isIndependent()) {
			twinGeo.addToUpdateSets(algorithm);
		}

		return added;
	}

	/**
	 * s algorithm from update set of this GeoCasCell and also from the update
	 * set of an independent twinGeo.
	 */
	@Override
	public boolean removeFromUpdateSets(final AlgoElement algorithm) {
		final boolean removed = super.removeFromUpdateSets(algorithm);
		if (twinGeo != null && twinGeo.isIndependent()) {
			twinGeo.removeFromUpdateSets(algorithm);
		}

		return removed;
	}

	/**
	 * @return output value as valid expression
	 */
	@Override
	public ValidExpression getValue() {
		return outputVE;
	}

	// public void setIgnoreTwinGeoUpdate(boolean ignoreTwinGeoUpdate) {
	// this.ignoreTwinGeoUpdate = ignoreTwinGeoUpdate;
	// }
	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	@Override
	public String getVarString(final StringTemplate tpl) {
		if (getInputVE() instanceof FunctionNVar) {
			return ((FunctionNVar) getInputVE()).getVarString(tpl);
		}
		return "";
	}

	/**
	 * @return function variables in list
	 */
	public MyList getFunctionVariableList() {
		if (getInputVE() instanceof FunctionNVar) {
			MyList ml = new MyList(kernel);
			for (FunctionVariable fv : ((FunctionNVar) getInputVE())
					.getFunctionVariables()) {
				ml.addListElement(fv);
			}
			return ml;
		}
		return null;
	}

	/**
	 * @return function variables of input function
	 */
	@Override
	public FunctionVariable[] getFunctionVariables() {
		if (getInputVE() instanceof FunctionNVar) {
			return ((FunctionNVar) getInputVE()).getFunctionVariables();

		}
		return new FunctionVariable[0];
	}

	private void setInputVE(ValidExpression inputVE) {
		this.inputVE = inputVE;
	}

	@Override
	public GColor getAlgebraColor() {
		if (twinGeo == null) {
			return GColor.BLACK;
		}
		return twinGeo.getAlgebraColor();
	}

	/**
	 * @param b
	 *            whether this cell was stored as native in XML
	 */
	public void setNative(final boolean b) {
		nativeOutput = b;
	}

	/**
	 * @return whether output was computed without using GeoGebra fallback
	 */
	public boolean isNative() {
		return nativeOutput;
	}

	/**
	 * toggles the euclidianVisibility of the twinGeo, if there is no twinGeo
	 * toggleTwinGeoEuclidianVisible tries to create one and set the visibility
	 * to true
	 */
	public void toggleTwinGeoEuclidianVisible() {
		boolean visible;
		if (hasTwinGeo()) {
			visible = !twinGeo.isEuclidianVisible()
					&& twinGeo.isEuclidianShowable();
		} else {
			// creates a new twinGeo, if not possible return
			if (outputVE == null || !plot()) {
				return;
			}
			visible = hasTwinGeo() && twinGeo.isEuclidianShowable();
		}
		if (hasTwinGeo()) {
			twinGeo.setEuclidianVisible(visible);
			twinGeo.updateVisualStyle(GProperty.VISIBLE);
		}
		kernel.getApplication().storeUndoInfo();
		kernel.notifyRepaint();
	}

	/**
	 * Assigns result to a variable if possible
	 * 
	 * @return false if it is not possible to plot this GeoCasCell true if there
	 *         is already a twinGeo, or a new twinGeo was created successfully
	 */
	public boolean plot() {
		if (getEvalVE() == null || "".equals(input)) {
			return false;
		} else if (hasTwinGeo()) { // there is already a twinGeo, this means
									// this cell is plotable,
			return true;
		}

		String oldEvalComment = evalComment;
		ValidExpression oldEvalVE = evalVE;
		ValidExpression oldInputVE = getInputVE();
		String oldAssignmentVar = assignmentVar;
		AssignmentType oldOVEAssignmentType = getAssignmentType();

		assignmentVar = getPlotVar();
		adjustPointList(false);
		this.firstComputeOutput = true;
		this.computeOutput(true, true);
		if (twinGeo != null && !dependsOnDummy(twinGeo)) {
			twinGeo.setLabel(null);
		}
		if (twinGeo != null && twinGeo.getLabelSimple() != null
				&& twinGeo.isEuclidianShowable()) {
			String twinGeoLabelSimple = twinGeo.getLabelSimple();
			changeAssignmentVar(assignmentVar, twinGeoLabelSimple);

			// we use EvalVE here as it's more transparent to push the command
			// to the input
			// except Evaluate and KeepInput
			ValidExpression ex = getEvalVE().deepCopy(kernel);
			CommandRemover remover;
			if (input.startsWith("Numeric[")) {
				remover = CommandRemover.getRemover("KeepInput", "Evaluate");
			} else {
				remover = CommandRemover.getRemover("KeepInput", "Evaluate",
						"Numeric");
			}
			ex.traverse(remover);
			setAssignmentType(AssignmentType.DEFAULT);
			if (twinGeo instanceof GeoSurfaceCartesianND) {
				StringBuilder sb = new StringBuilder();
				sb.append(twinGeoLabelSimple);
				sb.append("(");
				sb.append(((GeoSurfaceCartesianND) twinGeo)
						.getVarString(StringTemplate.defaultTemplate));
				sb.append(")");
				ex.setLabel(sb.toString());
			} else {
				ex.setLabel(twinGeo
						.getAssignmentLHS(StringTemplate.defaultTemplate));
			}
			if (twinGeo instanceof GeoFunction) {
				ex.traverse(Traversing.FunctionCreator.getCreator());
			}

			setAssignmentType(AssignmentType.DEFAULT);
			getEvalVE().setLabel(
					twinGeo.getAssignmentLHS(StringTemplate.defaultTemplate));
			boolean wasKeepInputUsed = isKeepInputUsed();
			boolean wasNumericUsed = "Numeric".equals(evalCmd);
			setInput(ex.toAssignmentString(StringTemplate.numericDefault,
					AssignmentType.DEFAULT));
			if (wasKeepInputUsed) {
				setKeepInputUsed(true);
				setEvalCommand("KeepInput");
			} else if (wasNumericUsed) {
				setProcessingInformation("",
						"Numeric[" + inputVE
								.toString(StringTemplate.defaultTemplate) + "]",
						"");
				setEvalCommand("Numeric");
			}
			computeOutput(false, false);
			this.update();
			clearStrings();
			cons.addToConstructionList(twinGeo, true);
			// notify only construction protocol
			// needed for GGB-810
			kernel.notifyConstructionProtocol(twinGeo);
			// dependent CAS cell algo needs to reference twin geo as output
			if (getParentAlgorithm() instanceof AlgoDependentCasCell) {
				((AlgoDependentCasCell) getParentAlgorithm()).setInputOutput();
			}
		} else {
			// Log.debug("Fail" + oldEvalComment);
			if (twinGeo != null && twinGeo.getLabelSimple() != null) {
				twinGeo.doRemove();
			}
			// plot failed, undo assignment
			assignmentVar = oldAssignmentVar;
			setAssignmentType(oldOVEAssignmentType);
			this.firstComputeOutput = true;
			evalComment = oldEvalComment;
			evalVE = oldEvalVE;
			// needed for GGB-525
			// reevaluate Solve, Solutions etc without requiring pointList
			pointList = false;

			setInputVE(oldInputVE);

			this.computeOutput(true, false);
			return false;
		}
		return true;
	}

	private boolean inequalityInEvalVE() {
		if (expandedEvalVE == null) {
			return false;
		}
		return expandedEvalVE.inspect(IneqFinder.INSTANCE);
	}

	private void clearStrings() {
		tooltip = null;
		latex = null;

	}

	private String getPlotVar() {
		boolean isCasVector = false;
		if (outputVE == null) {
			return PLOT_VAR;
		}
		ExpressionValue unwrapped = outputVE.unwrap();
		if (unwrapped == null) {
			return PLOT_VAR;
		}
		if (unwrapped instanceof MyVecNDNode) {
			isCasVector = unwrapped.evaluatesToVectorNotPoint();
		}
		if (isCasVector) {
			return PLOT_VAR.toLowerCase();
		}
		return PLOT_VAR;
	}

	/**
	 * @param pointList2
	 *            whether evalVE needs to be wrapped in PointList when
	 *            evaluating
	 */
	public void setPointList(boolean pointList2) {
		pointList = pointList2;
	}

	@Override
	public boolean hasCoords() {
		return outputVE != null && outputVE.hasCoords();
	}

	@Override
	public String getTooltipText(final boolean colored,
			final boolean alwaysOn) {
		if (isError()) {
			return localizedError(StringTemplate.defaultTemplate);
		}
		if (tooltip == null && outputVE != null) {
			tooltip = getOutput(StringTemplate.defaultTemplate);
			tooltip = tooltip.replace("gGbSuM(", Unicode.Sigma + "(");
			tooltip = tooltip.replace("gGbInTeGrAl(", Unicode.INTEGRAL + "(");

			if (tooltip.length() > TOOLTIP_SCREEN_WIDTH && tooltip.indexOf('{') > -1) {
				int listStart = tooltip.indexOf('{');
				StringBuilder sb = new StringBuilder(tooltip.length() + 20);
				sb.append(tooltip.substring(0, listStart + 1));

				int currLine = 0;
				for (int i = listStart + 1; i < tooltip.length(); i++) {
					if (tooltip.charAt(i) == ',') {
						int nextComma = tooltip.indexOf(',', i + 1);
						if (nextComma == -1) {
							nextComma = tooltip.length() - 1;
						}
						if (currLine + (nextComma - i) > TOOLTIP_SCREEN_WIDTH) {
							sb.append(",\n");
							currLine = 0;
							i++;
						}
					}
					currLine++;
					sb.append(tooltip.charAt(i));
				}
				tooltip = sb.toString();
			}
			tooltip = GeoElement.indicesToHTML(tooltip, true);
		}
		return tooltip;
	}

	/**
	 * @return information about eval command for display in the cell
	 */
	public String getCommandAndComment() {
		if (!this.showOutput()) {
			return "";
		}
		StringBuilder evalCmdLocal = new StringBuilder();
		if (pointList) {
			evalCmdLocal.append(getLoc().getCommand("PointList"));
		} else if ("".equals(evalCmd)) {
			return getOutputPrefix();
		} else if ("Numeric".equals(evalCmd)) {
			return Unicode.CAS_OUTPUT_NUMERIC + "";
		} else if ("KeepInput".equals(evalCmd)) {
			return Unicode.CAS_OUTPUT_KEEPINPUT + "";
		} else {
			evalCmdLocal.append(getLoc().getCommand(evalCmd));
		}

		if (input.startsWith(evalCmdLocal.toString()) || (localizedInput != null
				&& localizedInput.startsWith(evalCmdLocal.toString()))) {
			// don't show command if it is already at beginning of input
			return getOutputPrefix();
		}

		// eval comment (e.g. "x=5, y=8")
		if (evalComment.length() > 0) {
			if (evalCmdLocal.length() != 0) {
				evalCmdLocal.append(", ");
			}
			evalCmdLocal.append(evalComment);
		}
		evalCmdLocal.append(":");
		return evalCmdLocal.toString();
	}

	private String getOutputPrefix() {
		if (kernel.getLocalization().rightToLeftReadingOrder) {
			return Unicode.CAS_OUTPUT_PREFIX_RTL + "";
		}
		return Unicode.CAS_OUTPUT_PREFIX + "";
	}

	/**
	 * @return whether this cell depends on variables or was created using a
	 *         command
	 */
	public boolean hasVariablesOrCommands() {
		if (getGeoElementVariables() != null) {
			return true;
		}
		return inputVE != null && inputVE.inspect(CommandFinder.INSTANCE);
	}

	/**
	 * Sets pointList variable to the right value
	 * 
	 * @param onlySolutions
	 *            true if set point list only for Solutions NSolutions and
	 *            CSolutions
	 */
	public void adjustPointList(boolean onlySolutions) {
		if (evalVE.isTopLevelCommand()
				&& (getPlotVar().equals(assignmentVar))) {
			String cmd = evalVE.getTopLevelCommand().getName();
			if (!inequalityInEvalVE() && (("Solutions".equals(cmd)
					|| "CSolutions".equals(cmd) || "NSolutions".equals(cmd))
					|| (!onlySolutions && ("Solve".equals(cmd)
							|| "CSolve".equals(cmd) || "NSolve".equals(cmd)
							|| "Root".equals(cmd)
							|| "ComplexRoot".equals(cmd))))) {
				// if we got evalVE by clicking Solve button, inputVE might just
				// contain the equations
				// we want the command in input as well
				if (!pointList) {
					inputVE = evalVE;
				}
				pointList = true;
			}
		}
	}

	private void updateDependentCellInput() {
		List<AlgoElement> algos = getAlgorithmList();
		if (algos != null) {
			for (AlgoElement algo : algos) {
				if (algo instanceof AlgoCasCellInterface) {
					AlgoCasCellInterface algoCell = (AlgoCasCellInterface) algo;
					algoCell.getCasCell()
							.updateInputStringWithRowReferences(true);
				}
			}
		}
	}

	@Override
	public int getPrintDecimals() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPrintFigures() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSerifFont() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSerifFont(boolean serifFont) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean useSignificantFigures() {
		return false;
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return false;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	/**
	 * @param tpl
	 *            string template (might be MathQuill or JLM)
	 * @return input in LaTeX form
	 */
	public String getLaTeXInput(StringTemplate tpl) {

		if (useAsText) {
			return "\\text{" + this.commentText.getTextString() + "}";
		}

		return latexInput == null
				? (inputVE == null ? input
						: inputVE.toAssignmentString(tpl, getAssignmentType()))
				: latexInput;
	}

	/**
	 * Stores input only, no processing
	 * 
	 * @param latexInput
	 *            LaTeX form of input
	 */
	public void setLaTeXInput(String latexInput) {
		this.latexInput = latexInput;
	}

	/**
	 * Returns input, wrapped in used command if necessary
	 */
	@Override
	public String getDefinitionDescription(StringTemplate tpl) {
		if (evalVE.unwrap() instanceof Command
				&& "Evaluate".equals(((Command) evalVE.unwrap()).getName())) {
			return ((Command) evalVE.unwrap()).getArgument(0).toString(tpl);
		}
		return evalVE.toString(tpl);
	}

	@Override
	public ValueType getValueType() {
		// TODO Auto-generated method stub
		if (outputVE != null) {
			return outputVE.getValueType();
		}
		return inputVE != null ? inputVE.getValueType() : ValueType.UNKNOWN;
	}

	/**
	 * transforms evalComment into set of substitutions in case of Substitution
	 * command
	 * 
	 * @return set of substitutions
	 */
	private ArrayList<Vector<String>> getSubstListFromSubstComment(
			String evalCommentStr) {
		substList = new ArrayList<>();

		String[] splitComment = evalCommentStr.split(",");
		for (int i = 0; i < splitComment.length; i++) {
			String[] currSubstPair = splitComment[i].split("=");
			Vector<String> substRow = new Vector<>(2);
			substRow.add(currSubstPair[0]);
			substRow.add(currSubstPair[1]);
			substList.add(substRow);
		}

		return substList;
	}

	/**
	 * @return substitution list
	 */
	public ArrayList<Vector<String>> getSubstList() {
		return substList;
	}

	/**
	 * @param list
	 *            substitution list for this cell
	 */
	public void setSubstList(ArrayList<Vector<String>> list) {
		this.substList = list;
	}

	/**
	 * @param order
	 *            derivative order
	 * @param fast
	 *            true to avoid CAS computation
	 * @return derivative
	 */
	public ExpressionValue getGeoDerivative(int order, boolean fast) {
		return getTwinGeo() == null ? null
				: ((Functional) getTwinGeo()).getGeoDerivative(order, fast);
	}

	@Override
	public double evaluateDouble() {
		if (twinGeo != null) {
			return twinGeo.evaluateDouble();
		}
		return super.evaluateDouble();
	}

	/**
	 * @param tpl
	 *            template
	 * @param output
	 *            whether to substitute variables
	 * @return input or output
	 */
	public String getOutputOrInput(StringTemplate tpl, boolean output) {
		if (!output) {
			return getValue().toAssignmentString(
					getFormulaString(tpl, output), getAssignmentType());
		}
		return getOutput(tpl);
	}

	/**
	 * Reset row number (to avoid double deletion) and save it to preference
	 */
	public void resetRowNumber() {
		if (getRowNumber() >= 0) {
			preferredRowNumber = getRowNumber();
		}
		setRowNumber(-1);
	}

	/**
	 * Reload the current row number from saved preference
	 */
	public void reloadRowNumber() {
		setRowNumber(preferredRowNumber);
		preferredRowNumber = -1;
	}

	@Override
	public void setNeedsUpdatedBoundingBox(boolean b) {
		//
	}

	@Override
	public void calculateCornerPoint(GeoPoint corner, int double1) {
		corner.setUndefined();
	}

	/**
	 * @param var
	 *            variable name
	 * @return default label
	 */
	protected String getPointVectorDefault(String var) {
		if (!StringUtil.isLowerCase(var.charAt(0))) {
			return getLabelManager()
					.getNextIndexedLabel(LabelType.pointLabels);
		}
		return getDefaultLabel();
	}

	private boolean hasSymbolicConstant() {
		Command cmd = expandedEvalVE.getTopLevelCommand();
		return cmd != null && "IntegralSymbolic".equals(cmd.getName());
	}

}
