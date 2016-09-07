package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.util.CancelEvents;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;
import org.geogebra.web.web.gui.util.ButtonPopupMenu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * MatrixRadioButtonTreeItem for creating matrices (2-dimensional lists in the
 * algebra view
 * 
 * File created by Arpad Fekete
 */
public class MatrixTreeItem extends MathQuillTreeItem {

	enum MatrixOps {
		APPEND_ROW, APPEND_COLUMN, REMOVE_LAST_ROW, REMOVE_LAST_COLUMN
	}

	/** plus button */
	PushButton pButton;
	/** Popup for adding rows */
	ButtonPopupMenu specialPopup;

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public MatrixTreeItem(final GeoElement ge) {
		super(ge);

		pButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_matrix_size()));
		pButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_matrix_size_hover()));
		pButton.addStyleName("XButtonNeighbour");
		pButton.addStyleName("shown");
		pButton.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent mde) {
				mde.preventDefault();
				mde.stopPropagation();

				if (specialPopup != null) {
					if (EuclidianStyleBarW.CURRENT_POP_UP != specialPopup
							|| !app.wasPopupJustClosed()) {
						if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
							EuclidianStyleBarW.CURRENT_POP_UP.hide();
						}
						EuclidianStyleBarW.CURRENT_POP_UP = specialPopup;

						app.registerPopup(specialPopup);
						specialPopup.showRelativeTo(pButton);
						specialPopup.getFocusPanel().getElement().focus();
					} else {
						specialPopup.setVisible(false);
						EuclidianStyleBarW.CURRENT_POP_UP = null;
					}
				}
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

		// do not redefine TouchStartHandlers, as they are
		// simulate mouse events!

		specialPopup = new ButtonPopupMenu(app.getPanel()) {
			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);

				// if another button is pressed only the visibility is changed,
				// by firing the event we can react as if it was closed
				CloseEvent.fire(this, this, false);
			}

			@Override
			public void hide() {
				super.hide();
				if (EuclidianStyleBarW.CURRENT_POP_UP.equals(this)) {
					EuclidianStyleBarW.CURRENT_POP_UP = null;
				}
			}
		};
		specialPopup.setAutoHideEnabled(true);
		specialPopup.getPanel().addStyleName("AVmenuListContainer");
		specialPopup.addStyleName("MouseDownDoesntExitEditingFeature");
		specialPopup.addStyleName("BlurDoesntUpdateGUIFeature");

		UnorderedList itemList = new UnorderedList();
		itemList.setStyleName("AVmenuListContent");
		specialPopup.getPanel().add(itemList);

		ListItem actual = new ListItem();
		actual.add(new Image(GuiResources.INSTANCE.algebra_new()));
		actual.add(new Label(loc.getMenu("AddRow")));
		// ClickHandler is okay here, but maybe MouseDownHandler is better?
		actual.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				ce.preventDefault();
				specialPopup.setVisible(false);
				EuclidianStyleBarW.CURRENT_POP_UP = null;

				appendRow();
			}
		}, ClickEvent.getType());
		itemList.add(actual);

		actual = new ListItem();
		actual.add(new Image(GuiResources.INSTANCE.algebra_remove()));
		actual.add(new Label(loc.getMenu("RemoveRow")));
		actual.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				ce.preventDefault();
				specialPopup.setVisible(false);
				EuclidianStyleBarW.CURRENT_POP_UP = null;

				removeLastRow();
			}
		}, ClickEvent.getType());
		itemList.add(actual);

		actual = new ListItem();
		actual.add(new Image(GuiResources.INSTANCE.algebra_new()));
		actual.add(new Label(loc.getMenu("AddColumn")));
		actual.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				ce.preventDefault();
				specialPopup.setVisible(false);
				EuclidianStyleBarW.CURRENT_POP_UP = null;

				appendColumn();
			}
		}, ClickEvent.getType());
		itemList.add(actual);

		actual = new ListItem();
		actual.add(new Image(GuiResources.INSTANCE.algebra_remove()));
		actual.add(new Label(loc.getMenu("RemoveColumn")));
		actual.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				ce.stopPropagation();
				ce.preventDefault();
				specialPopup.setVisible(false);
				EuclidianStyleBarW.CURRENT_POP_UP = null;

				removeLastColumn();
			}
		}, ClickEvent.getType());
		itemList.add(actual);
	}

	@Override
	protected PushButton getPButton() {
		return pButton;
	}

	@Override
	protected void maybeSetPButtonVisibility(boolean bool) {
		pButton.setVisible(bool);
	}

	/**
	 * @param kern
	 *            kernel
	 * @return matrix
	 */
	public static GeoList create2x2IdentityMatrix(Kernel kern) {

		Construction cons = kern.getConstruction();
		// this works in a similar was as AlgoIdentity
		GeoList ret = new GeoList(cons);
		GeoList row = new GeoList(cons);
		row.add(new GeoNumeric(cons, 1));
		row.add(new GeoNumeric(cons, 0));
		ret.add(row);
		row = new GeoList(cons);
		row.add(new GeoNumeric(cons, 0));
		row.add(new GeoNumeric(cons, 1));
		ret.add(row);
		ret.setLabel(ret.getDefaultLabel());
		return ret;
	}

	/**
	 * Appends a new row to the matrix.
	 */
	public void appendRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				changeSize(MatrixOps.APPEND_ROW);
			}
		});
	}

	/**
	 * Removes the last row from the matrix.
	 */
	public void removeLastRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				changeSize(MatrixOps.REMOVE_LAST_ROW);
			}
		});
	}

	/**
	 * Appends a new column to the matrix.
	 */
	public void appendColumn() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				changeSize(MatrixOps.APPEND_COLUMN);
			}
		});
	}

	/**
	 * Removes the last column from the matrix.
	 */
	public void removeLastColumn() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				changeSize(MatrixOps.REMOVE_LAST_COLUMN);
			}
		});
	}

	/**
	 * 
	 * @param op
	 *            matrix operation
	 */
	void changeSize(MatrixOps op) {

		boolean edit = commonEditingCheck();

		if (!edit) {
			ensureEditing();
		}

		switch (op) {
		case APPEND_COLUMN:
			MathQuillHelper.appendColToMatrix(latexItem);

			break;
		case APPEND_ROW:
			MathQuillHelper.appendRowToMatrix(latexItem);

			break;
		case REMOVE_LAST_COLUMN:
			MathQuillHelper.removeLastColFromMatrix(latexItem);
			break;
		case REMOVE_LAST_ROW:
			MathQuillHelper.removeLastRowFromMatrix(latexItem);
			break;
		default:
			break;

		}

		MathQuillHelper.endEditingEquationMathQuillGGB(MatrixTreeItem.this,
				latexItem);

		if (edit) {
			av.startEditing(geo);
		}
	}
}
