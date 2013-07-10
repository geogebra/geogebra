package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.elements.ggt.SaveMaterialPanel;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveGUI extends VerticalPanel {
	private SaveBar saveBar;
	private VerticalMaterialPanel filePanel;
	private FileManagerM fm;
	private TouchApp app;
	public SaveGUI(TouchApp app, FileManagerM fm){
		this.setStyleName("tubesearchgui");
		this.saveBar = new SaveBar(fm, app);
		this.filePanel = new SaveMaterialPanel(app, fm, this.saveBar);
		this.fm = fm;
		this.app = app;
		this.add(this.saveBar);
		this.add(this.filePanel);
	}
	
	public void reloadFiles(){
		List<Material> files = this.fm.getAllFiles();
		if(!this.fm.hasFile(this.app.getConstructionTitle())){
			Material unsaved = new Material(0,MaterialType.ggb);
			unsaved.setTitle(this.app.getConstructionTitle());
			unsaved.setURL(this.app.getConstructionTitle());
			files.add(0, unsaved);
		}
		
		this.filePanel.setMaterials(2, files);
		this.filePanel.markByURL(this.app.getConstructionTitle());
	}
}
