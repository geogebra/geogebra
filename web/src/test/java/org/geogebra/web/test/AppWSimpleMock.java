package org.geogebra.web.test;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ViewW;

/**
 * Like production, mock async loading of commands
 *
 */
public class AppWSimpleMock extends AppWsimple {
	private ViewW view;

	/**
	 * @param article
	 *            article element
	 * @param frame
	 * 			  GeoGebraFrame
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWSimpleMock(AppletParameters article, GeoGebraFrameW frame,
						  boolean undoActive) {
		super(DomMocker.getGeoGebraElement(), article, frame, undoActive);
	}

	@Override
	public ViewW getViewW() {
		if (view == null) {
			view = new ViewWMock(this);
		}
		return view;
	}

	@Override
	public CommandDispatcherW newCommandDispatcher(Kernel cmdKernel) {
		return new CommandDispatcherWSync(cmdKernel);
	}
}