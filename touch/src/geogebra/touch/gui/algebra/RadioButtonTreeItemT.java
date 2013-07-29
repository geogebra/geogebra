package geogebra.touch.gui.algebra;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.safehtml.shared.SafeUri;

public class RadioButtonTreeItemT extends RadioButtonTreeItem {
  private final TouchController controller;
  private long lastClick = -1;

  public static final int TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK = 500;

  public RadioButtonTreeItemT(GeoElement ge, SafeUri showUrl, SafeUri hiddenUrl, MouseDownHandler mdh, TouchController controller) {
    super(ge, showUrl, hiddenUrl, mdh);
    this.controller = controller;
  }

  @Override
  public void onClick(ClickEvent evt) {
    if (System.currentTimeMillis() - this.lastClick < TIME_BETWEEN_CLICKS_FOR_DOUBLECLICK) {
      // doubleClick
      this.controller.redefine(this.getGeo());
    } else {
      // first click or single click
      final Hits hits = new Hits();
      hits.add(this.getGeo());
      this.controller.handleEvent(hits);
    }
    this.lastClick = System.currentTimeMillis();
  }

  @Override
  public void onDoubleClick(DoubleClickEvent evt) {
    // done in the onClick
  }

  @Override
  public void onMouseMove(MouseMoveEvent evt) {
    // don't do anything
  }
}
