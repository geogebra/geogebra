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

package org.geogebra.common.main.localization;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;

/**
 * Creates user facing messages, when a command is invalid.
 */
public class CommandErrorMessageBuilder implements ExamRestrictable {

	private Localization localization;

	private boolean showingSyntax;

	private StringBuilder builder;

	/**
	 * Create new CommandErrorMessageBuilder
	 * @param localization localization
	 */
	public CommandErrorMessageBuilder(Localization localization) {
		this.localization = localization;
		this.showingSyntax = true;
	}

	/**
	 * Set to true to show the syntax in the error message.
	 * @param showingSyntax true to show syntax
	 */
	public void setShowingSyntax(boolean showingSyntax) {
		this.showingSyntax = showingSyntax;
	}

	/**
	 * Build a command error message with wrong argument number
	 * @param command command
	 * @param argNumber number of wrong argument
	 * @return error message
	 */
	public String buildArgumentNumberError(String command, int argNumber) {
		resetStringBuilder();
		buildPrefix(command);

		if (argNumber > -1) {
			builder.append(":\n");
			builder.append(Errors.IllegalArgumentNumber.getError(localization));
			builder.append(": ");
			builder.append(argNumber);
		}

		maybeAddSyntax(command);

		return builder.toString();
	}

	/**
	 * Build a command error message with wrong argument
	 * @param command command
	 * @param arg wrong argument
	 * @return error message
	 */
	public String buildArgumentError(String command, ExpressionValue arg) {
		resetStringBuilder();

		buildPrefix(command);

		builder.append(":\n");
		builder.append(Errors.IllegalArgument.getError(localization));
		builder.append(": ");
		if (arg instanceof GeoElement) {
			builder.append(((GeoElement) arg).getNameDescription());
		} else if (arg != null) {
			builder.append(arg.toString(StringTemplate.defaultTemplate));
		}

		maybeAddSyntax(command);
		return builder.toString();
	}

	private void buildPrefix(String command) {
		boolean reverseOrder = localization.isReverseNameDescriptionLanguage();
		String commandLocalized = localization.getCommand("Command");
		String commandNameLocalized = localization.getCommand(command);

		if (!reverseOrder) {
			// standard order: "Command ..."
			builder.append(commandLocalized);
			builder.append(' ');
			builder.append(commandNameLocalized);
		} else {
			// reverse order: "... command"
			builder.append(commandNameLocalized);
			builder.append(' ');
			builder.append(commandLocalized.toLowerCase(Locale.ROOT));
		}
	}

	private void maybeAddSyntax(String command) {
		if (showingSyntax) {
			builder.append("\n\n");
			builder.append(localization.getMenu("Syntax"));
			builder.append(":\n");
			builder.append(localization.getCommandSyntax(command));
		}
	}

	private void resetStringBuilder() {
		if (builder == null) {
			builder = new StringBuilder();
		} else {
			builder.setLength(0);
		}
	}

	// ExamRestrictable

	@Override
	public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		setShowingSyntax(false);
	}

	@Override
	public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType) {
		setShowingSyntax(true);
	}
}
