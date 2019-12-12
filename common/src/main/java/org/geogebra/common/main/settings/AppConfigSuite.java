package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;

import org.geogebra.common.gui.toolcategorization.AppType;

/**
 * Config for the Suite app (currently graphing before tool removal)
 */
public class AppConfigSuite extends AppConfigGraphing {

	@Override
	public String getAppCode() {
		return "suite";
	}

	@Override
	public CommandFilter getCommandFilter() {
		return null;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.SUITE;
	}

	@Override
	public OperationArgumentFilter createOperationArgumentFilter() {
		return null;
	}

	@Override
	public ParserFunctions createParserFunctions() {
		return ParserFunctionsFactory.createParserFunctions();
	}
}
