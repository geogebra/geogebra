package geogebra.common.cas.view;

import geogebra.common.kernel.CASException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.main.App;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
/**
 * Handles CAS input 
 */
public class CASInputHandler {

	private CASView casView;
	private Kernel kernel;
	private CASTable consoleTable;
	/**
	 * @param view CAS view
	 */
	public CASInputHandler(CASView view) {
		this.casView = view;
		kernel = view.getApp().getKernel();
		consoleTable = view.getConsoleTable();
	}

	/**
	 * Process input of current row.
	 * 
	 * @param ggbcmd
	 *            command like "Factor" or "Integral"
	 * @param params
	 *            optional command parameters like "x"
	 */
	public void processCurrentRow(String ggbcmd, String[] params) {
		// get editor
		CASTableCellEditor cellEditor = consoleTable.getEditor();

		if ((ggbcmd.equalsIgnoreCase("Solve") || 
				ggbcmd.equalsIgnoreCase("NSolve"))
				&& (casView.getRowHeader().getSelectedIndices().length > 1)) {
			processMultipleRows(ggbcmd);
			return;
		}

		// get possibly selected text
		String selectedText = cellEditor.getInputSelectedText();
	
		int selStart = cellEditor.getInputSelectionStart();
		int selEnd = cellEditor.getInputSelectionEnd();
		String selRowInput = cellEditor.getInput();
		if(selRowInput!=null && selRowInput.startsWith("@")){ 
			try { 
			String s = kernel.getGeoGebraCAS().evaluateRaw(selRowInput.substring(1)); 
			kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling("casOutput=\""+s+"\"", false, false, false); 
			} catch (Throwable e) { 
			e.printStackTrace(); 
			} 
			return; 
		} 
		if (selRowInput == null || selRowInput.length() == 0) {
			if (consoleTable.getSelectedRow() != -1) {
				consoleTable.startEditingRow(consoleTable.getSelectedRow());
				GeoCasCell cellValue = consoleTable.getGeoCasCell(consoleTable
						.getSelectedRow());
				if (cellValue.getInputVE() != null)
					selRowInput = cellValue
							.toString(StringTemplate.numericDefault);
			}
			if (selRowInput.length() == 0)
				return;
		}

		// save the edited value into the table model
		consoleTable.stopEditing();

		// get current row and input text
		int selRow = consoleTable.getSelectedRow();
		if (selRow < 0)
			selRow = consoleTable.getRowCount() - 1;
		GeoCasCell cellValue = consoleTable.getGeoCasCell(selRow);

		// STANDARD CASE: GeoGebraCAS input
		// break text into prefix, evalText, postfix
		String prefix, evalText, postfix;
		boolean hasSelectedText = meaningfulSelection(selectedText);
		if (hasSelectedText) {
			// selected text: break it up into prefix, evalText, and postfix
			prefix = selRowInput.substring(0, selStart).trim();
			if (selStart > 0 || selEnd < selRowInput.length()) {
				// part of input is selected
				evalText = "(" + selectedText + ")";
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

			// FIX common INPUT ERRORS in evalText
			if (!hasSelectedText
					&& (ggbcmd.equals("Evaluate") || ggbcmd.equals("KeepInput"))) {
				String fixedInput = fixInputErrors(selRowInput);
				if (!fixedInput.equals(selRowInput)) {
					cellValue.setInput(fixedInput);
					evalText = fixedInput;
				}
			}

			// remember input selection information for future calls of
			// processRow()
			// check if structure of selection is ok
			boolean structureOK = cellValue
					.isStructurallyEqualToLocalizedInput(prefix + evalText
							+ postfix);
			if (!structureOK) {
				// show current selection again
				consoleTable.startEditingRow(selRow);
				cellEditor = consoleTable.getEditor();
				cellEditor.setInputSelectionStart(selStart);
				cellEditor.setInputSelectionEnd(selEnd);
				return;
			}

			boolean isAssignment = cellValue.getAssignmentVariable() != null;
			boolean isEvaluate = ggbcmd.equals("Evaluate");
			boolean isNumeric = ggbcmd.equals("Numeric");
			boolean isKeepInput = ggbcmd.equals("KeepInput");
			
			
			// Substitute dialog
			if (ggbcmd.equals("Substitute")) {
				//if cell has assignment and nothing other is selected -> use input without defnition
				//eg. a:=b+c
				//use only b+c
				if(isAssignment && !hasSelectedText){
					evalText = cellValue.getInputVE().toString(StringTemplate.defaultTemplate);
				}
				// show substitute dialog
				casView.showSubstituteDialog(prefix, evalText, postfix, selRow);
				return;
			}
			
			// assignments are processed immediately, the ggbcmd creates a new
			// row below
			if (isAssignment) {
				// tell row that KeepInput was used
				if (isKeepInput) {
					cellValue.setEvalCommand("KeepInput");
				}
				if(isNumeric && cellValue.getInputVE()!=null){
					cellValue.setInput(cellValue.getInputVE().getLabelForAssignment()+
							cellValue.getInputVE().getAssignmentOperator()+
							ggbcmd+"["+ cellValue.getInputVE().toString(StringTemplate.numericDefault)+"]"						
							);
					processRowThenEdit(selRow, true);
					return;
				}
				// evaluate assignment row
				boolean needInsertRow = !isEvaluate && !isKeepInput;
				boolean success = processRowThenEdit(selRow, !needInsertRow);

				// insert a new row below with the assignment label and process
				// it
				// using the current command
				if (success && needInsertRow) {
					String assignmentLabel = cellValue.getEvalVE()
							.getLabelForAssignment();
					GeoCasCell newRowValue = new GeoCasCell(
							kernel.getConstruction());
					newRowValue.setInput(assignmentLabel);
					consoleTable.insertRow(newRowValue, true);
					processCurrentRow(ggbcmd, params);
				}

				return;
			}



			// standard case: build eval command
			String paramString = null;

			// don't wrap Numeric[pi, 20] with a second Numeric command
			// as this would remove precision
			boolean wrapEvalText = !isEvaluate
					&& !(isNumeric && (evalText.startsWith("N[")
							|| evalText.startsWith("N(")
							|| evalText.startsWith("Numeric[") || evalText
								.startsWith("Numeric(")));

			if (wrapEvalText) {
				// prepare evalText as ggbcmd[ evalText, parameters ... ]
				StringBuilder sb = new StringBuilder();
				sb.append(ggbcmd);
				sb.append("[");
				sb.append(evalText);
				if (params != null && !StringUtil.representsMultipleExpressions(evalText)) {
					StringBuilder paramSB = new StringBuilder();
					for (int i = 0; i < params.length; i++) {
						paramSB.append(", ");
						paramSB.append(resolveButtonParameter(params[i],
								cellValue));
					}
					paramString = paramSB.substring(2);
					sb.append(paramSB);
				}
				sb.append("]");
				evalText = sb.toString();
			}

			// remember evalText and selection for future calls of processRow()
			cellValue.setProcessingInformation(prefix, evalText, postfix);
			cellValue.setEvalComment(paramString);

		} catch (CASException ex) {
			cellValue.setError(ex.getKey());
		}
		// process given row and below, then start editing
		processRowThenEdit(selRow, true);
	}

	/**
	 * We want to ignore selected text if it's just ) or ] because of double click
	 * @param text
	 * @return whether it is meaningful to consider this as a selection
	 */	
	private static boolean meaningfulSelection(String text) {
		 if(text==null)
			 return false;
		 String trimmed = text.trim();
		 if(trimmed.length()==0)
			 return false;
		 if(trimmed.length()==1 && "]})".indexOf(trimmed)>-1)
					return false;
		return true;
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
	 * @param params
	 *            the list of parameters
	 */
	private void processMultipleRows(String ggbcmd) {
		StringTemplate tpl = StringTemplate.defaultTemplate;
		// get current row and input text
		consoleTable.stopEditing();
		int selRow = consoleTable.getSelectedRow();
		if (selRow < 0)
			selRow = consoleTable.getRowCount() - 1;

		int currentRow = selRow;

		int[] selectedIndices = casView.getRowHeader().getSelectedIndices();
		int nrEquations;

		// remove empty cells because empty cells' inputVE vars are null
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < selectedIndices.length; i++) {
			if (!consoleTable.isRowEmpty(selectedIndices[i]))
				l.add(selectedIndices[i]);
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
		if (cellValue != null) {
			if (!cellValue.isEmpty() && !oneRowOnly) {
				cellValue = new GeoCasCell(kernel.getConstruction());
				currentRow = consoleTable.getRowCount() - 1;
				consoleTable.insertRow(cellValue, false);
			}
		} else {
			cellValue = new GeoCasCell(kernel.getConstruction());
			currentRow = consoleTable.getRowCount() - 1;
			consoleTable.insertRow(cellValue, false);
		}

		// generates an array of references (e.g. $1,a,...) and
		// an array of equations
		int counter = 0;
		String[] references = new String[nrEquations];
		for (int i = 0; i < selectedIndices.length; i++) {
			GeoCasCell selCellValue = consoleTable
					.getGeoCasCell(selectedIndices[i]);
			String cellText;
			String assignedVariable = selCellValue.getAssignmentVariable();
			boolean inTheSelectedRow = currentRow == selectedIndices[i];
			if (assignedVariable != null) {
				references[i] = assignedVariable;
			} else {
				cellText = selCellValue.getInputVE().toString(tpl);
				cellText = resolveCASrowReferences(cellText,
						selectedIndices[i], GeoCasCell.ROW_REFERENCE_STATIC,
						false);
				if (!inTheSelectedRow)
					references[i] = "$" + (selectedIndices[i] + 1);
				else {
					assert (false) : "this should not be possible";
					references[counter] = cellText;
				}
			}
		}

		String evalText;

		StringBuilder cellText = new StringBuilder("{");
		for (int i = 0; i < nrEquations; i++) {
			if (i != 0)
				cellText.append(", ");
			cellText.append(references[i]);
		}
		cellText.append("}");

		// FIX common INPUT ERRORS in evalText
		if ((ggbcmd.equals("Evaluate") || ggbcmd.equals("KeepInput"))) {
			String fixedInput = fixInputErrors(cellText.toString());
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
		processRowThenEdit(currentRow, true);
	}

	/**
	 * Replaces %0, %1, %2 etc. by input variables of cellValue. Note that x, y,
	 * z are used if possible.
	 * 
	 * @param param
	 * @param cellValue
	 * @return
	 */
	private static String resolveButtonParameter(String param,
			GeoCasCell cellValue) {
		if (param.charAt(0) == '%') {
			int n = Integer.parseInt(param.substring(1));

			// to make sure that for an input like x+y+z the
			// parameters are not resolved to %0=x %1=x %2=x

			// try x, y, z first
			String[] vars = { "x", "y", "z" };
			for (int i = 0; i < vars.length; i++) {
				if (cellValue.isFunctionVariable(vars[i])
						|| cellValue.isInputVariable(vars[i])) {
					if (0 == n)
						return vars[i];

					n--;
				}
			}

			// try function variable like m in f(m) := 2m + b
			String resolvedParam = cellValue.getFunctionVariable();
			if (resolvedParam != null)
				return resolvedParam;

			// try input variables like a in c := a + b
			resolvedParam = cellValue.getInVar(n);
			if (resolvedParam != null)
				return resolvedParam;

			return "x";
		}

		// standard case
		return param;
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
	public boolean processRowThenEdit(int selRow, boolean startEditing) {
		GeoCasCell cellValue = consoleTable.getGeoCasCell(selRow);
		boolean success;
		boolean isLastRow = consoleTable.getRowCount() <= selRow + 1;
		if (!cellValue.isError()) {
			// evaluate output and update twin geo
			kernel.getAlgebraProcessor().processCasCell(cellValue,isLastRow);
		}

		kernel.notifyRepaint();

		// check success
		success = !cellValue.isError();
		if (startEditing || !success) {
			// start editing row below successful evaluation
			
			boolean goDown = success &&
			// we are in last row or next row is empty
					(isLastRow || consoleTable.isRowEmpty(selRow + 1));
			consoleTable.startEditingRow(goDown ? selRow + 1 : selRow);
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
	 *            if true no parentheses will be added in every case<br/>
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
		switch (delimiter) {
		case GeoCasCell.ROW_REFERENCE_DYNAMIC:
		case GeoCasCell.ROW_REFERENCE_STATIC:
			App.debug(selectedRow + ": " + str);

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
					// expression, because in this case addParantheses isn't
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
			break;
		}
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
		boolean parantheses = addParentheses;
		// don't add parenthesis if the given expression is just a positive
		// number
		if (isPositiveNumber(reference)) {
			parantheses = false;
		}
		// or if the given reference is just one variable
		else {
			try {
				String parsed = kernel.getParser().parseLabel(reference);
				// since parseLabel parses only the first label we need to check
				// if the parsed String is the full reference
				if (parsed.equals(reference)) {
					parantheses = false;
				}
			} catch (ParseException e) {
				// do nothing because the reference isn't a label
			}
		}

		if (parantheses && !noParentheses) {
			sb.append("(" + reference + ")");
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
	 * Fixes common input errors and returns the corrected input String.
	 * 
	 * @param input
	 * @return
	 */
	private String fixInputErrors(String input) {
		String inputTrim = input.trim();

		// replace a := with Delete[a]
		if (inputTrim.endsWith(":=")) {
			inputTrim = casView.getApp().getCommand("Delete") + "["
					+ inputTrim.substring(0, inputTrim.length() - 2).trim()
					+ "];";
		}

		// remove trailing =
		else if (inputTrim.endsWith("=")) {
			inputTrim = inputTrim.substring(0, inputTrim.length() - 1);
		}

		return inputTrim;
	}

}
