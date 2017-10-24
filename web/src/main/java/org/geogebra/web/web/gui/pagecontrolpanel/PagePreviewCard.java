package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel {

	private EuclidianView view;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private FlowPanel titlePanel;
	private Label title;

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
	}

	private void setPreviewImage() {
		image = ((EuclidianViewWInterface) view).getExportImageDataUrl(0.1,
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
		title.setText("Page " + (pageIndex + 1));
	}

	/*
	 * private void rename(String title) { titleLabel.setText(title); }
	 */

	/**
	 * @return the page that is associated with this preview card
	 */
	public EuclidianView getAssociatedView() {
		return view;
	}
}
