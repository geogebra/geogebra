package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.header.WorksheetHeaderPanel;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Label;

public class WorksheetGUI extends HeaderPanel
{
	private Label instructionsPost, instructionsPre;
	private Frame frame = new Frame();
	private WorksheetHeaderPanel header;
	private AppWeb app;
	private FileManagerM fm;
	private FlowPanel content;
	private EuclidianViewPanel contentLocal;
	TabletGUI tabletGUI;
	
	public WorksheetGUI(AppWeb app, FileManagerM fm, TabletGUI tabletGUI)
	{
		this.setStyleName("worksheetgui");
		this.header = new WorksheetHeaderPanel(app, fm, this, tabletGUI);
		this.setHeaderWidget(this.header);
		this.content = new FlowPanel();
		this.app = app;
		this.fm = fm;
		this.tabletGUI = tabletGUI;
		
		this.instructionsPost = new Label();
		this.instructionsPre = new Label();
		this.instructionsPre.setStyleName("instructionsPre");
		this.instructionsPost.setStyleName("instructionsPost");
	}

	public void loadWorksheet(Material m)
	{
		this.header.setMaterial(m);
		this.contentLocal = this.tabletGUI.getEuclidianViewPanel();
		
		if (m.getId() > 0)
		{
			this.content.add(this.instructionsPre);
			this.content.add(this.frame);
			this.content.add(this.instructionsPost);
			this.setContentWidget(this.content);
			
			TouchEntryPoint.allowEditing(false);
			this.frame.setUrl("http://www.geogebratube.org/student/e" + 
					m.getId() + "?mobile=true&touch=true&width="+m.getWidth()+"&height="+m.getHeight());
			this.frame.setPixelSize(m.getWidth() + 2, m.getHeight() + 2);
			this.instructionsPre.setText(m.getInstructionsPre());
			this.instructionsPost.setText(m.getInstructionsPost());
		}
		else
		{
			TouchEntryPoint.allowEditing(false);
			this.fm.getMaterial(m, this.app);
			this.setContentWidget(this.contentLocal);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			
			@Override
			public void execute()
			{
				WorksheetGUI.this.tabletGUI.updateViewSizes(WorksheetGUI.this.tabletGUI.isAlgebraShowing());
			}
		});
		App.debug("loading" + m.getTitle());
	}

	public void setLabels()
  {
	  this.header.setLabels();
  }
	
	public EuclidianViewPanel getContentLocal()
	{
		return this.contentLocal;
	}
}
