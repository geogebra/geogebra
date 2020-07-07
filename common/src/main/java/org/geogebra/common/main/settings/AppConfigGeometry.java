package org.geogebra.common.main.settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.settings.updater.GeometrySettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdater;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.BasePropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * App-specific behaviors of Geometry app
 * 
 * @author Zbynek
 *
 */
public class AppConfigGeometry implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.setLocation("3");
			dp.setToolMode(true);
		}
		else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
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
		return "Perspective.Geometry";
	}

	@Override
	public String getAppName() {
		return "GeoGebraGeometry";
	}

	@Override
	public String getAppNameShort() {
		return "Geometry";
	}

	@Override
	public String getTutorialKey() {
		return "TutorialGeometry";
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
		return true;
	}

	@Override
	public int getDefaultPrintDecimals() {
		return Kernel.STANDARD_PRINT_DECIMALS_GEOMETRY;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return false;
	}

	@Override
	public int[] getDecimalPlaces() {
		return new int[] {0, 1, 2, 3, 4, 5, 10, 15};
	}

	@Override
	public int[] getSignificantFigures() {
		return new int[] {3, 5, 10, 15};
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
		return "_geometry";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.GEOMETRY + "";
	}

	@Override
	public boolean isEnableStructures() {
		return true;
	}

	@Override
	public AppType getToolbarType() {
		return AppType.GEOMETRY_CALC;
	}

    @Override
    public boolean showGridOnFileNew() {
        return false;
    }

    @Override
    public boolean showAxesOnFileNew() {
        return false;
    }

	@Override
	public boolean hasTableView() {
		return false;
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
		return Kernel.ALGEBRA_STYLE_DESCRIPTION;
	}

	@Override
	public String getDefaultSearchTag() {
		return "ft.phone-2d";
	}

	@Override
	public int getDefaultLabelingStyle() {
		return ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.createNoCasCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@Nullable
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public boolean showToolsPanel() {
		return true;
	}

	@Override
	public String getAppCode() {
		return "geometry";
	}

	@Override
	public SettingsUpdater createSettingsUpdater() {
		return new GeometrySettingsUpdater();
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.GEOMETRY;
	}

	@Override
	public boolean hasExam() {
		return false;
	}

	@Override
	public String getExamMenuItemText() {
		return "";
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
	public int getDefaultAngleUnit() {
		return Kernel.ANGLE_DEGREE;
	}

	@Override
	public boolean isAngleUnitSettingEnabled() {
		return true;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return true;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new BasePropertiesFactory();
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.GEOMETRY;
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
		return true;
	}
}
