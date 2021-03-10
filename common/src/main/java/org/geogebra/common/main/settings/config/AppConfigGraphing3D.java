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
import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.parser.function.ParserFunctionsFactory;
import org.geogebra.common.main.App;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;
import org.geogebra.common.properties.factory.G3DPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

/**
 * Config for 3D Graphing Calculator app
 */
public class AppConfigGraphing3D extends AppConfigGraphing {

	public AppConfigGraphing3D() {
		super(GeoGebraConstants.G3D_APPCODE, null);
	}

	public AppConfigGraphing3D(String appCode) {
		super(appCode, GeoGebraConstants.G3D_APPCODE);
	}

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.setLocation("3");
		} else if (dp.getViewId() == App.VIEW_EUCLIDIAN3D) {
			dp.makeVisible();
			dp.setLocation("1");
		}
	}

	@Override
	public String getAppTitle() {
		return "Graphing3D";
	}

	@Override
	public String getAppName() {
		return "GeoGebra3DGrapher";
	}

	@Override
	public String getAppNameShort() {
		return "GeoGebra3DGrapher.short";
	}

	@Override
	public String getAppNameWithoutCalc() {
		return "GeoGebra3DGrapher.short";
	}

	@Override
	public String getTutorialKey() {
		return getSubAppCode() == null ? "Tutorial3D" : "TutorialSuite";
	}

	@Override
	public int getDefaultPrintDecimals() {
		return Kernel.STANDARD_PRINT_DECIMALS_SHORT;
	}

	@Override
	public boolean hasSingleEuclidianViewWhichIs3D() {
		return true;
	}

	@Override
	public boolean hasTableView() {
		return false;
	}

	@Override
	public int[] getDecimalPlaces() {
		return new int[]{0, 1, 2, 3, 4, 5, 10, 15};
	}

	@Override
	public boolean isCASEnabled() {
		return true;
	}

	@Override
	public String getPreferencesKey() {
		return "_3d";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.GRAPHER_3D + "";
	}

	@Override
	public AppType getToolbarType() {
		return AppType.GRAPHER_3D;
	}

	@Override
	public boolean showGridOnFileNew() {
		return false;
	}

	@Override
	public String getDefaultSearchTag() {
		return "ft.phone-3d";
	}

	@Override
	public int getDefaultLabelingStyle() {
		return ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC;
	}

	@Override
	public CommandFilter getCommandFilter() {
		return CommandFilterFactory.create3DGraphingCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@CheckForNull
	@Override
	public SyntaxFilter newCommandSyntaxFilter() {
		return null;
	}

	@Override
	public boolean hasPreviewPoints() {
		return false;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.GRAPHING_3D;
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
	public int getEnforcedLineEquationForm() {
		return -1;
	}

	@Override
	public int getEnforcedConicEquationForm() {
		return -1;
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
	public boolean hasAnsButtonInAv() {
		return false;
	}

	@Override
	public boolean isCoordinatesObjectSettingEnabled() {
		return true;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new G3DPropertiesFactory();
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}
}
