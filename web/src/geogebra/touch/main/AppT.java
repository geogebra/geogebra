package geogebra.touch.main;

import geogebra.common.main.DialogManager;
import geogebra.html5.gui.History;
import geogebra.html5.main.FileManagerI;
import geogebra.html5.util.ArticleElement;
import geogebra.touch.gui.dialog.DialogManagerT;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplication;

/**
 * App for tablets and phones
 * 
 */
public class AppT extends AppWapplication {

	private History history;

	public AppT(final ArticleElement article,
	        final GeoGebraAppFrame geoGebraAppFrame, final boolean undoActive,
	        final int dimension, final GLookAndFeel laf) {
		super(article, geoGebraAppFrame, undoActive, dimension, laf);
		this.setLabelDragsEnabled(false);
	}

	public AppT(final ArticleElement article,
	        final GeoGebraAppFrame geoGebraAppFrame, final int dimension,
	        final GLookAndFeel laf) {
		super(article, geoGebraAppFrame, dimension, laf);
		this.setLabelDragsEnabled(false);
	}

	public History getHistory() {
		if (this.history == null) {
			this.history = new History();
		}
		return this.history;
	}

	/**
	 * different behavior for phone and tablet
	 * 
	 * @return FileManagerInterface
	 */
	@Override
	public FileManagerI getFileManager() {
		return null;
	}

	@Override
	public DialogManager getDialogManager() {
		if (this.dialogManager == null) {
			this.dialogManager = new DialogManagerT(this);
		}
		return this.dialogManager;
	}
}
