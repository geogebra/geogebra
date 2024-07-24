package org.geogebra.common.main.localization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.main.Localization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class CommandErrorMessageBuilderTest extends BaseUnitTest {

	private CommandErrorMessageBuilder builder;

	@Before
	public void setupTest() {
		Localization localization = Mockito.mock(Localization.class);
		Answer<String> keyAnswer = invocation -> invocation.getArgument(0);
		Mockito.when(localization.getCommand(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getMenu(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getError(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getCommandSyntax(Mockito.anyString())).then(
				invocation -> invocation.getArgument(0) + ".Syntax");

		builder = new CommandErrorMessageBuilder(localization);
	}

	@Test
	public void testBuildArgumentNumberError() {
		builder.setShowingSyntax(true);
		String message = builder.buildArgumentNumberError("Cmd", 1);
		assertThat("It contains the command syntax", message, containsString("Cmd.Syntax"));
		assertThat("It contains the illegal argument number",
				message, containsString(Integer.toString(1)));
		assertThat("It contains the command name",
				message, containsString("Command Cmd"));

		builder.setShowingSyntax(false);
		message = builder.buildArgumentNumberError("Cmd", 1);
		assertThat("It does not contain the syntax", message, not(containsString("Cmd.Syntax")));
		assertThat("It contains the illegal argument number",
				message, containsString(Integer.toString(1)));
		assertThat("It contains the command name",
				message, containsString("Command Cmd"));
	}

	@Test
	public void testBuildArgumentError() {
		final String expressionValue = "expression";
		ExpressionValue value = Mockito.mock(ExpressionValue.class);
		Mockito.when(value.toString(Mockito.any(StringTemplate.class))).then(
				invocation -> expressionValue);

		builder.setShowingSyntax(true);
		String message = builder.buildArgumentError("Cmd", value);
		assertThat("It contains the command syntax", message, containsString("Cmd.Syntax"));
		assertThat("It contains the expression value",
				message, containsString(expressionValue));
		assertThat("It contains the command name",
				message, containsString("Command Cmd"));

		builder.setShowingSyntax(false);
		message = builder.buildArgumentError("Cmd", value);
		assertThat("It does not contain the syntax", message,
				not(containsString("Cmd.Syntax")));
		assertThat("It contains the expression value",
				message, containsString(expressionValue));
		assertThat("It contains the command name",
				message, containsString("Command Cmd"));
	}
}
