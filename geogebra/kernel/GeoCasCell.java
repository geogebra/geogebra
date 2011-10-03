package geogebra.kernel;

import geogebra.cas.error.CASException;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.util.Util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Cell pair of input and output strings used in the CAS view.
 * This needs to be a GeoElement in order to handle dependencies between 
 * cells and other GeoElements together with AlgoSymbolic.
 * 
 * @author Markus Hohenwarter
 */
public class GeoCasCell extends GeoElement {

	private ValidExpression inputVE, evalVE, outputVE;
	private String input, prefix, postfix, error, latex;
	private String localizedInput;
	private Locale currentLocale;
	private boolean suppressOutput = false;
	
	// input variables of this cell
	private TreeSet <String> invars, functionvars;
	// defined input GeoElements of this cell
	private TreeSet<GeoElement> inGeos;
	private boolean isCircularDefinition;	

	// twin geo, e.g. GeoCasCell m := 8 creates GeoNumeric m = 8
	private GeoElement twinGeo;
	private GeoElement lastOutputEvaluationGeo;
	private boolean firstComputeOutput;
	private boolean ignoreTwinGeoUpdate;


	// internal command names used in the input expression
	private HashSet <Command> commands;
	private String assignmentVar;
	private boolean includesRowReferences;
	private boolean includesNumericCommand;
	private boolean useGeoGebraFallback;
		
	private String evalCmd, evalComment;
	private int row = -1; // for CAS view, set by Construction

	public GeoCasCell(Construction c) {
		super(c);
		
		input = "";
		localizedInput = "";
		inputVE = null;
		outputVE = null;
		prefix = "";
		evalVE = null;
		postfix = "";
		evalCmd = "";
		evalComment = "";
	}		
	
	/**
	 * Sets this GeoCasCell to the current state of geo which also needs to be a GeoCasCell.
	 * Note that twinGeo is kept null.
	 */
	@Override
	public void set(GeoElement geo) {
//		GeoCasCell casCell = (GeoCasCell) geo;
//				
//		inputVE = casCell.inputVE;
//		evalVE = casCell.evalVE;
//		outputVE = casCell.outputVE;
//		
//		input = casCell.input;
//		prefix = casCell.prefix;
//		postfix = casCell.postfix;
//		error = casCell.error;
//		latex = casCell.latex;
//		localizedInput = casCell.localizedInput;
//		currentLocale = casCell.currentLocale;
//		suppressOutput = casCell.suppressOutput;
//		
//		// input variables of this cell
//		invars = casCell.invars;
//		// defined input GeoElements of this cell
//		inGeos = casCell.inGeos;
//		isCircularDefinition = casCell.isCircularDefinition;	
//
//		// twin geo, e.g. GeoCasCell m := 8 creates GeoNumeric m = 8
////		twinGeo = casCell.twinGeo;		
////		firstComputeOutput = casCell.firstComputeOutput;
////		useGeoGebraFallback = casCell.useGeoGebraFallback;
////		ignoreTwinGeoUpdate = casCell.ignoreTwinGeoUpdate;
//
//		// internal command names used in the input expression
//		cmdNames = casCell.cmdNames;
//		assignmentVar = casCell.assignmentVar;
//		includesRowReferences = casCell.includesRowReferences;
//		includesNumericCommand = casCell.includesNumericCommand;
//			
//		evalCmd = casCell.evalCmd;
//		evalComment = casCell.evalCmd;
	}
	
	/** 
	 * Returns the input of this row. Command names are localized when 
	 * kernel.isPrintLocalizedCommandNames() is true, otherwise internal command 
	 * names are used.
	 */
	public String getInput() {
		if (kernel.isPrintLocalizedCommandNames()) {
			// input with localized command names
			if (currentLocale != kernel.getApplication().getLocale()) {
				updateLocalizedInput();
			}
			return localizedInput;
		} 
		else {
			// input with internal command names
			return input;
		}
	}

	/** 
	 * Returns the output of this row.
	 */
	public String getOutput() {
		if (error != null) {
			if (kernel.isPrintLocalizedCommandNames())
				return kernel.getApplication().getError(error);
			else 
				return error;
		}
			
		if (outputVE == null) 
			return "";
		else
			return outputVE.toAssignmentString();
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	/** 
	 * Returns the evaluation text (between prefix and postfix) of this row using internal command names.
	 * This method is important to process this row using GeoGebraCAS.
	 */
	public String getEvalText() {
		if (evalVE == null) 
			return "";
		else
			return evalVE.toString();
	}
	
	/** 
	 * Returns the evaluation expression (between prefix and postfix) of this row.
	 * This method is important to process this row using GeoGebraCAS.
	 */
	public ValidExpression getEvalVE() {
		return evalVE;
	}
	
	public ValidExpression getInputVE(){
		return inputVE;
	}
	
	public String getPostfix() {
		return postfix;
	}
	
//	public void setAllowLaTeX(boolean flag) {
//		allowLaTeX = flag;
//	}
	
	public String getLaTeXOutput() {
		if (isError())
			return null;
		else if (latex == null) {
			if (outputVE != null) {
				// TODO Uncomment once support for latex line breaking is implemented.
				// Kernel kernel = app.getKernel();
				//boolean oldLineBreaks = kernel.isInsertLineBreaks();
				//kernel.setInsertLineBreaks(true);
				
				// create LaTeX string
				latex = outputVE.toAssignmentLaTeXString();
				
				// TODO Uncomment once support for latex line breaking is implemented.
				//kernel.setInsertLineBreaks(oldLineBreaks);
			}
		}
		
		return latex;
	}
		
	public boolean isEmpty() {
		return isInputEmpty() && isOutputEmpty();
	}
	
	public boolean isInputEmpty() {
		return inputVE == null;
	}
	
	public boolean isOutputEmpty() {
		return outputVE == null && error == null;
	}
	
	public boolean showOutput() {
		return !isOutputEmpty()  && !suppressOutput();
	}
	
	private boolean suppressOutput() {
		return suppressOutput && !isError();
	}
	
	/**
	 * Returns false as we don't want CAS cells to send
	 * their values to the CAS in update(). This is done in
	 * computeOutput() anyway.
	 */
    public boolean isSendingUpdatesToCAS() {
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
			String assignmentStr = twinGeo.toCasAssignment(ExpressionNode.STRING_TYPE_GEOGEBRA);
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
	 * @param inValue
	 * @return success
	 */
	public boolean setInput(String inValue) {
		suppressOutput = inValue.endsWith(";");
				
		// parse input into valid expression
		inputVE = parseGeoGebraCASInputAndResolveDummyVars(inValue);
				
		input = inValue != null ? inValue : ""; // remember exact user input
		prefix = "";
		evalVE = inputVE;
		postfix = "";
		setEvalCommand("");
		evalComment = "";
		
		// update input and output variables
		updateInputVariables(inputVE);

		// input should have internal command names
		internalizeInput();
		
		// for efficiency: input with localized command names
		updateLocalizedInput();
		
		// make sure cmputeOutput() knows that input has changed
		firstComputeOutput = true;
		
		if (!isEmpty()) {
			// make sure we put this casCell into the construction set
			cons.addToGeoSetWithCasCells(this);			
		}
		return true;
	}	
	
	private void updateLocalizedInput() {
		// for efficiency: localized input with local command names
		currentLocale = cons.getApplication().getLocale();
		localizedInput = localizeInput(input);
	}
	
	/**
	 * Sets row number for CAS view. 
	 * This method should only be called by Construction.updateCasCellRows().
	 */
	final public void setRowNumber(int row) {
		this.row = row;	
	}
	
	/***
	 * Returns position of the given GeoCasCell object (free or dependent) in the construction list.
	 * This is the row number used in the CAS view.
	 * 
	 * @return row number of casCell for CAS view or -1 if casCell is not in construction list
	 */
	final public int getRowNumber() {
		return row;
	}
	
	
	/**
	 * Updates row references strings in input by setting
	 * input = inputVE.toString()
	 */
	public void updateInputStringWithRowReferences() {
		if (!includesRowReferences) return;
		
		// inputVE will print the correct label, e.g. $4 for
		// the row reference
		boolean oldValue = kernel.isPrintLocalizedCommandNames();
		kernel.setPrintLocalizedCommandNames(false);
		input = inputVE.toAssignmentString();
		kernel.setPrintLocalizedCommandNames(oldValue);
		
		updateLocalizedInput();
	}
	
	/**
	 * Sets how this row should be evaluated. Note that the input
	 * is NOT changed by this method, so you need to call setInput()
	 * first. Make sure that input = prefix + eval without wrapper command + postfix. 
	 * 
	 * @param prefix: beginning part that should NOT be evaluated, e.g. "25a +"
	 * @param eval: part of the input that needs to be evaluated, e.g. "Expand[(a+b)^2]"
	 * @param postfix: end part that should NOT be evaluated, e.g. " + "5 (c+d)"
	 */
	public void setProcessingInformation(String prefix, String eval, String postfix) {
		setEvalCommand("");
		evalComment = "";
		if (prefix == null) prefix = "";
		if (postfix == null) postfix = "";
		
		// stop if input is assignment
		if (isAssignment()) {						
			if (eval.startsWith("KeepInput")) {
				setEvalCommand("KeepInput");
			}
			return;			
		}
		
		// nothing to do
		if ("".equals(prefix) && "".equals(postfix) && localizedInput.equals(eval))
			return;
		
		// parse eval text into valid expression
		evalVE = parseGeoGebraCASInputAndResolveDummyVars(eval);
		if (evalVE != null) {
			evalVE = resolveInputReferences(evalVE, inGeos);
			if (evalVE.isTopLevelCommand()) {
				// extract command from eval
				setEvalCommand(evalVE.getTopLevelCommand().getName());
			}
			this.prefix = prefix;
			this.postfix = postfix;
		}
		else {
			evalVE = inputVE;
			this.prefix = "";
			this.postfix = "";
		}
	}
	
//	private boolean hasPrefixOrPostfix() {
//		 return prefix.length() > 0 && postfix.length() > 0; 
//	}
	
	/**
	 * Checks if newInput is structurally equal to the current input String.
	 */
	public boolean isStructurallyEqualToLocalizedInput(String newInput) {
		if (localizedInput != null && localizedInput.equals(newInput)) 
			return true;
		
		// check if the structure of inputVE and prefix + evalText + postfix is equal
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
	private ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue) {
		try {			
			return kernel.getGeoGebraCAS().getCASparser().parseGeoGebraCASInputAndResolveDummyVars(inValue);
		}catch (Throwable e) {
			return null;
		} 
	}
	
	/**
	 * Updates the set of input variables and array of input GeoElements. 
	 * For example, the input "b := a + 5" has the input variable "a" 
	 */
	private void updateInputVariables(ValidExpression ve) {		
		// clear var sets
		clearInVars();

		if (ve == null) return;
		
		// get all command names
		commands = new HashSet<Command>();
		ve.addCommands(commands);
		if (commands.isEmpty()) {
			commands = null;
		} else {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand || "Numeric".equals(cmdName);
				
				// if command not known to CAS
				if (!kernel.getGeoGebraCAS().isCommandAvailable(cmd)) {
					if (kernel.lookupCasCellLabel(cmdName) != null ||
						kernel.lookupLabel(cmdName) != null) 
					{
						// treat command name as defined user function name
						getInVars().add(cmdName);
					}
					else if (kernel.getAlgebraProcessor().isCommandAvailable(cmdName)) {
						// command is known to GeoGebra: use possible fallback
						useGeoGebraFallback = true;
					}
					else {
						// treat command name as undefined user function name
						getInVars().add(cmdName);
					}
				}
				
			}
		}
		
		// get all used GeoElement variables
		// check for function
		boolean isFunction = ve instanceof FunctionNVar;
			
		// outvar of assignment b := a + 5 is "b"
		setAssignmentVar(ve.getLabel());

		// get input vars:
		HashSet<GeoElement> geoVars = ve.getVariables();
		if (geoVars != null) {
			for( GeoElement geo : geoVars) {
				String var = geo.getLabel();
				
				// local function variables are NOT input variables
				if (isFunction && ((FunctionNVar) ve).isFunctionVariable(var)) {
					// function variable, e.g. k in f(k) := k^2 + 3
					getFunctionVars().add(var);					
				} else {
					// input variable, e.g. b in a + 3 b
					getInVars().add(var);
				}
			}
		}

		// create Array of defined input GeoElements
		inGeos = updateInputGeoElements(invars);
		
		// replace GeoDummyVariable objects in inputVE by the found inGeos
		// This is important for row references and renaming of inGeos to work
		inputVE = resolveInputReferences(inputVE, inGeos);	
		
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
	
	/**
	 * Sets input to use internal command names and translatedInput 
	 * to use localized command names. As a side effect, all command
	 * names are added as input variables as they could be function names.
	 */
	private void internalizeInput() {	
		// local commands -> internal commands
		input = translate(input, false); 
	}
	
	/** 
	 * Returns the input using command names in the current language.
	 */
	private String localizeInput(String input) {
		// replace all internal command names in input by local command names
		if (kernel.isPrintLocalizedCommandNames()) {
			// internal commands -> local commands
			return translate(input, true); 
		} else 
			// keep internal commands
			return input;
	}
	
	/**
	 * Translates given expression by replacing all command names
	 * @param exp
	 * @param toLocalCmd true: internalCmd -> localCmd, false: localCmd -> internalCmd
	 * @return translated expression
	 */
	private String translate(String exp, boolean toLocalCmd) {
		if (commands == null) return exp;
		
		String translatedExp = exp;
		Iterator<Command> it = commands.iterator();
		while (it.hasNext()) {
			String internalCmd = it.next().getName();
			String localCmd = cons.getApplication().getCommand(internalCmd);
			
			if (toLocalCmd) {
				// internal command names -> local command names
				translatedExp = replaceAllCommands(translatedExp, internalCmd, localCmd);
			} else {
				// local command names -> internal command names
				translatedExp = replaceAllCommands(translatedExp, localCmd, internalCmd);
			}
		}
		
		return translatedExp;
	}
	
	/**
	 * Replaces oldCmd command names by newCmd command names in expression.
	 */
	private static String replaceAllCommands(String expression, String oldCmd, String newCmd) {
		// build regex to find local command names
		StringBuilder regexPrefix = new StringBuilder();	
		regexPrefix.append("(?i)"); // ignore case
		regexPrefix.append("\\b"); // match words for command only, not parts of a word
		
		// replace commands with [
		StringBuilder regexSb = new StringBuilder(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\[]");
		StringBuilder newCmdSb = new StringBuilder(newCmd);
		newCmdSb.append("[");
		expression = expression.replaceAll(regexSb.toString(), newCmdSb.toString());
		
		// replace commands with (
		regexSb.setLength(0);
		regexSb.append(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\(]");
		newCmdSb.setLength(0);
		newCmdSb.append(newCmd);
		newCmdSb.append("(");
		return expression.replaceAll(regexSb.toString(), newCmdSb.toString());
	}
	
	/**
	 * Set assignment var of this cell. For example "b := a^2 + 3"
	 * has assignment var "b".
	 * @param var
	 */
	private void setAssignmentVar(String var) {
		if (assignmentVar != null && assignmentVar.equals(var))
			return;
		
		if (assignmentVar != null)
			// remove old label from construction
			cons.removeCasCellLabel(assignmentVar);
		
		// make sure we are using an unused label
		if (var == null || cons.isFreeLabel(var)) {
			assignmentVar = var;
		} else {
			changeAssignmentVar(var, getDefaultLabel());						
		}
			
		// store label of this CAS cell in Construction
		if (assignmentVar != null) {
			if (twinGeo != null)
				twinGeo.rename(assignmentVar);		
			cons.putCasCellLabel(this, assignmentVar);
		} else {
			// remove twinGeo if we had one
			setTwinGeo(null);
		}
		
		//view.setAssignment(var, this);
	}		
	
	/**
	 * Removes assignment variable from CAS.
	 */
    public void unbindVariableInCAS() {
    	// remove assignment variable
    	if (isAssignment()) {
    		kernel.unbindVariableInGeoGebraCAS(assignmentVar);
    	}   
    }
	
	/**
	 * Replace old assignment var in input, e.g. "m := 8" becomes "a := 8"
	 * @param oldLabel
	 * @param newLabel
	 */
	private void changeAssignmentVar(String oldLabel, String newLabel) {
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
	 * @param i
	 * @return
	 */
	public String getInVar(int n) {
		if (invars == null) return null;
		
		Iterator<String> it = invars.iterator();
		int pos=0; 
		while (it.hasNext()) {
			String var = it.next();
			if (pos == n) return var;
			pos++;
		}
		
		return null;
	}
	
	/**
	 * Returns all GeoElement input variables including
	 * GeoCasCell objects and row references in construction order.
	 * 
	 * @return input GeoElements including GeoCasCell objects
	 */
	public TreeSet<GeoElement> getGeoElementVariables() {
		if (inGeos == null) {
			inGeos = updateInputGeoElements(invars);
		}
		return inGeos;		
	}
	
	private TreeSet<GeoElement> updateInputGeoElements(TreeSet<String> invars) {
		if (invars == null || invars.isEmpty())
			return null;
		
		// list to collect geo variables
		TreeSet<GeoElement> geoVars = new TreeSet<GeoElement>();
			
		// go through all variables
		for (String varLabel : invars) {
			// lookup GeoCasCell first
			GeoElement geo = kernel.lookupCasCellLabel(varLabel);
			
			if (geo == null) {
				// try row reference lookup
				// $ for previous row
				if (varLabel.equals(ExpressionNode.CAS_ROW_REFERENCE_PREFIX)) {			
					geo = row > 0 ? cons.getCasCell(row-1) : cons.getLastCasCell();
				} else {
					geo = kernel.lookupCasRowReference(varLabel);
				}
				if (geo != null)
					includesRowReferences = true;
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
		
		if (geoVars.size() == 0)
			return null;
		else 
			return geoVars;				
	}
	
	/**
	 * Replaces GeoDummyVariable objects in inputVE by the found inGeos.
	 * This is important for row references and renaming of inGeos to work.
	 */
	private ValidExpression resolveInputReferences(ValidExpression ve, TreeSet<GeoElement> inGeos) {
		if (ve == null) return ve;
		
		ValidExpression ret;
		
		// make sure we have an expression node
		ExpressionNode node;
		if (ve instanceof FunctionNVar) {
			node = ((FunctionNVar) ve).getExpression();
			ret = ve; // make sure we return the Function
		}
		else if (ve instanceof ExpressionNode) {
			node = (ExpressionNode) ve;
			ret = ve; // return the original ExpressionNode
		} 
		else {
			node = new ExpressionNode(kernel, ve);
			node.setLabel(ve.getLabel());
			ret = node; // return a new ExpressionNode
		}
		
		// replace GeoDummyVariable occurances for each geo
		if (inGeos != null) {
			for (GeoElement inGeo : inGeos) {
				boolean success = node.replaceGeoDummyVariables(inGeo.getLabel(), inGeo);
				if (!success) {
					// try $ row reference
					node.replaceGeoDummyVariables(ExpressionNode.CAS_ROW_REFERENCE_PREFIX, inGeo);
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
	private void resolveFunctionVariableReferences(ValidExpression outputVE) {
		if (!(outputVE instanceof FunctionNVar)) return;
		
		FunctionNVar fun = (FunctionNVar) outputVE;

		// replace function variables in tree
		for (FunctionVariable fVar : fun.getFunctionVariables()) {			
			// look for GeoDummyVariable objects with name of function variable and	replace them		
			fun.getExpression().replaceVariables(fVar.getSetVarString(), fVar);		
		}		
	}
	
	/**
	 * Replaces GeoDummyVariable objects in outputVE by GeoElements from kernel
	 * that are not GeoCasCells.
	 */
	private void resolveGeoElementReferences(ValidExpression outputVE) {
		if (invars == null || !(outputVE instanceof FunctionNVar)) return;
		FunctionNVar fun = (FunctionNVar) outputVE;

		// replace function variables in tree
		for (String varLabel : invars) {			
			GeoElement geo = kernel.lookupLabel(varLabel);
			if (geo != null) {
				// look for GeoDummyVariable objects with name of function variable and	replace them		
				fun.getExpression().replaceGeoDummyVariables(varLabel, geo);	
			}
		}		
	}
	
	/**
	 * Returns whether this object only depends on named GeoElements defined in the kernel.
	 */
	final public boolean includesOnlyDefinedVariables() {
		if (invars == null) return true;
		
		for (String varLabel : invars) {
			if (kernel.lookupLabel(varLabel) == null)
				return false;
		}
		return true;
	}
	
	/**
	 * Returns whether var is an input variable of this cell. For example,
	 * "b" is an input variable of "c := a + b"
	 */
	final public boolean isInputVariable(String var) {
		return invars != null && invars.contains(var);
	}
	
	/**
	 * Returns whether var is a function variable of this cell. For example,
	 * "y" is a function variable of "f(y) := 2y + b"
	 */
	final public boolean isFunctionVariable(String var) {
		return functionvars != null && functionvars.contains(var);
	}
	
	/**
	 * Returns the function variable string if input is a function or null otherwise. 
	 * For example, "m" is a function variable of "f(m) := 2m + b"
	 */
	final public String getFunctionVariable() {
		if (functionvars != null && !functionvars.isEmpty()) {
			return functionvars.first();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns whether this cell includes row references like $2.
	 */
	final public boolean includesRowReferences() {
		return includesRowReferences;
	}
	
	/**
	 * Returns whether this cell includes any Numeric[] commands.
	 */
	final public boolean includesNumericCommand() {
		return includesNumericCommand;
	}
	
	/**
	 * Returns the assignment variable of this cell. For example,
	 * "c" is the assignment variable of "c := a + b"
	 * @return may be null
	 */
	final public String getAssignmentVariable() {
		return assignmentVar;
	}
	
	final public boolean isAssignment() {
		return assignmentVar != null;
	}
	
	final public String getEvalCommand() {
		return evalCmd;
	}
	
	final public void setEvalCommand(String cmd) {
		evalCmd = cmd;		

		includesNumericCommand = includesNumericCommand || 
			evalCmd != null && evalCmd.equals("Numeric");
		setKeepInputUsed(evalCmd != null && evalCmd.equals("KeepInput"));
	}
	
	public void setKeepInputUsed(boolean keepInputUsed) {
		if (inputVE != null)
			inputVE.setKeepInputUsed(keepInputUsed);
		if (evalVE != null)
			evalVE.setKeepInputUsed(keepInputUsed);
	}
	
	final public void setEvalComment(String comment) {
		if (comment != null)
			evalComment = comment;
	}
	
	final public String getEvalComment() {
		return evalComment;
	}

	public void setOutput(String output) {
		error = null;
		latex = null;

		boolean oldValue = kernel.isKeepCasNumbers();
		
		// make sure numbers and their precision are kept from Numeric[] commands
		kernel.setKeepCasNumbers(includesNumericCommand);
		
		// when input is a function declaration, output also needs to become a function
		// so we need to add f(x,y) := if it is missing
		boolean isFunctionDeclaration = isAssignment() 
			&& functionvars != null; 
		// note: MPReduce returns "f" for a function definition "f(x) := x^2"
		//	&& !output.startsWith(assignmentVar);
		
		if (isFunctionDeclaration) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputVE.getLabelForAssignment());
			sb.append(inputVE.getAssignmentOperator());
			sb.append(output);
			output = sb.toString();
		}
			
		// parse output into valid expression
		outputVE = parseGeoGebraCASInputAndResolveDummyVars(output);	

		if (isFunctionDeclaration) {
			// replace GeoDummyVariable objects in outputVE by the function variables	
			resolveFunctionVariableReferences(outputVE);
			// replace GeoDummyVariable objects in outputVE by GeoElements from kernel
			resolveGeoElementReferences(outputVE);
		} else if (isAssignment())
			outputVE.setLabel(assignmentVar);
				
		kernel.setKeepCasNumbers(oldValue);
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
		if (!isAssignment() || isError() || !includesOnlyDefinedVariables()) 
			return;
		
		// check that assignment variable is not a reserved name in GeoGebra
		if (ExpressionNodeConstants.RESERVED_FUNCTION_NAMES.contains(assignmentVar))
			return;
		
		// try to create twin geo for assignment, e.g. m := c + 3
		GeoElement newTwinGeo = silentEvalInGeoGebra(outputVE);		
		if (newTwinGeo != null) {
			setTwinGeo(newTwinGeo);
		}				
	}
	
	/**
	 * Sets the label of twinGeo.
	 * @return whether label was set
	 */
	public boolean setLabelOfTwinGeo() {
		if (twinGeo == null || twinGeo.isLabelSet() || !isAssignment()) return false;		
		
		// allow GeoElement to get same label as CAS cell, so we temporarily remove the label
		// but keep it in the underlying CAS
		cons.removeCasCellLabel(assignmentVar, false);
		// set Label of twinGeo
		twinGeo.setLabel(assignmentVar);
		// set back CAS cell label
		cons.putCasCellLabel(this, assignmentVar);
		
		return true;
	}
	
//	/**
//	 * Redefine twinGeo using current output
//	 */
//	private void redefineTwinGeo() {
//		if (!isAssignment()) {
//			// remove twinGeo when we no longer have an assignment
//			setTwinGeo(null);
//			return;
//		}
//		else if (isError() || !includesOnlyDefinedVariables()) {
//			if (twinGeo.hasChildren()) {
//				// set twinGeo to undefined
//				twinGeo.setUndefined();
//			} else {
//				// remove twinGeo when we no longer have an assignment
//				setTwinGeo(null);
//			}
//			return;
//		}
//		
//		// try simple evaluation of independent output first
//		if (twinGeo.isIndependent() && getGeoElementVariables() == null) {
//			simpleUpdateTwinGeo();
//			if (twinGeo.isDefined()) 
//				return; // success
//		}
//		
//		// redefine twinGeo using outputVE
//		GeoElement newTwinGeo = kernel.getAlgebraProcessor().changeGeoElement(twinGeo, outputVE, true, false);   
//		if (newTwinGeo != null) {
//			// redefinition successful
//			app.doAfterRedefine(newTwinGeo);	
//			setTwinGeo(newTwinGeo);
//		} 
//		else {
//			// redefinition failed
//			twinGeo.setUndefined();
//		}						
//	}
	
	/**
	 * Sets twinGeo using current output
	 */
	private void simpleUpdateTwinGeo() {
		if (twinGeo == null) 
			return;		
		else if (isError()) {
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
	
	 public void updateCascade() {
		update();

		if (twinGeo != null) {
			ignoreTwinGeoUpdate = true;
			twinGeo.update();
			ignoreTwinGeoUpdate = false;
			updateAlgoUpdateSetWith(twinGeo);
		}
		else if (algoUpdateSet != null) {
			// update all algorithms in the algorithm set of this GeoElement
			algoUpdateSet.updateAll();
		}
	}
	
//	/**
//	 * Evaluates the given expression in GeoGebra and returns one GeoElement or null.
//	 * 
//	 * @param ve
//	 * @return result GeoElement or null
//	 */
//	private GeoElement silentEvalInGeoGebra(String exp) {
//		ValidExpression ve;
//	
//		try {
//			ve = kernel.getParser().parseGeoGebraExpression(exp);
//		} catch (Throwable e) {
//			System.err.println("GeoCasCell.silentEvalInGeoGebra: " + exp + "\n\terror: " + e.getMessage());
//			return null;
//		}	
//		
//		return silentEvalInGeoGebra(ve);
//	}
	
	/**
	 * Evaluates ValidExpression in GeoGebra and returns one GeoElement or null.
	 * 
	 * @param ve
	 * @return result GeoElement or null
	 */
	private GeoElement silentEvalInGeoGebra(ValidExpression ve) {
		boolean oldValue = kernel.isSilentMode();
		kernel.setSilentMode(true);
		
		try {
			// evaluate in GeoGebra
			GeoElement [] ggbEval = kernel.getAlgebraProcessor().doProcessValidExpression(ve);
			if (ggbEval != null) {
				return ggbEval[0];
			} else {
				return null;
			}
		} 
		catch (Throwable e) {
			System.err.println("GeoCasCell.silentEvalInGeoGebra: " + ve + "\n\terror: " + e.getMessage());
			return null;
		}
		finally {
			kernel.setSilentMode(oldValue);
		}							
	}
	
	/**
	 * Computes the output of this CAS cell based on its current 
	 * input settings. Note that this will also change a corresponding twinGeo.
	 * 
	 * @return success
	 */
	final public boolean computeOutput() {		
		return computeOutput(true);
	}		
		
	/**
	 * Computes the output of this CAS cell based on its current 
	 * input settings. 
	 * @param doTwinGeoUpdate whether twin geo should be updated or not
	 * 
	 * @return success
	 */
	private boolean computeOutput(boolean doTwinGeoUpdate) {
		// check for circular definition before we do anything
		if (isCircularDefinition) {			
			setError("CircularDefinition");
			if (doTwinGeoUpdate)
				updateTwinGeo();
			return false;
		}		
		
		String result = null;
		boolean success = false;
		CASException ce = null;
		
		if (!useGeoGebraFallback) {
			// CAS EVALUATION		
			try {
					if (evalVE == null)
						throw new CASException("Invalid input (evalVE is null)");
					result = kernel.getGeoGebraCAS().evaluateGeoGebraCAS(evalVE);
					success = result != null;
			} 
			catch (CASException e) {
				System.err.println("GeoCasCell.computeOutput(), CAS eval: " + evalVE + "\n\terror: " + e.getMessage());
				success = false;	
				ce = e;
			}			
		}
		
		// TODO make fallback more efficient to only use algebra processor once
		// then just use output geos to set twin geo
		// GEOGEBRA FALLBACK
		else {
			// EVALUATE evalVE in GeoGebra
			boolean oldValue = kernel.isSilentMode();
			kernel.setSilentMode(true);
			
			try {
				// process inputExp in GeoGebra					
				GeoElement [] geos = kernel.getAlgebraProcessor().
					processAlgebraCommandNoExceptionHandling( evalVE.toAssignmentString(), false, false, false );
				
				//GeoElement evalGeo = silentEvalInGeoGebra(evalVE);
				if (geos != null) {
					success = true;
					kernel.setTemporaryPrintFigures(15);
					result = geos[0].toValueString();
					kernel.restorePrintAccuracy();
					AlgoElement parentAlgo = geos[0].getParentAlgorithm();
					//cons.removeFromConstructionList(parentAlgo);		
					if (parentAlgo != null)
						parentAlgo.remove();
				}				
			} catch (Throwable th2) {
				System.err.println("GeoCasCell.computeOutput(), GeoGebra eval: " + evalVE + "\n error: " + th2.getMessage());
				success = false;
			} finally {
				kernel.setSilentMode(oldValue);
			}
		}		
		
		// set Output
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
			if (ce == null)
				setError("CAS.GeneralErrorMessage");
			else
				setError(ce.getKey());
		}
		
		// update twinGeo
		if (doTwinGeoUpdate)
			updateTwinGeo();
		
		// set back firstComputeOutput, see setInput()
		firstComputeOutput = false;
		return success;
	}
	
	public void setError(String error) {
		this.error = error;
		latex = null;
		outputVE = null;
	}
	
	public boolean isError() {
		return error != null;
	}
	
	public boolean isCircularDefinition() {
		return isCircularDefinition;
	}
	
	/**
	 * Appends <cascell caslabel="m"> XML tag to StringBuilder.
	 */
	protected void getElementOpenTagXML(StringBuilder sb) {		
		sb.append("<cascell");
		if (assignmentVar != null) {
			sb.append(" caslabel=\"");
			sb.append(Util.encodeXML(assignmentVar));
			sb.append("\" ");
		}
		sb.append(">\n");
	}

	/**
	 * Appends </cascell> XML tag to StringBuilder.
	 */
	protected void getElementCloseTagXML(StringBuilder sb) {
		sb.append("</cascell>\n");	
	}
	
	/**
	 * Appends <cellPair> XML tag to StringBuilder.
	 */
	protected void getXMLtags(StringBuilder sb) {
		//StringBuilder sb = new StringBuilder();
		sb.append("\t<cellPair>\n");

		// inputCell
		if (!isInputEmpty()) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(input));
			sb.append("\" ");
			
			if (evalVE != inputVE) {
				if (!"".equals(prefix)) {
					sb.append(" prefix=\"");
					sb.append(Util.encodeXML(prefix));
					sb.append("\" ");
				}
				
				sb.append(" eval=\"");
				sb.append(Util.encodeXML(getEvalText()));
				sb.append("\" ");
				
				if (!"".equals(postfix)) {
					sb.append(" postfix=\"");
					sb.append(Util.encodeXML(postfix));
					sb.append("\" ");
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
			sb.append(Util.encodeXML(getOutput()));
			sb.append("\"");
			if (isError()) {
				sb.append(" error=\"true\"");
			}
			
			if (!"".equals(evalCmd)) {
				sb.append(" evalCommand=\"");
				sb.append(Util.encodeXML(evalCmd));
				sb.append("\" ");
			}
			
			if (!"".equals(evalComment)) {
				sb.append(" evalComment=\"");
				sb.append(Util.encodeXML(evalComment));
				sb.append("\" ");
			}
			
			sb.append("/>\n");
			sb.append("\t\t");
			sb.append("</outputCell>\n");
		}
		
		sb.append("\t</cellPair>\n");

		//return sb.toString();
	}

	@Override
	public int getGeoClassType() {		
		return GEO_CLASS_CAS_CELL;
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
		if (twinGeo != null)
			twinGeo.setUndefined();
	}

	@Override
	public String toValueString() {
		return toString();
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
	protected String getTypeString() {
		return "CasCell";
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		return false;
	}

	@Override
	public String getClassName() {
		return "GeoCasCell";
	}
	
	/**
	 * Returns assignment variable, e.g. "a" for "a := 5"
	 * or row reference, e.g. "$5$". Note that kernel.getCASPrintForm()
	 * is taken into account, e.g. row references return the output of this cell
	 * (instead of the label) for the underlying CAS. 
	 */
	public String getLabel() {
		// standard case: assignment
		if (assignmentVar != null)
			return kernel.printVariableName(assignmentVar);

		// row reference like $5
		StringBuilder sb = new StringBuilder();
		switch (kernel.getCASPrintForm()) {
		// send output to underlying CAS
		case ExpressionNodeConstants.STRING_TYPE_MPREDUCE:
		case ExpressionNodeConstants.STRING_TYPE_MAXIMA:
			sb.append(" (");
			sb.append(outputVE == null ? "?" : outputVE.toString());
			sb.append(") ");
			break;

		default:
			// standard case: return current row, e.g. $5
			if (row >= 0) {
				sb.append(ExpressionNode.CAS_ROW_REFERENCE_PREFIX);
				sb.append(row + 1);
			}
			break;
		}
		return sb.toString();
	}
	
	public String toString() {
		return getInput();
	}
	
	public boolean isGeoCasCell() {
		return true;
	}
	
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
	private void setTwinGeo(GeoElement newTwinGeo) {
		if (newTwinGeo == null && twinGeo != null) {
			GeoElement oldTwinGeo = twinGeo;
			twinGeo = null;			
			oldTwinGeo.setCorrespondingCasCell(null);
			oldTwinGeo.remove();
		}
		
		twinGeo = newTwinGeo;
		
		if (twinGeo != null) {
			twinGeo.setCorrespondingCasCell(this);
		}
	}
	
	public GeoElement getTwinGeo() {
		return twinGeo;
	}
	
	/**
	 * Adds algorithm to update set of this GeoCasCell
	 * and also to the update set of an independent twinGeo.
	 */
	public void addToUpdateSets(AlgoElement algorithm) {
	     super.addToUpdateSets(algorithm);
	     if (twinGeo != null && twinGeo.isIndependent()) {
	         twinGeo.addToUpdateSets(algorithm);
	     }
	}

	/**
	 * s algorithm from update set of this GeoCasCell
	 * and also from the update set of an independent twinGeo.
	 */
	public void removeFromUpdateSets(AlgoElement algorithm) {
	     super.removeFromUpdateSets(algorithm);
	     if (twinGeo != null && twinGeo.isIndependent()) {
	         twinGeo.removeFromUpdateSets(algorithm);
	     }
	}

	public ValidExpression getOutputValidExpression() {
		return outputVE;
	}

//	public void setIgnoreTwinGeoUpdate(boolean ignoreTwinGeoUpdate) {
//		this.ignoreTwinGeoUpdate = ignoreTwinGeoUpdate;
//	}
	public  boolean isLaTeXDrawableGeo(String latexStr) {
		return isLaTeXneeded(latexStr);
	}

}
