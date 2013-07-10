package geogebra.touch.gui;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HeaderPanel;

public class WorksheetGUI extends HeaderPanel{
	
	private Frame content = new Frame();
	
	public WorksheetGUI()
	{
		//TODO add header!
		
		this.setContentWidget(this.content);
	}
	
	public void loadWorksheet(String url)
	{
		this.content.setUrl(url);
	}
}
