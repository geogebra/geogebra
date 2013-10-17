package geogebra.html5.gui.inputfield;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.html5.main.AppWeb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup for use in the text editor panel. Displays a list of the labels of
 * current GeoElements
 * 
 * @author G. Sturr
 * 
 */
public class GeoListPopup extends PopupPanel implements ClickHandler {

	private AppWeb app;
	private ListBox geoListBox;
	private TextEditPanel editPanel;

	/**
	 * @param app
	 * @param editPanel
	 * @param invoker
	 */
	public GeoListPopup(AppWeb app, TextEditPanel editPanel, Widget invoker) {
		super(true);
		this.app = app;
		this.editPanel = editPanel;

		createGeoListBox();
		add(geoListBox);

		// prevent autohide when clicking on the popup button
		addAutoHidePartner(invoker.getElement());

	}

	private void createGeoListBox() {
		geoListBox = new ListBox(true);
		geoListBox.setWidth("12em");
		geoListBox.setVisibleItemCount(10);
		geoListBox.addClickHandler(this);
	}

	public void updateGeoList() {
		geoListBox.clear();
		String[] geoLabels = getGeoObjectList(editPanel.getEditGeo());
		for (int i = 0; i < geoLabels.length; i++) {
			geoListBox.addItem(geoLabels[i]);
		}
	}

	/**
	 * Creates an array of labels of existing geos that can be inserted into the
	 * editor content
	 */
	private String[] getGeoObjectList(GeoText editGeo) {

		TreeSet<GeoElement> ts = app.getKernel().getConstruction()
		        .getGeoSetLabelOrder();
		ArrayList<String> list = new ArrayList<String>();

		// first possibility : create empty box
		list.add(app.getPlain("EmptyBox"));

		// add all geos
		Iterator<GeoElement> iter = ts.iterator();
		while (iter.hasNext()) {
			GeoElement g = iter.next();
			if (g.isLabelSet() && !g.equals(editGeo)) {
				list.add(g.getLabelSimple());
			}
		}
		String[] geoArray = new String[list.size()];
		geoArray = list.toArray(geoArray);
		return geoArray;
	}

	public void onClick(ClickEvent event) {
		String label = geoListBox.getItemText(geoListBox.getSelectedIndex());
		editPanel.insertGeoElement(app.getKernel().lookupLabel(label));
		hide();
	}

	/**
	 * Ensure the popup toggle button is updated after hiding
	 */
	@Override
	public void hide(boolean autoClosed) {
		super.hide(autoClosed);
		editPanel.setGeoListButton(false);
	}

}
