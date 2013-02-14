package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class MaterialPanel extends LayoutPanel
{
	private ScrollPanel scrollPanel;
	private HorizontalPanel horizontalPanel;

	public MaterialPanel()
	{
		this.scrollPanel = new ScrollPanel();
		this.horizontalPanel = new HorizontalPanel();

		this.scrollPanel.add(this.horizontalPanel);
		this.scrollPanel.setScrollingEnabledX(true);
		this.scrollPanel.setScrollingEnabledY(false);
		this.add(this.scrollPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		for (Material m : materials)
		{
			MaterialPreview preview = new MaterialPreview(m);

			this.horizontalPanel.add(preview);
		}
	}
}
