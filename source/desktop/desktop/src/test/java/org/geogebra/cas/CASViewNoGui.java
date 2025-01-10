package org.geogebra.cas;

import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.headless.AppDNoGui;

public class CASViewNoGui extends CASView {

	private AppDNoGui app;
	private CASTableNoGui casTable;

	/**
	 * @param app application
	 * @param inputs CAS table contents
	 */
	public CASViewNoGui(AppDNoGui app, String... inputs) {
		super(app.getKernel());
		this.app = app;
		this.casTable = new CASTableNoGui(inputs, app);
	}

	@Override
	public void resetItems(boolean unselectAll) {
		// mock implementation
	}

	@Override
	public boolean isShowing() {
		// mock implementation
		return false;
	}

	@Override
	public void repaintView() {
		// mock implementation
	}

	@Override
	public boolean suggestRepaint() {
		// mock implementation
		return false;
	}

	@Override
	public boolean hasFocus() {
		// mock implementation
		return false;
	}

	@Override
	public CASTableNoGui getConsoleTable() {
		return casTable;
	}

	@Override
	public App getApp() {
		return app;
	}

	@Override
	public void showSubstituteDialog(String prefix, String evalText,
			String postfix, int selRow) {
		// mock implementation
	}

}
