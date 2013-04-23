package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.main.App;
import geogebra.web.gui.app.VerticalPanelSmart;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class may be redundant since EuclidianDockPanelW,
 * but GeoGebra Desktop also uses two different classes for similar
 * purposes, so its behaviour was imitated here.
 *  
 * @author arpad
 */

public class Euclidian2DockPanelW extends EuclidianDockPanelWAbstract {

	DockLayoutPanel toplevel;

	VerticalPanelSmart ancestor;
	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground
	
	Euclidian2DockPanelW thisPanel;

	public Euclidian2DockPanelW(boolean stylebar) {
		super(
				App.VIEW_EUCLIDIAN2,	// view id 
				"DrawingPad2", 				// view title
				null,						// toolbar string
				stylebar,					// style bar?
				6,							// menu order
				'2' // ctrl-shift-1
			);
		
		//TODO: temporary fix to make applets work until
		// dockpanels works for applets
		
		if(stylebar){
			component = loadComponent();
			thisPanel = this;
		}else{
			loadComponent();
			buildGUI();
		}
	}

	
	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.add(eview1);
		}

		return euclidianpanel;
	}
	
	class EuclidianPanel extends AbsolutePanel implements RequiresResize {

		Euclidian2DockPanelW dockPanel;

		int oldHeight = 0;
		int oldWidth = 0;
		
		public EuclidianPanel(Euclidian2DockPanelW dockPanel) {
			this.dockPanel = dockPanel;
		}

		public void onResize() {
		
			if (app != null) {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {

						int h = dockPanel.getComponentInteriorHeight();
						int w = dockPanel.getComponentInteriorWidth();

						// TODO handle this better?
						// exit if new size cannot be determined
						if (h < 0 || w < 0) {
							return;
						}
						if (h != oldHeight || w != oldWidth) {
							app.ggwGraphicsView2DimChanged(w, h);
							oldHeight = h;
							oldWidth = w;
						}
					}
				});

			}
		}
	}
	
	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getEuclidianView2().getStyleBar();
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
		
	}

	public Euclidian2DockPanelW getEuclidianView2Wrapper() {
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
