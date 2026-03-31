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

package org.geogebra.common.properties.impl;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.dialog.handler.RedefineInputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;

/**
 * Helper for redefining a {@link GeoElement} that was created by a command,
 * by substituting one of the command's parameters with a new value.
 */
public final class CommandRedefineHelper {

	private CommandRedefineHelper() {
	}

	/**
	 * Redefines {@code geoElement} by rebuilding its parent command string with one parameter replaced
	 * (or appended, if {@code paramIndex} is beyond the current input count).
	 * @param geoElement the element to redefine
	 * @param algoElement the parent algorithm
	 * @param paramIndex zero-based index of the parameter to replace/append
	 * @param newParameterDefinition string representation of the new parameter value
	 * @param app the application
	 */
	public static void redefineWithParam(
			@Nonnull GeoElement geoElement, @Nonnull AlgoElement algoElement, int paramIndex,
			@Nonnull String newParameterDefinition, @Nonnull App app) {
		String commandName = algoElement.getClassName().getCommand();
		GeoElement[] parameterElements = algoElement.getInput();
		int totalNumberOfParameters = Math.max(parameterElements.length, paramIndex + 1);
		String[] parameterDefinitions = new String[totalNumberOfParameters];
		for (int i = 0; i < totalNumberOfParameters; i++) {
			if (i == paramIndex) {
				parameterDefinitions[i] = newParameterDefinition;
			} else {
				parameterDefinitions[i] = getInputString(parameterElements[i]);
			}
		}
		String newDefinition = commandName + "(" + String.join(", ", parameterDefinitions) + ")";
		RedefineInputHandler handler = new RedefineInputHandler(app, geoElement,
				geoElement.getRedefineString(false, true));
		handler.processInput(newDefinition, ErrorHelper.silent(), ok -> {
			if (ok && geoElement != handler.getGeoElement()) {
				app.getSelectionManager().clearSelectedGeos(false, false);
				app.getSelectionManager().addSelectedGeo(handler.getGeoElement());
			}
		});
	}

	/**
	 * Returns the best string representation of a command input for command reconstruction.
	 * @param input command input
	 * @return command-compatible string representation
	 */
	public static String getInputString(GeoElement input) {
		if (input.getLabelSimple() != null) {
			return input.getLabelSimple();
		}
		String definition = input.getDefinition(StringTemplate.defaultTemplate);
		if (definition != null && !definition.isBlank()) {
			return definition;
		}
		return input.toValueString(StringTemplate.defaultTemplate);
	}
}
