package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.gui.SaveBar;

public class SaveMaterialPanel extends VerticalMaterialPanel {

	private SaveBar sb;

	public SaveMaterialPanel(AppWeb app, FileManagerM fm, SaveBar sb) {
		super(app, fm);
		this.sb = sb;
	}

	@Override
	protected MaterialListElement buildListElement(Material m, AppWeb app2,
			FileManagerM fm2) {
		MaterialListElement mle = new SaveMaterialListElement(m, app2, fm2,
				this);
		mle.initButtons();
		return mle;
	}
	
	@Override
	public void rememberSelected(MaterialListElement mle){
		super.rememberSelected(mle);
		this.sb.setTitleText(mle.getMaterialTitle());
	}
	

}
