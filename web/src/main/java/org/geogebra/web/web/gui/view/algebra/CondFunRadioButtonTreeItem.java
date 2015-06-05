package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.web.html5.gui.util.CancelEvents;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
public class CondFunRadioButtonTreeItem extends RadioButtonTreeItem {

	PushButton xButton;
	PushButton pButton;

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public CondFunRadioButtonTreeItem(final GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super(ge, showUrl, hiddenUrl);

		xButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_delete()));
		xButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_delete_hover()));
		xButton.addStyleName("XButton");
		xButton.addStyleName("shown");
		xButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ge.remove();
				event.stopPropagation();
				// event.preventDefault();
			}
		});

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
		pButton.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent moe) {
				moe.preventDefault();
				moe.stopPropagation();
				((DrawEquationWeb) app.getDrawEquation()).setMouseOut(false);
			}
		});
		// btnRow.addMouseMoveHandler(new MouseMoveHandler() {
		// public void onMouseMove(MouseMoveEvent moe) {
		// moe.preventDefault();
		// moe.stopPropagation();
		// ((DrawEquationWeb) app.getDrawEquation()).setMouseOut(false);
		// }
		// });

		// basically, everything except onClick,
		// static to prevent more instances
		pButton.addClickHandler(CancelEvents.instance);
		pButton.addDoubleClickHandler(CancelEvents.instance);
		// btnRow.addMouseDownHandler(cancelEvents);
		pButton.addMouseUpHandler(CancelEvents.instance);
		pButton.addMouseMoveHandler(CancelEvents.instance);
		// btnRow.addMouseOverHandler(cancelEvents);
		pButton.addMouseOutHandler(CancelEvents.instance);
		pButton.addTouchStartHandler(CancelEvents.instance);
		pButton.addTouchEndHandler(CancelEvents.instance);
		pButton.addTouchMoveHandler(CancelEvents.instance);

		// pButton.addStyleName("RadioButtonTreeItemSpecButton");
		add(xButton);// dirty hack of adding it two times!
		add(pButton);// same...
		// getElement().getParentElement().appendChild(xButton.getElement());
		// getElement().getParentElement().appendChild(pButton.getElement());

		// ihtml.getElement().appendChild(auxPanel.getElement());
	}

	public void replaceXButtonDOM(TreeItem item) {
		// App.debug("replaceXButtonDOM called!!!");
		item.getElement().addClassName("CondFunParent");
		item.getElement().appendChild(xButton.getElement());
		item.getElement().appendChild(pButton.getElement());
		// App.debug("replaceXButtonDOM done.");
	}

	public static GeoFunction createBasic(Kernel kern) {
		boolean oldVal = kern.isUsingInternalCommandNames();
		kern.setUseInternalCommandNames(true);
		GeoElement[] ret = kern.getAlgebraProcessor().processAlgebraCommand(
				"If[x>0,x,0]", false);
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
	public void startEditing() {
		super.startEditing();
		xButton.setVisible(true);
		pButton.setVisible(true);
	}

	@Override
	public boolean stopEditing(String s) {
		xButton.setVisible(false);
		pButton.setVisible(false);
		return super.stopEditing(s);
	}

	@Override
	public void stopEditingSimple(String s) {
		xButton.setVisible(false);
		pButton.setVisible(false);
		super.stopEditingSimple(s);
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
