package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
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
    public static final int PANEL_HEIGHT = 100;

    private final SimplePanel image;
    private final VerticalPanel infos;

    protected VerticalPanel links;
    private final Label title, date;

    private Label sharedBy;

    private final VerticalMaterialPanel vmp;

    Material material;
    AppWeb app;
    FileManagerM fm;

    private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
    private final StandardImageButton openButton = new StandardImageButton(LafIcons.document_viewer());
    private final StandardImageButton editButton = new StandardImageButton(LafIcons.document_edit());
    private final StandardImageButton deleteButton = new StandardImageButton(LafIcons.dialog_trash());

    public MaterialListElement(final Material m, final AppWeb app, VerticalMaterialPanel vmp) {
	// TODO set infos alignment
	this.image = new SimplePanel();
	this.image.addStyleName("fileImage");
	this.infos = new VerticalPanel();
	// this.infos.setSpacing(5);
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
	    this.image.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
	} else {
	    this.image.getElement().getStyle().setBackgroundImage("url(" + this.fm.getThumbnailDataUrl(m.getURL()) + ")");
	}

	this.title = new Label(m.getTitle());
	this.title.setStyleName("fileTitle");
	this.infos.add(this.title);

	this.date = new Label(m.getDate());
	this.infos.add(this.date);

	// no shared Panel for local files
	if (MaterialListElement.this.material.getId() > 0) {
	    this.sharedBy = new Label(app.getLocalization().getPlain("SharedByA", m.getAuthor()));
	    this.sharedBy.setStyleName("sharedPanel");
	    this.infos.add(this.sharedBy);
	}

	// this.add(this.infos);

	this.links.setStyleName("fileLinks");

	this.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
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
	this.deleteButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		event.stopPropagation();
		MaterialListElement.this.fm.delete(MaterialListElement.this.material.getURL());
	    }
	}, ClickEvent.getType());

    }

    private void initEditButton() {
	this.links.add(this.editButton);
	this.editButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		event.stopPropagation();
		MaterialListElement.this.fm.getMaterial(MaterialListElement.this.material, MaterialListElement.this.app);
		TouchEntryPoint.allowEditing(true);
		TouchEntryPoint.goBack();
	    }
	}, ClickEvent.getType());
    }

    private void initOpenButton() {

	this.links.add(this.openButton);
	this.openButton.addDomHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		event.stopPropagation();
		TouchEntryPoint.showWorksheetGUI(MaterialListElement.this.material);
	    }
	}, ClickEvent.getType());

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
	this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA", this.material.getAuthor()));
    }
}
