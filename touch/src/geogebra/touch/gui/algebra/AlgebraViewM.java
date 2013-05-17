package geogebra.touch.gui.algebra;

import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.html5.main.AppWeb;
import geogebra.touch.gui.CommonResources;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;
import geogebra.web.gui.view.algebra.InlineLabelTreeItem;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * Taken from the web-project.
 * 
 */
public class AlgebraViewM extends AlgebraViewWeb {

	/**
	 * Creates new AlgebraView.
	 * 
	 * @param algCtrl
	 *            : AlgebraController
	 * 
	 */
	public AlgebraViewM(EuclidianController ctr) {
		super((AppWeb) ctr.getApplication());
		// this is the default value
		this.treeMode = SortMode.TYPE;

		// initializes the tree model
		initModel();

		setLabels();

		getElement().setId("View_" + App.VIEW_ALGEBRA);
	}

	public void updateFonts() {
		GFont font = this.app.getPlainFontCommon();
		getStyleElement().getStyle().setFontStyle(
				Style.FontStyle.valueOf(font.isItalic() ? "ITALIC" : "NORMAL"));
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
		getStyleElement().getStyle().setFontWeight(
				Style.FontWeight.valueOf(font.isBold() ? "BOLD" : "NORMAL"));
	}

	@Override
	public Object getPathForLocation(int x, int y) {
		return null;
	}

	public boolean editing = false;
	private boolean showing = true;

	@Override
	public void startEditing(GeoElement geo, boolean shiftDown) {
	}

	// temporary proxies for the temporary implementation of AlgebraController
	// in
	// common
	@Override
	public GeoElement getGeoElementForPath(Object tp) {
		// return getGeoElementForPath((TreePath)tp);
		return null;
	}

	@Override
	public GeoElement getGeoElementForLocation(Object tree, int x, int y) {
		// return getGeoElementForLocation((JTree)tree, x, y);
		return null;
	}

	@Override
	public Object getPathBounds(Object tp) {
		// return getPathBounds((TreePath)tp);
		return null;
	}

	// temporary proxies end

	@Override
	public void cancelEditing() {
		this.editing = false;
		setAnimationEnabled(true);
	}

	@Override
	public boolean isEditing() {
		return this.editing;
	}

	@Override
	protected boolean isKeyboardNavigationEnabled(TreeItem ti) {
		// keys should move the geos in the EV
		// if (isEditing())
		return false;
		// return super.isKeyboardNavigationEnabled(ti);
	}

	@Override
	public void setUserObject(TreeItem ti, Object ob) {
		ti.setUserObject(ob);
		if (ob instanceof GeoElement) {
			ti.setWidget(new RadioButtonTreeItem((GeoElement) ob,
					CommonResources.INSTANCE.algebra_shown().getSafeUri(),
					CommonResources.INSTANCE.algebra_hidden().getSafeUri()));
			ti.getElement().getStyle().setPadding(0, Unit.PX);
			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"inline-block");
		} else {
			ti.setWidget(new InlineLabelTreeItem(
					this.app.getSelectionManager(), ti, ob.toString()));
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (!this.editing)
			super.onBrowserEvent(event);
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	/**
	 * TODO: should return false if the panel is not extended!
	 */
	@Override
	public boolean isShowing() {
		return this.showing;
	}

	@Override
	public boolean isRenderLaTeX() {
		return false;
	}

	public void setShowing(boolean flag) {
		this.showing = flag;
		if(flag)
			repaint();
	}

}
