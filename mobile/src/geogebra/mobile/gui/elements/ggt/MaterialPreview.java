package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialPreview extends LayoutPanel
{
	private VerticalPanel infos;
	private HorizontalPanel shared;
	private Label title, author, sharedBy;

	public MaterialPreview(Material m)
	{
		this.setWidth("200px");
		this.setHeight("100px");

		this.infos = new VerticalPanel();
		this.infos.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		this.infos.getElement().getStyle().setBackgroundColor(GeoGebraTubeStyle.InfoBackground);
		this.infos.getElement().getStyle().setOpacity(GeoGebraTubeStyle.InfoBackgroundOpacity);
		this.infos.setSize("100%", "50%");

		this.shared = new HorizontalPanel();
		this.shared.setWidth("100%");

		this.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		this.getElement().getStyle().setBorderColor(GeoGebraTubeStyle.BorderColor);
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(GeoGebraTubeStyle.BorderWidth, Unit.PX);
		this.getElement().getStyle().setMarginLeft(5, Unit.PX);
		this.getElement().getStyle().setMarginRight(5, Unit.PX);

		this.title = new Label(m.getTitle());
		this.title.setWordWrap(true);
		this.title.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		this.title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.title.getElement().getStyle().setColor(GeoGebraTubeStyle.TitleColor);

		this.sharedBy = new Label(GeoGebraTubeStyle.SharedBy);
		this.sharedBy.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.sharedBy.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.author = new Label(m.getAuthor());
		this.author.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.infos.add(this.title);
		this.shared.add(this.sharedBy);
		this.shared.add(this.author);
		this.infos.add(this.shared);
		this.add(this.infos);
		this.setWidgetVerticalPosition(this.infos, Alignment.END);
		
		
	}
}
