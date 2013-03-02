package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.main.App;
import geogebra.web.gui.app.VerticalPanelSmart;
import geogebra.web.gui.layout.DockPanelW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends DockPanelW {

	DockLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground

	public EuclidianDockPanelW(boolean stylebar) {
		super(0, null, null, stylebar, 0);
	
		buildGUI();
	}

	@Override
    protected Widget loadComponent() {
		if( euclidianpanel == null){
		euclidianpanel = new EuclidianPanel(this);
		eview1 = Canvas.createIfSupported();
		eview1.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		eview1.getElement().getStyle().setZIndex(0);
		euclidianpanel.add(eview1);
		}		
				
		return euclidianpanel;
	}
	
	class EuclidianPanel extends AbsolutePanel implements RequiresResize {

		EuclidianDockPanelW dockPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(EuclidianDockPanelW dockPanel) {
			this.dockPanel = dockPanel;
		}
		
		public void onResize() {

			if (app != null){
				int h = dockPanel.getComponentInteriorHeight();
				int w = dockPanel.getComponentInteriorWidth();
				if(h != oldHeight || w != oldWidth){
				app.ggwGraphicsViewDimChanged(
						dockPanel.getComponentInteriorWidth(), dockPanel.getComponentInteriorHeight());
				oldHeight = h;
				oldWidth = w;
				}
			}
		}
	}
	
	
	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getActiveEuclidianView().getStyleBar();
		}

		return (Widget) espanel;
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
		super.attachApp(app);
		//if (espanel != null)
			//espanel.attachApp(app);
	}

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
	}

	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub	    
    }
}
