package org.geogebra.common.main.settings.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.GraphingOperationArgumentFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.GraphingCommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.settings.updater.GraphingSettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdater;
import org.geogebra.common.main.syntax.suggestionfilter.GraphingSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.BasePropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Config for Graphing Calculator app
 */
public class AppConfigGraphing extends AbstractAppConfig {

	public AppConfigGraphing() {
		super(GeoGebraConstants.GRAPHING_APPCODE);
	}

	public AppConfigGraphing(String appCode) {
		super(appCode, GeoGebraConstants.GRAPHING_APPCODE);
	}

	AppConfigGraphing(String appCode, String subAppCode) {
		super(appCode, subAppCode);
	}

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.setLocation("3");
		} else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
			dp.makeVisible();
			dp.setLocation("1");
		}
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public int getLineDisplayStyle() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public String getAppTitle() {
		return "GraphingCalculator";
	}

	@Override
	public String getAppName() {
		return "GeoGebraGraphingCalculator";
	}

	@Override
	public String getAppNameShort() {
		return "GraphingCalculator.short";
	}

	@Override
	public String getAppNameWithoutCalc() {
		return "GraphingCalculator.short";
	}

	@Override
	public String getTutorialKey() {
		return "TutorialGraphing";
	}

	@Override
	public boolean showKeyboardHelpButton() {
		return true;
	}

	@Override
	public boolean isSimpleMaterialPicker() {
		return false;
	}

	@Override
	public boolean hasPreviewPoints() {
		return true;
	}

	@Override
	public boolean allowsSuggestions() {
		return true;
	}

	@Override
	public boolean shouldKeepRatioEuclidian() {
		return false;
	}

	@Override
	public int getDefaultPrintDecimals() {
		return Kernel.STANDARD_PRINT_DECIMALS_GRAPHING;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return false;
	}

	@Override
	public int[] getDecimalPlaces() {
		return new int[]{0, 1, 2, 3, 4, 5, 10, 13, 15};
	}

	@Override
	public int[] getSignificantFigures() {
		return new int[]{3, 5, 10, 15};
	}

	@Override
	public boolean isGreekAngleLabels() {
		return true;
	}

	@Override
	public boolean isCASEnabled() {
		return false;
	}

	@Override
	public String getPreferencesKey() {
		return "_graphing";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.GRAPHING + "";
	}

	@Override
	public boolean isEnableStructures() {
		return true;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.GRAPHING_CALCULATOR;
	}

	@Override
	public boolean showGridOnFileNew() {
		return true;
	}

	@Override
	public boolean showAxesOnFileNew() {
		return true;
	}

	@Override
	public boolean hasTableView() {
		return true;
	}

	@Override
	public SymbolicMode getSymbolicMode() {
		return SymbolicMode.NONE;
	}

	@Override
	public boolean hasSlidersInAV() {
		return true;
	}

	@Override
	public boolean hasAutomaticLabels() {
		return true;
	}

	@Override
	public boolean hasAutomaticSliders() {
		return true;
	}

	@Override
	public int getDefaultAlgebraStyle() {
		return Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE;
	}

	@Override
	public String getDefaultSearchTag() {
		return "ft.phone-2d";
	}

	@Override
	public int getDefaultLabelingStyle() {
		return ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createGraphingCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return new GraphingCommandArgumentFilter();
	}

	@CheckForNull
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return new GraphingSyntaxFilter();
	}

	@Override
	public boolean showToolsPanel() {
		return true;
	}

	@Override
	public SettingsUpdater createSettingsUpdater() {
		return new GraphingSettingsUpdater();
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.GRAPHING;
	}

	@Override
	public boolean hasExam() {
		return true;
	}

	@Override
	public String getExamMenuItemText() {
		return "ExamGraphingCalc.short";
	}

	@Override
	public Set<FillType> getAvailableFillTypes() {
		Set<FillType> set = new HashSet<>(Arrays.asList(FillType.values()));
		set.remove(FillType.IMAGE);
		return set;
	}

	@Override
	public boolean isObjectDraggingRestricted() {
		return true;
	}

	@Override
	public int getDefaultAngleUnit() {
		return Kernel.ANGLE_DEGREE;
	}

	@Override
	public boolean isAngleUnitSettingEnabled() {
		return true;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return false;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new BasePropertiesFactory();
	}

	@Override
	public boolean disableTraceCM() {
		return false;
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.GRAPHING;
	}

	@Override
	public OperationArgumentFilter createOperationArgumentFilter() {
		return new GraphingOperationArgumentFilter();
	}

	@Override
	public ParserFunctionsFactory createParserFunctionsFactory() {
		return ParserFunctionsFactory.createGraphingParserFunctionsFactory();
	}

	@Override
	public int getEnforcedLineEquationForm() {
		return GeoLine.EQUATION_USER;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return GeoConic.EQUATION_USER;
	}

	@Override
	public boolean shouldHideEquations() {
		return true;
	}

	@Override
	public boolean hasAnsButtonInAv() {
		return true;
	}

	@Override
	public StringTemplate getOutputStringTemplate() {
		return StringTemplate.latexTemplate;
	}

	@Override
	public boolean sendKeyboardEvents() {
		return false;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}
}
