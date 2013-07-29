package geogebra.touch.gui.elements.stylingbar;

import geogebra.touch.TouchEntryPoint;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OptionsBox extends PopupPanel {
  VerticalPanel optionsWrapper;
  Panel optionsBoxArrowPanel;
  Panel contentPanel;

  OptionsBox(OptionsContent content) {
    this.optionsWrapper = new VerticalPanel();
    this.optionsWrapper.setStyleName("optionsBoxWrapper");

    // Little arrow for options box
    this.optionsBoxArrowPanel = new LayoutPanel();
    final String html = "<img src=\"" + TouchEntryPoint.getLookAndFeel().getIcons().optionsBoxArrow().getSafeUri().asString() + "\" />";
    this.optionsBoxArrowPanel.getElement().setInnerHTML(html);
    this.optionsBoxArrowPanel.setStyleName("optionsBoxArrow");

    // content of the options box
    this.contentPanel = content;
    this.contentPanel.setStyleName("optionsBox");

    // wrapper which contains little arrow and content
    this.optionsWrapper.add(this.optionsBoxArrowPanel);
    this.optionsWrapper.add(this.contentPanel);

    this.add(this.optionsWrapper);
  }
}
