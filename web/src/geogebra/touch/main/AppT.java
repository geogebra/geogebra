package geogebra.touch.main;

import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.DialogManager;
import geogebra.html5.gui.History;
import geogebra.html5.main.FileManager;
import geogebra.html5.util.ArticleElement;
import geogebra.touch.gui.DialogManagerT;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplication;

import java.util.ArrayList;

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

	@Override
	protected void afterCoreObjectsInited() {
		super.afterCoreObjectsInited();

		ArrayList<Perspective> list = new ArrayList<Perspective>();
		for (Perspective p : Layout.defaultPerspectives) {
			if (!p.getId().equals("Perspective.3DGraphics")) {
				list.add(p);
			}
		}
		Layout.defaultPerspectives = list.toArray(new Perspective[0]);
		setTmpPerspectives(list);
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
	public FileManager getFileManager() {
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
