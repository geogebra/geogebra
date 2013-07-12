package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.touch.gui.elements.AuxiliaryHeaderPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HeaderPanel;

public class WorksheetGUI extends HeaderPanel{
	
	private Frame content = new Frame();
	private AuxiliaryHeaderPanel header;
	
	public WorksheetGUI(Localization loc)
	{
		//TODO add header!
		this.setStyleName("tubesearchgui");
		this.header = new AuxiliaryHeaderPanel("", loc);
		this.setHeaderWidget(this.header);
		this.content.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		this.setContentWidget(this.content);
	}
	
	public void loadWorksheet(Material m)
	{
		if (m.getId() > 0) {
			this.content.setUrl("http://geogebratube.org/student/m"
							+ m.getId()
							+ "?mobile=true");
			this.header.setText(m.getTitle());
		} else {
			//local materials not possible ATM
		}
		App.debug("loading" + m.getTitle());
	}
}
