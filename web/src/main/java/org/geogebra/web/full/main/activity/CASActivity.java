package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.CommandNotLoadedError;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollectionCAS;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for CAS app
 */
public class CASActivity extends BaseActivity {

	/**
	 * Graphing activity
	 */
	public CASActivity() {
		super(new AppConfigCas());
	}

	@Override
	public SVGResource getIcon() {
		return SvgPerspectiveResources.INSTANCE.menu_icon_cas();
	}

	@Override
	public boolean useValidInput() {
		return false;
	}

	@Override
	public MenuItemCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionCAS(view);
	}

	@Override
	public void start(AppW app) {
		Kernel kernel = app.getKernel();
		kernel.getGeoGebraCAS().initCurrentCAS();
		kernel.getParser().setHighPrecisionParsing(true);
		CommandDispatcher dispatcher = kernel.getAlgebraProcessor().getCommandDispatcher();
		tryLoadingCasDispatcher(dispatcher);
		tryLoadingAdvancedDispatcher(dispatcher);
		tryLoadingScriptingDispatcher(dispatcher);
	}

	private void tryLoadingCasDispatcher(CommandDispatcher dispatcher) {
		try {
			dispatcher.getCASDispatcher();
		} catch (CommandNotLoadedError error) {
			//ignore
		}
	}

	private void tryLoadingAdvancedDispatcher(CommandDispatcher dispatcher) {
		try {
			dispatcher.getAdvancedDispatcher();
		} catch (CommandNotLoadedError e) {
			// ignore
		}
	}

	private void tryLoadingScriptingDispatcher(CommandDispatcher dispatcher) {
		try {
			dispatcher.getScriptingDispatcher();
		} catch (CommandNotLoadedError e) {
			// ignore
		}
	}

	@Override
	public SVGResource getExamIcon() {
		return MaterialDesignResources.INSTANCE.exam_cas();
	}
}
