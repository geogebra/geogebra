/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.AnalyticsErrorLogger;
import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

/**
 * Error handler for AV input
 *
 */
public final class AVErrorHandler implements ErrorLogger, AnalyticsErrorLogger {

	private final RadioTreeItem radioTreeItem;
	private final boolean valid;
	private final boolean allowSliders;
	private final boolean withSliders;

	/**
	 * @param radioTreeItem
	 *            parent item
	 * @param valid
	 *            previous input valid
	 * @param allowSliders
	 *            whether to allow sliders at all
	 * @param withSliders
	 *            whether to allow slider creation without asking
	 */
	public AVErrorHandler(RadioTreeItem radioTreeItem, boolean valid, boolean allowSliders,
			boolean withSliders) {
		this.radioTreeItem = radioTreeItem;
		this.valid = valid;
		this.allowSliders = allowSliders;
		this.withSliders = withSliders;
	}

	@Override
	public void showError(String msg) {
		radioTreeItem.errorMessage = valid ? msg
				: radioTreeItem.loc.getInvalidInputError();

		radioTreeItem.commandError = null;
		radioTreeItem.showCurrentError();
		radioTreeItem.saveError();
	}

	@Override
	public void resetError() {
		radioTreeItem.hideCurrentError();
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		if (withSliders) {
			return true;
		}

		if (!(allowSliders && valid) && this.radioTreeItem.app.getLocalization()
				.getReverseCommand(getCurrentCommand()) != null) {
			showCommandError(this.radioTreeItem.app.getLocalization()
					.getReverseCommand(getCurrentCommand()), null);

			return false;
		}
		callback.callback(new String[] { "7" });
		return false;
	}

	@Override
	public void showCommandError(final String command,
			final String message) {
		this.radioTreeItem.commandError = command;
		this.radioTreeItem.errorMessage = message;
		this.radioTreeItem.showCurrentError();
		this.radioTreeItem.saveError();
	}

	@Override
	public String getCurrentCommand() {
		return this.radioTreeItem.getCommand();
	}

	@Override
	public void log(Throwable e) {
		if (e instanceof MyError || e instanceof ParseException) {
			Log.warn(e.getLocalizedMessage()) ;
		} else {
			Log.debug(e);
		}
	}
}