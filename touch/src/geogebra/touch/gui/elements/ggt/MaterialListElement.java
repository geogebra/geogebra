package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends HorizontalPanel {
	private SimplePanel image;
	private VerticalPanel infos;
	private VerticalPanel links;
	private Label title, date;
	private Label sharedBy;
	private final Material material;
	private final AppWeb app;
	private final FileManagerT fm;
	private HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private boolean isSelected = false;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final StandardButton openButton = new StandardButton(
			LafIcons.document_viewer());
	private final StandardButton editButton = new StandardButton(
			LafIcons.document_edit());
	private final StandardButton deleteButton = new StandardButton(
			LafIcons.dialog_trash());

	MaterialListElement(final Material m, final AppWeb app) {

		this.app = app;
		this.material = m;
		this.fm = ((TouchApp) app).getFileManager();
		this.setStyleName("browserFile");

		this.initButtons();
		this.initConfirmDeletePanel();
		this.initMaterialInfos();

		final VerticalPanel centeredContent = new VerticalPanel();
		centeredContent.setStyleName("centeredContent");
		centeredContent.add(this.infos);

		if (this.isLocalFile()) {
			centeredContent.add(this.confirmDeletePanel);
		}
		this.add(centeredContent);
		this.add(this.links);

		// clearPanel clears flow layout (needed for styling)
		final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.add(clearPanel);

		this.markUnSelected();

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				materialSelected();

			}
		}, ClickEvent.getType());
	}

	void materialSelected() {
		if (this.isSelected) {
			if (this.isLocalFile()) {
				onEdit();
			} else {
				onOpen();
			}
		} else {
			this.markSelected();
		}
	}

	private void initMaterialInfos() {
		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.infos = new VerticalPanel();
		this.infos.setStyleName("fileDescription");

		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
		this.infos.add(this.title);

		this.add(this.image);
		if (!this.isLocalFile()) {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(http:" + this.material.getThumbnail() + ")");

			// no shared Panel for local files
			this.sharedBy = new Label(this.app.getLocalization().getPlain(
					"SharedByA", this.material.getAuthor()));
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);

		} else {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url("
									+ this.fm.getThumbnailDataUrl(this.material
											.getURL()) + ")");
		}

		this.date = new Label(DateTimeFormat.getFormat(
				PredefinedFormat.DATE_TIME_MEDIUM).format(
				this.material.getDate()));
		this.infos.add(this.date);
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain(
				"Delete"));
		this.confirm.addStyleName("confirmButton");
		this.confirm.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.stopPropagation();
				onConfirmDelete();
			}
		}, ClickEvent.getType());

		this.cancel = new StandardButton(this.app.getLocalization().getPlain(
				"Cancel"));
		this.cancel.addStyleName("confirmCancelButton");
		this.cancel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.stopPropagation();
				onCancel();
			}
		}, ClickEvent.getType());

		this.confirmDeletePanel = new HorizontalPanel();
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
	}

	public String getMaterialTitle() {
		return this.material.getTitle();
	}

	private void initButtons() {
		this.links = new VerticalPanel();
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.links.setStyleName("fileLinks");

		this.initOpenButton();
		this.initEditButton();
		// remote material should not have this visible
		if (this.isLocalFile()) {
			this.initDeleteButton();
		}
	}

	private void initDeleteButton() {

		this.links.add(this.deleteButton);
		this.deleteButton.addStyleName("delete");
		this.deleteButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.stopPropagation();
				onDelete();
			}
		}, ClickEvent.getType());
	}

	void onDelete() {
		this.confirmDeletePanel.setVisible(true);
		this.links.setVisible(false);
	}

	private void initEditButton() {
		this.links.add(this.editButton);
		this.editButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.stopPropagation();
				onEdit();
			}
		}, ClickEvent.getType());
	}

	void onEdit() {
		this.fm.getMaterial(this.material, this.app);
		TouchEntryPoint.allowEditing(true);
		TouchEntryPoint.goBack();
	}

	private void initOpenButton() {
		this.links.add(this.openButton);
		this.openButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.stopPropagation();
				onOpen();
			}
		}, ClickEvent.getType());
	}

	void onOpen() {
		TouchEntryPoint.showWorksheetGUI(this.material);
	}

	private void markSelected() {
		this.isSelected = true;
		TouchEntryPoint.getBrowseGUI().unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
		TouchEntryPoint.getBrowseGUI().rememberSelected(this);
	}

	void onConfirmDelete() {
		this.fm.delete(this.material.getURL());
	}

	void onCancel() {
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
	}

	public void markUnSelected() {
		this.isSelected = false;
		this.removeStyleName("selected");
		this.links.setVisible(false);
		this.confirmDeletePanel.setVisible(false);
	}

	void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}

	private boolean isLocalFile() {
		return this.material.getId() <= 0;
	}
}