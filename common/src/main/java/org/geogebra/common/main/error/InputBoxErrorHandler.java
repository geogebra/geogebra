package org.geogebra.common.main.error;

import org.geogebra.common.kernel.geos.InputBoxProcessor;
import org.geogebra.common.util.AsyncOperation;

public class InputBoxErrorHandler implements ErrorHandler{

    private InputBoxProcessor inputBoxProcessor;
    private ErrorHandler handler;

    public InputBoxErrorHandler(InputBoxProcessor inputBoxProcessor, ErrorHandler handler) {
        this.inputBoxProcessor = inputBoxProcessor;
        this.handler = handler;
    }

    @Override
    public void showError(String msg) {
        handler.showError(msg);
        inputBoxProcessor.updateTempUserInput();
    }

    @Override
    public void showCommandError(String command, String message) {
        handler.showCommandError(command, message);
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
