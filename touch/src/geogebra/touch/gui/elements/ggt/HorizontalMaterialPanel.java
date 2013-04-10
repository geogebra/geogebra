package geogebra.touch.gui.elements.ggt;

import geogebra.web.util.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;

public class HorizontalMaterialPanel extends ScrollPanel
{
	private FlexTable contentPanel;

	public HorizontalMaterialPanel()
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
			MaterialPreview preview = new MaterialPreview(m);
			this.contentPanel.setWidget(0, i, preview);
			i++;
		}
	}
}