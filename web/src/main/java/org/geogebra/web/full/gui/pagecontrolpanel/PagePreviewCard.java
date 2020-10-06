package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel
		implements SetLabels {

	/** Margin of the cards. */
	static final int MARGIN = 16;

	/** Height of a card without margins */
	static final int CARD_HEIGHT = 184;

	/** Height of one card with the bottom margin */
	static final int TOTAL_HEIGHT = CARD_HEIGHT + MARGIN;

	private AppW app;
	private Localization loc;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private CardInfoPanel infoPanel;
	private ContextMenuButtonPreviewCard contextMenu;
	private int grabY; // where the user grabbed the card when dragging.
	/**
	 * ggb file
	 */
	protected GgbFile file;
	
	/**
	 * @param app
	 *            parent application
	 * @param pageIndex
	 *            current page index
	 * @param file
	 *            see {@link GgbFile}
	 */
	public PagePreviewCard(AppW app, int pageIndex, GgbFile file) {
		this.app = app;
		this.pageIndex = pageIndex;
		this.file = file;
		this.loc = app.getLocalization();
		this.image = file.get("geogebra_thumbnail.png");
		initGUI();
	}

	/**
	 * Duplicates card with pageIndex incremented by 1.
	 * 
	 * @param source
	 *            to duplicate.
	 * @param targetID
	 *            ID of the new slide
	 * @return The duplicated card.
	 */
	public static PagePreviewCard pasteAfter(PagePreviewCard source,
			String targetID, String json) {
		GgbFile file = targetID == null ? new GgbFile() : new GgbFile(targetID);
		source.app.getViewW().setFileFromJsonString(json, file);
		return new PagePreviewCard(source.app, source.getPageIndex() + 1,
				file);
	}
	
	private void initGUI() {
		resetTop();
		addStyleName("mowPagePreviewCard");
		if (!(Browser.isMobile())) {
			addStyleName("desktop");
		}

		imagePanel = new FlowPanel();
		imagePanel.addStyleName("mowImagePanel");

		infoPanel = new CardInfoPanel();
		infoPanel.addStyleName("mowTitlePanel");

		contextMenu = new ContextMenuButtonPreviewCard(app, this);
		infoPanel.add(contextMenu);

		add(imagePanel);
		add(infoPanel);
		if (StringUtil.empty(image)) {
			updatePreviewImage();
		} else {
			setPreviewImage(image);
		}
		updateLabel();
	}

	/**
	 * @return ggb file associated to this card
	 */
	public GgbFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            see {@link GgbFile}
	 */
	public void setFile(GgbFile file) {
		this.file = file;
	}

	private void setPreviewImage(String img) {
		image = img;
		if (image != null && image.length() > 0) {
			imagePanel.getElement().getStyle().setBackgroundImage(
					"url(" + Browser.normalizeURL(image) + ")");
		}
	}

	/**
	 * Updates the preview image
	 */
	public void updatePreviewImage() {
		imagePanel.clear();
		setPreviewImage(((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(0.5, false, false));
	}

	private void updateLabel() {
		infoPanel.setCardId(loc.getMenu("page") + " " + (pageIndex + 1));
	}

	/**
	 * get the index of the page
	 * 
	 * @return page index
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * set index of page
	 * 
	 * note: this will also update the title of the page
	 * 
	 * @param index
	 *            new index
	 */
	public void setPageIndex(int index) {
		pageIndex = index;
		updateLabel();
	}

	@Override
	public void setLabels() {
		contextMenu.setLabels();
		updateLabel();
	}

	/**
	 * 
	 * @param top
	 *            to set.
	 */
	public void setTop(int top) {
		getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * @return the top of card without margin.
	 */
	public int getTop() {
		return getElement().getOffsetTop();
	}

	/**
	 * 
	 * @param y
	 *            the top position of the drag.
	 * @return top of the card after drag
	 */
	public int getTopFromDrag(int y) {
		return y - getParent().getAbsoluteTop() - grabY;
	}

	/**
	 * 
	 * @return the bottom of the card.
	 */
	public int getBottom() {
		return getTop() + getOffsetHeight();
	}

	/**
	 * Checks if (x, y) is within the card.
	 * 
	 * @param x
	 *            coordinate to check.
	 * @param y
	 *            coordinate to check.
	 * @return if card was hit at (x, y).
	 */
	public boolean isHit(int x, int y) {
		int left = getAbsoluteLeft();
		int top = getAbsoluteTop();
		int right = left + getOffsetWidth();
		int bottom = top + getOffsetHeight();

		return x > left && x < right && y > top && y < bottom;
	}

	/**
	 * Make card grabbed (by pointer) at y.
	 * 
	 * @param y
	 *            coordinate where user has grabbed the card.
	 */
	public void grabCard(int y) {
		grabY = y - getAbsoluteTop();
	}

	public static int computeTop(int index) {
		return MARGIN + index * TOTAL_HEIGHT;
	}

	public int getComputedTop() {
		return computeTop(getPageIndex());
	}

	public void resetTop() {
		setTop(getComputedTop());
	}

	public static int clampTop(int top, int cardCount) {
		return Math.max(MARGIN, Math.min(top, computeTop(cardCount - 1)));
	}

	/**
	 * 
	 * @return the middle line of the card horizontally.
	 */
	public int getMiddleX() {
		return getAbsoluteLeft() + getOffsetWidth() / 2;
	}

	/**
	 * @param add
	 *            true to apply drag style
	 */
	public void addDragStartStyle(boolean add) {
		if (add) {
			addStyleName("dragCanStart");
		} else {
			removeStyleName("dragCanStart");
		}
	}

	public void setCardTitle(String title) {
		infoPanel.setCardTitle(title);
	}

	public String getCardTitle() {
		return infoPanel.getCardTitle();
	}
}
