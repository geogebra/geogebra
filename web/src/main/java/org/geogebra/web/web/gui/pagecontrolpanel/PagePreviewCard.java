package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel implements
TouchStartHandler, TouchMoveHandler, TouchEndHandler, SetLabels {
	private static final int LABELFONT_SIZE = 16;
	private AppW app;
	private Localization loc;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private FlowPanel titlePanel;
	private AutoCompleteTextFieldW textField;
	private boolean isTitleSet = false;
	private MyToggleButton moreBtn;
	private ContextMenuPagePreview contextMenu = null;
	
	/**
	 * ggb file
	 */
	protected GgbFile file;
	
	private HandlerRegistration hrTouchStart=null;
	private HandlerRegistration hrTouchMove=null;
	private HandlerRegistration hrTouchEnd=null;
	private CardListener listener =null;


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
		if (app.has(Feature.MOW_DRAG_AND_DROP_PAGES)) {
			if (Browser.isTabletBrowser()) {
				addTouchStartHandler(this);
				addTouchMoveHandler(this);
				addTouchEndHandler(this);
			} else {
				// no drag handlers.
				// TODO: implement with mouse handlers.
			}
		}
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

		add(imagePanel);
		add(titlePanel);
		if (StringUtil.empty(image)) {
			updatePreviewImage();
		} else {
			setPreviewImage(image);
		}
		addTextField();
		updateLabel();
		addMoreButton();
	}

	private void addTextField() {
		textField = InputPanelW.newTextComponent(app);
		textField.setAutoComplete(false);
		textField.addFocusListener(new FocusListenerW(this) {
			@Override
			protected void wrapFocusLost() {
				rename();
			}
		});
		textField.addKeyHandler(new KeyHandler() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					rename();
				}
			}
		});
		titlePanel.add(textField);
	}

	/**
	 * remember if title was renamed
	 */
	protected void rename() {
		if (textField.getText().equals(getDefaultLabel())) {
			isTitleSet = false;
		} else {
			isTitleSet = true;
		}
		setTextFieldWidth();
		textField.setFocus(false);
	}

	/**
	 * using an approximate calculation for text field width.
	 * 
	 */
	private void setTextFieldWidth() {
		int length = LABELFONT_SIZE * (textField.getText().length() + 2);
		textField.setWidth(Math.max(Math.min(length, 178), 10));
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
				.getExportImageDataUrl(0.2, false));
	}

	private String getDefaultLabel() {
		return loc.getMenu("page") + " " + (pageIndex + 1);
	}

	private void updateLabel() {
		if (!isTitleSet) {
			textField.setText(getDefaultLabel());
			setTextFieldWidth();
		}
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
			contextMenu.show(moreBtn.getAbsoluteLeft() - 134,
				moreBtn.getAbsoluteTop() + 33);
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

	private void addTouchStartHandler(TouchStartHandler handler) {
		hrTouchStart = addDomHandler(handler, TouchStartEvent.getType());
	}

	private void addTouchMoveHandler(TouchMoveHandler handler) {
		hrTouchMove = addDomHandler(handler, TouchMoveEvent.getType());
	}
	
	private void addTouchEndHandler(TouchEndHandler handler) {
		hrTouchEnd = addDomHandler(handler, TouchEndEvent.getType());
	}
	
	public void removeDragNDrop() {
		if (hrTouchStart != null) {
			hrTouchStart.removeHandler();
		}
		
		if (hrTouchMove != null) {
			hrTouchMove.removeHandler();
		}
		
		if (hrTouchEnd != null) {
			hrTouchEnd.removeHandler();
		}
	}

	public CardListener getReorderListener() {
		return listener;
	}


	public void setReorderListener(CardListener reorderListener) {
		this.listener = reorderListener;
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		event.stopPropagation();
		app.getPageController().startDrag(pageIndex);
		setDragPosition(event.getTargetTouches().get(0));
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {

		event.preventDefault();
		event.stopPropagation();
	
		Touch t = event.getTargetTouches().get(0);
		setDragPosition(t);

		int x = t.getClientX();
		int y = t.getClientY();
		app.getPageController().drag(x, y);
		
		
	}


	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		app.getPageController().stopDrag();
		if (listener != null) {
			Touch t = event.getTargetTouches().get(0);
			if (t == null) {
				t = event.getChangedTouches().get(0);
			}
			int x = t.getClientX();
			int y = t.getClientY();
			listener.dropTo(x, y);
		}
	}
	
	private void setDragPosition(Touch t) {
//		int x = getParent().getAbsoluteLeft();
		int y = t.getClientY() - getParent().getAbsoluteTop();
		
	//	getElement().getStyle().setLeft(x, Unit.PX);
		getElement().getStyle().setTop(y - getOffsetHeight() / 2, Unit.PX);
	}

	public boolean isHit(int x, int y) {
		boolean hit = x > getAbsoluteLeft() && y > getAbsoluteTop()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
		
		Log.debug("[DND] hit at (" + x + ", " + y + ") card ("
				+ getAbsoluteLeft() + "," + getAbsoluteTop() + "," +
				getAbsoluteLeft() + getOffsetWidth() + ","  + getAbsoluteTop() + getOffsetHeight() + "): " + hit);
		
		return hit;
	}
}

