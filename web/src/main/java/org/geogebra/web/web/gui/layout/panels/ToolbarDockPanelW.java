package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Laszlo Gal
 *
 */
public class ToolbarDockPanelW extends DockPanelW {


	private ToolbarPanel toolbar;
	private boolean toolMode;

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
		int activePerspective = this.app.getActivePerspective();

		if (activePerspective == Perspective.GRAPHER_3D - 1) {
			app.getSettings().getToolbarSettings().setType(ToolCategorization.AppType.GRAPHER_3D);
		}
		if (activePerspective == Perspective.GEOMETRY - 1) {
			app.getSettings().getToolbarSettings().setType(ToolCategorization.AppType.GEOMETRY_CALC);
			app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.EMPTY_CONSTRUCTION);
			app.getKernel().setAlgebraStyle(Kernel.ALGEBRA_STYLE_DESCRIPTION);
		}
		if (activePerspective == Perspective.GRAPHING - 1) {
			app.getKernel()
					.setAlgebraStyle(Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE);
		}
		app.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);
		toolbar = new ToolbarPanel(app);
		setToolMode(toolMode);
		return toolbar;
	}
	
	@Override
	public void onResize() {
		super.onResize();

		if (toolbar != null) {
			toolbar.resize();
		}

	}

	@Override
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				toolbar.resize();
			}
		});
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
		if (toolbar != null) {
			toolbar.scrollAVToBottom();
		}
	}

	@Override
	public void setToolMode(boolean toolMode) {
		if (toolbar != null) {
			// open with false: no fading here.
			if (toolMode) {
				toolbar.openTools(false);
			} else {
				toolbar.openAlgebra(false);
			}
			toolbar.updateHeader();
		}
		this.toolMode = toolMode;
	}

	public boolean isToolMode() {
		return toolMode;
	}

	@Override
	public void setLabels() {
		if (toolbar != null) {
			toolbar.setLabels();
		}
	}

}
