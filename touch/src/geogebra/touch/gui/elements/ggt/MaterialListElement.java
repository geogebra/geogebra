package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
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

	MaterialListElement(final Material m, final AppWeb app,
			final VerticalMaterialPanel vmp) {

		sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS);
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
		if (!this.isLocalFile()) {
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
		if (!this.isLocalFile()) {
			this.sharedBy = new Label(app.getLocalization().getPlain(
					"SharedByA", m.getAuthor()));
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);
		}

		this.links.setStyleName("fileLinks");
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

	void onEdit() {
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

	void onOpen() {
		TouchEntryPoint.showWorksheetGUI(this.material);
	}

	void markSelected() {
		this.vmp.unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.vmp.rememberSelected(this);
	}

	protected void markUnSelected() {
		this.removeStyleName("selected");
		this.links.setVisible(false);
	}

	void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}

	private boolean isLocalFile() {
		return this.material.getId() <= 0;
	}

	// FIXME change FastClick
	// used to handle double clicks
	// code triplicated (FastButton, RadioButtonTreeItemT, MaterialListElement
	// find solution for this!
	private static final long TIME_BETWEEN_CLICKS = 500;
	private boolean touchHandled, clickHandled, touchMoved;
	private int touchId;
	private long lastEvent = -1;

	@Override
	public void onBrowserEvent(final Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			touchStart(event);
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			break;
		}
		case Event.ONTOUCHMOVE: {
			touchMove(event);
			break;
		}
		case Event.ONCLICK: {
			click(event);
			break;
		}
		default: {
			// Let parent handle event if not one of the above (?)
			super.onBrowserEvent(event);
		}
		}
	}

	private void click(final Event event) {
		event.stopPropagation();
		event.preventDefault();

		if (this.touchHandled) {
			// if the touch is already handled, we are on a device that supports
			// touch (so you aren't in the desktop browser)

			this.touchHandled = false; // reset for next press
			this.clickHandled = true; // ignore future ClickEvents
		} else if (!this.clickHandled) {
			// Press not handled yet
			handleClick();
		}

		super.onBrowserEvent(event);
	}

	private void touchStart(final Event event) {

		// Stop the event from bubbling up
		event.stopPropagation();

		// Only handle if we have exactly one touch
		if (event.getTargetTouches().length() == 1) {
			final Touch start = event.getTargetTouches().get(0);
			this.touchId = start.getIdentifier();
			this.touchMoved = false;
		}
	}

	/**
	 * Check to see if the touch has moved off of the element.
	 * 
	 * NOTE that in iOS the elasticScroll may make the touch/move cancel more
	 * difficult.
	 * 
	 * @param event
	 */
	private void touchMove(final Event event) {

		if (!this.touchMoved) {
			Touch move = null;

			for (int i = 0; i < event.getChangedTouches().length(); i++) {
				if (event.getChangedTouches().get(i).getIdentifier() == this.touchId) {
					move = event.getChangedTouches().get(i);
				}
			}

			// Check to see if we moved off of the original element

			// Use Page coordinates since we compare with widget's absolute
			// coordinates
			if (move != null) {
				final int yCord = move.getPageY();
				final int xCord = move.getPageX();

				// is y above element
				final boolean yTop = this.getAbsoluteTop() > yCord;
				final boolean yBottom = (this.getAbsoluteTop() + this
						.getOffsetHeight()) < yCord; // y below

				// is x to the left of element
				final boolean xLeft = this.getAbsoluteLeft() > xCord;
				final boolean xRight = (this.getAbsoluteLeft() + this
						.getOffsetWidth()) < xCord; // x to the right

				if (yTop || yBottom || xLeft || xRight) {
					this.touchMoved = true;
				}
			}
		}
	}

	private void onTouchEnd(final Event event) {
		if (!this.touchMoved) {
			this.touchHandled = true;
			handleClick();
			event.preventDefault();
		}
	}

	private void handleClick() {
		final long currentTime = System.currentTimeMillis();

		if (currentTime - this.lastEvent < TIME_BETWEEN_CLICKS) {
			if (this.isLocalFile()) {
				onEdit(); // tabletGUI
			} else {
				onOpen(); // WorksheetGUI
			}
		} else {
			this.markSelected();
		}
		this.lastEvent = currentTime;
	}
}