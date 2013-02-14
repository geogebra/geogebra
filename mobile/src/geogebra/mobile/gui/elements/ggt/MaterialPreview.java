package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;

/**
 * GUI Element showing a Material
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialPreview extends LayoutPanel
{
	private Label title, author, sharedBy;

	public MaterialPreview(Material m)
	{
		this.setWidth("200px");
		this.setHeight("100px");

		this.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		this.getElement().getStyle().setBorderColor("#477aac");
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(2, Unit.PX);
		this.getElement().getStyle().setMarginLeft(5, Unit.PX);
		this.getElement().getStyle().setMarginRight(5, Unit.PX);

		this.title = new Label(m.getTitle());
		this.title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		this.sharedBy = new Label("Shared by:");
		this.sharedBy.setSize("70px", "20px");

		this.author = new Label(m.getAuthor());
		this.author.setWidth("100px");

		this.add(this.title);
		setWidgetLeftRight(this.title, 0.0, Unit.PX, 0.0, Unit.PX);
		setWidgetBottomHeight(this.title, 25.0, Unit.PX, 20.0, Unit.PX);
		this.add(this.sharedBy);
		setWidgetLeftWidth(this.sharedBy, 0.0, Unit.PX, 75.0, Unit.PX);
		setWidgetBottomHeight(this.sharedBy, -1.0, Unit.PX, 20.0, Unit.PX);
		this.add(this.author);
		setWidgetRightWidth(this.author, 0.0, Unit.PX, 125.0, Unit.PX);
		setWidgetTopHeight(this.author, 81.0, Unit.PX, 20.0, Unit.PX);
	}
}
