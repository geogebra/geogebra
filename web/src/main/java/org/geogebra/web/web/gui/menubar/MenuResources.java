package org.geogebra.web.web.gui.menubar;

import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.client.ui.MenuBar.Resources;

public class MenuResources implements Resources {

			@Override
            @ImageOptions(flipRtl = true)
            public ImageResource menuBarSubMenuIcon() {
	            return GuiResources.INSTANCE.menuBarSubMenuIconLTR();
            }
	

}
