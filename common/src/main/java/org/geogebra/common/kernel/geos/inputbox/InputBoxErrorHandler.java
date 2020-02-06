package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

class InputBoxErrorHandler implements ErrorHandler {

	private GeoInputBox inputBox;
	private ErrorHandler handler;

	private String tempUserDisplayInput;
	private String tempUserEvalInput;

	InputBoxErrorHandler(GeoInputBox inputBox, ErrorHandler handler,
						 String tempUserDisplayInput, String tempUserEvalInput) {
		this.inputBox = inputBox;
		this.handler = handler;
		this.tempUserDisplayInput = tempUserDisplayInput;
		this.tempUserEvalInput = tempUserEvalInput;
	}

	@Override
	public void showError(String msg) {
		handler.showError(msg);
		handleError();
	}

	@Override
	public void showCommandError(String command, String message) {
		handler.showCommandError(command, message);
		handleError();
	}

	void handleError() {
		setTempUserInput();
		setLinkedGeoUndefined();
	}

	private void setTempUserInput() {
		inputBox.setTempUserDisplayInput(tempUserDisplayInput);
		inputBox.setTempUserEvalInput(tempUserEvalInput);
	}

	private void setLinkedGeoUndefined() {
		GeoElementND geoElement = inputBox.getLinkedGeo();
		geoElement.setUndefined();
		geoElement.updateRepaint();
	}

	@Override
	public String getCurrentCommand() {
		return handler.getCurrentCommand();
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		return handler.onUndefinedVariables(string, callback);
	}

	@Override
	public void resetError() {
		handler.resetError();
	}
}
