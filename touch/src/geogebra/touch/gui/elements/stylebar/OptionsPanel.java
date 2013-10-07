package geogebra.touch.gui.elements.stylebar;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.utils.OptionType;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OptionsPanel extends PopupPanel {
	private VerticalPanel contentPanel;
	private FlowPanel optionPanel, arrowPanel;
	private StyleBar styleBar;
	private CaptionBar captionBar;
	private LineStyleBar lineStyleBar;
	private ColorBar colorBar;
	private OptionType type;

	OptionsPanel(StyleBar styleBar) {
		this.styleBar = styleBar;
		this.contentPanel = new VerticalPanel();
		this.contentPanel.setStyleName("optionsBox");

		// Little arrow for options box
		this.arrowPanel = new FlowPanel();
		final String html = "<img src=\""
				+ TouchEntryPoint.getLookAndFeel().getIcons().optionsBoxArrow()
						.getSafeUri().asString() + "\" />";
		this.arrowPanel.getElement().setInnerHTML(html);
		this.arrowPanel.setStyleName("optionsBoxArrow");

		// wrapper which contains little arrow and content
		this.contentPanel.add(this.arrowPanel);

		this.optionPanel = new FlowPanel();
		this.optionPanel.setStyleName("optionsBoxWrapper");

		this.contentPanel.add(this.optionPanel);

		this.setWidget(this.contentPanel);

		this.captionBar = new CaptionBar(this.styleBar.getTouchModel());
		this.colorBar = new ColorBar(this.styleBar,
				this.styleBar.getTouchModel());
		this.lineStyleBar = new LineStyleBar(this.styleBar.getTouchModel());
	}

	OptionsPanel getOptionsPanel(OptionType optionType) {

		this.type = optionType;
		this.contentPanel.remove(this.optionPanel);

		switch (optionType) {
		case CaptionStyle:
			this.optionPanel = this.captionBar;
			break;
		case Color:
			this.colorBar.update();
			this.optionPanel = this.colorBar;
			break;
		case LineStyle:
			this.lineStyleBar.update();
			this.optionPanel = this.lineStyleBar;
			break;

		case ToolBar:

		case None:
		default:
			break;
		}

		this.contentPanel.add(this.optionPanel);
		return this;
	}

	public OptionType getType() {
		return this.type;
	}

	public int getWidth() {
		switch (this.type) {
		case CaptionStyle:
			return 190;
		case Color:
			return 200;
		case LineStyle:
			return 250;
		case None:
			break;
		case ToolBar:
			break;
		default:
			break;

		}
		return 0;
	}

}
