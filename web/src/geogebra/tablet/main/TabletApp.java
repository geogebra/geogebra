package geogebra.tablet.main;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.FileManagerI;
import geogebra.html5.util.ArticleElement;
import geogebra.tablet.TabletFileManager;
import geogebra.tablet.gui.TabletGuiManager;
import geogebra.touch.main.AppT;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;

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
    public FileManagerI getFileManager() {
		if (this.fm == null) {
			this.fm = new TabletFileManager(this);
		}
		return this.fm;
	}
	
	@Override
	public void copyEVtoClipboard() {
		copyEVtoClipboard(getEuclidianView1());
	}
	
	@Override
	public void copyEVtoClipboard(EuclidianViewW ev) {
		String image = ev.getExportImageDataUrl(3, true);
		String title = getKernel().getConstruction().getTitle();
		title = "".equals(title) ? "GeoGebraImage" : title;
		nativeShare(image, title);
	}
	
	native void nativeShare(String base64, String title)/*-{
		if($wnd.android){
			$wnd.android.share(base64,title,'png');
		}
}-*/;
}
