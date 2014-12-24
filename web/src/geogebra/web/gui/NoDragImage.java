package geogebra.web.gui;

import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.user.client.ui.Image;

public class NoDragImage extends Image {
	public NoDragImage(String uri, int width) {
		super(uri);
		this.setWidth(width + "px");
		this.addDragHandler(new DragHandler() {

			@Override
			public void onDrag(DragEvent event) {
				event.preventDefault();
			}
		});
	}
}
