package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VerticalMaterialPanel extends ScrollPanel {
	public static final int SPACE = 20;
	private FlexTable contentPanel;
	private AppWeb app;
	private FileManagerM fm;
	private MaterialListElement lastSelected;
	private int columns = 2;
	private Map<String,MaterialListElement> titlesToPreviews= new HashMap<String, MaterialListElement>();
	public VerticalMaterialPanel(AppWeb app, FileManagerM fm) {
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.contentPanel = new FlexTable();
		this.app = app;
		this.fm = fm;

		this.setWidget(this.contentPanel);
	}

	public void setMaterials(int cols, List<Material> materials) {
		this.columns = cols;
		this.updateWidth();
		this.contentPanel.clear();

		int i = 0;
		for (Material m : materials) {
			MaterialListElement preview = buildListElement(m, this.app, this.fm);
			this.titlesToPreviews.put(m.getURL(), preview);
			this.contentPanel.setWidget(i / this.columns, i % this.columns,
					preview);
			i++;
		}
	}

	protected MaterialListElement buildListElement(Material m, AppWeb app2,
			FileManagerM fm2) {
		MaterialListElement mle = new MaterialListElement(m, app2, fm2, this);
		mle.initButtons();
		return mle;
	}

	@Override
	public int getOffsetHeight() {
		return MaterialListElement.PANEL_HEIGHT;
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);
		this.contentPanel.setWidth(width);
	}

	public void unselectMaterials() {
		if (this.lastSelected != null) {
			this.lastSelected.markUnSelected();
		}

	}
	
	public void markByURL(String url){
		MaterialListElement mle = this.titlesToPreviews.get(url);
		if(mle != null){
			mle.markSelected();
		}
	}

	public void rememberSelected(MaterialListElement materialElement) {
		this.lastSelected = materialElement;
	}

	public int getColumns() {
		return this.columns;
	}

	public void updateWidth() {
		this.setWidth((Window.getClientWidth() - SPACE) / 2 * this.columns
				+ "px");
	}
}
