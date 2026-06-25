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

package org.geogebra.common.util;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.editor.CommandSyntaxLookup;
import org.geogebra.editor.share.util.CommandParser;
import org.geogebra.editor.share.util.IntegralHelper;
import org.geogebra.editor.share.util.IntegralHelper.IntegralForm;

/** App-backed command syntax lookup implementation. */
public final class CommandSyntaxLookupImpl implements CommandSyntaxLookup {
	private final App app;
	private final AlgebraProcessor algebraProcessor;
	private final Localization localization;
	private final boolean isCasSyntax;

	/**
	 * Constructs the command syntax lookup.
	 * @param app the active app
	 */
	public CommandSyntaxLookupImpl(App app) {
		this.app = app;
		this.algebraProcessor = app.getKernel().getAlgebraProcessor();
		this.localization = app.getLocalization();
		this.isCasSyntax = GeoGebraConstants.CAS_APPCODE.equals(app.getConfig().getAppCode())
				|| GeoGebraConstants.CAS_APPCODE.equals(app.getConfig().getSubAppCode());
	}

	@Override
	public @CheckForNull String getInternalCommand(@Nonnull String commandName) {
		String internalCommand = app.getInternalCommand(commandName);
		return internalCommand == null ? app.getReverseCommand(commandName) : internalCommand;
	}

	@Override
	public @CheckForNull IntegralForm getIntegralForm(@Nonnull Tag tag, @Nonnull String syntax) {
		int syntaxIndex = getCommandSyntaxIndex(syntax);
		if (syntaxIndex == -1) {
			return null;
		}
		return IntegralHelper.getIntegralForm(tag, syntaxIndex, isCasSyntax);
	}

	private int getCommandSyntaxIndex(@Nonnull String syntax) {
		String commandName = CommandParser.parseCommand(syntax).get(0);
		String internalCommand = getInternalCommand(commandName);
		if (internalCommand == null) {
			return -1;
		}
		String syntaxes = isCasSyntax ? localization.getCommandSyntaxCAS(internalCommand)
				: algebraProcessor.getSyntax(localization.getCommandSyntax(), internalCommand,
				app.getSettings());
		String[] syntaxLines = syntaxes == null ? new String[0] : syntaxes.split("\\n");
		for (int syntaxIndex = 0; syntaxIndex < syntaxLines.length; syntaxIndex++) {
			if (CommandParser.parseCommand(syntax)
					.equals(CommandParser.parseCommand(syntaxLines[syntaxIndex]))) {
				return syntaxIndex;
			}
		}
		return -1;
	}
}
