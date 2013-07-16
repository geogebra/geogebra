package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultIcons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
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
public class MaterialListElement extends FlowPanel {
	public static final int PANEL_HEIGHT = 100;

	private SimplePanel image;
	private VerticalPanel infos;

	protected VerticalPanel links;
	private HorizontalPanel sharedPanel;
	private Label title, date, sharedBy, author;

	private VerticalMaterialPanel vmp;

	Material material;
	AppWeb app;
	FileManagerM fm;
	
	private static DefaultIcons LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
	private StandardImageButton openButton = new StandardImageButton(LafIcons.document_viewer());
	private StandardImageButton editButton = new StandardImageButton(LafIcons.document_edit());
	private StandardImageButton deleteButton = new StandardImageButton(LafIcons.dialog_trash());

	public MaterialListElement(final Material m, final AppWeb app,
		final FileManagerM fm, VerticalMaterialPanel vmp) {
		// TODO set infos alignment
		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.infos = new VerticalPanel();
		//this.infos.setSpacing(5);
		this.infos.setStyleName("fileDescription");
		this.sharedPanel = new HorizontalPanel();
		this.links = new VerticalPanel();
		
		this.vmp = vmp;
		this.app = app;
		this.fm = fm;
		this.material = m;

		this.setStyleName("browserFile");
		
		this.markUnSelected();

		this.add(this.image);
		if (m.getId() > 0) {
			this.image.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		} else {
			this.image.getElement().getStyle().setBackgroundImage("url(" + fm.getThumbnailDataUrl(m.getURL()) + ")");
		}

		this.title = new Label(m.getTitle());
		this.title.setStyleName("fileTitle");
		this.infos.add(this.title);

		this.date = new Label(m.getDate());
		this.infos.add(this.date);
		
		// no shared Panel for local files
		if (MaterialListElement.this.material.getId() > 0){
			this.sharedBy = new Label(GeoGebraTubeStyle.SharedBy);
			this.sharedPanel.add(this.sharedBy);
			this.author = new Label(m.getAuthor());
			this.sharedPanel.add(this.author);
			this.sharedPanel.setStyleName("sharedPanel");
			this.infos.add(this.sharedPanel);
		}
		
		this.add(this.infos);

		this.links.setStyleName("fileLinks");
		
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				MaterialListElement.this.markSelected();
			}
		}, ClickEvent.getType());

		this.add(this.links);
		
		// clearPanel clears flow layout (needed for styling)
		LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.add(clearPanel);
	}

	protected void initButtons() {		
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		initOpenButton();
		
		this.links.add(this.editButton);
		this.editButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				MaterialListElement.this.fm.getMaterial(MaterialListElement.this.material, MaterialListElement.this.app);
				TouchEntryPoint.goBack();
			}
		}, ClickEvent.getType());

		// remote material should not have this visible
		if (MaterialListElement.this.material.getId() <= 0) {
			initDeleteButton();
		}
	}

	private void initOpenButton() {
		
		this.links.add(this.openButton);
		this.openButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				if(MaterialListElement.this.material.getId() > 0){
					TouchEntryPoint.showWorksheetGUI(MaterialListElement.this.material);
				}else{
					MaterialListElement.this.fm.getMaterial(MaterialListElement.this.material, MaterialListElement.this.app);
					TouchEntryPoint.allowEditing(false);
					TouchEntryPoint.goBack();
				}
				
			}
		}, ClickEvent.getType());
		
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

	protected void markSelected() {
		this.vmp.unselectMaterials();
		addStyleName("selected");
		this.links.setVisible(true);
		this.vmp.rememberSelected(this);
	}

	protected void markUnSelected() {
		removeStyleName("selected");
		this.links.setVisible(false);
	}

	public String getMaterialTitle() {
		return this.material.getTitle();
	}
}
