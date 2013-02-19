package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class MaterialResultPanel extends LayoutPanel
{
	private ScrollPanel scrollPanel;
	private VerticalPanel verticalPanel;

	public MaterialResultPanel()
	{		
		this.scrollPanel = new ScrollPanel();
		this.verticalPanel = new VerticalPanel();

		this.scrollPanel.add(this.verticalPanel);
		this.scrollPanel.setScrollingEnabledX(false);
		this.scrollPanel.setScrollingEnabledY(true);
		this.add(this.scrollPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		for (Material m : materials)
		{
			MaterialSearchResult searchResult = new MaterialSearchResult(m);

			this.verticalPanel.add(searchResult);
		}
	}
}
