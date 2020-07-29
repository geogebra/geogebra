package org.geogebra.common.main.settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.AppKeyboardType;

/**
 * Config for the Suite app (currently graphing before tool removal)
 */
public class AppConfigSuite extends AppConfigGraphing {

	@Override
	public String getAppCode() {
		return "suite";
	}

	@Override
	public Set<FillType> getAvailableFillTypes() {
		return new HashSet<>(Arrays.asList(FillType.values()));
	}

	@Override
	public boolean isObjectDraggingRestricted() {
		return false;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return null;
	}

	@Override
	public String getAppTitle() {
		return "CalculatorSuite";
	}

	@Override
	public String getAppName() {
		return "GeoGebraCalculatorSuite";
	}

	@Override
	public String getAppNameShort() {
		return "CalculatorSuite.short";
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
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

	@Override
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public boolean hasAnsButtonInAv() {
		return false;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.SUITE;
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getTutorialKey() {
		return "TutorialSuite";
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.SUITE;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return true;
	}
}
