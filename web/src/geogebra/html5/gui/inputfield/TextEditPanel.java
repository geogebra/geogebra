package geogebra.html5.gui.inputfield;

import geogebra.common.main.App;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextEditPanel extends VerticalPanel {

	TextArea ta2;

	RichTextArea ta;
	Formatter f;
	HorizontalPanel toolBar;

	private Button openButton;

	public TextEditPanel() {
		super();
		ta = new RichTextArea();
		// ta.setSize("100%", "14em");

		f = ta.getFormatter();

		createToolBar();
	//	this.add(toolBar);
		this.add(ta);
	}

	public void setText(String text) {
		ta.setText(text);

	}

	public void insertText(String text) {
		f.insertHTML(text);
	}

	public String getText() {
		return ta.getText();
	}

	private String getHTML() {
		return ta.getHTML();
	}

	public RichTextArea getTextArea() {
		return ta;
	}

	private void createToolBar() {
		toolBar = new HorizontalPanel();
		
		
		// Only some test buttons for now .......
		
		
		MyToggleButton2 btn = new MyToggleButton2(
		        AppResources.INSTANCE.format_text_bold(), 18);

		btn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				f.insertHTML("<a>RED</a>");
				App.debug("HTML: " + ta.getHTML());
			}
		});

		MyToggleButton2 btn2 = new MyToggleButton2(
		        AppResources.INSTANCE.format_text_bold(), 18);

		btn2.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				f.insertHTML("<span style=\"border:2px solid black; padding:2px; background-color: lightYellow \">A</span>");
				App.debug("HTML: " + ta.getHTML());
			}
		});

		MyToggleButton2 btn3 = new MyToggleButton2(
		        AppResources.INSTANCE.format_text_bold(), 18);

		btn3.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				f.insertImage("geogebra/web/gui/images/format-text-bold.png");

				f.insertHTML("<input type=\"button\" name=\"name\"value=\"Object\">");

			}
		});

		createPopupButton();
		
		toolBar.add(btn);
		toolBar.add(btn2);
		toolBar.add(btn3);
		toolBar.add(openButton);
	}

	private void createPopupButton() {
		// Create a basic popup widget
		final DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
		simplePopup.ensureDebugId("cwBasicPopup-simplePopup");
		simplePopup.setWidth("150px");
		simplePopup.setWidget(new HTML("Symbols"));

		// Create a button to show the popup
		openButton = new Button("Symbols", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Reposition the popup relative to the button
				Widget source = (Widget) event.getSource();
				int left = source.getAbsoluteLeft() + 10;
				int top = source.getAbsoluteTop() + 10;
				simplePopup.setPopupPosition(left, top);

				// Show the popup
				simplePopup.show();
			}
		});
	}

}
