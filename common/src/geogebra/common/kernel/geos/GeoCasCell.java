package geogebra.common.kernel.geos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.Inspecting.IneqFinder;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import geogebra.common.kernel.arithmetic.Traversing.CommandReplacer;
import geogebra.common.kernel.arithmetic.Traversing.FunctionExpander;
import geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.script.GgbScript;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

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
		//some dead code removed in r20927
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
					|| !currentLanguage.equals(loc
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
				return loc.getError(error);
			}
			return error;
		}

		if (outputVE == null) {
			return "";
		}

		return outputVE.toAssignmentString(tpl);
	}

	/**
	 * Returns the output of this row without any definitions.
	 * where getOutput returns g: x+y=1, this returns only x+y=1
	 * 
	 * @param tpl
	 *            string template
	 * @return output string
	 */
	public String getOutputRHS(StringTemplate tpl){
		if (error != null) {
			if (tpl.isPrintLocalizedCommandNames()) {
				return loc.getError(error);
			}
			return error;
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
						sb.append(StringTemplate.latexTemplate.printVariableName(getAssignmentVariable()));

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
			cons.addToConstructionList(this, true);
			kernel.notifyAdd(this);
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
		return getInputVE() == null;
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
	 * Returns if this GeoCasCell has a twinGeo or not
	 * 
	 * @return if this GeoCasCell has a twinGeo or not
	 */
	public boolean hasTwinGeo() {
		return twinGeo != null;
	}

	/**
	 * Sets the input of this row using the current casTwinGeo.
	 * @param force force update (needed if twin geo is a slider)
	 */
	public void setInputFromTwinGeo(boolean force) {
		if (ignoreTwinGeoUpdate && !force) {
			return;
		}

		if (twinGeo != null && twinGeo.isIndependent() && twinGeo.isLabelSet()) {
			// Update ASSIGNMENT of twin geo
			// e.g. m = 8 changed in GeoGebra should set cell to m := 8
			String assignmentStr = twinGeo
					.toCasAssignment(StringTemplate.maxPrecision);
			if (suppressOutput)
				assignmentStr = assignmentStr + ";";
			if (setInput(assignmentStr)) {
				computeOutput(false,false);
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
		evalVE = getInputVE();
		postfix = "";
		setEvalCommand("");
		evalComment = "";
		setError(null);

		// update input and output variables
		updateInputVariables(getInputVE());

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
		currentLanguage = loc.getLanguage();
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

		input = getInputVE().toAssignmentString(StringTemplate.noLocalDefault);

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
	 * @param evaluate
	 *            part of the input that needs to be evaluated, e.g.
	 *            "Expand[(a+b)^2]"
	 * @param postfix
	 *            end part that should NOT be evaluated, e.g. " + "5 (c+d)"
	 */
	public void setProcessingInformation(final String prefix,
			final String evaluate, final String postfix) {
		String eval = evaluate;
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
		if(inputVE!=null && inputVE.getLabel()!=null){
			evalVE.setLabel(inputVE.getLabel());
			evalVE.setAssignmentType(inputVE.getAssignmentType());
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
		if (!kernel.getGeoGebraCAS().isStructurallyEqual(getInputVE(), newInput)) {
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
		}catch (CASException c){
			setError(loc.getError(c.getKey()));
			return null;
		}catch (Throwable e){
			
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

		// get input vars. Do this *before* we set the assignment variable to avoid name clash,
		// see #2599
		// f(x)=FitPoly[...] has no x on RHS, but we need it
		if(ve instanceof FunctionNVar){
			for(FunctionVariable fv:((FunctionNVar) ve).getFunctionVariables()){		
				getFunctionVars().add(fv.toString(StringTemplate.defaultTemplate));
			}
		}
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
					cons.getCASdummies().addAll(invars);
				}
			}
		}
				
		switch (getInputVE().getAssignmentType()) {
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
			String var = getFunctionVariable(ve);
			if (var != null)
				getFunctionVars().add(var);
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
		input = GgbScript.localizedScript2Script(app, input);
	}

	/**
	 * Returns the input using command names in the current language.
	 */
	private String localizeInput(final String input1, final StringTemplate tpl) {
		// replace all internal command names in input by local command names
		if (tpl.isPrintLocalizedCommandNames()) {
			// internal commands -> local commands
			return GgbScript.script2LocalizedScript(app,input1);
		}
		// keep internal commands
		return input1;
	}


	//make sure we don't enter setAssignmentVar from itself
	private boolean ignoreSetAssignment = false;
	/**
	 * Set assignment var of this cell. For example "b := a^2 + 3" has
	 * assignment var "b".
	 * 
	 * @param var
	 */
	
	private void setAssignmentVar(final String var) {
		if(ignoreSetAssignment){
			App.printStacktrace("");
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
			assignmentVar = var;

			// make sure we are using an unused label
		} else if (cons.isFreeLabel(var)) {
			// check for invalid assignment variables like $, $$, $1, $2, ...,
			// $1$, $2$, ... which are dynamic references

			if (!LabelManager.validVar(var)) {
				setError("CAS.VariableIsDynamicReference");
			}

			assignmentVar = var;
		} else {
			changeAssignmentVar(var, getDefaultLabel());
		}

		// store label of this CAS cell in Construction
		if (assignmentVar != null) {
			if (twinGeo != null) {
				ignoreSetAssignment = true;
				twinGeo.rename(assignmentVar);
			}
			cons.putCasCellLabel(this, assignmentVar);
		} else {
			// remove twinGeo if we had one
			setTwinGeo(null);
		}
		ignoreSetAssignment = false;
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

		getInputVE().setLabel(newLabel);
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
		AssignmentType assign = ve.getAssignmentType();
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
		ret.setAssignmentType(assign);
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
		if (getInputVE() != null)
			getInputVE().setKeepInputUsed(keepInputUsed);
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
				StringBuilder sb = new StringBuilder();
				sb.append(getInputVE().getLabelForAssignment());

				switch (getInputVE().getAssignmentType()) {
				case DEFAULT:
					sb.append(getInputVE().getAssignmentOperator());
					break;
				case DELAYED:
					sb.append(getInputVE().getDelayedAssignmentOperator());
					break;
				}

				sb.append(output);
				res = sb.toString();
			}

			// parse output into valid expression
			outputVE = parseGeoGebraCASInputAndResolveDummyVars(res);
			
			if(outputVE!=null){
				CommandReplacer cr = CommandReplacer.getReplacer(app);
				outputVE.traverse(cr);
				if(inputVE!=null)
					outputVE.setAssignmentType(inputVE.getAssignmentType());
			}else{
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
		}
	}

	/**
	 * Updates the given GeoElement using the given casExpression.
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
		if (isError())
			return;
		if (!isAssignmentVariableDefined())
			return;
		if ((getInputVE() instanceof Function)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new Function((ExpressionNode) outputVE,
					((Function) getInputVE()).getFunctionVariable());
			outputVE.setLabels(labels);
			outputVE.setAssignmentType(getInputVE().getAssignmentType());
		} else if ((getInputVE() instanceof FunctionNVar)
				&& (outputVE instanceof ExpressionNode)) {
			String[] labels = outputVE.getLabels();
			outputVE = new FunctionNVar((ExpressionNode) outputVE,
					((FunctionNVar) getInputVE()).getFunctionVariables());
			outputVE.setLabels(labels);
			outputVE.setAssignmentType(getInputVE().getAssignmentType());
		}

		// check that assignment variable is not a reserved name in GeoGebra
		if (app.getParserFunctions().isReserved(assignmentVar))
			return;

		// try to create twin geo for assignment, e.g. m := c + 3
		ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
		arbconst.reset();
		outputVE.traverse(repl);
		GeoElement newTwinGeo = silentEvalInGeoGebra(outputVE,allowFunction);
		if (newTwinGeo != null && !dependsOnDummy(newTwinGeo)) {
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
		cons.removeCasCellLabel(assignmentVar);
		// set Label of twinGeo
		twinGeo.setLabel(assignmentVar);
		// set back CAS cell label
		cons.putCasCellLabel(this, assignmentVar);

		return true;
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

		// silent evaluation of output in GeoGebra
		lastOutputEvaluationGeo = silentEvalInGeoGebra(outputVE,allowFunction);
		if (lastOutputEvaluationGeo != null
				&& !dependsOnDummy(lastOutputEvaluationGeo)) {
			try{
			twinGeo.set(lastOutputEvaluationGeo);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		} else {
			twinGeo.setUndefined();
		}
		twinGeo.update();
	}

	@Override
	public void updateCascade() {
		update();
		App.debug("updating"+getLabel(StringTemplate.defaultTemplate));
		if (twinGeo != null && !dependsOnDummy(twinGeo)) {
			ignoreTwinGeoUpdate = true;
			twinGeo.update();
			ignoreTwinGeoUpdate = false;
			updateAlgoUpdateSetWith(twinGeo);
		} else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			App.debug(algoUpdateSet.getSize());
			algoUpdateSet.updateAll();
		}
	}

	/**
	 * Evaluates ValidExpression in GeoGebra and returns one GeoElement or null.
	 * 
	 * @param ve
	 * @return result GeoElement or null
	 */
	private GeoElement silentEvalInGeoGebra(final ValidExpression ve,boolean allowFunction) {
		if (!nativeOutput && outputVE.isExpressionNode()
				&& ((ExpressionNode) outputVE).getLeft() instanceof GeoElement) {
			GeoElement ret = (GeoElement) ((ExpressionNode) outputVE).getLeft();
			return ret;
		}
		boolean wasFunction = outputVE instanceof FunctionNVar;
		FunctionVariable fv = new FunctionVariable(kernel,"x");
		ve.wrap().replaceVariables("x", fv);
		FunctionVariable fvY = new FunctionVariable(kernel,"y");
		ve.wrap().replaceVariables("y", fvY);
		App.debug("reeval");
		boolean oldValue = kernel.isSilentMode();

		kernel.setSilentMode(true);

		try {
			// evaluate in GeoGebra
			GeoElement[] ggbEval = kernel.getAlgebraProcessor()
					.doProcessValidExpression(ve);
			
			if (ggbEval != null) {
				if(!allowFunction && (ggbEval[0] instanceof FunctionalNVar) && !wasFunction)
					return null;
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
	 */
	final public void computeOutput() {
		// do not compute output if this cell is used as a text cell
		if (!useAsText) {
			//input VE is noll sometimes, ie if Solve is used on a=b+c,b
			if(getEvalVE()==null)
				return;
			computeOutput(getEvalVE().getAssignmentType()!=AssignmentType.DELAYED,false);
		}
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	private ValidExpression expandedEvalVE;

	/**
	 * Computes the output of this CAS cell based on its current input settings.
	 * 
	 * @param doTwinGeoUpdate
	 *            whether twin geo should be updated or not
	 */
	private void computeOutput(final boolean doTwinGeoUpdate, final boolean allowFunction) {
		// check for circular definition before we do anything
		if (isCircularDefinition) {
			setError("CircularDefinition");
			if (doTwinGeoUpdate) {
				updateTwinGeo(allowFunction);
			}
			return;
		}

		String result = null;
		boolean success = false;
		CASException ce = null;
		nativeOutput = true;
		//do not create a twin geo if the assigment type is delayed since this exists only in cas
		if (!useGeoGebraFallback || getInputVE().getAssignmentType()==AssignmentType.DELAYED) {
			// CAS EVALUATION
			try {
				if (evalVE == null) {
					throw new CASException("Invalid input (evalVE is null)");
				}
				expandedEvalVE = pointList ? wrapPointList(evalVE):evalVE;				
				if(!(expandedEvalVE.unwrap() instanceof Command) || !((Command)expandedEvalVE.unwrap()).getName().equals("Delete")){
					FunctionExpander fex = FunctionExpander.getCollector();
					expandedEvalVE = (ValidExpression) expandedEvalVE.wrap().getCopy(kernel).traverse(fex);
				}
				result = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(expandedEvalVE,
						null, StringTemplate.numericNoLocal);
				if(result!=null && evalVE.unwrap() instanceof Command && ((Command)evalVE.unwrap()).getName().equals("KeepInput")){
					result = ((Command)evalVE.unwrap()).getArgument(0).toString(StringTemplate.numericDefault);					
				}
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
					if (parentAlgo != null) {
						parentAlgo.remove();
					}
					//make sure fallback algos are synced with CAS, but not printed in XML (#2688)
					parentAlgo.setPrintedInXML(false);
					outputVE = new ExpressionNode(kernel, geos[0]);
					outputVE.setAssignmentType(getInputVE().getAssignmentType());
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
		finalizeComputation(success, result, ce, doTwinGeoUpdate,allowFunction);
	}
	/**
	 * Wraps an expression in PointList command and copies the assignment
	 * @param arg expression to be wrapped
	 * @return point list command
	 */
	private ValidExpression wrapPointList(ValidExpression arg) {
		Command c= new Command(kernel,"PointList",false);
		c.addArgument(arg.wrap());
		ExpressionNode expr = c.wrap();
		expr.setAssignmentType(arg.getAssignmentType());
		expr.setLabel(arg.getLabel());
		return expr;
	}

	private void finalizeComputation(final boolean success,
			final String result, final CASException ce,
			final boolean doTwinGeoUpdate,boolean allowFunction) {
		if (success) {
			if (prefix.length() == 0 && postfix.length() == 0) {
				// no prefix, no postfix: just evaluation
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

		if (outputVE != null && (!doTwinGeoUpdate || twinGeo == null)) {
			ArbconstReplacer repl = ArbconstReplacer.getReplacer(arbconst);
			arbconst.reset();

			// Bugfix for ticket: 2468
			// if outputVE is only a constant -> insert branch otherwise
			// traverse did not work correct
			outputVE = (ValidExpression) outputVE.traverse(repl);
		}
		// set back firstComputeOutput, see setInput()
		firstComputeOutput = false;
		//invalidate latex
		clearStrings();

	}

	/**
	 * @param error
	 *            error message
	 */
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
			sb.append("<FontSizeM");
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
		if (!isInputEmpty() || useAsText
				|| (input != null && input.length() > 0)) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			if (useAsText) {
				StringUtil.encodeXML(sb, commentText.getTextString());
				sb.append("\" ");
			} else {
				StringUtil.encodeXML(sb, GgbScript.localizedScript2Script(app, input));
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
			if (pointList) {
				sb.append(" pointList=\"true\"");
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
		
		return outputVE!=null ? outputVE.toValueString(tpl) : toString(tpl);
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
	public boolean isEqual(final GeoElement Geo) {
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
		case MPREDUCE:
		case MAXIMA:
			sb.append(" (");
			sb.append(outputVE == null ? "?" : outputVE.toString(tpl));
			sb.append(") ");
			break;

		default:
			// standard case: return current row, e.g. $5
			if (row >= 0) {
				if(tpl.hasType(StringType.LATEX)){
					sb.append("\\$");
				}else{
					sb.append(ExpressionNodeConstants.CAS_ROW_REFERENCE_PREFIX);
				}
				sb.append(row + 1);
			}
			break;
		}
		return sb.toString();
	}

	/**
	 * This might appear when we use KeepInput and display the result => we want to show symbolic version
	 */
	@Override
	public String toString(final StringTemplate tpl) {
		return getLabel(tpl);
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
		if(this.isInConstructionList())
			cons.updateCasCells();
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

	private static boolean dependsOnDummy(final GeoElement geo) {
		if (geo instanceof GeoDummyVariable) {
			return true;
		}
		if (geo.isGeoList()) {
			for (int i = 0; i < ((GeoList)geo).size(); i++)
				if (dependsOnDummy(((GeoList)geo).get(i))) {
					return true;
				}
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
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

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
	public FunctionVariable[] getFunctionVariables() {
		if (getInputVE() instanceof FunctionNVar) {
			return ((FunctionNVar) getInputVE())
					.getFunctionVariables();
			
		}
		return new FunctionVariable[0];
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
			if (outputVE == null || !plot()) {
				return;
			}
			visible = hasTwinGeo() && twinGeo.isEuclidianShowable();
		}
		if (hasTwinGeo()) {
			twinGeo.setEuclidianVisible(visible);
			twinGeo.updateVisualStyle();
		}
		app.storeUndoInfo();
		kernel.notifyRepaint();
	}
	private boolean pointList;

	private String tooltip;
	/**
	 * Assigns result to a variable if possible
	 * 
	 * @return false if it is not possible to plot this GeoCasCell true if there
	 *         is already a twinGeo, or a new twinGeo was created successfully
	 */
	public boolean plot() {
		if (getEvalVE() == null || input.equals("")) {
			return false;
		}

		String oldEvalComment = evalComment;
		ValidExpression oldEvalVE = evalVE;
		ValidExpression oldInputVE = getInputVE();
		String oldAssignmentVar = assignmentVar;
		AssignmentType oldOVEAssignmentType = outputVE.getAssignmentType();
		AssignmentType oldIVEAssignmentType = getInputVE()==null? evalVE.getAssignmentType() : 
			getInputVE().getAssignmentType();
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

		if (evalVE.isTopLevelCommand()) {
			String cmd = evalVE.getTopLevelCommand().getName();
			if (!inequalityInEvalVE() && cmd.equals("Solve") 
					|| cmd.equals("Solutions")
					|| cmd.equals("CSolve")
					|| cmd.equals("CSolutions")
					|| cmd.equals("NSolve")
					|| cmd.equals("NSolutions")
					|| cmd.equals("Root")
					|| cmd.equals("ComplexRoot")) {
				//if we got evalVE by clicking Solve button, inputVE might just contain the equations
				//we want the command in input as well
				if(!pointList){
					inputVE = evalVE;
				}
				pointList = true;
			}
		}

		this.firstComputeOutput = true;
		this.computeOutput(true,true);
		if (twinGeo != null  && !dependsOnDummy(twinGeo))
			twinGeo.setLabel(null);
		if (twinGeo != null && twinGeo.getLabelSimple() != null
				&& twinGeo.isEuclidianShowable()) {
			String twinGeoLabelSimple = twinGeo.getLabelSimple();
			changeAssignmentVar(assignmentVar, twinGeoLabelSimple);
			/**
			 * We use EvalVE here as it's more transparent to push the command to the input
			 */
			getEvalVE().setAssignmentType(AssignmentType.DEFAULT);
			getEvalVE().setLabel(assignmentVar);		
			setInput(getEvalVE()
						.toAssignmentString(StringTemplate.numericDefault));
			
			computeOutput(false,false);
			this.update();
			clearStrings();
			
		} else {
			App.debug("Fail" + oldEvalComment);
			// plot failed, undo assignment
			assignmentVar = oldAssignmentVar;
			outputVE.setAssignmentType(oldOVEAssignmentType);
			getEvalVE().setAssignmentType(oldIVEAssignmentType);
			this.firstComputeOutput = true;
			evalComment = oldEvalComment;
			evalVE = oldEvalVE;
			setInputVE(oldInputVE);
			this.computeOutput(true,false);
			return false;
		}
		return true;
	}
	
	private boolean inequalityInEvalVE() {
		if(expandedEvalVE == null)
			return false;
		return expandedEvalVE.inspect(IneqFinder.INSTANCE);
	}

	private void clearStrings() {
		tooltip = null;
		latex = null;
		
	}

	/**
	 * @param pointList2 whether evalVE needs to be wrapped in PointList when evaluating
	 */
	public void setPointList(boolean pointList2) {
		pointList = pointList2;
	}
	
	@Override
	public boolean hasCoords() {
		return outputVE!=null && outputVE.hasCoords();
	}
	private int SCREEN_WIDTH = 80;
	@Override
	public String getTooltipText(final boolean colored, final boolean alwaysOn) {
		if(isError())
			return loc.getError(error);
		if(tooltip == null && outputVE!=null){				
				tooltip = getOutput(StringTemplate.defaultTemplate);
				tooltip = tooltip.replace("gGbSuM(", "\u03a3(");
				tooltip = tooltip.replace("gGbInTeGrAl(", "\u222b(");			
					
				if(tooltip.length()>SCREEN_WIDTH && tooltip.indexOf('{')>-1){
					int listStart = tooltip.indexOf('{');
					StringBuilder sb = new StringBuilder(tooltip.length()+20);
					sb.append(tooltip.substring(0,listStart+1));
					
					int currLine = 0;
					for(int i=listStart+1;i<tooltip.length();i++){
						if(tooltip.charAt(i)==','){
							App.debug(i);
							int nextComma = tooltip.indexOf(',', i+1);
							if(nextComma ==-1)
								nextComma = tooltip.length()-1;
							if(currLine+(nextComma-i)>SCREEN_WIDTH){
								sb.append(",\n");
								currLine=0;
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
		StringBuilder evalCmdLocal = new StringBuilder();
		if(pointList){
			evalCmdLocal.append(loc.getCommand("PointList"));
		}else if("".equals(evalCmd)){
			return Unicode.CAS_OUTPUT_PREFIX;
		}else if("Numeric".equals(evalCmd)){
			return Unicode.CAS_OUTPUT_NUMERIC;
		}else if("KeepInput".equals(evalCmd)){
			return Unicode.CAS_OUTPUT_KEEPINPUT;
		}else{
			evalCmdLocal.append(loc.getCommand(evalCmd));
		}

		if (input.startsWith(evalCmdLocal.toString())) {
			// don't show command if it is already at beginning of input
			return Unicode.CAS_OUTPUT_PREFIX;
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

}
