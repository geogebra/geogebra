package geogebra.web.gui.layout.panels;

import geogebra.common.main.Localization;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.AlgebraSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.main.AppW;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class AlgebraStyleBarW extends StyleBarW implements ValueChangeHandler<Boolean>, SettingListener {
	MyToggleButton2 auxiliary;
	private AppW app;
	public AlgebraStyleBarW(AppW app){
		this.app = app;
		auxiliary = new MyToggleButton2(StyleBarResources.INSTANCE.auxiliary());
		auxiliary.setDown(app.showAuxiliaryObjects());
		auxiliary.addValueChangeHandler(this);
		add(auxiliary);
		app.getSettings().getAlgebra().addListener(this);
		setToolTips();
	}
	
	private void setToolTips() {

	    Localization loc = app.getLocalization();
	    auxiliary.setToolTipText(loc.getMenu("auxiliary"));
	}

	@Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
	    if(event.getSource() == auxiliary){
	    	app.setShowAuxiliaryObjects(auxiliary.isDown());
	    }
	    
    }

	@Override
    public void setOpen(boolean showStyleBar) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void settingsChanged(AbstractSettings settings) {
	    AlgebraSettings as = (AlgebraSettings) settings;
	    auxiliary.setDown(as.getShowAuxiliaryObjects());
	    
    }
}
