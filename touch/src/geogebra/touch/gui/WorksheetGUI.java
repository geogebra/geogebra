package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.gui.elements.WorksheetHeaderPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Label;

public class WorksheetGUI extends HeaderPanel
{
	private Label instructionsPost, instructionsPre;
	private Frame frame = new Frame();
	private WorksheetHeaderPanel header;

	public WorksheetGUI(AppWeb app, FileManagerM fm)
	{
		this.setStyleName("worksheetgui");
		this.header = new WorksheetHeaderPanel(app, fm);
		this.setHeaderWidget(this.header);
		FlowPanel content = new FlowPanel();
		content.setStyleName("worksheet");
		
		this.instructionsPost = new Label();
		this.instructionsPre = new Label();
		this.instructionsPre.setStyleName("instructionsPre");
		this.instructionsPost.setStyleName("instructionsPost");
		
		content.add(this.instructionsPre);
		content.add(this.frame);
		content.add(this.instructionsPost);
		
		this.setContentWidget(content);
	}

	public void loadWorksheet(Material m)
	{
		if (m.getId() > 0)
		{
			this.frame.setUrl("http://www.geogebratube.org/student/e" + 
					m.getId() + "?mobile=true&touch=true&width="+m.getWidth()+"&height="+m.getHeight());
			this.header.setMaterial(m);
			this.frame.setPixelSize(m.getWidth() + 2, m.getHeight() + 2);
			this.instructionsPre.setText(m.getInstructionsPre());
			this.instructionsPost.setText(m.getInstructionsPost());
		}
		else
		{
			// local materials not possible ATM
		}
		App.debug("loading" + m.getTitle());
	}

	public void setLabels()
  {
	  this.header.setLabels();
  }
}
