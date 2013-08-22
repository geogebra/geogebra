package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
public class MaterialListElement extends FlowPanel {
	private final SimplePanel image;
	private final VerticalPanel infos;
	private final VerticalPanel links;
	private final Label title, date;
	private Label sharedBy;
	private final VerticalMaterialPanel vmp;
	private final Material material;
	private final AppWeb app;
	private final FileManagerT fm;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final StandardButton openButton = new StandardButton(
			LafIcons.document_viewer());
	private final StandardButton editButton = new StandardButton(
			LafIcons.document_edit());
	private final StandardButton deleteButton = new StandardButton(
			LafIcons.dialog_trash());

	public MaterialListElement(final Material m, final AppWeb app,
			final VerticalMaterialPanel vmp) {

		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.infos = new VerticalPanel();
		this.infos.setStyleName("fileDescription");
		this.links = new VerticalPanel();

		this.vmp = vmp;
		this.app = app;
		this.fm = ((TouchApp) app).getFileManager();
		this.material = m;

		this.setStyleName("browserFile");

		this.markUnSelected();

		this.add(this.image);
		if (m.getId() > 0) {
			this.image.getElement().getStyle()
					.setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		} else {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(" + this.fm.getThumbnailDataUrl(m.getURL())
									+ ")");
		}

		this.title = new Label(m.getTitle());
		this.title.setStyleName("fileTitle");
		this.infos.add(this.title);

		this.date = new Label(m.getDate());
		this.infos.add(this.date);

		// no shared Panel for local files
		if (MaterialListElement.this.material.getId() > 0) {
			this.sharedBy = new Label(app.getLocalization().getPlain(
					"SharedByA", m.getAuthor()));
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);
		}

		this.links.setStyleName("fileLinks");

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				MaterialListElement.this.markSelected();
			}
		}, ClickEvent.getType());

		this.add(this.links);

		this.add(this.infos);

		// clearPanel clears flow layout (needed for styling)
		final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.add(clearPanel);
	}

	public String getMaterialTitle() {
		return this.material.getTitle();
	}

	protected void initButtons() {
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.initOpenButton();
		this.initEditButton();
		// remote material should not have this visible
		if (MaterialListElement.this.material.getId() <= 0) {
			this.initDeleteButton();
		}
	}

	protected void initDeleteButton() {

		this.links.add(this.deleteButton);
		this.deleteButton.addStyleName("delete");
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
	}

	protected void onDelete() {
		this.fm.delete(this.material.getURL());
	}

	private void initEditButton() {
		this.links.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onEdit();
			}
		});
	}

	protected void onEdit() {
		this.fm.getMaterial(this.material, this.app);
		TouchEntryPoint.allowEditing(true);
		TouchEntryPoint.goBack();
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

	protected void onOpen() {
		TouchEntryPoint.showWorksheetGUI(this.material);
	}

	protected void markSelected() {
		this.vmp.unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.vmp.rememberSelected(this);
	}

	protected void markUnSelected() {
		this.removeStyleName("selected");
		this.links.setVisible(false);
	}

	public void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}
}