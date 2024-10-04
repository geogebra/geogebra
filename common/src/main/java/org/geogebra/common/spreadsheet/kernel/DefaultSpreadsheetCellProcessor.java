package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.Unicode.ASSIGN_STRING;
import static org.geogebra.common.util.StringUtil.isNumber;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.TokenMgrException;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

/**
 * Sends spreadsheet cell editor input towards the AlgebraProcessor.
 * (This class is an adapter between the Spreadsheet core and the Kernel.)
 */
public class DefaultSpreadsheetCellProcessor implements SpreadsheetCellProcessor {

	private final AlgebraProcessor algebraProcessor;
	private final ErrorHandler errorHandler;
	private String cellName;
	private String input;

	/**
	 * Constructor.
	 * @param algebraProcessor {@link AlgebraProcessor}
	 */
	public DefaultSpreadsheetCellProcessor(@Nonnull AlgebraProcessor algebraProcessor) {
		this.algebraProcessor = algebraProcessor;
		this.errorHandler = new SpreadsheetErrorHandler(this);
	}

	/**
	 * Depending on input, processor makes text or evaluates input.
	 *
	 * @param input The input to process.
	 * @param row Identifies the cell to receive the input.
	 * @param column Identifies the cell to receive the input.
	 */
	@Override
	public void process(String input, int row, int column) {
		String cellName = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
		algebraProcessor.getKernel().getApplication().getAsyncManager()
				.scheduleCallback(() -> process(input, cellName));
	}

	/**
	 * Same as {@link #process(String, int, int)}, only with a cell name formed from a row/column
	 * pair.
	 * @param input The input to process.
	 * @param cellName Identifies the cell to receive the input.
	 */
	protected void process(String input, String cellName) {
		this.cellName = cellName;
		this.input = input;
		Kernel kernel = algebraProcessor.getKernel();
		if (checkCircularDefinition(input, kernel)) {
			markError();
			kernel.getApplication().storeUndoInfo();
			return;
		}
		try {
			processInput(buildProperInput(input, cellName), errorHandler,
					(geos) -> {
						if (geos != null && geos.length > 0) {
							((GeoElement) geos[0]).setEmptySpreadsheetCell(false);
						}

						kernel.getApplication().storeUndoInfo();
					});
		} catch (Exception e) {
			Log.debug("error " + e.getLocalizedMessage());
		}
	}

	private boolean checkCircularDefinition(String input, Kernel kernel) {
		try {
			ValidExpression parsed = kernel.getParser().parseGeoGebraExpression(input);
			if (parsed.inspect(v -> v instanceof Variable
					&& cellName.equals(((Variable) v).getName()))) {
				return true;
			}
		} catch (ParseException | TokenMgrException e) {
			// continue
		}
		return false;
	}

	private String buildProperInput(String input, String cellName) {
		StringBuilder sb = new StringBuilder();

		appendCellAssign(cellName, sb);

		if (isNumber(input) && input.lastIndexOf('-') < 1) {
			sb.append(input);
		} else if (isCommand(input)) {
			appendAsCommand(input, sb);
		} else {
			appendAsText(input, sb);
		}

		return sb.toString();
	}

	private static void appendCellAssign(String cellName, StringBuilder sb) {
		sb.append(cellName);
		sb.append(ASSIGN_STRING);
	}

	private static void appendAsCommand(String input, StringBuilder sb) {
		sb.append(input.substring(1));
	}

	private static void appendAsText(String input, StringBuilder sb) {
		sb.append("\"");
		sb.append(input.replaceAll("\"", ""));
		sb.append("\"");
	}

	private void processInput(String command, ErrorHandler handler, AsyncOperation<GeoElementND[]>
			callback) {
		algebraProcessor.processAlgebraCommandNoExceptionHandling(command, false,
				handler, false, callback);
	}

	private static boolean isCommand(String input) {
		return input.startsWith("=");
	}

	private String buildRestoredInput() {
		StringBuilder stringBuilder = new StringBuilder();
		appendCellAssign(cellName, stringBuilder);

		stringBuilder.append("ParseToNumber[\"");
		stringBuilder.append(input.startsWith("=") ? input.substring(1) : input);
		stringBuilder.append("\"]");

		return stringBuilder.toString();
	}

	@Override
	public void markError() {
		setOldInputUndefined();
		buildNewInputWithErrorMark();
	}

	private void setOldInputUndefined() {
		GeoElement geo = algebraProcessor.getKernel().lookupLabel(cellName);
		if (geo != null) {
			geo.setUndefined();
		}
	}

	private void buildNewInputWithErrorMark() {
		processInput(buildRestoredInput(), ErrorHelper.silent(), null);
	}
}
