package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel
		implements SetLabels {

	static final int MARGIN = 16;
	static final int SPACE_HEIGHT = 172 + 2 * MARGIN;
	private AppW app;
	private Localization loc;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private FlowPanel titlePanel;
	private Label titleLabel;
	// private static final int LABELFONT_SIZE = 16;
	// private AutoCompleteTextFieldW textField;
	// private boolean isTitleSet = false;
	private ContextMenuPagePreview contextMenu;
	private int grabY; // where the user grabbed the card when dragging.
	/**
	 * ggb file
	 */
	protected GgbFile file;
	private Integer lastTop = null;
	

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
	 * @param source to duplicate.
	 * @return The duplicated card.
	 */
	public static PagePreviewCard duplicate(PagePreviewCard source) {
		return new PagePreviewCard(source.app, source.getPageIndex() + 1, source.getFile().duplicate());
	}
	
	private void initGUI() {
		addStyleName("mowPagePreviewCard");

		imagePanel = new FlowPanel();
		imagePanel.addStyleName("mowImagePanel");

		titlePanel = new FlowPanel();
		titlePanel.addStyleName("mowTitlePanel");
		titleLabel = new Label("");
		titlePanel.add(titleLabel);

		contextMenu = new ContextMenuPagePreview(app, this);
		titlePanel.add(contextMenu);

		add(imagePanel);
		add(titlePanel);
		if (StringUtil.empty(image)) {
			updatePreviewImage();
		} else {
			setPreviewImage(image);
		}
		// addTextField();
		updateLabel();
	}

	/*
	 * private void addTextField() { textField =
	 * InputPanelW.newTextComponent(app); textField.setAutoComplete(false);
	 * textField.addFocusListener(new FocusListenerW(this) {
	 * 
	 * @Override protected void wrapFocusLost() { rename(); } });
	 * textField.addKeyHandler(new KeyHandler() {
	 * 
	 * @Override public void keyReleased(KeyEvent e) { if (e.isEnterKey()) {
	 * rename(); } } }); titlePanel.add(textField); }
	 */

	/**
	 * remember if title was renamed
	 */
	/*
	 * protected void rename() { if
	 * (textField.getText().equals(getDefaultLabel())) { isTitleSet = false; }
	 * else { isTitleSet = true; } setTextFieldWidth();
	 * textField.setFocus(false); }
	 */

	/**
	 * using an approximate calculation for text field width.
	 * 
	 */
	/*
	 * private void setTextFieldWidth() { int length = LABELFONT_SIZE *
	 * (textField.getText().length() + 2);
	 * textField.setWidth(Math.max(Math.min(length, 178), 10)); }
	 */


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
				.getExportImageDataUrl(0.5, false));
	}

	/*
	 * private String getDefaultLabel() { return loc.getMenu("page") + " " +
	 * (pageIndex + 1); }
	 */

	private void updateLabel() {
		titleLabel.setText(loc.getMenu("page") + " " + (pageIndex + 1));
		/*
		 * if (!isTitleSet) { textField.setText(getDefaultLabel());
		 * setTextFieldWidth(); }
		 */
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
	 * @param x
	 *            is unused for now.
	 * @param y
	 *            coordinate.
	 */
	public void setDragPosition(int x, int y) {
		lastTop = getAbsoluteTop();
		int top = getTopFromDrag(y);
		getElement().getStyle().setTop(top, Unit.PX);
	}
	
	/**
	 * 
	 * @param y
	 *            the top position of the drag.
	 * @return
	 */
	private int getTopFromDrag(int y) {
		return y - getParent().getAbsoluteTop() - grabY;
	}

	/**
	 * 
	 * @return the bottom of the card.
	 */
	public int getAbsoluteBottom() {
		return getAbsoluteTop() + getOffsetHeight();
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

		boolean hit = x > left && x < right && y > top && y < bottom;
		if (hit) {
			grabY = y - top + 32;
		} else {
			grabY = 0;
		}
		return hit;
	}

	/**
	 * 
	 * @return the middle line of the card horizontally.
	 */
	public int getMiddleX() {
		return getAbsoluteLeft() + getOffsetWidth() / 2;
	}
	
	/**
	 * 
	 * @return the middle line of the card vertically
	 */
	public int getMiddleY() {
		return getAbsoluteTop() + getOffsetHeight() / 2;
	}

	/**
	 * Adds space before the card for animation.
	 * @param down 
	 */
	public void addSpaceTop() {
		getElement().getStyle().setMarginTop(SPACE_HEIGHT, Unit.PX);
		getElement().getStyle().setMarginBottom(MARGIN, Unit.PX);
	}

	/**
	 * Removes space before the card for animation.
	 */
	public void removeSpace() {
		getElement().getStyle().setMarginTop(MARGIN, Unit.PX);
		getElement().getStyle().setMarginBottom(MARGIN, Unit.PX);
	}

	/**
	 * Sets margins for drag animation.
	 * @param  to set.
	 */
	public void setSpaceValue(int value, boolean down) {
		int opposite = SPACE_HEIGHT - value;
		getElement().getStyle().setMarginTop(down ? opposite: value, Unit.PX);
		getElement().getStyle().setMarginBottom(down ? value: opposite, Unit.PX);
	}

	public Integer getLastTop() {
		return lastTop;
	}
}
