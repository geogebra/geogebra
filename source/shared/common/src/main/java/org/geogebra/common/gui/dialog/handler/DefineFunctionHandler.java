package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Class to handle functions change in Scientific Table View
 */
public class DefineFunctionHandler implements ErrorHandler {
	private final Kernel kernel;
	private boolean errorOccurred;

	/**
	 * @param kernel {@link Kernel}
	 */
	public DefineFunctionHandler(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Redefine the geo according to the text input.
	 * @param text input to process.
	 * @param geo to redefine.
	 */
	public void handle(String text, GeoEvaluatable geo) {
		errorOccurred = false;
		String input = nameWithVariable(geo) + (text.isEmpty() ? "?" : text);
		if (geo instanceof GeoFunction) {
			EvalInfo info = new EvalInfo(!kernel.getConstruction()
					.isSuppressLabelsActive(), false, false)
					.withForceFunctionsEnabled(true);
			kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo,
						input, info, false, null, this);
		}
	}

	private String nameWithVariable(GeoEvaluatable geo) {
		return geo.getLabel(StringTemplate.defaultTemplate) + "("
				+ ((VarString) geo).getVarString(StringTemplate.defaultTemplate) + ")=";
	}

	@Override
	public void showError(String msg) {
		errorOccurred = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccurred = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccurred = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccurred = false;
	}

	/**
	 * @return whether errors occurred.
	 */
	public boolean hasErrorOccurred() {
		return errorOccurred;
	}
}
