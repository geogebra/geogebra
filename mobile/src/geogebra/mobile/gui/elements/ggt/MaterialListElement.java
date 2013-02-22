package geogebra.mobile.gui.elements.ggt;

import geogebra.mobile.utils.ggtapi.Material;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.Button;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends HorizontalPanel
{
	private static final String PANEL_HEIGHT = "100px";

	private SimplePanel image, likeIcon;
	private VerticalPanel infos, links;
	private HorizontalPanel sharedPanel, likesPanel;
	private Label title, date, sharedBy, author, likes;
	private Button open;

	public MaterialListElement(Material m)
	{
		this.image = new SimplePanel();
		this.infos = new VerticalPanel();
		this.infos.setSpacing(5);
		this.sharedPanel = new HorizontalPanel();
		this.likesPanel = new HorizontalPanel();
		this.links = new VerticalPanel();

		// TODO Change to icon
		this.open = new Button("OPEN");

		this.setWidth(Window.getClientWidth() + "px");
		this.setHeight(PANEL_HEIGHT);
		this.getElement().getStyle().setBackgroundColor(GeoGebraTubeStyle.InfoBackground);
		this.getElement().getStyle().setBorderColor(GeoGebraTubeStyle.BorderColor);
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(GeoGebraTubeStyle.BorderWidth, Unit.PX);
		this.getElement().getStyle().setMarginTop(5, Unit.PX);
		this.getElement().getStyle().setMarginBottom(5, Unit.PX);

		this.add(this.image);
		this.image.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		this.image.getElement().getStyle().setMarginRight(5, Unit.PX);
		this.image.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		this.image.setSize("100px", "100px");

		this.title = new Label(m.getTitle());
		this.infos.add(this.title);
		this.title.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
		this.title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.title.getElement().getStyle().setColor(GeoGebraTubeStyle.TitleColor);

		this.date = new Label(m.getDate());
		this.infos.add(this.date);
		this.date.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.sharedBy = new Label(GeoGebraTubeStyle.SharedBy);
		this.sharedPanel.add(this.sharedBy);
		this.sharedBy.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.sharedBy.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.author = new Label(m.getAuthor());
		this.sharedPanel.add(this.author);
		this.author.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);
		this.infos.add(this.sharedPanel);

		this.likeIcon = new SimplePanel();
		this.likeIcon.getElement().getStyle().setBackgroundImage("url(http://www.geogebratube.org/images/like-neutral-small.png)");
		this.likesPanel.add(this.likeIcon);
		this.likesPanel.setCellVerticalAlignment(this.likeIcon, HasVerticalAlignment.ALIGN_MIDDLE);
		this.likesPanel.setSpacing(5);
		this.likeIcon.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		this.likeIcon.setSize("12px", "11px");

		this.likes = new Label(String.valueOf(m.getLikes()));
		this.likesPanel.add(this.likes);
		this.likes.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);
		this.infos.add(this.likesPanel);
		this.add(this.infos);
		this.infos.setSize(Window.getClientWidth() - 200 + "px", PANEL_HEIGHT);

		this.links.add(this.open);
		this.links.setCellVerticalAlignment(this.open, HasVerticalAlignment.ALIGN_MIDDLE);
		this.open.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				// TODO Load material for editing
				Window.alert("MATERIAL FOR EDITING!");
			}
		}, ClickEvent.getType());

		this.add(this.links);
		setCellVerticalAlignment(this.links, HasVerticalAlignment.ALIGN_MIDDLE);

		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				// TODO Load worksheet for students
				Window.alert("WORKSHEET FOR STUDENTS!");
			}
		}, ClickEvent.getType());
	}
}
