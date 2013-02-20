package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends LayoutPanel
{
	private SimplePanel image;
	private Label title, author, sharedBy;

	public MaterialListElement(Material m)
	{
		this.setWidth(Window.getClientWidth() + "px");
		this.setHeight("100px");

		this.image = new SimplePanel();
		this.image.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		this.image.setWidth("200px");
		this.image.setHeight("100px");

		this.getElement().getStyle().setBorderColor("#477aac");
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(2, Unit.PX);
		this.getElement().getStyle().setMarginTop(5, Unit.PX);
		this.getElement().getStyle().setMarginBottom(5, Unit.PX);

		this.title = new Label(m.getTitle());
		this.sharedBy = new Label("Shared by:");
		this.author = new Label(m.getAuthor());
		this.author.setWidth("100px");

		this.add(this.image);
		this.add(this.title);
		setWidgetRightWidth(this.title, 0.0, Unit.PX, 657.0, Unit.PX);
		setWidgetTopHeight(this.title, 204.0, Unit.PX, 45.0, Unit.PX);
		this.add(this.sharedBy);
		setWidgetLeftWidth(this.sharedBy, 115.0, Unit.PX, 100.0, Unit.PX);
		setWidgetTopHeight(this.sharedBy, 255.0, Unit.PX, 45.0, Unit.PX);
		this.add(this.author);
		setWidgetLeftWidth(this.author, 221.0, Unit.PX, 100.0, Unit.PX);
		setWidgetTopHeight(this.author, 255.0, Unit.PX, 45.0, Unit.PX);

	}
}
