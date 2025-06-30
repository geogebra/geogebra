package org.geogebra.common.main.syntax;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Class representing the syntax of commands.
 */
public final class Syntax {
	private final Commands command;
	private final List<ArgumentMatcher> argumentMatchers;

	/**
	 * Predicate interface for matching arguments of commands with syntaxes.
	 */
	public interface ArgumentMatcher {
		/**
		 * Checks whether the provided command argument matches with the syntax argument.
		 * @param argument the command argument to check
		 * @return {@code true} if the argument matches with the syntax, {@code false} otherwise
		 */
		boolean matches(GeoElement argument);

		/**
		 * Constructs a syntax argument matcher for checking
		 * whether the argument is a number.
		 * @return an {@code ArgumentMatcher} for checking number arguments
		 */
		static ArgumentMatcher isNumber() {
			return argument -> argument.isNumberValue() && !argument.isGeoBoolean();
		}
	}

	private Syntax(@Nonnull Commands command, @Nonnull List<ArgumentMatcher> argumentMatchers) {
		this.command = command;
		this.argumentMatchers = argumentMatchers;
	}

	private ArgumentMatcher argumentMatcherAt(int index) {
		return argumentMatchers.get(index);
	}

	/**
	 * @param command a command
	 * @param argumentMatchers list of argument matchers
	 * @return {@code Syntax} with the given command and argument matchers
	 */
	public static Syntax of(@Nonnull Commands command,
			@Nonnull ArgumentMatcher... argumentMatchers) {
		return new Syntax(command, List.of(argumentMatchers));
	}

	/**
	 * Checks whether the given {@link Command} is allowed based
	 * on a map of commands and a set of their allowed syntaxes.
	 * <p>
	 * <ul>
	 *     <li>
	 *         If the given command is not in the map, then it is ignored.
	 *     </li>
	 *     <li>
	 *         If the given command is part of the map, but there are no matching syntaxes
	 *         with the same number of arguments, then an argument number exception is thrown
	 *         with the wrong argument number.
	 *     </li>
	 *     <li>
	 *         If the given command is part of the map and there is at least one syntax with the
	 *         same number of arguments, but at least one of these arguments doesn't match,
	 *         then an illegal argument exception is thrown with the first mismatching argument.
	 *     </li>
	 * </ul>
	 * @param allowedSyntaxesForRestrictedCommands map containing pairs of commands
	 * and their allowed syntaxes
	 * @param command the command to check
	 * @param commandProcessor the command processor for the given command
	 * @throws MyError whenever there are wrong number of arguments (with error type
	 * {@link org.geogebra.common.main.MyError.Errors#IllegalArgumentNumber}) or at least one wrong
	 * argument (with error type {@link org.geogebra.common.main.MyError.Errors#IllegalArgument}
	 */
	public static void checkRestrictedSyntaxes(
			@Nonnull Map<Commands, Set<Syntax>> allowedSyntaxesForRestrictedCommands,
			@Nonnull Command command, @Nonnull CommandProcessor commandProcessor) throws MyError {
		Commands currentCommand = Commands.stringToCommand(command.getName());

		// If the command is not restricted, we return
		if (!allowedSyntaxesForRestrictedCommands.containsKey(currentCommand)) {
			return;
		}
		Set<Syntax> allowedSyntaxes =
				allowedSyntaxesForRestrictedCommands.get(currentCommand);
		GeoElement[] currentArguments = commandProcessor.resArgs(command);

		// If the command is restricted but this syntax is allowed, we return
		if (allowedSyntaxes.stream().anyMatch(syntax ->
				matches(syntax, currentCommand, currentArguments))) {
			return;
		}
		Set<Syntax> syntaxesWithSameNumberOfArguments = allowedSyntaxes.stream()
				.filter(syntax -> syntax.argumentMatchers.size() == currentArguments.length)
				.collect(Collectors.toSet());

		// If none of the allowed syntaxes have the same number of arguments,
		// then throw argument number exception
		if (syntaxesWithSameNumberOfArguments.isEmpty()) {
			throw commandProcessor.argNumErr(command, currentArguments.length);
		}

		// Find the first mismatching argument of the closest allowed syntax
		// with same number of arguments and throw an argument exception using that
		Syntax closestSyntax = findMostSimilarSyntax(
				currentArguments, syntaxesWithSameNumberOfArguments);
		int firstMismatchingArgumentIndex = findFirstMismatchingArgumentIndex(
				currentArguments, closestSyntax);
		throw commandProcessor.argErr(command, command.getArgument(firstMismatchingArgumentIndex));
	}

	private static Syntax findMostSimilarSyntax(
			GeoElement[] arguments, Collection<Syntax> syntaxes) {
		Syntax bestCandidate = syntaxes.stream().findFirst().get();
		int highestNumberOfMatchingArguments = 0;
		for (Syntax syntax : syntaxes) {
			int numberOfMatchingArguments = 0;
			for (int argIndex = 0; argIndex < syntax.argumentMatchers.size(); argIndex++) {
				if (syntax.argumentMatcherAt(argIndex).matches(arguments[argIndex])) {
					numberOfMatchingArguments++;
				} else {
					break;
				}
			}
			if (numberOfMatchingArguments > highestNumberOfMatchingArguments) {
				bestCandidate = syntax;
				highestNumberOfMatchingArguments = numberOfMatchingArguments;
			}
		}

		return bestCandidate;
	}

	private static int findFirstMismatchingArgumentIndex(GeoElement[] arguments, Syntax syntax) {
		for (int argIndex = 0; argIndex < arguments.length; argIndex++) {
			if (!syntax.argumentMatcherAt(argIndex).matches(arguments[argIndex])) {
				return argIndex;
			}
		}
		return -1; // Should never reach
	}

	private static boolean matches(Syntax syntax, Commands command, GeoElement[] arguments) {
		if (syntax.command != command) {
			return false;
		}
		if (syntax.argumentMatchers.size() != arguments.length) {
			return false;
		}
		for (int argIndex = 0; argIndex < arguments.length; argIndex++) {
			if (!syntax.argumentMatcherAt(argIndex).matches(arguments[argIndex])) {
				return false;
			}
		}
		return true;
	}
}
