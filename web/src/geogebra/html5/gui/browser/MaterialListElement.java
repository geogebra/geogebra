package geogebra.html5.gui.browser;

import geogebra.common.kernel.commands.CmdGetTime;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.View;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends FlowPanel implements ResizeListener {
	private SimplePanel image;
	private VerticalPanel infos;
	private VerticalPanel links;
	private Label title, date;
	private Label sharedBy;
	private final Material material;
	private final AppWeb app;
	
	private HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private boolean isSelected = false;

	//TODO: Translate Insert Worksheet and Edit
	private final StandardButton openButton = new StandardButton(
			BrowseResources.INSTANCE.document_viewer(), "Insert Worksheet");
	private final StandardButton editButton = new StandardButton(
			BrowseResources.INSTANCE.document_edit(), "Edit");
	//Steffi: Delete not needed here
	/*private final StandardButton deleteButton = new StandardButton(
			BrowseResources.INSTANCE.dialog_cancel());*/
	private BrowseGUI bg;

	MaterialListElement(final Material m, final AppWeb app, BrowseGUI bg) {

		this.app = app;
		this.material = m;
		this.bg = bg;
		this.setStyleName("browserFile");

		this.initButtons();
		/*this.initConfirmDeletePanel();*/
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
		/*final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.add(clearPanel);*/

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
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);

		} else {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(http://www.geogebra.org/static/images/logo.png)");
		}
		String format = this.app.getLocalization().isRightToLeftReadingOrder() ? "\\Y "+Unicode.LeftToRightMark+"\\F"+Unicode.LeftToRightMark+" \\j" : "\\j \\F \\Y";
		
		this.date = new Label(CmdGetTime.buildLocalizedDate(format, this.material.getDate(),
				this.app.getLocalization()));
		this.infos.add(this.date);
	}

	/*private void initConfirmDeletePanel() {
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
	}*/

	public String getMaterialTitle() {
		return this.material.getTitle();
	}

	private void initButtons() {
		this.links = new VerticalPanel();
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.links.setStyleName("fileLinks");
		
		FlowPanel arrowPanel = new FlowPanel();
		Image arrow = new Image(BrowseResources.INSTANCE.arrow_submenu());
		arrowPanel.add(arrow);
		arrowPanel.setStyleName("arrowPanel");
		
		this.links.add(arrowPanel);

		this.initEditButton();
		this.initOpenButton();
		// remote material should not have this visible
		/*if (this.isLocalFile()) {
			this.initDeleteButton();
		}*/
	}

	// Steffi: Delete not needed here
	/*private void initDeleteButton() {

		this.links.add(this.deleteButton);
		this.deleteButton.addStyleName("delete");
		this.deleteButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				onDelete();
			}
		});
	}*/

	/*void onDelete() {
		this.confirmDeletePanel.setVisible(true);
		this.links.setVisible(false);
	}*/

	private void initEditButton() {
		this.links.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onEdit();
			}
		});
	}

	void onEdit() {
		/* TODO */
		new View(RootPanel.getBodyElement(), app)
		.processFileName("http://www.geogebratube.org/files/material-"
				+ material.getId() + ".ggb");
		app.setUnsaved();
		bg.close();
	}

	private void initOpenButton() {
		this.links.add(this.openButton);
		this.openButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onOpen();
			}
		});
	}

	void onOpen() {
		openTubeWindow(material.getId());
	}

	private native void openTubeWindow(int id)/*-{
		$wnd.open("http://www.geogebratube.org/material/show/id/"+id);
	}-*/;
	
	private void markSelected() {
		this.isSelected = true;
		bg.unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		//this.confirmDeletePanel.setVisible(false);
		bg.rememberSelected(this);
	}

	void onConfirmDelete() {
		/* TODO */
	}

	void onCancel() {
		this.links.setVisible(true);
		/*this.confirmDeletePanel.setVisible(false);*/
	}

	public void markUnSelected() {
		this.isSelected = false;
		this.removeStyleName("selected");
		this.links.setVisible(false);
		/*this.confirmDeletePanel.setVisible(false);*/
	}

	void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}

	private boolean isLocalFile() {
		return this.material.getId() <= 0;
	}

	@Override
	public void onResize() {
		if (Window.getClientWidth() < 780) {
			this.image.addStyleName("scaleImage");
		}
		else {
			this.image.removeStyleName("scaleImage");
		}
	}
}