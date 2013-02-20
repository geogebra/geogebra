package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class HorizontalMaterialPanel extends ScrollPanel
{
	private HorizontalPanel horizontalPanel;

	public HorizontalMaterialPanel()
	{
		this.setWidth(Window.getClientWidth() + "px");
		this.setScrollingEnabledX(true);
		this.setScrollingEnabledY(false);

		this.horizontalPanel = new HorizontalPanel();
		this.horizontalPanel.setWidth("100%");
		this.setWidget(this.horizontalPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		this.horizontalPanel.clear();

		for (Material m : materials)
		{
			MaterialPreview preview = new MaterialPreview(m);

			this.horizontalPanel.add(preview);
		}
	}
}