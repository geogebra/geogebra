package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.gui.elements.ggt.SaveMaterialPanel;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveGUI extends VerticalPanel {
	private SaveBar saveBar;
	private VerticalMaterialPanel filePanel;
	private FileManagerM fm;
	public SaveGUI(AppWeb app, FileManagerM fm){
		this.setStyleName("tubesearchgui");
		this.saveBar = new SaveBar(fm, app);
		this.filePanel = new SaveMaterialPanel(app, fm, this.saveBar);
		this.fm = fm;
		this.add(this.saveBar);
		this.add(this.filePanel);
		reloadFiles();
	}
	
	public void reloadFiles(){
		List<Material> files = this.fm.getAllFiles();
		this.filePanel.setMaterials(2, files);
	}
}
