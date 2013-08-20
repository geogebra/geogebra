package geogebra.touch.gui.elements.ggt;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileContainer extends VerticalPanel implements ResizeListener {

	private FlowPanel fileControlPanel;
	private final VerticalMaterialPanel filePanel;
	private HorizontalPanel filePages;
	private Label heading = new Label();

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final StandardImageButton prevButton = new StandardImageButton(
			LafIcons.arrow_go_previous());
	private final StandardImageButton nextButton = new StandardImageButton(
			LafIcons.arrow_go_next());

	public FileContainer(String headingName, final VerticalMaterialPanel filePanel) {
		this.filePanel = filePanel;
		this.addHeading(headingName);
		this.add(filePanel);
		this.addPageControl();
	}

	private void addHeading(String headingName) {
		this.heading.setText(headingName);
		this.heading.setStyleName("filePanelTitle");
		this.add(this.heading);
	}
	
	private void addPageControl() {
		// Panel for page controls, with next/prev buttons
		this.fileControlPanel = new FlowPanel();
		this.fileControlPanel.setStyleName("fileControlPanel");

		this.prevButton.addStyleName("prevButton");
		this.prevButton.addStyleName("disabled");
		this.fileControlPanel.add(this.prevButton);
		this.prevButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onSingleClick() {
				onPrevPage();
			}
			
			@Override
			public void onDoubleClick() {
				// TODO Auto-generated method stub
				
			}
		});

		this.filePages = new HorizontalPanel();
		this.filePages.setStyleName("filePageControls");
		// TODO: add number buttons here
		this.fileControlPanel.add(this.filePages);

		this.nextButton.addStyleName("nextButton");
		this.nextButton.addStyleName("disabled");
		this.fileControlPanel.add(this.nextButton);
		this.nextButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onSingleClick() {
				onNextPage();
			}
			
			@Override
			public void onDoubleClick() {
				// TODO Auto-generated method stub
				
			}
		});

		this.add(this.fileControlPanel);
	}

	protected void onPrevPage() {
		this.filePanel.prevPage();
		updateNextPrevButtons();
	}
	
	protected void onNextPage() {
		this.filePanel.nextPage();
		updateNextPrevButtons();
	}
	
	public void updateNextPrevButtons() {
		if (this.filePanel.hasNextPage()) {
			this.nextButton.removeStyleName("disabled");
			this.nextButton.setEnabled(true);
		} else {
			this.nextButton.addStyleName("disabled");
			this.nextButton.setEnabled(false);
		}
		if (this.filePanel.hasPrevPage()) {
			this.prevButton.removeStyleName("disabled");
			this.prevButton.setEnabled(true);
		} else {
			this.prevButton.addStyleName("disabled");
			this.prevButton.setEnabled(false);
		}
	}
	
	public void setHeading(String headingName) {
		this.heading.setText(headingName);
	}

	@Override
	public void onResize() {
		int contentHeight = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getBrowseHeaderHeight();
		this.setHeight(contentHeight + "px");
		this.filePanel.setHeight(contentHeight - BrowseGUI.HEADING_HEIGHT
				- BrowseGUI.CONTROLS_HEIGHT + "px");
		this.updateNextPrevButtons();
	}  
}
