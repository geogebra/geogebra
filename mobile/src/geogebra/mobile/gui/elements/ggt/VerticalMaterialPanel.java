package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class VerticalMaterialPanel extends ScrollPanel
{
	private VerticalPanel verticalPanel;

	public VerticalMaterialPanel()
	{
		this.impl.setHeight(Window.getClientHeight() - 250 + "px");
		this.setScrollingEnabledX(false);
		this.setScrollingEnabledY(true);

		this.verticalPanel = new VerticalPanel();
		this.verticalPanel.setHeight(Window.getClientHeight() - 250 + "px");
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
