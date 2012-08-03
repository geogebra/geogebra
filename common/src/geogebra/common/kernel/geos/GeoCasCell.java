package geogebra.common.kernel.geos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.cas.CASException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import geogebra.common.kernel.arithmetic.Traversing.CommandReplacer;
import geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;
import geogebra.common.util.StringUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Cell pair of input and output strings used in the CAS view. This needs to be
 * a GeoElement in order to handle dependencies between cells and other
 * GeoElements together with AlgoSymbolic.
 * 
 * @author Markus Hohenwarter
 */

public class GeoCasCell extends GeoElement implements VarString {

	/**
	 * Symbol for static reference
	 */
	public static final char ROW_REFERENCE_STATIC = '#';

	/**
	 * Symbol for dynamic reference
	 */
	public static final char ROW_REFERENCE_DYNAMIC = '$';

	private ValidExpression inputVE, evalVE, outputVE;
	private String input, prefix, postfix, error, latex;
	private String localizedInput;
	private String currentLanguage;
	private boolean suppressOutput = false;

	// input variables of this cell
	private TreeSet<String> invars, functionvars;
	// defined input GeoElements of this cell
	private TreeSet<GeoElement> inGeos;
	private boolean isCircularDefinition;

	// twin geo, e.g. GeoCasCell m := 8 creates GeoNumeric m = 8
	private GeoElement twinGeo;
	private GeoElement lastOutputEvaluationGeo;
	private boolean firstComputeOutput;
	private boolean ignoreTwinGeoUpdate;

	// internal command names used in the input expression
	private HashSet<Command> commands;
	private String assignmentVar;
	private boolean includesRowReferences;
	private boolean includesNumericCommand;
	private boolean useGeoGebraFallback;

	private String evalCmd, evalComment;
	private int row = -1; // for CAS view, set by Construction

	// use this cell as text field
	private boolean useAsText;
	// for the future, is only holding font infos
	private GeoText commentText;
	private boolean nativeOutput;

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
	}

	/**
	 * Sets this GeoCasCell to the current state of geo which also needs to be a
	 * GeoCasCell. Note that twinGeo is kept null.
	 */
	@Override
	public void set(final GeoElement geo) {
		// GeoCasCell casCell = (GeoCasCell) geo;
		//
		// inputVE = casCell.inputVE;
		// evalVE = casCell.evalVE;
		// outputVE = casCell.outputVE;
		//
		// input = casCell.input;
		// prefix = casCell.prefix;
		// postfix = casCell.postfix;
		// error = casCell.error;
		// latex = casCell.latex;
		// localizedInput = casCell.localizedInput;
		// currentLocale = casCell.currentLocale;
		// suppressOutput = casCell.suppressOutput;
		//
		// // input variables of this cell
		// invars = casCell.invars;
		// // defined input GeoElements of this cell
		// inGeos = casCell.inGeos;
		// isCircularDefinition = casCell.isCircularDefinition;
		//
		// // twin geo, e.g. GeoCasCell m := 8 creates GeoNumeric m = 8
		// // twinGeo = casCell.twinGeo;
		// // firstComputeOutput = casCell.firstComputeOutput;
		// // useGeoGebraFallback = casCell.useGeoGebraFallback;
		// // ignoreTwinGeoUpdate = casCell.ignoreTwinGeoUpdate;
		//
		// // internal command names used in the input expression
		// cmdNames = casCell.cmdNames;
		// assignmentVar = casCell.assignmentVar;
		// includesRowReferences = casCell.includesRowReferences;
		// includesNumericCommand = casCell.includesNumericCommand;
		//
		// evalCmd = casCell.evalCmd;
		// evalComment = casCell.evalCmd;
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
			if (currentLanguage == null
					|| !currentLanguage.equals(kernel.getApplication()
							.getLanguage())) {
				updateLocalizedInput(tpl);
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
		if (error != null) {
			if (tpl.isPrintLocalizedCommandNames()) {
				return kernel.getApplication().getError(error);
			}
			return error;
		}

		if (outputVE == null) {
			return "";
		}
		if (tpl == StringTemplate.xmlTemplate) {
			App.debug(outputVE.toAssignmentString(tpl));
		}
		return outputVE.toAssignmentString(tpl);
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
		if (isError())
			return null;
		else if (latex == null) {
			if (outputVE != null) {
				// TODO Uncomment once support for latex line breaking is
				// implemented.
				// Kernel kernel = app.getKernel();
				// boolean oldLineBreaks = kernel.isInsertLineBreaks();
				// kernel.setInsertLineBreaks(true);
				StringBuilder sb = new StringBuilder("\\mathbf{");
				// create LaTeX string
				if (nativeOutput || !(outputVE instanceof ExpressionNode)) {
					sb.append(outputVE
							.toAssignmentLaTeXString(includesNumericCommand() ? StringTemplate.numericLatex
									: StringTemplate.latexTemplate));
				} else {
					GeoElement geo = ((GeoElement) ((ExpressionNode) outputVE)
							.getLeft());
					if (isAssignmentVariableDefined()) {
						sb.append(getAssignmentVariable());

						switch (outputVE.getAssignmentType()) {
						case DEFAULT:
							sb.append(outputVE.getAssignmentOperator().trim());
							break;
						case DELAYED:
							sb.append(outputVE.getDelayedAssignmentOperator()
									.trim());
							break;
						}
					}
					sb.append(geo.toValueString(StringTemplate.latexTemplate));
				}
				sb.append("}");
				latex = sb.toString();
				// TODO Uncomment once support for latex line breaking is
				// implemented.
				// kernel.setInsertLineBreaks(oldLineBreaks);
			}
		}

		return latex;
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
	 * @param ft
	 *            font
	 */
	public void setFont(GFont ft) {
		setFontSizeMultiplier((double) ft.getSize()
				/ (double) app.getFontSize());
		setFontStyle(ft.getStyle());
	}

	/**
	 * @param style
	 *            font style
	 */
	public void setFontStyle(int style) {
		commentText.setFontStyle(style);
	}

	/**
	 * @return font color
	 */
	public geogebra.common.awt.GColor getFontColor() {
		return this.getObjectColor();
	}

	/**
	 * @param c
	 *            font color
	 */
	public void setFontColor(geogebra.common.awt.GColor c) {
		this.setObjColor(c);
	}

	/**
	 * @return font style
	 */
	public int getFontStyle() {
		return commentText.getFontStyle();
	}

	/**
	 * @param d
	 *            font size multiplier
	 */
	public void setFontSizeMultiplier(double d) {
		commentText.setFontSizeMultiplier(d);
	}

	/**
	 * @return font size
	 */
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
		return inputVE == null;
	}

	/**
	 * @return whether output is empty
	 */
	public boolean isOutputEmpty() {
		return outputVE == null && error == null;
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
	 * @return false as we don't want CAS cells to send their values to the CAS
	 *         in update(). This is done in computeOutput() anyway.
	 */
	@Override
	public boolean isSendingUpdatesToCAS() {
		return false;
	}

	/**
	 * Returns if this GeoCasCell has a twinGeo or not
	 * 
	 * @return
	 */
	public boolean hasTwinGeo() {
		if (twinGeo != null) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the input of this row using the current casTwinGeo.
	 */
	public void setInputFromTwinGeo() {
		if (ignoreTwinGeoUpdate)
			return;

		if (twinGeo != null && twinGeo.isIndependent() && twinGeo.isLabelSet()) {
			// Update ASSIGNMENT of twin geo
			// e.g. m = 8 changed in GeoGebra should set cell to m := 8
			String assignmentStr = twinGeo
					.toCasAssignment(StringTemplate.maxPrecision);
			if (suppressOutput)
				assignmentStr = assignmentStr + ";";
			if (setInput(assignmentStr)) {
				computeOutput(false);
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

		// if the cell is used as comment, treat it as empty
		if (useAsText) {
			suppressOutput = true;
			setInputVE(new ExpressionNode());
			this.commentText.setTextString(inValue != null ? inValue : "");
		} else { // parse input into valid expression
			suppressOutput = inValue.endsWith(";");
			setInputVE(parseGeoGebraCASInputAndResolveDummyVars(inValue));
		}
		input = inValue != null ? inValue : ""; // remember exact user input
		prefix = "";
		evalVE = inputVE;
		postfix = "";
		setEvalCommand("");
		evalComment = "";
		setError(null);

		// update input and output variables
		updateInputVariables(inputVE);

		// input should have internal command names
		internalizeInput();

		// for efficiency: input with localized command names
		updateLocalizedInput(StringTemplate.defaultTemplate);

		// make sure cmputeOutput() knows that input has changed
		firstComputeOutput = true;

		if (!isEmpty()) {
			// make sure we put this casCell into the construction set
			cons.addToGeoSetWithCasCells(this);
		}
		return true;
	}

	private void updateLocalizedInput(final StringTemplate tpl) {
		// for efficiency: localized input with local command names
		currentLanguage = cons.getApplication().getLanguage();
		localizedInput = localizeInput(input, tpl);
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
		if (!includesRowReferences)
			return;

		// inputVE will print the correct label, e.g. $4 for
		// the row reference

		input = inputVE.toAssignmentString(StringTemplate.noLocalDefault);

		// TODO this always translates input.
		updateLocalizedInput(StringTemplate.defaultTemplate);
	}

	/**
	 * Sets how this row should be evaluated. Note that the input is NOT changed
	 * by this method, so you need to call setInput() first. Make sure that
	 * input = prefix + eval without wrapper command + postfix.
	 * 
	 * @param prefix
	 *            beginning part that should NOT be evaluated, e.g. "25a +"
	 * @param eval
	 *            part of the input that needs to be evaluated, e.g.
	 *            "Expand[(a+b)^2]"
	 * @param postfix
	 *            end part that should NOT be evaluated, e.g. " + "5 (c+d)"
	 */
	public void setProcessingInformation(final String prefix,
			final String eval, final String postfix) {
		String postfix1 = postfix;
		String prefix1 = prefix;
		setEvalCommand("");
		evalComment = "";
		if (prefix1 == null) {
			prefix1 = "";
		}
		if (postfix1 == null) {
			postfix1 = "";
		}

		// stop if input is assignment
		if (isAssignmentVariableDefined()) {
			if (eval.startsWith("KeepInput")) {
				setEvalCommand("KeepInput");
			}
			return;
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
		if (evalVE != null) {
			evalVE = resolveInputReferences(evalVE, inGeos);
			if (evalVE.isTopLevelCommand()) {
				// extract command from eval
				setEvalCommand(evalVE.getTopLevelCommand().getName());
			}
			this.prefix = prefix1;
			this.postfix = postfix1;
		} else {
			evalVE = inputVE;
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
	 * @param newInput
	 *            new input
	 * @return whether newInput and current input have same stucture
	 */
	public boolean isStructurallyEqualToLocalizedInput(final String newInput) {
		if (localizedInput != null && localizedInput.equals(newInput))
			return true;

		// check if the structure of inputVE and prefix + evalText + postfix is
		// equal
		// this is important to catch wrong selections, e.g.
		// 2 + 2/3 is not equal to the selection (2+2)/3
		if (!kernel.getGeoGebraCAS().isStructurallyEqual(inputVE, newInput)) {
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
		try {
			return (kernel.getGeoGebraCAS()).getCASparser()
					.parseGeoGebraCASInputAndResolveDummyVars(inValue);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * Updates the set of input variables and array of input GeoElements. For
	 * example, the input "b := a + 5" has the input variable "a"
	 */
	private void updateInputVariables(final ValidExpression ve) {
		// clear var sets
		clearInVars();

		if (ve == null || useAsText)
			return;

		// get all command names
		commands = new HashSet<Command>();
		ve.traverse(CommandCollector.getCollector(commands));
		if (commands.isEmpty()) {
			commands = null;
		} else {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand
						|| ("Numeric".equals(cmdName) && cmd
								.getArgumentNumber() > 1);

				// if command not known to CAS
				if (!kernel.getGeoGebraCAS().isCommandAvailable(cmd)) {
					if (kernel.lookupCasCellLabel(cmdName) != null
							|| kernel.lookupLabel(cmdName) != null) {
						// treat command name as defined user function name
						getInVars().add(cmdName);
					} else if (kernel.getAlgebraProcessor().isCommandAvailable(
							cmdName)) {
						// command is known to GeoGebra: use possible fallback
						useGeoGebraFallback = true;
					} else {
						// treat command name as undefined user function name
						getInVars().add(cmdName);
					}
				}

			}
		}

		// get all used GeoElement variables
		// check for function
		boolean isFunction = ve instanceof FunctionNVar;

		switch (inputVE.getAssignmentType()) {
		case NONE:
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

		// get input vars:
		HashSet<GeoElement> geoVars = ve.getVariables();
		if (geoVars != null) {
			for (GeoElement geo : geoVars) {
				String var = geo.getLabel(StringTemplate.defaultTemplate);
				if (isFunction && ((FunctionNVar) ve).isFunctionVariable(var)) {
					// function variable, e.g. k in f(k) := k^2 + 3
					getFunctionVars().add(var);
				} else {
					// input variable, e.g. b in a + 3 b
					getInVars().add(var);
				}
			}
		}
		if (ve.getLabel() != null && getFunctionVars().isEmpty()) {
			String var = getFunctionVariable(ve);
			if (var != null)
				getFunctionVars().add(var);
		}
		// create Array of defined input GeoElements
		inGeos = updateInputGeoElements(invars);

		// replace GeoDummyVariable objects in inputVE by the found inGeos
		// This is important for row references and renaming of inGeos to work
		setInputVE(resolveInputReferences(inputVE, inGeos));

		// check for circular definition
		isCircularDefinition = false;
		if (inGeos != null) {
			for (GeoElement inGeo : inGeos) {
				if (inGeo.isChildOf(this)) {
					isCircularDefinition = true;
					setError("CircularDefinition");
				}
			}
		}
	}

	private static String getFunctionVariable(final ValidExpression ve) {
		if (!ve.isTopLevelCommand())
			return null;
		Command cmd = ve.getTopLevelCommand();
		if ("Derivative".equals(cmd.getName())) {
			if (cmd.getArgumentNumber() > 1) {

				if (!cmd.getArgument(1).isLeaf()
						|| !(cmd.getArgument(1).getLeft() instanceof GeoDummyVariable))
					return null;
				return ((GeoElement) cmd.getArgument(1).getLeft())
						.toString(StringTemplate.defaultTemplate);// StringTemplate.defaultTemplate);
			}
			App.debug(cmd.getArgument(0).getLeft().getClass());

			Iterator<GeoElement> it = cmd.getArgument(0).getVariables()
					.iterator();
			while (it.hasNext()) {
				GeoElement em = it.next();
				if (ve.getKernel().lookupLabel(
						em.toString(StringTemplate.defaultTemplate)) == null)
					return em.toString(StringTemplate.defaultTemplate);
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
		input = translate(input, false);
	}

	/**
	 * Returns the input using command names in the current language.
	 */
	private String localizeInput(final String input1, final StringTemplate tpl) {
		// replace all internal command names in input by local command names
		if (tpl.isPrintLocalizedCommandNames()) {
			// internal commands -> local commands
			return translate(input1, true);
		}
		// keep internal commands
		return input1;
	}

	/**
	 * Translates given expression by replacing all command names
	 * 
	 * @param exp
	 * @param toLocalCmd
	 *            true: internalCmd -> localCmd, false: localCmd -> internalCmd
	 * @return translated expression
	 */
	private String translate(final String exp, final boolean toLocalCmd) {
		if (commands == null) {
			return exp;
		}

		String translatedExp = exp;
		Iterator<Command> it = commands.iterator();
		while (it.hasNext()) {
			String internalCmd = it.next().getName();
			String localCmd = cons.getApplication().getCommand(internalCmd);

			if (toLocalCmd) {
				// internal command names -> local command names
				translatedExp = replaceAllCommands(translatedExp, internalCmd,
						localCmd);
			} else {
				// local command names -> internal command names
				translatedExp = replaceAllCommands(translatedExp, localCmd,
						internalCmd);
			}
		}

		return translatedExp;
	}

	/**
	 * Replaces oldCmd command names by newCmd command names in expression.
	 */
	private static String replaceAllCommands(final String expression,
			final String oldCmd, final String newCmd) {
		String expression1 = expression;
		// build regex to find local command names
		StringBuilder regexPrefix = new StringBuilder();
		regexPrefix.append("(?i)"); // ignore case
		regexPrefix.append("\\b"); // match words for command only, not parts of
									// a word

		// replace commands with [
		StringBuilder regexSb = new StringBuilder(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\[]");
		StringBuilder newCmdSb = new StringBuilder(newCmd);
		newCmdSb.append("[");
		expression1 = expression1.replaceAll(regexSb.toString(),
				newCmdSb.toString());

		// replace commands with (
		regexSb.setLength(0);
		regexSb.append(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\(]");
		newCmdSb.setLength(0);
		newCmdSb.append(newCmd);
		newCmdSb.append("(");
		return expression1.replaceAll(regexSb.toString(), newCmdSb.toString());
	}

	/**
	 * Set assignment var of this cell. For example "b := a^2 + 3" has
	 * assignment var "b".
	 * 
	 * @param var
	 */
	private void setAssignmentVar(final String var) {
		if (assignmentVar != null && assignmentVar.equals(var)) {
			return;
		}

		if (assignmentVar != null) {
			// remove old label from construction
			cons.removeCasCellLabel(assignmentVar);
		}

		// make sure we are using an unused label
		if (var == null || cons.isFreeLabel(var)) {
			// check for invalid assignment variables like $, $$, $1, $2, ...,
			// $1$, $2$, ... which are dynamic references
			if (var.charAt(0) == ROW_REFERENCE_DYNAMIC) {
				boolean validVar = false;
				// if var.length() == 1 we have "$" and the for-loop won't be
				// entered
				for (int i = 1; i < var.length(); i++) {
					if (!Character.isDigit(var.charAt(i))) {
						if (i == 1 && var.charAt(1) == ROW_REFERENCE_DYNAMIC) {
							// "$$" so far, so it can be valid (if var.length >
							// 2) or invalid if "$$" is the whole var
						} else if (i == var.length() - 1
								&& var.charAt(var.length() - 1) == ROW_REFERENCE_DYNAMIC) {
							// "$dd...dd$" where all d are digits -> invalid
						} else {
							// "$xx..xx" where not all x are numbers and the
							// first x is not a '$' (there can only be one x)
							validVar = true;
							break;
						}
					}
				}

				if (!validVar) {
					setError("CAS.VariableContainsDynamicReferenceSymbol");
				}
			}

			assignmentVar = var;
		} else {
			changeAssignmentVar(var, getDefaultLabel());
		}

		// store label of this CAS cell in Construction
		if (assignmentVar != null) {
			if (twinGeo != null) {
				twinGeo.rename(assignmentVar);
			}
			cons.putCasCellLabel(this, assignmentVar);
		} else {
			// remove twinGeo if we had one
			setTwinGeo(null);
		}
	}

	/**
	 * Removes assignment variable from CAS.
	 */
	@Override
	public void unbindVariableInCAS() {
		// remove assignment variable
		if (isAssignmentVariableDefined()) {
			kernel.unbindVariableInGeoGebraCAS(assignmentVar);
		}
	}

	/**
	 * Replace old assignment var in input, e.g. "m := 8" becomes "a := 8"
	 * 
	 * @param oldLabel
	 * @param newLabel
	 */
	private void changeAssignmentVar(final String oldLabel,
			final String newLabel) {
		if (newLabel.equals(oldLabel))
			return;

		inputVE.setLabel(newLabel);
		if (oldLabel != null) {
			input = input.replaceFirst(oldLabel, newLabel);
			localizedInput = localizedInput.replaceFirst(oldLabel, newLabel);
		}
		assignmentVar = newLabel;
	}

	private TreeSet<String> getInVars() {
		if (invars == null)
			invars = new TreeSet<String>();
		return invars;
	}

	private TreeSet<String> getFunctionVars() {
		if (functionvars == null)
			functionvars = new TreeSet<String>();
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
		if (invars == null)
			return null;

		Iterator<String> it = invars.iterator();
		int pos = 0;
		while (it.hasNext()) {
			String var = it.next();
			if (pos == n)
				return var;
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
		if (inputVars == null || inputVars.isEmpty())
			return null;

		// list to collect geo variables
		TreeSet<GeoElement> geoVars = new TreeSet<GeoElement>();

		// go through all variables
		for (String varLabel : inputVars) {
			// lookup GeoCasCell first
			GeoElement geo = kernel.lookupCasCellLabel(varLabel);

			if (geo == null) {
				// try row reference lookup
				// $ for previous row
				if (varLabel
						.equals(ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX)) {
					geo = row > 0 ? cons.getCasCell(row - 1) : cons
							.getLastCasCell();
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

		ValidExpression ret;

		// make sure we have an expression node
		ExpressionNode node;
		if (ve.isTopLevelCommand() && getFunctionVars().iterator().hasNext()) {
			App.warn("wrong function syntax");
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
						inGeo.getLabel(StringTemplate.defaultTemplate), inGeo);
				node.traverse(ge);
				if (!ge.didReplacement()) {
					// try $ row reference
					ge = GeoDummyReplacer.getReplacer(
							ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX,
							inGeo);
					node.traverse(ge);

				}
			}
		}

		// handle GeoGebra Fallback
		if (useGeoGebraFallback) {
			if (!includesOnlyDefinedVariables()) {
				useGeoGebraFallback = false;
			}
		}

		return ret;
	}

	/**
	 * Replaces GeoDummyVariable objects in outputVE by the function inGeos.
	 * This is important for row references and renaming of inGeos to work.
	 */
	private static void resolveFunctionVariableReferences(
			final ValidExpression outputVE) {
		if (!(outputVE instanceof FunctionNVar))
			return;

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
		if (invars == null || !(outVE instanceof FunctionNVar))
			return;
		FunctionNVar fun = (FunctionNVar) outVE;

		// replace function variables in tree
		for (String varLabel : invars) {
			GeoElement geo = kernel.lookupLabel(varLabel);
			if (geo != null) {
				// look for GeoDummyVariable objects with name of function
				// variable and replace them
				GeoDummyReplacer ge = GeoDummyReplacer.getReplacer(varLabel,
						geo);
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
		if (invars == null)
			return true;

		for (String varLabel : invars) {
			if (!(ignoreUndefinedXY && (varLabel.equals("x") || varLabel
					.equals("y")))) // provide definitions of funktions like f:
									// x+y = 1 //TODO: find a better way
				if (kernel.lookupLabel(varLabel) == null)
					return false;
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
	 * @return evaluated command
	 */
	final public String getEvalCommand() {
		return evalCmd;
	}

	/**
	 * @param cmd
	 *            command
	 */
	final public void setEvalCommand(final String cmd) {
		evalCmd = cmd;

		// includesNumericCommand = includesNumericCommand || evalCmd != null
		// && evalCmd.equals("Numeric");
		setKeepInputUsed(evalCmd != null && evalCmd.equals("KeepInput"));
	}

	/**
	 * @param keepInputUsed
	 *            true if KeepInput was used
	 */
	public void setKeepInputUsed(final boolean keepInputUsed) {
		if (inputVE != null)
			inputVE.setKeepInputUsed(keepInputUsed);
		if (evalVE != null)
			evalVE.setKeepInputUsed(keepInputUsed);
	}

	/**
	 * @param comment
	 *            comment
	 */
	final public void setEvalComment(final String comment) {
		if (comment != null) {
			evalComment = comment;
		}
	}

	/**
	 * @return comment
	 */
	final public String getEvalComment() {
		return evalComment;
	}

	/**
	 * @param output
	 *            output string (from CAS)
	 */
	public void setOutput(final String output) {
		error = null;
		latex = null;

		// when input is a function declaration, output also needs to become a
		// function
		// so we need to add f(x,y) := if it is missing
		boolean isFunctionDeclaration = isAssignmentVariableDefined()
				&& functionvars != null && !functionvars.isEmpty();
		// note: MPReduce returns "f" for a function definition "f(x) := x^2"
		// && !output.startsWith(assignmentVar);
		if (nativeOutput) {
			String res = output;
			if (isFunctionDeclaration) {
				StringBuilder sb = new StringBuilder();
				sb.append(inputVE.getLabelForAssignment());

				switch (inputVE.getAssignmentType()) {
				case DEFAULT:
					sb.append(inputVE.getAssignmentOperator());
					break;
				case DELAYED:
					sb.append(inputVE.getDelayedAssignmentOperator());
					break;
				}

				sb.append(output);
				res = sb.toString();
			}

			// parse output into valid expression
			outputVE = parseGeoGebraCASInputAndResolveDummyVars(res);
			CommandReplacer cr = CommandReplacer.getReplacer(app);
			outputVE.traverse(cr);
			outputVE.setAssignmentType(inputVE.getAssignmentType());
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
		}
	}

	/**
	 * Updates the given GeoElement using the given casExpression.
	 */
	public void updateTwinGeo() {
		ignoreTwinGeoUpdate = true;

		if (firstComputeOutput && twinGeo == null) {
			// create twin geo
			createTwinGeo();
		} else {
			// input did not change: just do a simple update
			simpleUpdateTwinGeo();
		}

		ignoreTwinGeoUpdate = false;
	}

	/**
	 * Creates a twinGeo using the current output
	 */
	private void createTwinGeo() {
		if (isError())
			return;
		if (!isAssignmentVariableDefined())
			return;
		if ((inputVE instanceof Function)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new Function((ExpressionNode) outputVE,
					((Function) inputVE).getFunctionVariable());
			outputVE.setLabels(labels);
			outputVE.setAssignmentType(inputVE.getAssignmentType());
		} else if ((inputVE instanceof FunctionNVar)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new FunctionNVar((ExpressionNode) outputVE,
					((FunctionNVar) inputVE).getFunctionVariables());
			outputVE.setLabels(labels);
			outputVE.setAssignmentType(inputVE.getAssignmentType());
		}

		// check that assignment variable is not a reserved name in GeoGebra
		if (app.getParserFunctions().isReserved(assignmentVar))
			return;

		// try to create twin geo for assignment, e.g. m := c + 3
		ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
		arbconst.reset();
		outputVE.traverse(repl);
		GeoElement newTwinGeo = silentEvalInGeoGebra(outputVE);
		if (newTwinGeo != null) {
			setTwinGeo(newTwinGeo);
		}
	}

	/**
	 * Sets the label of twinGeo.
	 * 
	 * @return whether label was set
	 */
	public boolean setLabelOfTwinGeo() {
		if (twinGeo == null || twinGeo.isLabelSet()
				|| !isAssignmentVariableDefined())
			return false;

		// allow GeoElement to get same label as CAS cell, so we temporarily
		// remove the label
		// but keep it in the underlying CAS
		cons.removeCasCellLabel(assignmentVar, false);
		// set Label of twinGeo
		twinGeo.setLabel(assignmentVar);
		// set back CAS cell label
		cons.putCasCellLabel(this, assignmentVar);

		return true;
	}

	/**
	 * Sets twinGeo using current output
	 */
	private void simpleUpdateTwinGeo() {
		if (twinGeo == null) {
			return;
		} else if (isError()) {
			twinGeo.setUndefined();
			return;
		}

		// silent evaluation of output in GeoGebra
		lastOutputEvaluationGeo = silentEvalInGeoGebra(outputVE);
		if (lastOutputEvaluationGeo != null) {
			twinGeo.set(lastOutputEvaluationGeo);
		} else {
			twinGeo.setUndefined();
		}
	}

	@Override
	public void updateCascade() {
		update();

		if (twinGeo != null) {
			ignoreTwinGeoUpdate = true;
			twinGeo.update();
			ignoreTwinGeoUpdate = false;
			updateAlgoUpdateSetWith(twinGeo);
		} else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			algoUpdateSet.updateAll();
		}
	}

	/**
	 * Evaluates ValidExpression in GeoGebra and returns one GeoElement or null.
	 * 
	 * @param ve
	 * @return result GeoElement or null
	 */
	private GeoElement silentEvalInGeoGebra(final ValidExpression ve) {
		if (!nativeOutput && outputVE.isExpressionNode()
				&& ((ExpressionNode) outputVE).getLeft() instanceof GeoElement) {
			GeoElement ret = (GeoElement) ((ExpressionNode) outputVE).getLeft();
			return ret;
		}
		App.debug("reeval");
		boolean oldValue = kernel.isSilentMode();

		kernel.setSilentMode(true);

		try {
			// evaluate in GeoGebra
			GeoElement[] ggbEval = kernel.getAlgebraProcessor()
					.doProcessValidExpression(ve);
			if (ggbEval != null) {
				return ggbEval[0];
			}
			return null;

		} catch (Throwable e) {
			System.err.println("GeoCasCell.silentEvalInGeoGebra: " + ve
					+ "\n\terror: " + e.getMessage());
			return null;
		} finally {
			kernel.setSilentMode(oldValue);
		}
	}

	/**
	 * Computes the output of this CAS cell based on its current input settings.
	 * Note that this will also change a corresponding twinGeo.
	 * 
	 * @return success
	 */
	final public boolean computeOutput() {
		// do not compute output if this cell is used as a text cell
		if (!useAsText) {
			return computeOutput(true);
		}
		return true; // simulate success
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	/**
	 * Computes the output of this CAS cell based on its current input settings.
	 * 
	 * @param doTwinGeoUpdate
	 *            whether twin geo should be updated or not
	 * 
	 * @return success
	 */
	private boolean computeOutput(final boolean doTwinGeoUpdate) {
		// check for circular definition before we do anything
		if (isCircularDefinition) {
			setError("CircularDefinition");
			if (doTwinGeoUpdate) {
				updateTwinGeo();
			}
			return false;
		}

		String result = null;
		boolean success = false;
		CASException ce = null;
		nativeOutput = true;
		if (!useGeoGebraFallback) {
			// CAS EVALUATION
			try {
				if (evalVE == null) {
					throw new CASException("Invalid input (evalVE is null)");
				}
				result = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(evalVE,
						null, StringTemplate.numericDefault);
				success = result != null;
			} catch (CASException e) {
				System.err.println("GeoCasCell.computeOutput(), CAS eval: "
						+ evalVE + "\n\terror: " + e.getMessage());
				success = false;
				ce = e;
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
				GeoElement[] geos = kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(
								evalVE.toString(StringTemplate.maxPrecision),
								false, false, false);

				// GeoElement evalGeo = silentEvalInGeoGebra(evalVE);
				if (geos != null) {
					success = true;
					result = geos[0]
							.toValueString(StringTemplate.numericDefault);
					AlgoElement parentAlgo = geos[0].getParentAlgorithm();
					// cons.removeFromConstructionList(parentAlgo);
					if (parentAlgo != null) {
						parentAlgo.remove();
					}
					outputVE = new ExpressionNode(kernel, geos[0]);
					outputVE.setAssignmentType(inputVE.getAssignmentType());
					// geos[0].addCasAlgoUser();
					nativeOutput = false;
				}
			} catch (Throwable th2) {
				System.err
						.println("GeoCasCell.computeOutput(), GeoGebra eval: "
								+ evalVE + "\n error: " + th2.getMessage());
				success = false;
			} finally {
				kernel.setSilentMode(oldValue);
			}
		}

		// set Output
		finalizeComputation(success, result, ce, doTwinGeoUpdate);
		return success;
	}

	private void finalizeComputation(final boolean success,
			final String result, final CASException ce,
			final boolean doTwinGeoUpdate) {
		if (success) {
			if (prefix.length() == 0 && postfix.length() == 0) {
				// no prefix, no postfix: just evaluation
				setOutput(result);
			} else {
				// make sure that evaluation is put into parentheses
				StringBuilder sb = new StringBuilder();
				sb.append(prefix);
				sb.append(" (");
				sb.append(result);
				sb.append(") ");
				sb.append(postfix);
				setOutput(sb.toString());
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
			updateTwinGeo();
		}
		if (outputVE != null && (!doTwinGeoUpdate || twinGeo == null)) {
			ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
			arbconst.reset();

			// Bugfix for ticket: 2468
			// if outputVE is only a constant -> insert branch otherwise
			// traverse did not work correct
			if (outputVE.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode) outputVE;
				if (en.getOperation() == Operation.ARBINT
						|| en.getOperation() == Operation.ARBCONST
						|| en.getOperation() == Operation.ARBCOMPLEX) {
					outputVE = new ExpressionNode(kernel, outputVE,
							Operation.NO_OPERATION, null);
				}
			}

			outputVE.traverse(repl);
		}
		// set back firstComputeOutput, see setInput()
		firstComputeOutput = false;

	}

	/**
	 * @param error
	 *            error message
	 */
	public void setError(final String error) {
		this.error = error;
		latex = null;
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
	 * Appends <cascell caslabel="m"> XML tag to StringBuilder.
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
	 * Appends &lt;/cascell> XML tag to StringBuilder.
	 */
	@Override
	protected void getElementCloseTagXML(StringBuilder sb) {
		sb.append("</cascell>\n");
	}

	/**
	 * Appends <cellPair> XML tag to StringBuilder.
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		// StringBuilder sb = new StringBuilder();
		sb.append("\t<cellPair>\n");

		// useAsText
		if (useAsText) {
			sb.append("\t\t");
			sb.append("<useAsText>\n");
			sb.append("\t\t\t");

			sb.append("<FontStyle");
			sb.append(" value=\"");
			sb.append(getFontStyle());
			sb.append("\" ");
			sb.append("/>\n");

			sb.append("\t\t\t");
			sb.append("<FontSize");
			sb.append(" value=\"");
			sb.append(getFontSizeMultiplier());
			sb.append("\" ");
			sb.append("/>\n");

			sb.append("\t\t\t");
			sb.append("<FontColor");
			sb.append(" r=\"");
			sb.append(getFontColor().getRed());
			sb.append("\" ");
			sb.append(" b=\"");
			sb.append(getFontColor().getBlue());
			sb.append("\" ");
			sb.append(" g=\"");
			sb.append(getFontColor().getGreen());
			sb.append("\" ");
			sb.append("/>\n");

			sb.append("\t\t");
			sb.append("</useAsText>\n");
		}

		// inputCell
		if (!isInputEmpty()) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			if (useAsText) {
				StringUtil.encodeXML(sb, commentText.getTextString());
				sb.append("\" ");
			} else {
				StringUtil.encodeXML(sb, translate(input, false));
				sb.append("\" ");

				if (evalVE != inputVE) {
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
				}
			}
			sb.append("/>\n");
			sb.append("\t\t");
			sb.append("</inputCell>\n");
		}

		// outputCell
		if (!isOutputEmpty()) {
			sb.append("\t\t");
			sb.append("<outputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");

			sb.append(" value=\"");
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
			sb.append("\t\t");
			sb.append("</outputCell>\n");
		}

		sb.append("\t</cellPair>\n");

		// return sb.toString();
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
		return toString(tpl);
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	@Override
	public String getTypeString() {
		return "CasCell";
	}

	@Override
	public boolean isEqual(final GeoElement Geo) {
		return false;
	}

	@Override
	public String getClassName() {
		return "GeoCasCell";
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
			return kernel.printVariableName(assignmentVar, tpl);
		}

		// row reference like $5
		StringBuilder sb = new StringBuilder();
		switch (tpl.getStringType()) {
		// send output to underlying CAS
		case MPREDUCE:
		case MAXIMA:
			sb.append(" (");
			sb.append(outputVE == null ? "?" : outputVE.toString(tpl));
			sb.append(") ");
			break;

		default:
			// standard case: return current row, e.g. $5
			if (row >= 0) {
				sb.append(ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX);
				sb.append(row + 1);
			}
			break;
		}
		return sb.toString();
	}

	@Override
	public String toString(final StringTemplate tpl) {
		return getInput(tpl);
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
	}

	/**
	 * 
	 * @param newTwinGeo
	 */
	private void setTwinGeo(final GeoElement newTwinGeo) {
		if (newTwinGeo == null && twinGeo != null) {
			GeoElement oldTwinGeo = twinGeo;
			twinGeo = null;
			oldTwinGeo.setCorrespondingCasCell(null);
			oldTwinGeo.remove();
		}

		twinGeo = newTwinGeo;
		if (twinGeo == null) {
			return;
		}
		twinGeo.setCorrespondingCasCell(this);
		if (dependsOnDummy(twinGeo)) {
			twinGeo.setUndefined();
			twinGeo.setAlgebraVisible(false);
		} else {
			twinGeo.setAlgebraVisible(true);
		}
	}

	private boolean dependsOnDummy(final GeoElement geo) {
		if (geo instanceof GeoDummyVariable) {
			return true;
		}
		if (geo.isIndependent()) {
			return false;
		}
		AlgoElement algo = geo.getParentAlgorithm();
		for (int i = 0; i < algo.getInput().length; i++)
			if (dependsOnDummy(algo.getInput()[i])) {
				return true;
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
	public void addToUpdateSets(final AlgoElement algorithm) {
		super.addToUpdateSets(algorithm);
		if (twinGeo != null && twinGeo.isIndependent()) {
			twinGeo.addToUpdateSets(algorithm);
		}
	}

	/**
	 * s algorithm from update set of this GeoCasCell and also from the update
	 * set of an independent twinGeo.
	 */
	@Override
	public void removeFromUpdateSets(final AlgoElement algorithm) {
		super.removeFromUpdateSets(algorithm);
		if (twinGeo != null && twinGeo.isIndependent()) {
			twinGeo.removeFromUpdateSets(algorithm);
		}
	}

	/**
	 * @return output value as valid expression
	 */
	public ValidExpression getOutputValidExpression() {
		return outputVE;
	}

	// public void setIgnoreTwinGeoUpdate(boolean ignoreTwinGeoUpdate) {
	// this.ignoreTwinGeoUpdate = ignoreTwinGeoUpdate;
	// }
	@Override
	public boolean isLaTeXDrawableGeo(final String latexStr) {
		return isLaTeXneeded(latexStr);
	}

	public String getVarString(final StringTemplate tpl) {
		if (inputVE instanceof FunctionNVar) {
			return ((FunctionNVar) inputVE).getVarString(tpl);
		}
		return "";
	}

	/**
	 * @return function variables in list
	 */
	public MyList getFunctionVariableList() {
		if (inputVE instanceof FunctionNVar) {
			MyList ml = new MyList(kernel);
			for (FunctionVariable fv : ((FunctionNVar) inputVE)
					.getFunctionVariables()) {
				ml.addListElement(fv);
			}
			return ml;
		}
		return null;
	}

	private void setInputVE(ValidExpression inputVE) {
		this.inputVE = inputVE;
	}

	@Override
	public GColor getAlgebraColor() {
		if (twinGeo == null)
			return GColor.BLACK;
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
			if (!plot()) {
				return;
			}
			visible = twinGeo.isEuclidianShowable();
		}
		twinGeo.setEuclidianVisible(visible);
		twinGeo.updateVisualStyle();
		app.storeUndoInfo();
		kernel.notifyRepaint();
	}

	/**
	 * Assigns result to a variable if possible
	 * 
	 * @return false if it is not possible to plot this GeoCasCell true if there
	 *         is already a twinGeo, or a new twinGeo was created successfully
	 */
	public boolean plot() {
		if (inputVE == null || input.equals("")) {
			return false;
		}

		// there is already a twinGeo, this means this cell is plotable,
		// therefore return true
		if (hasTwinGeo()) {
			return true;
		}

		// this has to be upper case that the input of (1,1) leads to a
		// definition of a point
		// instead of a vector
		assignmentVar = "GgbmpvarPlot";

		// wrap output of Solve and Solutions to make them plotable

		// TODO remove "wasSolveSolutions" since it isn't used?
		boolean wasSolveSolutions = false;
		if (evalVE.isTopLevelCommand()) {
			Command topLevel = evalVE.getTopLevelCommand();
			if ((topLevel.getName()).equals("Solve")
					|| (topLevel.getName()).equals("Solutions")) {
				Command c = new Command(kernel, "PointList", true);
				c.addArgument(evalVE.wrap());
				evalVE = c.wrap();
				wasSolveSolutions = true;
			}
		}

		boolean isFunctionAble;
		if (outputVE.isExpressionNode()) {
			isFunctionAble = !kernel.getAlgebraProcessor().isNotFunctionAble(
					(ExpressionNode) outputVE);
		} else {
			isFunctionAble = !kernel.getAlgebraProcessor().isNotFunctionAbleEV(
					outputVE);
		}
		// if output is just one number -> do not make a function, make a
		// constant
		if (outputVE.isLeaf()) {
			if ((((ExpressionNode) outputVE).getLeft()).isConstant()) {
				isFunctionAble = false;
			}
		}

		if (isFunctionAble) {
			if (!outputVE.isExpressionNode()) {
				outputVE = new ExpressionNode(kernel, outputVE,
						Operation.NO_OPERATION, null);
			}
			outputVE = new Function((ExpressionNode) outputVE);
			outputVE.setAssignmentType(inputVE.getAssignmentType());
			this.firstComputeOutput = true;
			this.updateTwinGeo();
		} else {
			this.firstComputeOutput = true;
			this.computeOutput(true);
		}

		twinGeo.setLabel(null);
		if (twinGeo.getLabelSimple() != null && twinGeo.isEuclidianShowable()) {
			String twinGeoLabelSimple = twinGeo.getLabelSimple();
			changeAssignmentVar(assignmentVar, twinGeoLabelSimple);
			setEvalComment("Plot");
			inputVE.setLabel(assignmentVar);
			outputVE.setLabel(assignmentVar);
			latex = null;
		} else {
			// plot failed, undo assignment
			assignmentVar = null;
			outputVE.setAssignmentType(AssignmentType.NONE);
			this.firstComputeOutput = true;
			this.computeOutput(true);
		}
		return true;
	}

}
