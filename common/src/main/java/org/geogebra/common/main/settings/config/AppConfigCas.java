package org.geogebra.common.main.settings.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CASCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.settings.updater.CasSettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdater;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.CasPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Config for CAS Calculator app
 */
public class AppConfigCas extends AppConfigGraphing {

	public AppConfigCas() {
		super(GeoGebraConstants.CAS_APPCODE, null);
	}

	public AppConfigCas(String appCode) {
		super(appCode, GeoGebraConstants.CAS_APPCODE);
	}

	@Override
	public String getAppTitle() {
		return "CASCalculator";
	}

	@Override
	public String getAppName() {
		return "GeoGebraCASCalculator";
	}

	@Override
	public String getAppNameShort() {
		return "CAS";
	}

	@Override
	public String getAppNameWithoutCalc() {
		return "CAS";
	}

	@Override
	public String getTutorialKey() {
		return getSubAppCode() == null ? "cas_tutorials" : "TutorialSuite";
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "_cas";
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.SYMBOLIC_AV;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createCasCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return new CASCommandArgumentFilter();
	}

	@CheckForNull
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public boolean hasAutomaticLabels() {
		return false;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return false;
	}

	@Override
	public boolean showToolsPanel() {
		return false;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.CAS;
	}

	@Override
	public String getExamMenuItemText() {
		return "ExamCAS.short";
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
	public OperationArgumentFilter createOperationArgumentFilter() {
		return null;
	}

	@Override
	public ParserFunctionsFactory createParserFunctionsFactory() {
		return ParserFunctionsFactory.createParserFunctionsFactory();
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public int getDefaultAngleUnit() {
		return Kernel.ANGLE_RADIANT;
	}

	@Override
	public boolean isAngleUnitSettingEnabled() {
		return false;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return true;
	}

	@Override
	public SettingsUpdater createSettingsUpdater() {
		return new CasSettingsUpdater();
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new CasPropertiesFactory();
	}

	@Override
	public StringTemplate getOutputStringTemplate() {
		return StringTemplate.numericLatex;
	}

	@Override
	public boolean hasLabelForDescription() {
		return false;
	}
}
