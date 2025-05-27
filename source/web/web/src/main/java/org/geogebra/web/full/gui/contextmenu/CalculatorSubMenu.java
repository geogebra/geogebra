package org.geogebra.web.full.gui.contextmenu;

import static org.geogebra.common.GeoGebraConstants.BAYERN_GRAPHING_APPCODE;

import java.util.Set;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.full.gui.dialog.AppDescription;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.main.AppW;

public class CalculatorSubMenu extends AriaMenuBar {
	private final AppW app;
	private final ContextMenuItemFactory factory;
	private final EmbedManager embedManager;
	private final Set<SuiteSubApp> restrictions;
	private final String appOrExamModeName;

	/**
	 * Constructor
	 * @param app - application
	 */
	public CalculatorSubMenu(AppW app) {
		this.app = app;
		factory = new ContextMenuItemFactory();
		embedManager = app.getEmbedManager();
		ExamType examType = ExamType.byName(app.getAppletParameters().getParamFeatureSet());
		restrictions = examType == null ? Set.of()
				: ExamRestrictions.forExamType(examType).getDisabledSubApps();
		appOrExamModeName = examType == null
				? GeoGebraConstants.SUITE_APPCODE : examType.name().toLowerCase();
		if (embedManager != null) {
			if (!app.isMebis() || examType != ExamType.BAYERN_GR) {
				addItem(SuiteSubApp.GRAPHING);
			}
			addItem(SuiteSubApp.G3D);
			addItem(SuiteSubApp.GEOMETRY);
			addItem(SuiteSubApp.CAS);
			addItem(SuiteSubApp.PROBABILITY);
			addItem(SuiteSubApp.SCIENTIFIC);
			if (app.isMebis() && (examType == null || examType == ExamType.BAYERN_GR)) {
				addItem(factory.newAriaMenuItem(null,
						"Grafikrechner (Bayern)",
						() -> embedManager.addCalcWithPreselectedApp(BAYERN_GRAPHING_APPCODE,
								GeoGebraConstants.GRAPHING_APPCODE)));
			}
		}
	}

	private void addItem(SuiteSubApp subApp) {
		if (restrictions.contains(subApp)) {
			return;
		}
		AppDescription description = AppDescription.get(subApp);
		addItem(factory.newAriaMenuItem(null,
				app.getLocalization().getMenu(description.getNameKey()),
				() -> embedManager.addCalcWithPreselectedApp(appOrExamModeName,
						subApp.appCode)));
	}
}
