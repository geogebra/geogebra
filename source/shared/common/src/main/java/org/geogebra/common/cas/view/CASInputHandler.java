package org.geogebra.common.cas.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionExpander;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Handles CAS input
 */
public class CASInputHandler {

	private CASView casView;
	private Kernel kernel;
	private CASTable consoleTable;
	private CASCellProcessor casCellProcessor;

	/**
	 * @param view
	 *            CAS view
	 */
	public CASInputHandler(CASView view) {
		this.casView = view;
		kernel = view.getApp().getKernel();
		consoleTable = view.getConsoleTable();
		casCellProcessor = new CASCellProcessor(
				view.getApp().getLocalization());
	}

	/**
	 * Process input of current row.
	 * 
	 * @param command
	 *            command like "Factor" or "Integral"
	 * @param focus
	 *            whether focus should stay in CAS
	 */
	public void processCurrentRow(String command, boolean focus, String oldXML) {
		String ggbcmd = command;
		int selRow = consoleTable.getSelectedRow();
		GeoCasCell cellValue = consoleTable.getGeoCasCell(selRow);
		if (cellValue == null) {
			return;
		}
		// Text cells do not need the processing below
		if (cellValue.isUseAsText()) {
			processRowThenEdit(selRow, true, oldXML);
			return;
		}
		// Multiple cells selected and solve button clicked
		if (("Solve".equalsIgnoreCase(ggbcmd) || "NSolve"
				.equalsIgnoreCase(ggbcmd))
				&& (consoleTable.getSelectedRows().length > 1)) {
			processMultipleRows(ggbcmd, oldXML);
			return;
		}
		cellValue.setError(null);
		// get editor
		CASTableCellEditor cellEditor = consoleTable.getEditor();

		// get possibly selected text
		String selectedText = cellEditor.getInputSelectedText();

		int selStart = cellEditor.getInputSelectionStart();
		int selEnd = cellEditor.getInputSelectionEnd();
		String selRowInput = cellEditor.getInput();

		// needed for GGB-517
		if (cellValue.getLocalizedInput().equals("")) {
			cellValue.setInput(selRowInput);
		}

		// hack for debugging the underlying cas
		if (selRowInput != null && selRowInput.startsWith("@")) {
			try {
				String s = kernel.getGeoGebraCAS()
						.evaluateRaw(selRowInput.substring(1));
				GeoText text = kernel
						.lookupLabel("casOutput") instanceof GeoText
								? (GeoText) kernel.lookupLabel("casOutput")
								: new GeoText(kernel.getConstruction());
				if (!text.isLabelSet()) {
					text.setLabel("casOutput");
				}
				// Log.debug(s);
				text.setTextString(s);
				text.updateRepaint();

			} catch (Throwable e) {
				Log.debug(e);
			}
			return;
		}

		if (selRowInput == null || selRowInput.length() == 0) {
			if (consoleTable.getSelectedRow() != -1) {
				consoleTable.startEditingRow(consoleTable.getSelectedRow());
				GeoCasCell cell = consoleTable
						.getGeoCasCell(consoleTable.getSelectedRow());
				if (cell.getInputVE() != null) {
					selRowInput = cell.getInputVE()
							.toString(StringTemplate.numericDefault);
				}
			}
			// process empty row
			if (selRowInput.length() == 0) {
				// not first row
				if (selRow > 0) {
					selRowInput = wrapPrevCell(selRow, cellValue);
				} else {
					return;
				}
			}
		}

		// save the edited value into the table model
		consoleTable.stopEditing();

		// STANDARD CASE: GeoGebraCAS input
		// break text into prefix, evalText, postfix
		String prefix, evalText, postfix;
		boolean hasSelectedText = meaningfulSelection(selectedText);
		if (hasSelectedText) {
			// selected text: break it up into prefix, evalText, and postfix
			prefix = selRowInput.substring(0, selStart).trim() + " ";
			if (selStart > 0 || selEnd < selRowInput.length()) {
				// part of input is selected
				evalText = "(" + selectedText + ")";
				// for avoiding splitting text like 7-(x-2) as 7(-(x-2))
				// as they are different, a '+' is appended to the prefix
				char firstEvalChar = selectedText.trim().charAt(0);
				if (firstEvalChar == '+' || firstEvalChar == '-') {
					prefix = prefix + "+";
				}
			} else {
				// full input is selected
				evalText = selectedText;
			}
			postfix = selRowInput.substring(selEnd).trim();
		} else {
			// no selected text: evaluate input using current cell
			prefix = "";
			evalText = selRowInput;
			postfix = "";
		}

		try {
			// resolve static row references and change input field accordingly
			boolean staticReferenceFound = false;
			String newPrefix = resolveCASrowReferences(prefix, selRow,
					GeoCasCell.ROW_REFERENCE_STATIC, false);
			if (!newPrefix.equals(prefix)) {
				staticReferenceFound = true;
				prefix = newPrefix;
			}
			String newEvalText = resolveCASrowReferences(evalText, selRow,
					GeoCasCell.ROW_REFERENCE_STATIC, hasSelectedText);
			if (!newEvalText.equals(evalText)) {
				staticReferenceFound = true;
				evalText = newEvalText;
			}
			String newPostfix = resolveCASrowReferences(postfix, selRow,
					GeoCasCell.ROW_REFERENCE_STATIC, false);
			if (!newPostfix.equals(postfix)) {
				staticReferenceFound = true;
				postfix = newPostfix;
			}
			if (staticReferenceFound) {
				// change input if necessary
				cellValue.setInput(newPrefix + newEvalText + newPostfix);
			}

			if ("NSolve".equals(ggbcmd)) {
				String inputStrForNSolve = handleNSolve(cellValue, evalText);

				// get input string for NSolve

				if (inputStrForNSolve != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(inputStrForNSolve);

					// sb.append("]");
					if (!cellValue.getLocalizedInput()
							.equals(sb.toString())) {
						cellValue.setNSolveCmdNeeded(true);
						cellValue.setInput(sb.toString());
						selRowInput = sb.toString();
						evalText = sb.toString();
					}
				}
			}

			if (cellValue.getNSolveCmdNeeded() && !"NSolve".equals(ggbcmd)) {
				if (cellValue.getInputVE() != null && cellValue.getInputVE()
						.getTopLevelCommand() != null) {
					cellValue.setNSolveCmdNeeded(false);
				} else {
					ggbcmd = "NSolve";
				}
			}

			// FIX common INPUT ERRORS in evalText
			if (!hasSelectedText && ("Evaluate".equals(ggbcmd)
					|| "KeepInput".equals(ggbcmd))) {
				String fix = casCellProcessor.fixInput(cellValue, selRowInput,
						staticReferenceFound);
				if (fix != null) {
					evalText = fix;
				}
			}

			// we want to avoid user selecting a+b in (a+b)/c
			// TODO cache this somehow
			boolean structureOK = cellValue.isStructurallyEqualToLocalizedInput(
					prefix + evalText + postfix);
			if (!structureOK) {
				// show current selection again
				consoleTable.startEditingRow(selRow);
				cellEditor = consoleTable.getEditor();
				cellEditor.setInputSelectionStart(selStart);
				cellEditor.setInputSelectionEnd(selEnd);
				return;
			}

			boolean isAssignment = cellValue.getAssignmentVariable() != null;
			boolean isEvaluate = "Evaluate".equals(ggbcmd);
			boolean isNumeric = "Numeric".equals(ggbcmd);
			boolean isKeepInput = "KeepInput".equals(ggbcmd);

			// Substitute dialog
			if ("Substitute".equals(ggbcmd)) {
				// if cell has assignment and nothing other is selected -> use
				// input without defnition
				// eg. a:=b+c
				// use only b+c
				if (isAssignment && !hasSelectedText) {
					evalText = cellValue.getInputVE()
							.toString(StringTemplate.defaultTemplate);
				}
				// show substitute dialog
				casView.showSubstituteDialog(prefix, evalText, postfix, selRow);
				return;
			}

			// assignments are processed immediately, the ggbcmd creates a new
			// row below
			if (isAssignment) {
				processAssignment(ggbcmd, cellValue, prefix, postfix, focus,
						selRow, isKeepInput || isEvaluate || isNumeric);
				return;
			}

			// standard case: build eval command
			// don't wrap Numeric[pi, 20] with a second Numeric command
			// as this would remove precision
			// don't wrap in KeepInput neither
			boolean wrapEvalText = !isEvaluate && !isKeepInput
					&& !(isNumeric && (evalText.startsWith("Numeric[")
							|| evalText.startsWith("Numeric(")));

			if (wrapEvalText) {
				// prepare evalText as ggbcmd[ evalText, parameters ... ]
				StringBuilder sb = new StringBuilder();
				sb.append(ggbcmd);
				sb.append("[");
				sb.append(evalText);
				sb.append("]");
				evalText = sb.toString();
			}

			// remember evalText and selection for future calls of processRow()
			cellValue.setProcessingInformation(prefix, evalText, postfix);
			cellValue.setEvalCommand(ggbcmd);

		} catch (CASException ex) {
			cellValue.setError(ex.getKey());
		}
		// process given row and below, then start editing
		processRowThenEdit(selRow, focus, oldXML);
	}

	private String wrapPrevCell(int selRow, GeoCasCell cellValue) {
		// get previous cell
		GeoCasCell prevCell = consoleTable.getGeoCasCell(selRow - 1);
		if (prevCell != null && prevCell.getValue() != null) {
			// get output of previous cell
			StringBuilder prevCellName = new StringBuilder();
			prevCellName.append(prevCell.getAssignmentVariable());
			if (!prevCellName.toString().equals("null")) {
				if (prevCell.getFunctionVariables() != null) {
					prevCellName.append("(");
					FunctionVariable[] fVars = prevCell.getFunctionVariables();
					for (int i = 0; i < fVars.length; i++) {
						prevCellName.append(fVars[i]
								.toString(StringTemplate.defaultTemplate));
						if (i != fVars.length - 1) {
							prevCellName.append(",");
						}
					}
					prevCellName.append(")");
				}
				cellValue.setInput(prevCellName.toString());
				return prevCellName.toString();
			}
			cellValue.setInput("$" + selRow);
			return "$" + selRow;
		}
		return "";
	}

	private void processAssignment(String ggbcmd, GeoCasCell cellValue,
			String prefix, String postfix, boolean focus,
			int selRow, boolean isBasicTool) {
		boolean isNumeric = "Numeric".equals(ggbcmd);
		ValidExpression inVE = cellValue.getInputVE();
		StringBuilder oldXML = cellValue.getConstruction().getCurrentUndoXML(false);
		// if evaluation mode is Numeric, only the evaluation text is
		// wrapped, input is left unchanged
		if (isNumeric && inVE != null) {
			// evaluation text is wrapped only if the input is not
			// already wrapped
			if (inVE.getTopLevelCommand() == null
					|| !inVE.getTopLevelCommand().getName().equals("Numeric")) {
				cellValue.setProcessingInformation(prefix,
						ggbcmd + "["
								+ inVE.toString(StringTemplate.numericNoLocal)
								+ "]",
						postfix);
			}
			// otherwise set the evaluation text to input
		} else {
			cellValue.setProcessingInformation(prefix,
					cellValue.getLocalizedInput(),
					postfix);
		}
		if (isBasicTool) {
			cellValue.setEvalCommand(ggbcmd);
		}
		// evaluate assignment row
		boolean needInsertRow = !isBasicTool;
		boolean success = processRowThenEdit(selRow, !needInsertRow && focus,
				oldXML.toString());

		// insert a new row below with the assignment label and process
		// it
		// using the current command
		if (success && needInsertRow) {
			String ggbcmd1 = ggbcmd;
			ValidExpression outputVE = cellValue.getValue();
			String assignmentLabel = outputVE.getLabelForAssignment();
			String label = cellValue.getEvalVE().getLabelForAssignment();
			GeoCasCell newRowValue = new GeoCasCell(kernel.getConstruction());
			StringBuilder sb = new StringBuilder(label);
			boolean isDerivative = "Derivative".equals(ggbcmd);
			boolean isIntegral = !isDerivative && "Integral".equals(ggbcmd);
			if ((isDerivative || isIntegral)
					&& outputVE.unwrap() instanceof FunctionNVar) {
				if (isDerivative) {
					sb.append('\'');
				}
				sb.append('(')
						.append(((FunctionNVar) outputVE.unwrap())
								.getVarString(StringTemplate.defaultTemplate))
						.append(')');
				sb.append(outputVE.getAssignmentOperator());
				sb.append(ggbcmd).append('[').append(assignmentLabel)
						.append(']');
				ggbcmd1 = "Evaluate";
			}
			newRowValue.setInput(sb.toString());
			casView.insertRow(newRowValue, true);
			processCurrentRow(ggbcmd1, focus, oldXML.toString());
		}

	}

	// function to handle NSolve input for non-polynomial equations
	private String handleNSolve(GeoCasCell cellValue, String evalText) {
		boolean isEquList = false;
		StringBuilder sb = new StringBuilder();
		// sb.append("NSolve[");
		sb.append(cellValue.getLocalizedInput());

		ExpressionValue expandValidExp = null;
		// case input is a cell
		if (evalText.charAt(0) == (GeoCasCell.ROW_REFERENCE_DYNAMIC)) {
			int row = Integer.parseInt(evalText.substring(1, 2));
			GeoCasCell geoCasCell = consoleTable.getGeoCasCell(row - 1);
			expandValidExp = geoCasCell.getInputVE();
		} else {
			try {
				expandValidExp = (kernel.getGeoGebraCAS()).getCASparser()
						.parseGeoGebraCASInput(evalText, null)
						.traverse(FunctionExpander.newFunctionExpander());
			} catch (Exception e) {
				return null;
			}
		}

		GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
		try {
			String casResult = "";
			if (expandValidExp == null) {
				return sb.toString();
			}
			// use NSolve tool with list of equations
			else if (expandValidExp.unwrap() instanceof MyList) {
				isEquList = true;
				MyList equList = (MyList) expandValidExp.unwrap();
				// handle case list with two equations
				// TODO handle list with n equations
				casResult = cas.getCurrentCAS()
						.evaluateRaw(
								CustomFunctions.GGBIS_POLYNOMIAL + "("
										+ equList.get(0).toString(
												StringTemplate.giacTemplate)
										+ ") && "
										+ CustomFunctions.GGBIS_POLYNOMIAL + "("
										+ equList.get(1).toString(
												StringTemplate.giacTemplate)
										+ ")");
			}
			// use NSolve tool with one equation
			else {
				casResult = cas.getCurrentCAS()
						.evaluateRaw(CustomFunctions.GGBIS_POLYNOMIAL + "("
						+ expandValidExp.toString(StringTemplate.giacTemplate)
						+ ")");
			}

			// case it is not
			if ("0".equals(casResult) || "false".equals(casResult)) {
				ValidExpression ve = cellValue.getEvalVE();
				Set<GeoElement> vars = ve
						.getVariables(SymbolicMode.NONE);
				if (!vars.isEmpty()) {
					Iterator<GeoElement> it = vars.iterator();
					while (it.hasNext()) {
						GeoElement next = it.next();
						if (next instanceof GeoDummyVariable) {
							// for non-polynomial equation list
							// we have to add all vars
							if (isEquList) {
								sb.append(",{");
								Set<String> varsStrSet = getVariableStrSet(
										vars);
								if (!varsStrSet.isEmpty()) {
									Iterator<String> itStrSet = varsStrSet
											.iterator();
									while (itStrSet.hasNext()) {
										String nextStr = itStrSet.next();
										sb.append(nextStr);
										sb.append("=1");
										sb.append(",");
									}
									sb.setLength(sb.length() - 1);
									sb.append("}");
								}
								varsStrSet.clear();
							} else {
								// add var=1
								String var = next.toString(
										StringTemplate.defaultTemplate);
								sb.append(",");
								sb.append(var);
								sb.append("=1");
							}
							break;
						}
						if (next instanceof GeoCasCell) {
							String var = next
									.toString(StringTemplate.defaultTemplate);
							GeoElement geo = kernel.getConstruction()
									.lookupLabel(var);
							if (geo instanceof GeoFunction) {
								FunctionVariable[] varsOfFunc = ((GeoFunction) geo)
										.getFunction().getFunctionVariables();
								if (varsOfFunc.length > 0) {
									var = varsOfFunc[0].toString(
											StringTemplate.defaultTemplate);
								}
							} else {
								break;
							}
							sb.append(",");
							sb.append(var);
							sb.append("=1");
							break;
						}
					}
				}
				vars.clear();
			} else {
				if (sb.toString().contains("$")) {
					cellValue.setNSolveCmdNeeded(true);
				}
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.debug(e);
		}

		return sb.toString();
	}

	/**
	 * @return the set with geoDummy names
	 */
	private static Set<String> getVariableStrSet(Set<GeoElement> vars) {
		Set<String> varsStrSet = new HashSet<>();
		if (!vars.isEmpty()) {
			Iterator<GeoElement> it = vars.iterator();
			while (it.hasNext()) {
				GeoElement next = it.next();
				if (next instanceof GeoDummyVariable) {
					String var = next.toString(StringTemplate.defaultTemplate);
					if (!varsStrSet.contains(var)) {
						varsStrSet.add(var);
					}
				}
			}
		}
		return varsStrSet;
	}

	/**
	 * We want to ignore selected text if it's just ) or ] because of double
	 * click
	 * 
	 * @param text
	 *            selected text
	 * @return whether it is meaningful to consider this as a selection
	 */
	private static boolean meaningfulSelection(String text) {
		if (text == null) {
			return false;
		}
		String trimmed = text.trim();
		return !trimmed.isEmpty() && !(trimmed.length() == 1 && "]})".indexOf(trimmed) > -1);
	}

	/**
	 * Deletes current row, including all dependent objects
	 */
	public void deleteCurrentRow() {
		int[] selected = consoleTable.getSelectedRows();
		for (int current : selected) {
			GeoCasCell cell = consoleTable.getGeoCasCell(current);
			if (cell != null) {
				cell.remove();
				consoleTable.getApplication().storeUndoInfo();
			}
		}

	}

	/**
	 * Determines the selected rows and tries to solve (solve is the only
	 * implemented function up to now) the equations or lists of equations found
	 * in the selected rows. The result is written into the active cell.
	 * 
	 * @param ggbcmd
	 *            is the given command (just Solve is supported)
	 */
	private void processMultipleRows(String ggbcmd, String oldXML) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		// get current row and input text
		consoleTable.stopEditing();
		int selRow = consoleTable.getSelectedRow();
		if (selRow < 0) {
			selRow = consoleTable.getRowCount() - 1;
		}

		int currentRow = selRow;

		int[] selectedIndices = consoleTable.getSelectedRows();
		int nrEquations;

		// remove empty cells because empty cells' inputVE vars are null
		ArrayList<Integer> l = new ArrayList<>();
		for (int i = 0; i < selectedIndices.length; i++) {
			if (!casView.isRowEmpty(selectedIndices[i])) {
				l.add(selectedIndices[i]);
			}
		}
		selectedIndices = new int[l.size()];
		for (int i = 0; i < l.size(); i++) {
			selectedIndices[i] = l.get(i);
		}

		boolean oneRowOnly = false;
		if (selectedIndices.length == 1) {
			oneRowOnly = true;
			nrEquations = 1;
		} else {
			nrEquations = selectedIndices.length;
		}

		GeoCasCell cellValue;
		try {
			cellValue = consoleTable.getGeoCasCell(currentRow);
		} catch (ArrayIndexOutOfBoundsException e) {
			cellValue = null;
		}

		// insert new row if the row below the last selected row is not empty
		if (cellValue == null || (!cellValue.isEmpty() && !oneRowOnly)) {
			cellValue = new GeoCasCell(kernel.getConstruction());
			currentRow = consoleTable.getRowCount() - 1;
			casView.insertRow(cellValue, false);
		}

		ArrayList<GeoElement> vars = new ArrayList<>();
		boolean foundNonPolynomial = false;
		// generates an array of references (e.g. $1,a,...) and
		// an array of equations
		int counter = 0;
		String[] references = new String[nrEquations];
		for (int i = 0; i < selectedIndices.length; i++) {
			GeoCasCell selCellValue = consoleTable
					.getGeoCasCell(selectedIndices[i]);
			if ("NSolve".equals(ggbcmd) && selCellValue != null) {
				GeoGebraCAS cas = (GeoGebraCAS) kernel.getGeoGebraCAS();
				try {
					StringBuilder inputStr = new StringBuilder();
					// check if input is polynomial
					inputStr.append(CustomFunctions.GGBIS_POLYNOMIAL);
					inputStr.append("(");
					inputStr.append(selCellValue.getValue()
							.toString(StringTemplate.giacTemplate));
					inputStr.append(")");

					String casResult = cas.getCurrentCAS()
							.evaluateRaw(inputStr.toString());
					Set<GeoElement> cellVars = selCellValue.getInputVE()
							.getVariables(SymbolicMode.NONE);
					Iterator<GeoElement> it = cellVars.iterator();
					while (it.hasNext()) {
						GeoElement curr = it.next();
						// if input was geoCasCell
						if (curr instanceof GeoCasCell) {
							// we should use only the variables from output
							Set<GeoElement> currCellVars = ((GeoCasCell) curr)
									.getValue()
									.getVariables(SymbolicMode.NONE);
							Iterator<GeoElement> currIt = currCellVars
									.iterator();
							if (vars.isEmpty()) {
								vars.addAll(currCellVars);
							} else {
								while (currIt.hasNext()) {
									GeoElement currEl = currIt.next();
									int j;
									for (j = 0; j < vars.size(); j++) {
										if (currEl
												.toString(
														StringTemplate.defaultTemplate)
												.equals(vars.get(j).toString(
														StringTemplate.defaultTemplate))) {
											break;
										}
									}
									if (j == vars.size()) {
										vars.add(currEl);
									}
								}
							}
							continue;
						}
						if (vars.isEmpty()) {
							vars.add(curr);
						} else {
							int j;
							for (j = 0; j < vars.size(); j++) {
								if (curr.toString(
										StringTemplate.defaultTemplate)
										.equals(vars.get(j).toString(
												StringTemplate.defaultTemplate))) {
									break;
								}
							}
							if (j == vars.size()) {
								vars.add(curr);
							}
						}
					}
					// case it is not
					if ("false".equals(casResult) || "0".equals(casResult)) {
						foundNonPolynomial = true;
					}
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.debug(e);
				}
			}
			String cellText;
			String assignedVariable = selCellValue != null
					? selCellValue.getAssignmentVariable() : null;
			boolean inTheSelectedRow = currentRow == selectedIndices[i];
			if (assignedVariable != null) {
				references[i] = assignedVariable;
			} else {
				cellText = selCellValue.getInputVE().toString(tpl);
				cellText = resolveCASrowReferences(cellText, selectedIndices[i],
						GeoCasCell.ROW_REFERENCE_STATIC, false);
				if (!inTheSelectedRow) {
					references[i] = "$" + (selectedIndices[i] + 1);
				} else {
					assert false : "this should not be possible";
					references[counter] = cellText;
				}
			}
		}

		String evalText;

		StringBuilder cellText = new StringBuilder("{");
		for (int i = 0; i < nrEquations; i++) {
			if (i != 0) {
				cellText.append(", ");
			}
			cellText.append(references[i]);
		}
		cellText.append("}");
		if (!vars.isEmpty() && foundNonPolynomial) {
			cellText.append(",{");
			boolean first = true;
			for (int i = 0; i < vars.size(); i++) {
				if (!first) {
					cellText.append(",");
				}
				if (vars.get(i) instanceof GeoDummyVariable) {
					first = false;
					cellText.append(vars.get(i).toString(
							StringTemplate.defaultTemplate));
					cellText.append("=1");
				}
			}
			cellText.append("}");
		}

		// FIX common INPUT ERRORS in evalText
		if ("Evaluate".equals(ggbcmd) || "KeepInput".equals(ggbcmd)) {
			String fixedInput = casCellProcessor
					.fixInputErrors(cellText.toString());
			if (!fixedInput.equals(cellText.toString())) {
				evalText = fixedInput;
			}
		}

		cellValue.setInput(cellText.toString());

		// prepare evalText as ggbcmd[ evalText, parameters ... ]
		StringBuilder sb = new StringBuilder();
		sb.append(ggbcmd);
		sb.append("[");
		sb.append(cellText);
		sb.append("]");
		evalText = sb.toString();

		// remember evalText and selection for future calls of processRow()
		cellValue.setProcessingInformation("", evalText, "");

		// TODO: write some evaluation comment
		// cellValue.setEvalComment(paramString);

		// process given row and below, then start editing
		processRowThenEdit(currentRow, true, oldXML);
	}

	/**
	 * Processes given row.
	 * 
	 * @param selRow
	 *            row index
	 * @param startEditing
	 *            start editing
	 * @return success
	 */
	public boolean processRowThenEdit(int selRow, boolean startEditing, String oldXML) {
		GeoCasCell cellValue = consoleTable.getGeoCasCell(selRow);
		boolean success;
		boolean isLastRow = consoleTable.getRowCount() <= selRow + 1;
		if (!cellValue.isError() && !cellValue.isUseAsText()) {
			// evaluate output and update twin geo
			kernel.getAlgebraProcessor().processCasCell(cellValue, isLastRow, oldXML);
		} else if (cellValue.isIndependent() && !cellValue.isUseAsText()) {
			// make sure the cell is in construction list, so CAS cells have the
			// right index, see #3241
			kernel.getConstruction().addToConstructionList(cellValue, true);
		} else if (cellValue.isUseAsText()) {
			kernel.getConstruction().addToConstructionList(cellValue, true);
			kernel.notifyAdd(cellValue);
		}

		kernel.notifyRepaint();

		// if redefinition occurred, the row number could have changed
		// we need to update the variables
		int rowNum = cellValue.getRowNumber();
		isLastRow = consoleTable.getRowCount() <= rowNum + 1;

		// check success
		success = !cellValue.isError();
		if (startEditing || consoleTable.keepEditing(!success, rowNum)) {
			// start editing row below successful evaluation

			boolean goDown = success
			// we are in last row or next row is empty
					&& (isLastRow || casView.isRowOutputEmpty(rowNum + 1));
			consoleTable.startEditingRow(goDown ? rowNum + 1 : rowNum);
		}

		return success;
	}

	/**
	 * Replaces references to other rows (e.g. #, #3, $3, ##, #3#, $$, $3$) in
	 * the input string by the values from those rows. Warning: dynamic
	 * references (with $) are also replaced statically.
	 * 
	 * @param str
	 *            the input expression
	 * @param selectedRow
	 *            the row this expression is in
	 * @param delimiter
	 *            the delimiter to look for
	 * @param noParentheses
	 *            if true no parentheses will be added in every case<br>
	 *            if false parentheses will be added around replaced references
	 *            except the replacement is just a positive number, a variable
	 *            or the whole term (given by parameter str) was nothing but the
	 *            reference
	 * @return the string with resolved references.
	 * @author Johannes Renner
	 * @throws CASException
	 *             if the number of the row reference is invalid (the number is
	 *             higher than the current number of rows or the reference
	 *             number is the number of the current row) then an
	 *             {@link CASException} is thrown
	 */
	public String resolveCASrowReferences(String str, int selectedRow,
			char delimiter, boolean noParentheses) throws CASException {
		boolean newNoParentheses = noParentheses;

		StringBuilder sb = new StringBuilder();
		// switch (delimiter) {
		// case GeoCasCell.ROW_REFERENCE_DYNAMIC:
		// case GeoCasCell.ROW_REFERENCE_STATIC:
			// Log.debug(selectedRow + ": " + str);

			boolean foundReference = false;
			boolean addParentheses = false;
			boolean startOfReferenceNumber = false;
			boolean needOutput = true;

			// -1 means reference without a number (to the previous row)
			int referenceNumber = -1;

			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);

				if (foundReference) {
					if (StringUtil.isDigit(c)) {
						if (startOfReferenceNumber) {
							startOfReferenceNumber = false;
							referenceNumber = 0;
						}
						referenceNumber = referenceNumber * 10
								+ Character.digit(c, 10);
						continue;
					} else if (c == delimiter) {
						// ## or $$ or #n# or $n$
						needOutput = false;
						continue;
					}

					foundReference = false;
					// needed if the reference is the first term in the
					// expression, because in this case addParentheses isn't
					// true yet
					if (c != ')') {
						addParentheses = true;
						newNoParentheses = false;
					}

					handleReference(sb, selectedRow, referenceNumber,
							addParentheses, newNoParentheses, needOutput);
				}

				if (c != delimiter) {
					sb.append(c);
					addParentheses = true;
					// if a part of the expression was selected the given String
					// str has parentheses in the beginning and the end
					// --> if just the reference is between these parentheses no
					// more parenthesis should be added (--> leave
					// newNoParentheses true), otherwise newNoParentheses will
					// be set to false above in the for-loop
					if (i == 0 && c != '(' || i > 0 && c != ')') {
						newNoParentheses = false;
					}
				} else {
					foundReference = true;
					startOfReferenceNumber = true;
				}
			}

			if (foundReference) {
				handleReference(sb, selectedRow, referenceNumber,
						addParentheses, newNoParentheses, needOutput);
			}
		// break;
		// }
		return sb.toString();
	}

	private void handleReference(StringBuilder sb, int selectedRow,
			int referenceNumber, boolean addParentheses, boolean noParentheses,
			boolean needOutput) throws CASException {

		if (referenceNumber > 0 && referenceNumber != selectedRow + 1
				&& referenceNumber <= casView.getRowCount()) {
			String reference;
			if (needOutput) {
				// a # (or $) with a following number is in the the input (for
				// example #3)
				reference = casView.getRowOutputValue(referenceNumber - 1);
			} else {
				// a # (or $) with a following number and the same delimiter
				// again is in the the input (for example #3#)
				reference = casView.getRowInputValue(referenceNumber - 1);
			}

			appendReference(sb, reference, addParentheses, noParentheses);

		} else if (referenceNumber == -1 && selectedRow > 0) {
			String reference;
			if (needOutput) {
				// just a # (or $) is in the input (without a number)
				reference = casView.getRowOutputValue(selectedRow - 1);
			} else {
				// ## or $$
				reference = casView.getRowInputValue(selectedRow - 1);
			}

			appendReference(sb, reference, addParentheses, noParentheses);

		} else {
			CASException ex = new CASException("CAS.InvalidReferenceError");
			ex.setKey("CAS.InvalidReferenceError");
			throw ex;
		}
	}

	private void appendReference(StringBuilder sb, String reference,
			boolean addParentheses, boolean noParentheses) {
		boolean parentheses = addParentheses;
		// don't add parenthesis if the given expression is just a positive
		// number
		if (isPositiveNumber(reference)) {
			parentheses = false;
		}
		// or if the given reference is just one variable
		else {
			try {
				String parsed = kernel.getParser().parseLabel(reference);
				// since parseLabel parses only the first label we need to check
				// if the parsed String is the full reference
				if (parsed.equals(reference)) {
					parentheses = false;
				}
			} catch (ParseException e) {
				// do nothing because the reference isn't a label
			}
		}

		if (parentheses && !noParentheses) {
			sb.append("(").append(reference).append(")");
		} else {
			sb.append(reference);
		}
	}

	private static boolean isPositiveNumber(String s) {
		try {
			double d = Double.parseDouble(s);
			return d >= 0;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * @param cell
	 *            cell whose state should be displayed in the barble
	 * @param renderer
	 *            renderer of the cell
	 */
	public static void handleMarble(GeoCasCell cell, MarbleRenderer renderer) {
		boolean marbleShown = cell.hasTwinGeo()
				&& cell.getTwinGeo().isEuclidianVisible()
				&& cell.getTwinGeo().isEuclidianShowable();
		ValidExpression ve = cell.getValue();
		boolean isPlottable = true;
		int dim = cell.getKernel().getApplication().is3D() ? 3 : 2;
		if (ve != null) {
			if (ve.unwrap() instanceof MyList) {
				MyList ml = (MyList) ve.unwrap();
				int i = 0;
				while (i < ml.size() && isPlottable) {
					isPlottable &= !(ml.getItem(i)
							.unwrap() instanceof MySpecialDouble)
							&& !ml.getItem(i++).unwrap()
									.inspect(Inspecting.UnplottableChecker
											.getChecker(dim));
				}
			} else if (ve.unwrap() instanceof Command) {
				isPlottable &= ((Command) ve.unwrap()).getName().equals("If");
			} else {
				isPlottable = cell.getTwinGeo() != null && cell.getTwinGeo().isEuclidianShowable();
			}
		}
		if (ve != null
				&& !cell.getAssignmentType().equals(AssignmentType.DELAYED)) {
			if (cell.showOutput() && !cell.isError()
					&& (isPlottable || !ve.unwrap().inspect(
							Inspecting.UnplottableChecker.getChecker(dim)))) {
				renderer.setMarbleValue(marbleShown);
				renderer.setMarbleVisible(true);
			} else {
				renderer.setMarbleVisible(false);
			}
		} else {
			renderer.setMarbleVisible(false);
		}

	}

}
