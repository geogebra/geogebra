package geogebra.tablet.main;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.FileManager;
import geogebra.html5.util.ArticleElement;
import geogebra.tablet.TabletFileManager;
import geogebra.tablet.gui.TabletGuiManager;
import geogebra.touch.main.AppT;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;

public class TabletApp extends AppT {
	
	public TabletApp(final ArticleElement article, final GeoGebraAppFrame geoGebraAppFrame,
            final boolean undoActive, final int dimension, final GLookAndFeel laf) {
	    super(article, geoGebraAppFrame, undoActive, dimension, laf);
    }

	public TabletApp(final ArticleElement article, final GeoGebraAppFrame geoGebraAppFrame, final int dimension, final GLookAndFeel laf) {
		super(article, geoGebraAppFrame, dimension, laf);
	}
	
	/**
	 * @return a GuiManager for GeoGebraWeb
	 */
	@Override
	protected GuiManagerW newGuiManager() {
		return new TabletGuiManager(this);
	}
	
	
	@Override
    public FileManager getFileManager() {
		if (this.fm == null) {
			this.fm = new TabletFileManager(this);
		}
		return this.fm;
	}

}
