package org.geogebra.common.main.localization;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.main.Localization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CommandErrorMessageBuilderTest extends BaseUnitTest {

	private CommandErrorMessageBuilder builder;

	@Before
	public void setupTest() {
		Localization localization = Mockito.mock(Localization.class);
		Answer<String> keyAnswer = new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) {
				return invocation.getArgument(0);
			}
		};
		Mockito.when(localization.getCommand(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getMenu(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getError(Mockito.anyString())).then(keyAnswer);
		Mockito.when(localization.getCommandSyntax(Mockito.anyString())).then(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) {
				return invocation.getArgument(0) + ".Syntax";
			}
		});

		builder = new CommandErrorMessageBuilder(localization);
	}

	@Test
	public void testBuildArgumentNumberError() {
		builder.setShowingSyntax(true);
		String message = builder.buildArgumentNumberError("Cmd", 1);
		Assert.assertTrue("It contains the command syntax", message.contains("Cmd.Syntax"));
		Assert.assertTrue("It contains the illegal argument number",
				message.contains(Integer.toString(1)));
		Assert.assertTrue("It contains the command name",
				message.contains("Command Cmd"));

		builder.setShowingSyntax(false);
		message = builder.buildArgumentNumberError("Cmd", 1);
		Assert.assertFalse("It does not contain the syntax", message.contains("Cmd.Syntax"));
		Assert.assertTrue("It contains the illegal argument number",
				message.contains(Integer.toString(1)));
		Assert.assertTrue("It contains the command name",
				message.contains("Command Cmd"));
	}

	@Test
	public void testBuildArgumentError() {
		final String expressionValue = "expression";
		ExpressionValue value = Mockito.mock(ExpressionValue.class);
		Mockito.when(value.toString(Mockito.any(StringTemplate.class))).then(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) {
				return expressionValue;
			}
		});

		builder.setShowingSyntax(true);
		String message = builder.buildArgumentError("Cmd", value);
		Assert.assertTrue("It contains the command syntax", message.contains("Cmd.Syntax"));
		Assert.assertTrue("It contains the expression value",
				message.contains(expressionValue));
		Assert.assertTrue("It contains the command name",
				message.contains("Command Cmd"));

		builder.setShowingSyntax(false);
		message = builder.buildArgumentError("Cmd", value);
		Assert.assertFalse("It does not contain the syntax", message.contains("Cmd.Syntax"));
		Assert.assertTrue("It contains the expression value",
				message.contains(expressionValue));
		Assert.assertTrue("It contains the command name",
				message.contains("Command Cmd"));
	}
}
