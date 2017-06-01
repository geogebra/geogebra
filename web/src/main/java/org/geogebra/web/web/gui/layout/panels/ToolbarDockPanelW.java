package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Laszlo Gal
 *
 */
public class ToolbarDockPanelW extends DockPanelW {

	private static final int HEIGHT_CLOSED = 57;
	private static final int WIDTH_AUTO_CLOSE = 86;
	private static final int HEIGHT_AUTO_CLOSE = 86;

	private ToolbarPanel toolbar;

	/**
	 * 
	 */
	public ToolbarDockPanelW() {
		super(
				App.VIEW_ALGEBRA, // view id
				"ToolbarWindow", 			// view title phrase
				null,						// toolbar string
				false,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
	}

	@Override
	protected Widget loadComponent() {
		toolbar = new ToolbarPanel(app);
		return toolbar;
	}

	private void resizeView(int w, int h) {
		toolbar.setPixelSize(w, h);
	}

	@Override
	public void onResize() {
/*
		int h1 = getComponentInteriorHeight() - navHeightIfShown();
		int w1 = getComponentInteriorWidth();

		resizeView(w1, h1);

		Log.debug("toolbar onresize");
		Log.debug("toolbar offsetheight before: " + toolbar.getOffsetHeight());
*/
		toolbar.resize();
		if (toolbar.isPortrait()) {
			int h = toolbar.getOffsetHeight();
			if (h > HEIGHT_CLOSED) {
				if (h < HEIGHT_AUTO_CLOSE) {
					toolbar.close();
				} else {
					toolbar.open();
				}
			}
		} else {
			if (toolbar.getOffsetWidth() < WIDTH_AUTO_CLOSE) {
				toolbar.close();
			} else {
				toolbar.open();
			}
		}

		Log.debug2(
				"toolbar offsetheight after: " + toolbar.getOffsetHeight());
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		if (toolbar.isAlgebraViewActive()) {
			return toolbar.getKeyboardListener();
		}

		return super.getKeyboardListener();
	}

	/**
	 * Delegating to toolbar.
	 * 
	 * @param ml
	 *            the litstener
	 * @return the updated listener;
	 */
	public MathKeyboardListener updateKeyboardListener(MathKeyboardListener ml) {
		if (toolbar.isAlgebraViewActive()) {
			return toolbar.updateKeyboardListener(ml);
		}
		return null;
	}

	/**
	 * 
	 * @return the tabbed toolbar.
	 */
	public ToolbarPanel getToolbar() {
		return toolbar;
	}

	/**
	 * Saves the scroll position of algebra view
	 */
	public void saveAVScrollPosition() {
		toolbar.saveAVScrollPosition();
	}

	/**
	 * Scrolls Algebra View to the bottom.
	 */
	public void scrollAVToBottom() {
		toolbar.scrollAVToBottom();
	}

}
