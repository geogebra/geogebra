package geogebra.touch.gui.elements.ggt;

import geogebra.touch.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VerticalMaterialPanel extends ScrollPanel
{
	private VerticalPanel verticalPanel;

	public VerticalMaterialPanel()
	{
		this.setHeight(Window.getClientHeight() - 250 + "px");
		this.setTouchScrollingDisabled(false);
		this.getElement().setAttribute("align", "center");

		this.verticalPanel = new VerticalPanel();
		this.verticalPanel.setHeight("100%");
		this.setWidget(this.verticalPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		this.verticalPanel.clear();

		for (Material m : materials)
		{
			MaterialListElement searchResult = new MaterialListElement(m);
			this.verticalPanel.add(searchResult);
		}
	}
}
