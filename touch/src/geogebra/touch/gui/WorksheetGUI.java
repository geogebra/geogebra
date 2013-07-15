package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.gui.elements.WorksheetHeaderPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HeaderPanel;

public class WorksheetGUI extends HeaderPanel
{

	private Frame content = new Frame();
	private WorksheetHeaderPanel header;

	public WorksheetGUI(AppWeb app, FileManagerM fm)
	{
		// TODO add header!
		this.setStyleName("tubesearchgui");
		this.header = new WorksheetHeaderPanel(app, fm);
		this.setHeaderWidget(this.header);
		this.content.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		this.setContentWidget(this.content);
	}

	public void loadWorksheet(Material m)
	{
		if (m.getId() > 0)
		{
			this.content.setUrl("http://geogebratube.org/student/m" + m.getId() + "?mobile=true");
			this.header.setMaterial(m);
		}
		else
		{
			// local materials not possible ATM
		}
		App.debug("loading" + m.getTitle());
	}
}
