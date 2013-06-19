package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VerticalMaterialPanel extends ScrollPanel
{
	private FlexTable contentPanel;
	private AppWeb app;

	public VerticalMaterialPanel(AppWeb app)
	{
		this.contentPanel = new FlexTable();
		this.app = app;

		this.setWidget(this.contentPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		this.contentPanel.clear();

		int i = 0;
		for (Material m : materials)
		{
			MaterialListElement preview = new MaterialListElement(m, this.app);
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
