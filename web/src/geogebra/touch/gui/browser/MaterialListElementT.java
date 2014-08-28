package geogebra.touch.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.Browser;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.browser.BrowseResources;
import geogebra.html5.gui.browser.MaterialListElement;
import geogebra.html5.main.AppWeb;
import geogebra.touch.main.AppT;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class MaterialListElementT extends MaterialListElement {

	private FlowPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;

	
	public MaterialListElementT(final Material m, final AppWeb app) {
	    super(m, app);
    }
	
	private void addDeleteButton() {
		this.deleteButton = new StandardButton(BrowseResources.INSTANCE.document_delete());
		this.infoPanel.add(this.deleteButton);
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
		initConfirmDeletePanel();
	}
	
	
	void onDelete() {
		this.deleteButton.addStyleName("deleteActive");
		this.editButton.setVisible(false);
		this.confirmDeletePanel.setVisible(true);
		this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete_active());
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain("Delete"));
		this.confirm.addStyleName("gwt-Button");
		this.confirm.addStyleName("deleteButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain("Cancel"));
		this.cancel.addStyleName("cancelButton");
		this.cancel.addStyleName("gwt-Button");
		this.cancel.addStyleName("minor");
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onCancel();
			}
		});

		this.confirmDeletePanel = new FlowPanel();
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
		this.infoPanel.add(this.confirmDeletePanel);
	}

	void onConfirmDelete() {
		((AppT) this.app).getFileManager().delete(this.material);
	}

	void onCancel() {
		showDetails(true);
	}
	
	
	@Override
	protected void addOptions() {
		if (this.material.getId() <= 0) {
			addEditButton();
			addDeleteButton();
		} else {
			addViewButton();
			addEditButton();
		}
	}
	
	@Override
	protected void setPicture(final SimplePanel background, String thumb) {
	    if (!isLocalFile()) {
	    	thumb = Browser.normalizeURL(thumb);
	    }
	    background.getElement().getStyle().setBackgroundImage("url(" + thumb + ")");
    }

	@Override
	protected void initMaterialInfos() {
		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
				
		if (!isLocalFile()) {
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
		}
	}
	
	@Override
	protected void openDefault() {
		if (isLocalFile()) {
			onEdit();
		} else {
			onView();
		}
	}
	
	@Override
	protected void addTextInfo() {
		this.infoPanel.add(this.title);
		if(!isLocalFile()) {
			this.infoPanel.add(this.sharedBy);
		}
	}
	
	@Override
	protected void showDetails(final boolean show) {
		if (isLocalFile()) {
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
		} else {
			this.sharedBy.setVisible(true);
			this.viewButton.setVisible(show);
		}
		this.editButton.setVisible(show);
		
		if (show) {
			this.infoPanel.addStyleName("detailed");
		} else {
			this.infoPanel.removeStyleName("detailed");
		}
	}

	@Override
	public void setLabels() {
		this.editButton.setText(app.getLocalization().getMenu("Edit"));
		if (isLocalFile()) {
			this.deleteButton.setText(app.getLocalization().getCommand("Delete"));
			this.cancel.setText(this.app.getLocalization().getPlain("Cancel"));
			this.confirm.setText(this.app.getLocalization().getPlain("Delete"));
		} else {
			this.viewButton.setText(app.getMenu(getInsertWorksheetTitle(material)));
		}
	}
	
	protected boolean isLocalFile() {
		return this.material.getId() <= 0;
	}
}
