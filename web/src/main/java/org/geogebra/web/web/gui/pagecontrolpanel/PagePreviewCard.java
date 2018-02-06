package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.web.css.MaterialDesignResources;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel
		implements SetLabels {

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
	private MyToggleButton moreBtn;
	private ContextMenuPagePreview contextMenu = null;
	
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

		add(imagePanel);
		add(titlePanel);
		if (StringUtil.empty(image)) {
			updatePreviewImage();
		} else {
			setPreviewImage(image);
		}
		// addTextField();
		updateLabel();
		addMoreButton();
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

	private void addMoreButton(){
		if (moreBtn == null) {
			moreBtn = new MyToggleButton(
					getImage(
							MaterialDesignResources.INSTANCE.more_vert_black()),
					app);
		}
		moreBtn.getUpHoveringFace()
				.setImage(getImage(
						MaterialDesignResources.INSTANCE.more_vert_mebis()));
		moreBtn.addStyleName("mowMoreButton");
		ClickStartHandler.init(moreBtn, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleContexMenu();
			}
		});
		titlePanel.add(moreBtn);
	}
	
	private static Image getImage(SVGResource res) {
		return new NoDragImage(res, 24, 24);
	}

	/**
	 * show context menu of preview card
	 */
	protected void toggleContexMenu() {
		if (contextMenu == null) {
			contextMenu = new ContextMenuPagePreview(app, this);
		}
		if (contextMenu.isShowing()) {
			contextMenu.hide();
			toggleMoreButton(false);
		} else {
			contextMenu.show(moreBtn.getAbsoluteLeft() - 122,
					moreBtn.getAbsoluteTop() + 36);
			toggleMoreButton(true);
		}
	}
	
	private void toggleMoreButton(boolean toggle) {
		if (toggle) {
			moreBtn.getUpFace().setImage(getImage(
					MaterialDesignResources.INSTANCE.more_vert_mebis()));
			moreBtn.addStyleName("active");
		} else {
			moreBtn.getUpFace().setImage(getImage(
					MaterialDesignResources.INSTANCE.more_vert_black()));
			moreBtn.removeStyleName("active");
		}
	}

	@Override
	public void setLabels() {
		if (moreBtn != null) {
			moreBtn.setAltText(loc.getMenu("Options"));
		}
		if (contextMenu != null) {
			contextMenu.setLabels();
		}
		updateLabel();
	}

	/**
	 * @param x
	 *            is unused for now.
	 * @param y
	 *            coordinate.
	 */
	public void setDragPosition(int x, int y) {
		int top = getTopFromDrag(y);

		int left = getAbsoluteLeft() + 10;
		getElement().getStyle().setTop(left, Unit.PX);
		getElement().getStyle().setTop(top, Unit.PX);
	}
	
	/**
	 * 
	 * @param y
	 *            the top position of the drag.
	 * @return
	 */
	private int getTopFromDrag(int y) {
		return y - getParent().getAbsoluteTop() - getOffsetHeight() / 2;
	}

	/**
	 * @param y
	 *            the next drag position.
	 * @return true for drag down, false for up.
	 */
	public boolean getDragDirection(int y) {
		return getAbsoluteTop() < getTopFromDrag(y);
	}

	/**
	 * 
	 * @return the bottom of the card.
	 */
	public int getBottom() {
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

		return hit;
	}

	/**
	 * 
	 * @param y
	 *            to check.
	 * @return true if y is greater than the card middle.
	 */
	public boolean isBellowMiddle(int y) {
		return y > getAbsoluteTop() + getOffsetHeight() / 2;
	}

	/**
	 * Set position of the card as absolute.
	 */
	public void setAbsolutePosition() {
		getElement().getStyle().setPosition(Position.ABSOLUTE);
	}

	/**
	 * Clears position css property.
	 */
	public void clearPosition() {
		getElement().getStyle().clearPosition();
	}

}

