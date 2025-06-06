package org.geogebra.common.main.settings.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Config for the Suite app (currently graphing before tool removal)
 */
public class AppConfigUnrestrictedGraphing extends AppConfigGraphing {

	public AppConfigUnrestrictedGraphing() {
		super(GeoGebraConstants.SUITE_APPCODE, null);
	}

	public AppConfigUnrestrictedGraphing(String appCode) {
		super(appCode, GeoGebraConstants.GRAPHING_APPCODE);
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
	public String getAppTitle() {
		return "CalculatorSuite";
	}

	@Override
	public String getAppName() {
		return "GeoGebraCalculatorSuite";
	}

	@Override
	public String getAppNameShort() {
		return GeoGebraConstants.SUITE_SHORT_NAME;
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
	public ExpressionFilter createExpressionFilter() {
		return null;
	}

	@Override
	public CommandFilter createCommandFilter() {
		return null;
	}

	@Override
	public @CheckForNull SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public ParserFunctionsFactory createParserFunctionsFactory() {
		return ParserFunctionsFactory.createParserFunctionsFactory();
	}

	@Override
	public void initializeEquationBehaviour() {
		equationBehaviour = new DefaultEquationBehaviour();
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public boolean hasAnsButtonInAv() {
		return true;
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

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}
}
