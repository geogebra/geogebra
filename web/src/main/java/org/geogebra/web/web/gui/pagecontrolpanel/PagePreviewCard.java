package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;

import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel implements SetLabels {

	private AppW app;
	private Localization loc;
	private EuclidianView view;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private FlowPanel titlePanel;
	private Label title;
	private MyToggleButton moreBtn;

	private ContextMenuPagePreview contextMenu = null;

	/**
	 * @param view
	 *            associated view
	 * @param pageIndex
	 *            current page index
	 */
	public PagePreviewCard(EuclidianView view, int pageIndex) {
		// TODO EuclidianView is used for testing, might have to be changed when
		// full functionality will be implemented
		this.view = view;
		this.pageIndex = pageIndex;
		this.app = (AppW) view.getApplication();
		this.loc = app.getLocalization();
		initGUI();
	}

	private void initGUI() {
		addStyleName("mowPagePreviewCard");

		imagePanel = new FlowPanel();
		imagePanel.addStyleName("mowImagePanel");

		titlePanel = new FlowPanel();
		titlePanel.addStyleName("mowTitlePanel");
		title = new Label();
		titlePanel.add(title);

		add(imagePanel);
		add(titlePanel);

		setPreviewImage();
		setDefaultLabel();
		addMoreButton();
	}

	private void setPreviewImage() {
		image = ((EuclidianViewWInterface) view).getExportImageDataUrl(0.2,
				false);

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
		setPreviewImage();
	}

	private void setDefaultLabel() {
		title.setText(loc.getMenu("page") + " " + (pageIndex + 1));
	}
	private void addMoreButton(){
		if (moreBtn == null) {
			moreBtn = new MyToggleButton(
					new Image(
					new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE.more_vert_black()
									.getSafeUri(),
									0, 0, 24, 24, false, false)),
					app);
		}
		moreBtn.getUpHoveringFace()
				.setImage(
						new Image(new ImageResourcePrototype(null,
								MaterialDesignResources.INSTANCE
										.more_vert_mebis().getSafeUri(),
								0, 0, 24, 24, false, false)));
		moreBtn.addStyleName("mowMoreButton");
		ClickStartHandler.init(moreBtn, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				showContexMenu();
			}

		});
		titlePanel.add(moreBtn);
	}
	
	public void rename(String title) {
		this.title.setText(title);
	}

	protected void showContexMenu() {
		if (contextMenu == null) {
			contextMenu = new ContextMenuPagePreview(app, this);
		}
		contextMenu.show(moreBtn.getAbsoluteLeft(), moreBtn.getAbsoluteTop() - 8);
	}
	
	/**
	 * @return the page that is associated with this preview card
	 */
	public EuclidianView getAssociatedView() {
		return view;
	}

	public void setLabels() {
		if (moreBtn != null) {
			moreBtn.setAltText(loc.getMenu("Options"));
		}
	}
}
