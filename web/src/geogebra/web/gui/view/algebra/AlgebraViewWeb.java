package geogebra.web.gui.view.algebra;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LayerView;
import geogebra.common.main.Localization;
import geogebra.web.main.AppWeb;

import com.google.gwt.user.client.ui.Tree;

public abstract class AlgebraViewWeb extends Tree implements LayerView, SetLabels, AlgebraView{

	protected final AppWeb app; // parent appame
	protected final Localization loc;
	protected final Kernel kernel;
	
	public AlgebraViewWeb(AppWeb app){
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
	}
	
	public abstract void doRepaint();
	
	public final void repaint() {

		// no need to repaint that which is not showing
		// (but take care of repainting if it appears!)
		if (!isShowing())
			return;

		app.getTimerSystem().viewRepaint(this);
    }
	
	public final void repaintView() {
		repaint();
	}

}
