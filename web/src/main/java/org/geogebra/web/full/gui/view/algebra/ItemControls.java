package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSlider;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.full.gui.view.algebra.AnimPanel.AnimPanelListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.GToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.util.CSSAnimation;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Item action bar
 *
 */
public class ItemControls extends FlowPanel
		implements AnimPanelListener, SetLabels {

	private final RadioTreeItem radioTreeItem;

	/** Deletes the whole item */
	private GPushButton btnDelete;

	/** opens context menu */
	private MyToggleButton btnMore;

	/** animation controls */
	private AnimPanel animPanel = null;

	private ContextMenuAVItemMore cmMore = null;
	private SuggestionBar suggestionBar;

	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public ItemControls(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		addStyleName("AlgebraViewObjectStylebar");
		addStyleName("smallStylebar");
		addStyleName("withContextMenu");
		buildGUI();
		if (radioTreeItem.app.has(Feature.AV_INPUT_3DOT)
				|| (!radioTreeItem.isInputTreeItem() && hasMoreMenu())) {
			add(getMoreButton());
			btnMore.setTabIndex(GUITabs.NO_TAB);
		}
		getElement().setTabIndex(GUITabs.NO_TAB);
		setLabels();
	}

	/**
	 * Gets (and creates if there is not yet) the delete button which geo item
	 * can be removed with from AV.
	 *
	 * @return The "X" button.
	 */
	public GPushButton getDeleteButton() {
		if (btnDelete == null) {
			btnDelete = new GPushButton(
					new NoDragImage(MaterialDesignResources.INSTANCE.clear(), 24));
			btnDelete.addStyleName("XButton");
			btnDelete.addStyleName("shown");
			ClickStartHandler.init(btnDelete,
					new ClickStartHandler(false, true) {

						@Override
						public boolean onClickStart(int x, int y,
								PointerEventType type, boolean right) {
							if (!right) {
								getController().removeGeo();
							}
							return true;
						}

						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {
							onClickStart(x, y, type, false);
						}
			});
		}

		return btnDelete;
	}

	/**
	 *
	 * @return The more button which opens the context menu.
	 */
	public GToggleButton getMoreButton() {
		if (btnMore == null) {
			btnMore = new MyToggleButton(
					new NoDragImage(
							MaterialDesignResources.INSTANCE.more_vert_black(),
							24),
					radioTreeItem.app);

			btnMore.setIgnoreTab();

			btnMore.getUpHoveringFace()
					.setImage(new NoDragImage(
							MaterialDesignResources.INSTANCE.more_vert_purple(),
							24));
			btnMore.addStyleName("XButton");
			btnMore.addStyleName("shown");
			btnMore.addStyleName("more");
			ClickStartHandler.init(btnMore, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					showMoreContexMenu();
				}

			});
			AriaHelper.setHidden(btnMore, true);
		}

		return btnMore;
	}

	/**
	 * Show the More context menu
	 */
	protected void showMoreContexMenu() {
		if (!radioTreeItem.hasMoreMenu()) {
			return;
		}

		cancelEditItem();
		closeBurgerMenu();
		createMoreContextMenu();

		cmMore.show(btnMore.getAbsoluteLeft(), btnMore.getAbsoluteTop() - 8);
	}

	private void cancelEditItem() {
		if (radioTreeItem.isInputTreeItem()) {
			return;
		}

		radioTreeItem.cancelEditing();
	}

	private void closeBurgerMenu() {
		AppWFull app = radioTreeItem.getApplication();
		if (app.isUnbundled() && app.isMenuShowing()) {
			app.toggleMenu();
		}
	}

	private void createMoreContextMenu() {
		if (cmMore != null) {
			cmMore.buildGUI();
			return;
		}

		MenuActionCollection<GeoElement> avMenuItems = radioTreeItem.getApplication()
				.getActivity().getAVMenuItems(radioTreeItem.getAV());
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

		if (!hasMoreMenu() && (radioTreeItem.app.isRightClickEnabled()
				|| radioTreeItem.app.showAlgebraInput())) {
			add(getDeleteButton());
		}
	}

	private void buildAnimPanel() {
		if (radioTreeItem.geo != null && radioTreeItem.geo.isAnimatable()
				&& animPanelFits(radioTreeItem.geo)) {
			if (animPanel == null) {
				createAnimPanel();
			}

			add(animPanel);
			reset();
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
	 * @param showX
	 *            whether to show x button
	 * @return whether this is shown (when single geo selected)
	 */
	public boolean update(boolean showX) {
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

			updateSuggestions(radioTreeItem.geo);

			if (showX) {
				add(hasMoreMenu() ? getMoreButton() : getDeleteButton());
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
	 * Add or remove suggestion bar
	 *
	 * @param geo
	 *            geo element (either from AV item or from preview)
	 */
	void updateSuggestions(GeoElement geo) {
		Suggestion sug = radioTreeItem.getSuggestion(geo);
		boolean slider = sug instanceof SuggestionSlider;

		if ((sug != null && geo != null) || slider) {
			if (suggestionBar == null) {
				suggestionBar = new SuggestionBar(radioTreeItem);
			}
			if (!suggestionBar.getStyleName().contains("animating")) {
				suggestionBar.removeStyleName("removing");
				suggestionBar.addStyleName("animating");
			}
			// suggestionBar.addStyleName("add");
			// if (sug.hasMode()) {
			// suggestionBar.setSuggestion(sug, radioTreeItem.app);

			// } else {
				suggestionBar.setSuggestion(sug, radioTreeItem.loc);
			// }
			if (!suggestionBar.isAttached()) {
				add(suggestionBar);
			}
			if (sug instanceof SuggestionSolve) {
				radioTreeItem.getApplication().getKernel().getGeoGebraCAS()
					.initCurrentCAS();
			}
			radioTreeItem.toggleSuggestionStyle(true);
		} else if (suggestionBar != null) {
			radioTreeItem.toggleSuggestionStyle(false);
			suggestionBar.addStyleName("removing");
			suggestionBar.removeStyleName("animating");
			CSSAnimation.runOnAnimation(new Runnable() {

				@Override
				public void run() {
					removeSuggestions();
				}
			}, radioTreeItem.getContentElement(), "noSuggestions");

		}
	}

	/**
	 * Removes the suggestion bar
	 */
	protected void removeSuggestions() {
		remove(suggestionBar);

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
	 * Reset animation panel
	 */
	public void reset() {
		if (hasAnimPanel()) {
			animPanel.reset();
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
		Dom.toggleClass(btnMore, "hidden", !visible);
	}

	/**
	 * Update position
	 */
	public void reposition() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				int right = getItemRightOffset();
				getElement().getStyle().setRight(right, Unit.PX);
			}
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
		if (show) {
			btnMore.removeStyleName("more");
			btnMore.addStyleName("more-hidden");
		} else {
			btnMore.removeStyleName("more-hidden");
			btnMore.addStyleName("more");
		}

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
}