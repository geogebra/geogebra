package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class SaveMaterialListElement extends MaterialListElement {

	private Button save;

	public SaveMaterialListElement(Material m, AppWeb app, FileManagerM fm,
			VerticalMaterialPanel vmp) {
		super(m, app, fm, vmp);
	}

	@Override
	protected void initButtons() {
		this.save = new Button("SAVE");

		this.links.add(this.save);
		this.save.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				if (SaveMaterialListElement.this.material.getId() > 0) {
					// remote save not implemented
				} else {
					SaveMaterialListElement.this.fm.saveFile(
							SaveMaterialListElement.this.material.getURL(),
							SaveMaterialListElement.this.app);
				}
				TouchEntryPoint.showTabletGUI();
			}
		}, ClickEvent.getType());
		
		initDeleteButton();
	}

}
