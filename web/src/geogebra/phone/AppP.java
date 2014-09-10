package geogebra.phone;

import geogebra.html5.main.FileManagerI;
import geogebra.html5.util.ArticleElement;
import geogebra.touch.main.AppT;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;

public class AppP extends AppT {

	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
            boolean undoActive, int dimension, GLookAndFeel laf) {
	    super(article, geoGebraAppFrame, undoActive, dimension, laf);
    }
	
	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame, int dimension, GLookAndFeel laf) {
		super(article, geoGebraAppFrame, dimension, laf);
	}
	
	
	@Override
    public FileManagerI getFileManager() {
		if (this.fm == null) {
			this.fm = new FileManagerP(this);
		}
		return this.fm;
	}
}
