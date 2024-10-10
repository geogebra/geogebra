package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.AnimPanel.AnimPanelListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.util.DataTest;
import org.geogebra.web.html5.util.HasDataTest;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Item action bar
 *
 */
public class ItemControls extends FlowPanel
		implements AnimPanelListener, SetLabels, HasDataTest {

	private final RadioTreeItem radioTreeItem;
	private final LatexTreeItemController ctrl;

	/** opens context menu */
	private StandardButton btnMore;

	/** animation controls */
	private AnimPanel animPanel = null;

	private ContextMenuAVItemMore cmMore = null;

	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public ItemControls(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		this.ctrl = radioTreeItem.getLatexController();
		addStyleName("algebraViewObjectStylebar");
		TestHarness.setAttr(this, "algebraViewObjectStylebar");
		buildGUI();
		if (!radioTreeItem.isInputTreeItem() && hasMoreMenu()) {
			add(getMoreButton());
		}
		setLabels();
	}

	/**
	 * @return The more button which opens the context menu.
	 */
	public StandardButton getMoreButton() {
		if (btnMore == null) {
			btnMore = new StandardButton(MaterialDesignResources.INSTANCE.more_vert_black(), 24);
			btnMore.addStyleName("more");
			btnMore.addFastClickHandler((event) -> {
					getController().preventBlur();
					openMoreMenu();
			});
			AriaHelper.setHidden(btnMore, true);
		}

		return btnMore;
	}

	/**
	 * Show the More context menu
	 */
	void openMoreMenu() {
		if (!radioTreeItem.hasMoreMenu()) {
			return;
		}

		cancelEditItem();
		closeBurgerMenu();
		createMoreContextMenu();
		if (radioTreeItem.isInputTreeItem()) {
			showMoreMenuForInput();
		} else {
			showMoreMenu();
		}
	}

	private void showMoreMenuForInput() {
		GeoElement geo = (GeoElement) ctrl.evaluateToGeo();
		if (geo == null || geo.isInTree()) {
			showDeleteItem();
			return;
		}
		ctrl.createGeoFromInput(createOpenMenuCallback());
	}

	/**
	 * @return callback for context menu after geo is created from preview
	 */
	AsyncOperation<GeoElementND[]> createOpenMenuCallback() {
		return obj -> {
			GeoElement geo = null;
			if (obj != null && obj.length == 1)  {
				geo = (GeoElement) obj[0];
			}

			if (geo == null) {
				showDeleteItem();
			} else {
				openMenuFor(geo);
			}
		};
	}

	/**
	 * Adds the delete menu that clears input and show it.
	 */
	void showDeleteItem() {
		cmMore.addClearInputItem();
		showMoreMenu();
	}

	/**
	 * Opens more menu for a particular geo
	 *
	 * @param geo
	 *     element to show the menu for.
	 */
	void openMenuFor(GeoElement geo) {
		radioTreeItem.getAV().openMenuFor(geo);
	}

	private void showMoreMenu() {
		cmMore.show(btnMore, 0, - 8);
	}

	private void cancelEditItem() {
		if (radioTreeItem.isInputTreeItem()) {
			return;
		}

		radioTreeItem.cancelEditing();
	}

	private void closeBurgerMenu() {
		AppWFull app = radioTreeItem.getApplication();
		if (app.isUnbundled()) {
			app.hideMenu();
		}
	}

	private void createMoreContextMenu() {
		if (cmMore != null) {
			cmMore.buildGUI();
			return;
		}

		MenuItemCollection<GeoElement> avMenuItems = radioTreeItem.getApplication()
				.getCurrentActivity().getAVMenuItems(radioTreeItem.getAV());
		cmMore = new ContextMenuAVItemMore(radioTreeItem, avMenuItems);
	}

	/**
	 * @param value
	 *            whether to show animation panel
	 */
	public void showAnimPanel(boolean value) {
		if (hasAnimPanel()) {
			animPanel.setVisible(value);
		}
	}

	private void buildGUI() {
		radioTreeItem.setFirst(radioTreeItem.first);
		clear();
		buildAnimPanel();
	}

	private void buildAnimPanel() {
		if (radioTreeItem.geo != null && radioTreeItem.geo.isAnimatable()
				&& animPanelFits(radioTreeItem.geo)) {
			if (animPanel == null) {
				createAnimPanel();
			}

			add(animPanel);
			updateAnimPanel();
			showAnimPanel(true);
		} else {
			showAnimPanel(false);
		}
	}

	private boolean hasMoreMenu() {
		return radioTreeItem.app.showAlgebraInput();
	}

	/**
	 *
	 */
	protected void createAnimPanel() {
		GeoElement geo = radioTreeItem.geo;
		if (geo.isAnimatable() && animPanelFits(geo)) {
			animPanel = new AnimPanel(radioTreeItem, this);
		} else {
			animPanel = null;
		}
	}

	/**
	 * @param geo
	 *            geo
	 * @return whether we have place for the animation panel: do not show it if
	 *         the user disabled AV slider for given number
	 */
	private boolean animPanelFits(GeoElement geo) {
		if (geo instanceof GeoNumeric) {
			return radioTreeItem.getItemFactory().matchSlider(geo);
		}
		return geo.getKernel()
				.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
				&& !(geo instanceof GeoTurtle);
	}

	/**
	 * Update animation panel
	 */
	public void updateAnimPanel() {
		if (hasAnimPanel()) {
			animPanel.update();
		}
	}

	/**
	 * @return animation panel
	 */
	public AnimPanel getAnimPanel() {
		return animPanel;
	}

	/**
	 * @return whether this is shown (when single geo selected)
	 */
	public boolean update() {
		radioTreeItem.setFirst(radioTreeItem.first);

		if (radioTreeItem.geo == null) {
			return false;
		}
		boolean ret = false;
		if (getController().selectionCtrl.isSingleGeo()
				|| getController().selectionCtrl.isEmpty()) {
			radioTreeItem.setFirst(radioTreeItem.first);
			clear();
			if (radioTreeItem.geo.isAnimatable()
					&& animPanelFits(radioTreeItem.geo)) {
				if (animPanel == null) {
					createAnimPanel();
				}

				add(animPanel);
			}

			if (hasMoreMenu()) {
				add(getMoreButton());
			}

			setVisible(true);

			radioTreeItem.getAV().setActiveTreeItem(radioTreeItem);
			ret = true;
		} else {
			radioTreeItem.getAV().removeCloseButton();
		}

		updateAnimPanel();
		return ret;
	}

	/**
	 * Remove animation panel
	 */
	public void removeAnimPanel() {
		if (hasAnimPanel()) {
			remove(animPanel);
		}
	}

	/**
	 * @return whether animation panel exists
	 */
	public boolean hasAnimPanel() {
		return animPanel != null;
	}

	@Override
	public void setVisible(boolean b) {
		if (getController().isEditing()) {
			return;
		}
		super.setVisible(b);
	}

	/**
	 * Called when item selected, shows the x button in edit mode
	 *
	 * @param value
	 *            whether this is the only selected item
	 */
	public void show(boolean value) {
		super.setVisible(true); // radioTreeItem.app.has(Feature.AV_ITEM_DESIGN)
	}

	/**
	 * Shows/Hides 3-dot button
	 *
	 * @param visible
	 *            whether to show it
	 *
	 */
	public void setMoreButtonVisible(boolean visible) {
		if (btnMore == null) {
			return;
		}
		Dom.toggleClass(btnMore, "hidden", !visible);
	}

	/**
	 * Update position
	 */
	public void reposition() {

		Scheduler.get().scheduleDeferred(() -> {
			int right = getItemRightOffset();
			getElement().getStyle().setRight(right, Unit.PX);
		});
	}

	/**
	 * @return distance of item's right border from AV right border
	 */
	protected int getItemRightOffset() {
		int itemWidth = radioTreeItem.getItemWidth();
		int avWidth = radioTreeItem.getAV().getOffsetWidth();
		return Math.max(0, itemWidth - avWidth);
	}

	/**
	 * @return controller
	 */
	RadioTreeItemController getController() {
		return this.radioTreeItem.getController();
	}

	@Override
	public void onPlay(boolean show) {
		if (btnMore == null) {
			return;
		}
		Dom.toggleClass(btnMore, "more-hidden", "more", show);
	}

	@Override
	public void setLabels() {
		if (cmMore != null) {
			cmMore.setLabels();
		}
		if (btnMore != null) {
			btnMore.setAltText(this.radioTreeItem.loc.getMenu("Options"));
		}
		if (animPanel != null) {
			animPanel.setLabels(this.radioTreeItem.loc);
		}
	}

	@Override
	public void updateDataTest(int index) {
		DataTest.ALGEBRA_ITEM_MORE_BUTTON.applyWithIndex(btnMore, index);
		if (animPanel != null) {
			animPanel.updateDataTest(index);
		}
	}
}
