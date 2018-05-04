package org.geogebra.cas;

import org.geogebra.common.cas.view.CASTable;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppDNoGui;

public class CASViewNoGui extends CASView {

	private AppDNoGui app;
	private String[] inputs;
	private CASTable casTable;

	public CASViewNoGui(AppDNoGui app, String... inputs) {
		super(app.getKernel());
		this.app = app;
		this.inputs = inputs;
		this.casTable = new CASTableNoGui(inputs, app);
	}

	public void resetItems(boolean unselectAll) {
		// TODO Auto-generated method stub

	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public boolean suggestRepaint() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CASTable getConsoleTable() {
		return casTable;
	}

	@Override
	public App getApp() {
		return app;
	}

	@Override
	public void showSubstituteDialog(String prefix, String evalText,
			String postfix, int selRow) {
		// TODO Auto-generated method stub

	}

}
