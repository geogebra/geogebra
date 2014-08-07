package geogebra.phone.gui.views.browseView;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.main.AppWeb;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;

public class MaterialListElementP extends MaterialListElement {

	public MaterialListElementP(final Material m, final AppWeb app) {
		super(m, app);
	}

	/**
	 * we don't need arrowPanel in phone
	 */
	@Override
    protected void addArrowPanel() {
	  
    }
	
	@Override
	protected void closeBrowseView() {
	    Phone.getGUI().scrollTo(View.Graphics);
    }
	
	@Override
	protected void markSelected() {
		this.isSelected = true;
		
		Phone.getGUI().getBrowseViewPanel().unselectMaterials();
		Phone.getGUI().getBrowseViewPanel().rememberSelected(this);
		
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
	}
}
