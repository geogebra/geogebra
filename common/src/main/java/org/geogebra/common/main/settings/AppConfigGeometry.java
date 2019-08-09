package org.geogebra.common.main.settings;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilterFactory;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

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
			dp.makeVisible();
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
	public boolean hasScientificKeyboard() {
		return false;
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
	public CommandNameFilter getCommandNameFilter() {
		return CommandNameFilterFactory.createNoCasCommandNameFilter();
	}

	@Override
	public boolean showToolsPanel() {
		return true;
	}

	@Override
	public String getAppCode() {
		return "geometry";
	}
}
