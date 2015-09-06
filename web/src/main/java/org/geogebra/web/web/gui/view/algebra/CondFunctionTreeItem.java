package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.web.html5.gui.util.CancelEvents;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * CondFunRadioButtonTreeItem for creating piecewise functions (conditional
 * functions, .isGeoFunctionConditional()) in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class CondFunctionTreeItem extends RadioTreeItem {

	PushButton pButton;

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public CondFunctionTreeItem(final GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);
		
		pButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_new()));
		pButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_new_hover()));
		pButton.addStyleName("XButtonNeighbour");
		pButton.addStyleName("shown");
		pButton.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent mde) {
				mde.preventDefault();
				mde.stopPropagation();
				addNewRow();
			}
		});
		pButton.addStyleName("MouseDownDoesntExitEditingFeature");
		pButton.addStyleName("BlurDoesntUpdateGUIFeature");

		// basically, everything except onClick,
		// static to prevent more instances
		pButton.addClickHandler(CancelEvents.instance);
		pButton.addDoubleClickHandler(CancelEvents.instance);
		// btnRow.addMouseDownHandler(cancelEvents);
		pButton.addMouseUpHandler(CancelEvents.instance);
		pButton.addMouseMoveHandler(CancelEvents.instance);
		// btnRow.addMouseOverHandler(cancelEvents);
		// pButton.addMouseOutHandler(CancelEvents.instance);

		// do not redefine TouchStartHandlers, as they simulate
		// mouse event handlers, so it would be harmful
	}

	@Override
	public void replaceXButtonDOM(TreeItem item) {
		buttonPanel.add(pButton);
		super.replaceXButtonDOM(item);
	}

	@Override
	protected void maybeSetPButtonVisibility(boolean bool) {
		pButton.setVisible(bool);
	}

	public static GeoFunction createBasic(Kernel kern) {
		boolean oldVal = kern.isUsingInternalCommandNames();
		kern.setUseInternalCommandNames(true);
		GeoElement[] ret = kern.getAlgebraProcessor().processAlgebraCommand(
				"If[x<1,x,x^2]", false);
		kern.setUseInternalCommandNames(oldVal);
		if ((ret != null) && (ret.length > 0) && (ret[0] != null)
				&& (ret[0] instanceof GeoFunction)) {
			return (GeoFunction) ret[0];
		}
		return null;
	}

	public void addNewRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {

				// could probably implement this for non-editing case
				// better (like in MatrixRadioButtonTreeItem),
				// but now it's only used in editing mode anyway
				if (!commonEditingCheck())
					ensureEditing();

				DrawEquationWeb.addNewRowToMatrix(seMayLatex);
			}
		});
	}

	@Override
	protected boolean shouldEditLaTeX() {
		// only allow editing for piecewise functions of variable "x"
		return super.shouldEditLaTeX() && hasXVar();
	}

	protected boolean hasXVar() {
		GeoFunction fun = (GeoFunction) geo;
		if (fun.getFunctionVariables().length == 0) {
			return false;
		} else if (fun.getFunctionVariables()[0] == null) {
			return false;
		}
		if ("x".equals((fun.getFunctionVariables()[0]).getSetVarString())) {
			return true;
		}
		return false;
	}
}
