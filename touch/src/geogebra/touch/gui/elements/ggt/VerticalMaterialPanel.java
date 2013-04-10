package geogebra.touch.gui.elements.ggt;

import geogebra.web.util.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VerticalMaterialPanel extends ScrollPanel
{
	private FlexTable contentPanel;

	public VerticalMaterialPanel()
	{
		this.contentPanel = new FlexTable();

		this.setWidget(this.contentPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		this.contentPanel.clear();

		int i = 0;
		for (Material m : materials)
		{
			MaterialListElement preview = new MaterialListElement(m);
			this.contentPanel.setWidget(i, 0, preview);
			i++;
		}
	}

	@Override
	public int getOffsetHeight()
	{
		return MaterialListElement.PANEL_HEIGHT;
	}

	@Override
	public void setWidth(String width)
	{
		super.setWidth(width);
		this.contentPanel.setWidth(width);
	}
}
