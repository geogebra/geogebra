package geogebra.phone.gui.views;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AlgebraViewPanel extends FlowPanel implements ResizeListener {
	
	private AlgebraViewW algebraView;
	private ScrollPanel content;
	private AppW app;

	public AlgebraViewPanel(AppW app) {
		this.app = app;
		this.algebraView = this.app.getGuiManager().getAlgebraView();
		this.content = new ScrollPanel(this.algebraView);
		this.content.setStyleName("algebraView");
		this.add(this.content);
	}

	@Override
	public void onResize() {
		//FIXME do this with LAF - use GLookAndFeel...
		this.setSize(Window.getClientWidth()+"px", Window.getClientHeight() - 43+"px");
    }
}
