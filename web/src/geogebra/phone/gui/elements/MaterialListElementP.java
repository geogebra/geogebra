package geogebra.phone.gui.elements;

import geogebra.common.kernel.commands.CmdGetTime;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.browser.BrowseResources;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.main.AppWeb;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MaterialListElementP extends MaterialListElement {
//	private FileManagerT fm;
	private HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;

	public MaterialListElementP(Material m, AppWeb app) {
		super(m, app);
//		this.fm = ((TouchApp) app).getFileManager();
		this.deleteButton = new StandardButton(BrowseResources.INSTANCE.document_delete());
		
		initConfirmDeletePanel();

		if (this.isLocalFile()) {
			this.centeredContent.add(this.confirmDeletePanel);
		}
	}

	@Override
	protected void materialSelected() {
		if (this.isSelected) {
			if (this.isLocalFile()) {
				onEdit();
			} else {
//				onOpen();
			}
		} else {
			this.markSelected();
		}
	}

	@Override
	protected void initMaterialInfos() {
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
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);

		} else {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(" + this.material.getThumbnail() + ")");
		}

		final String format = this.app.getLocalization()
				.isRightToLeftReadingOrder() ? "\\Y " + Unicode.LeftToRightMark
				+ "\\F" + Unicode.LeftToRightMark + " \\j" : "\\j \\F \\Y";

		this.date = new Label(CmdGetTime.buildLocalizedDate(format,
				this.material.getDate(), this.app.getLocalization()));
		this.infos.add(this.date);
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain(
				"Delete"));
		this.confirm.addStyleName("confirmButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain(
				"Cancel"));
		this.cancel.addStyleName("confirmCancelButton");
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onCancel();
			}
		});

		this.confirmDeletePanel = new HorizontalPanel();
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
	}

	@Override
	protected void initButtons() {
		this.links = new VerticalPanel();
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.links.setStyleName("fileLinks");
		this.links.setVisible(false);
		
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
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
	}

	void onDelete() {
		this.confirmDeletePanel.setVisible(true);
		this.links.setVisible(false);
	}

	@Override
	protected void onEdit() {
//		this.fm.getMaterial(this.material, this.app);
		Phone.getGUI().scrollTo(View.Graphics);
	}

	@Override
	protected void markSelected() {
		this.isSelected = true;
		
		Phone.getGUI().getBrowseViewPanel().unselectMaterials();
		Phone.getGUI().getBrowseViewPanel().rememberSelected(this);
		
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
		
	}

	void onConfirmDelete() {
//		this.fm.delete(this.material);
	}

	void onCancel() {
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
	}

	@Override
	public void markUnSelected() {
		super.markUnSelected();
		this.confirmDeletePanel.setVisible(false);
	}

	@Override
	protected void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}

	private boolean isLocalFile() {
		return this.material.getId() <= 0;
	}
}
