package geogebra.touch.gui.elements.header;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultIcons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WorksheetHeaderPanel extends AuxiliaryHeaderPanel {
	Material material;
	
	private static DefaultIcons LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
	private StandardImageButton editButton = new StandardImageButton(LafIcons.document_edit());

	public WorksheetHeaderPanel(final AppWeb app, final FileManagerM fm)
	{
		super("", app.getLocalization());
		this.rightPanel.add(this.editButton);
		this.editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				if (WorksheetHeaderPanel.this.material != null)
				{
					fm.getMaterial(WorksheetHeaderPanel.this.material, app);
					TouchEntryPoint.showTabletGUI();
					TouchEntryPoint.allowEditing(true, WorksheetHeaderPanel.this.material);
				}
			}
		});
	}

	public void setMaterial(Material m)
	{
		setText(m.getTitle());
		this.material = m;
	}

	@Override
  public void setLabels()
	{
		super.setLabels();
	}
	
}
