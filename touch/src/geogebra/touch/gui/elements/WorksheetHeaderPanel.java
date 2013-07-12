package geogebra.touch.gui.elements;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class WorksheetHeaderPanel extends AuxiliaryHeaderPanel {
	
	Material material;

	public WorksheetHeaderPanel(final AppWeb app,final FileManagerM fm) {
		super("", app.getLocalization());
		//TODO replace with icon
		Button edit = new Button("EDIT");
		this.queryPanel.add(edit);
		edit.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				if(WorksheetHeaderPanel.this.material!=null){
					fm.getMaterial(WorksheetHeaderPanel.this.material, app);
					TouchEntryPoint.showTabletGUI();
				}
				
			}
		}
		);
	}
	
	public void setMaterial(Material m){
		setText(m.getTitle());
		this.material = m;
	}

}
