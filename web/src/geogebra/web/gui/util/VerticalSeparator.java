package geogebra.web.gui.util;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class VerticalSeparator extends Composite {

	private static final long serialVersionUID = 1L;
	private Canvas button;
	private Context2d ctx = null;
	protected HorizontalPanel wrapper = null;

	public VerticalSeparator(int width, int height) {

		button = Canvas.createIfSupported();
		button.setWidth(width + "px");
		button.setHeight(height + "px");
		button.setCoordinateSpaceHeight(height);
		button.setCoordinateSpaceWidth(width);
		ctx = button.getContext2d();

		// prevent double width lines
		ctx.translate(0.5, 0.5);
		
		ctx.clearRect(0, 0, width, height);
		ctx.setLineWidth(1);
		
		ctx.setStrokeStyle("lightGray");
		ctx.beginPath();
		ctx.moveTo(width / 2, 0);
		ctx.lineTo(width / 2, height);
		ctx.closePath();
		ctx.stroke();
		
		ctx.setStrokeStyle("white");
		ctx.beginPath();
		ctx.moveTo(width / 2 + 1, 0);
		ctx.lineTo(width / 2 + 1, height);
		ctx.closePath();
		ctx.stroke();

		wrapper = new HorizontalPanel();
		wrapper.add(button);
		initWidget(wrapper);
	}
}
