package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class VerticalMaterialPanel extends FlowPanel
{
	public static final int SPACE = 20;
	private FlexTable contentPanel;
	private AppWeb app;
	private int materialHeight = 140;
	private FileManagerM fm;
	private MaterialListElement lastSelected;
	private int columns = 2;
	private Map<String, MaterialListElement> titlesToPreviews = new HashMap<String, MaterialListElement>();
	
	private int start;
	private List<Material> materials; 
	
	private static int maxHeight(){
		return Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight() - BrowseGUI.CONTROLS_HEIGHT;
	}

	public VerticalMaterialPanel(AppWeb app, FileManagerM fm)
	{
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.contentPanel = new FlexTable();
		this.app = app;
		this.fm = fm;

		//this.setWidget(this.contentPanel);
		this.add(this.contentPanel);
		this.contentPanel.setWidth("100%");
	}

	public void setMaterials(int cols, List<Material> materials)
	{
		setMaterials(cols, materials, 0);
	}

	private void setMaterials(int cols, List<Material> materials, int offset) {
		this.columns = cols;
		this.updateWidth();
		this.contentPanel.clear();
		this.start = offset;
		this.materials = materials;
		
		if (this.columns == 2) {
			this.contentPanel.getCellFormatter().setWidth(0, 0, "50%");
			this.contentPanel.getCellFormatter().setWidth(0, 1, "50%");
		} else {
			this.contentPanel.getCellFormatter().setWidth(0, 0, "100%");
		}

		for (int i = 0; i < materials.size() - this.start && i < maxHeight() / this.materialHeight; i++)
		{
			Material m = materials.get(i+this.start);
			MaterialListElement preview = new MaterialListElement(m, this.app, this.fm, this);
			preview.initButtons();
			this.titlesToPreviews.put(m.getURL(), preview);
			this.contentPanel.setWidget(i / this.columns, i % this.columns, preview);			
		}
		
		
	}

	@Override
	public int getOffsetHeight()
	{
		return MaterialListElement.PANEL_HEIGHT;
	}

	public void unselectMaterials()
	{
		if (this.lastSelected != null)
		{
			this.lastSelected.markUnSelected();
		}
	}

	public void markByURL(String url)
	{
		MaterialListElement mle = this.titlesToPreviews.get(url);
		if (mle != null)
		{
			mle.markSelected();
		}
	}

	public void rememberSelected(MaterialListElement materialElement)
	{
		this.lastSelected = materialElement;
	}

	public int getColumns()
	{
		return this.columns;
	}

	public void updateWidth()
	{
		this.setWidth(((Window.getClientWidth()) / 2 * this.columns) + "px");
	}
	
	public MaterialListElement getChosenMaterial()
	{
		return this.lastSelected;
	}
	
	public void setLabels(){
		for(MaterialListElement e: this.titlesToPreviews.values()){
			e.setLabels();
		}
	}

	public void nextPage() {
		if(this.start + maxHeight() / this.materialHeight >= this.materials.size()){
			return;
		}
		this.setMaterials(this.columns, this.materials, this.start + maxHeight() / this.materialHeight);	
	}
	
	public void prevPage() {
		if(this.start <= 0){
			return;
		}
		this.setMaterials(this.columns, this.materials, this.start - maxHeight() / this.materialHeight);	
	}

	public void updateHeight() {
		Iterator<MaterialListElement> material = this.titlesToPreviews.values().iterator();
		if(material.hasNext()){
			if(material.next().getOffsetHeight() > 0){
				this.materialHeight = material.next().getOffsetHeight();
			}
		}
		//if(this.materialHeight != oldMaterialHeight){
		if(this.materials != null){
			this.setMaterials(this.columns, this.materials, this.start);
		}
			//}
		
	}
}
