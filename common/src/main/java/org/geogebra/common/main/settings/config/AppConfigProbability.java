package org.geogebra.common.main.settings.config;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.factory.ProbabilityPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesFactory;

public class AppConfigProbability extends AppConfigGraphing {

	public AppConfigProbability(String appCode) {
		super(appCode, GeoGebraConstants.PROBABILITY_APPCODE);
	}

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.setLocation("3");
			dp.setTabId(DockPanelData.TabIds.DISTRIBUTION);
		} else if (dp.getViewId() == App.VIEW_PROBABILITY_CALCULATOR) {
			dp.setLocation("1");
		}
	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public String getAppTitle() {
		return "Probability";
	}

	@Override
	public String getAppName() {
		return "Probability";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.PROBABILITY + "";
	}

	@Override
	public String getAppNameShort() {
		return "Probability";
	}

	@Override
	public String getAppNameWithoutCalc() {
		return  "Probability";
	}

	@Override
	public String getPreferencesKey() {
		return "_probability";
	}

	@Override
	public boolean hasTableView() {
		return true;
	}

	@Override
	public boolean showToolsPanel() {
		return false;
	}

	@Override
	public GeoGebraConstants.Version getVersion() {
		return GeoGebraConstants.Version.PROBABILITY;
	}

	@Override
	public PropertiesFactory createPropertiesFactory() {
		return new ProbabilityPropertiesFactory();
	}

	@Override
	public boolean hasEuclidianView() {
		return false;
	}

	@Override
	public boolean hasDistributionView() {
		return true;
	}

	@Override
	public boolean hasAlgebraView() {
		return false;
	}

	@Override
	public int getMainGraphicsViewId() {
		return App.VIEW_PROBABILITY_CALCULATOR;
	}

	@Override
	public boolean isCASEnabled() {
		return getSubAppCode() != null;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return false;
	}
}