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
	protected Kernel kernel;
	
	public AlgebraViewWeb(AppWeb app){
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
	}
	public void doRepaint() {
	    // TODO Auto-generated method stub
	    
    }

}
