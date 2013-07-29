package geogebra.touch.gui.elements;

import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class ProgressIndicator extends PopupPanel {

  private static Image progressIndicator = new Image(DefaultResources.INSTANCE.progressIndicator());

  public ProgressIndicator() {
    super(false);
    this.setWidget(ProgressIndicator.progressIndicator);
  }

  @Override
  public void show() {
    super.show();
    this.center();
  }
}
