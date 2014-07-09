package geogebra.phone.gui.views;

import geogebra.html5.gui.ResizeListener;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 */
public class EuclidianViewPanel extends FlowPanel implements ResizeListener {
	private EuclidianViewW euclidianView;
	private AppW app;

	public EuclidianViewPanel(AppW app) {
		this.app = app;
		this.euclidianView = app.createEuclidianView();
		this.add(this.euclidianView.getCanvas());
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - 43);
	}

//	public void removeGBoxes() {
//	    for (Widget w: this.boxes) {
//	    	remove(w);
//	    }
//		this.boxes.clear();
//	}
//	private ArrayList<Widget> boxes = new ArrayList<Widget>();
//	public void removeBox(Widget impl) {
//		remove(impl);
//		this.boxes.remove(impl);
//		
//	}
//	
//	public void addBox(Widget impl, int x, int y) {
//		add(impl, x, y);
//		this.boxes.add(impl);
//		
//	}
}
