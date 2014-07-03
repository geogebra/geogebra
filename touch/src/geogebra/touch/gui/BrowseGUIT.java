package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.html5.main.AppWeb;
import geogebra.touch.gui.views.BrowseViewPanelT;

public class BrowseGUIT extends BrowseGUI {

	public BrowseGUIT(AppWeb app) {
		super(app);
		System.out.println("BrowseGUIT");
	}
	
	public void addToLocalList(Material mat) {
		((BrowseViewPanelT) this.content).addToLocalList(mat);
    }

	public void removeFromLocalList(Material mat) {
		((BrowseViewPanelT) this.content).removeFromLocalList(mat);
    }
	
	@Override
	protected void initContent() {
		this.content = new BrowseViewPanelT(this.app);
		this.addResizeListener(this.content);
	}
}


//package geogebra.touch.gui;
//
//import geogebra.common.move.ggtapi.models.Material;
//import geogebra.html5.gui.ResizeListener;
//import geogebra.html5.gui.browser.MaterialListElement;
//import geogebra.html5.main.AppWeb;
//import geogebra.touch.TouchEntryPoint;
//import geogebra.touch.gui.elements.ggt.MaterialListElementT;
//import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
//import geogebra.touch.gui.elements.header.BrowseHeaderPanel.SearchListener;
//import geogebra.touch.gui.views.BrowseViewPanelT;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.google.gwt.event.logical.shared.ResizeEvent;
//import com.google.gwt.event.logical.shared.ResizeHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.HeaderPanel;
//
///**
// * GeoGebraTube Search and Browse GUI
// * 
// */
//public class BrowseGUI extends HeaderPanel {
//
//	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
//	private BrowseHeaderPanel header;
//	BrowseViewPanelT content;
//
//	public final static int HEADING_HEIGHT = 50;
//
//	/**
//	 * Sets the viewport and other settings, creates a link element at the end
//	 * of the head, appends the css file and initializes the GUI elements.
//	 * 
//	 * @param app
//	 */
//	public BrowseGUI(final AppWeb app) {
//		this.setStyleName("browsegui");
//		
//		addHeader();
//		addContent(app);
//
//		Window.addResizeHandler(new ResizeHandler() {
//			@Override
//			public void onResize(final ResizeEvent event) {
//				BrowseGUI.this.updateViewSizes();
//			}
//		});
//		
//
//	}
//
//	void updateViewSizes() {
//		for (final ResizeListener res : this.resizeListeners) {
//			res.onResize();
//		}
//	}
//
//	private void addHeader() {
//		this.header = TouchEntryPoint.getLookAndFeel().buildBrowseHeader(this);
//		this.header.addSearchListener(new SearchListener() {
//			@Override
//			public void onSearch(final String query) {
//				BrowseGUI.this.content.displaySearchResults(query);
//			}
//		});
//	}
//
//	private void addContent(AppWeb app) {
//		this.content = new BrowseViewPanelT(app);
//		this.setContentWidget(this.content);
//	}
//
//	public void loadFeatured() {
//		this.content.loadFeatured();
//	}
//
//	
//	public void addResizeListener(final ResizeListener rl) {
//		this.resizeListeners.add(rl);
//	}
//
//	public void setLabels() {
//		this.header.setLabels();
////		this.content.setLabels();
//	}
//
//	public void removeFromLocalList(Material mat) {
//		this.content.removeFromLocalList(mat);
//	}
//	
//	public void addToLocalList(Material mat) {
//		this.content.addToLocalList(mat);
//	}
//
//	public MaterialListElement getChosenMaterial() {
//		return this.content.getChosenMaterial();
//	}
//
//	public void unselectMaterials() {
//		this.content.unselectMaterials();
//	}
//
//	public void rememberSelected(MaterialListElementT materialListElement) {
//		this.content.rememberSelected(materialListElement);
//	}
//}
