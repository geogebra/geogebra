package geogebra.phone;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.util.ArticleElement;
import geogebra.touch.FileManager;
import geogebra.touch.main.AppT;
import geogebra.web.gui.app.GeoGebraAppFrame;

public class AppP extends AppT {

	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
            boolean undoActive, int dimension, GLookAndFeel laf) {
	    super(article, geoGebraAppFrame, undoActive, dimension, laf);
    }
	
	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame, int dimension, GLookAndFeel laf) {
		super(article, geoGebraAppFrame, dimension, laf);
	}
	
	
	@Override
    public FileManager getFileManager() {
		if (this.fm == null) {
			this.fm = new FileManagerP();
		}
		return this.fm;
	}
}
