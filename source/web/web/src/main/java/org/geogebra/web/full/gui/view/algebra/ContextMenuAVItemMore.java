package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateInputAction;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.DuplicateOutputAction;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.Widget;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels {

	/** visible component */
	protected final GPopupMenuW wrappedPopup;
	/** localization */
	private final Localization loc;
	private final AppWFull mApp;
	private GeoElement geo;
	private final RadioTreeItem item;

	/**
	 * Creates new context menu
	 *
	 * @param item
	 *            application
	 */
	ContextMenuAVItemMore(RadioTreeItem item) {
		mApp = item.getApplication();
		loc = mApp.getLocalization();
		wrappedPopup = new GPopupMenuW(mApp);
		setGeo(item.geo);
		this.item = item;
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getApp() {
		return mApp;
	}

	/**
	 * Rebuild the UI
	 */
	public void buildGUI() {
		wrappedPopup.clearItems();
		List<AlgebraContextMenuItem> actions = GlobalScope.contextMenuFactory
				.makeAlgebraContextMenu(geo, mApp.getKernel().getAlgebraProcessor(),
						getApp().getSubAppCode());
		for (AlgebraContextMenuItem action : actions) {
			addAction(action);
		}
	}

	/**
	 * Sets geo for menu building the menu items
	 * @param geo for
	 */
	public void setGeo(GeoElement geo) {
		this.geo = geo;
		buildGUI();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(Widget source, int x, int y) {
		wrappedPopup.show(source, x, y);
		wrappedPopup.getPopupMenu().focusDeferred();
	}

	private void addAction(final AlgebraContextMenuItem menuItem) {
		AriaMenuItem mi = new AriaMenuItem(menuItem.getLocalizedTitle(loc),
				null, () -> select(menuItem));
		TestHarness.setAttr(mi, "menu" + menuItem.getTranslationKey());
		mi.addStyleName("no-image");
		wrappedPopup.addItem(mi);
	}

	/**
	 * @param menuItem
	 *            action to be executed
	 */
	protected void select(final AlgebraContextMenuItem menuItem) {
		AlgebraProcessor processor = mApp.getKernel().getAlgebraProcessor();
		AlgebraViewW av = mApp.getAlgebraView();
		switch (menuItem) {
		case Statistics:
			executeSuggestion(SuggestionStatistics.get(geo));
			break;
		case Delete:
			deleteItem();
			break;
		case DuplicateInput:
			new DuplicateInputAction(av).execute(geo);
			break;
		case DuplicateOutput:
			new DuplicateOutputAction(av).execute(geo);
			break;
		case Settings:
			mApp.getDialogManager().showPropertiesDialog(new ArrayList<>(List.of(geo)));
			break;
		case SpecialPoints:
			executeSuggestion(SuggestionIntersectExtremum.get(geo));
			break;
		case CreateTableValues:
			 mApp.getGuiManager().showTableValuesView(geo);
			break;
		case RemoveLabel:
			removeLabel();
			break;
		case AddLabel:
			addLabel();
			break;
		case CreateSlider:
			new CreateSlider(processor, new LabelController()).execute(geo);
			break;
		case RemoveSlider:
			new RemoveSlider(processor).execute(geo);
			break;
		case Solve:
			executeSuggestion(SuggestionSolveForSymbolic.isValid(geo)
					? SuggestionSolveForSymbolic.get(geo)
					: SuggestionSolve.get(geo));
			break;
		}
	}

	private void removeLabel() {
		new LabelController().hideLabel(geo);
		geo.removeDependentAlgos();
		mApp.storeUndoInfo();
	}

	private void addLabel() {
		new LabelController().showLabel(geo);
		mApp.storeUndoInfo();
	}

	private void deleteItem() {
		if (geo == null) {
			item.onClear();
		} else {
			mApp.getAlgebraView().resetDataTestOnDelete(geo);
			geo.remove();
			mApp.storeUndoInfo();
		}
	}

	private void executeSuggestion(Suggestion suggestion) {
		if (suggestion != null) {
			suggestion.execute(geo);
		}
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * Adds menu for clearing input.
	 */
	void addClearInputItem() {
		setGeo(null);
	}
}
