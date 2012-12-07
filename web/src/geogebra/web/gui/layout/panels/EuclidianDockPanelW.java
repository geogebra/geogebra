package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.app.EuclidianStyleBarPanel;
import geogebra.web.gui.app.AbsolutePanelSmart;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.layout.DockPanelW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class EuclidianDockPanelW extends DockPanelW {

	App application = null;

	SimpleLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	EuclidianStyleBarPanel espanel;
	AbsolutePanelSmart euclidianpanel;
	Canvas eview1 = null;

	public EuclidianDockPanelW(boolean stylebar) {
		super(0, null, null, stylebar, 0);
		if (stylebar) {
			initWidget(toplevel = new SimpleLayoutPanel());
			ancestor = new VerticalPanelSmart();
			ancestor.add(espanel = new EuclidianStyleBarPanel());
			ancestor.add(euclidianpanel = new AbsolutePanelSmart());
			toplevel.add(ancestor);
		} else {
			initWidget(euclidianpanel = new AbsolutePanelSmart());
		}

		eview1 = Canvas.createIfSupported();
		euclidianpanel.add(eview1);
	}

	protected Widget loadComponent() {
		return euclidianpanel;
	}

	protected Widget loadStyleBar() {
		return espanel;
	}

	public Canvas getCanvas() {
	    return eview1;
    }

	public AbsolutePanel getAbsolutePanel() {
	    return euclidianpanel;
    }

	public void onResize() {
		super.onResize();
		//App.debug("resized");
		/*if (application != null) {

			//if (sview != null) {
				// If this is resized, we may know its width and height

				int width = this.getOffsetWidth();
				int height = this.getOffsetHeight();
				ancestor.setWidth(width+"px");
				ancestor.setHeight(height+"px");

				height -=
					((EuclidianStyleBarW)application.getActiveEuclidianView().getStyleBar()).
					getOffsetHeight();

				eview1.setWidth(width+"px");
				eview1.setHeight(height+"px");
				((EuclidianViewW)application.getActiveEuclidianView()).setPreferredSize(width, height);
			//}
		}*/
    }

	public void add(Widget w, int x, int y) {
	    euclidianpanel.add(w,x,y);
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public void attachApp(App app) {
		this.application = app;
		espanel.attachApp(app);
	}

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
	}
}
