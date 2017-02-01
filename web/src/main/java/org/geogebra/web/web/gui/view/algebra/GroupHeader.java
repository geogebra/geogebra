package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.main.SelectionManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * AV group header
 */
public class GroupHeader extends FlowPanel {
	
	/**
	 * label
	 */
	protected GroupNameLabel il;

	/**
	 * +/- button
	 */
	protected OpenButton open;
	private String label;

	/**
	 * @param selection
	 *            selection manager
	 * @param parent
	 *            parent item
	 * @param strlab
	 *            localized name
	 * @param key
	 *            english name (for sorting)
	 * @param showUrl
	 *            image when open
	 * @param hiddenUrl
	 *            image when collapsed
	 */
	public GroupHeader(SelectionManager selection, TreeItem parent,
			String strlab, String key, SafeUri showUrl, SafeUri hiddenUrl) {
		
		this.setStyleName("elemHeading");
		this.label = key;
		
		add(open = new OpenButton(showUrl, hiddenUrl, parent));
		add(il = new GroupNameLabel(selection, parent, strlab));
	}
	
	/**
	 * Toggle button connected to tree item state
	 *
	 */
	public static class OpenButton extends SimplePanel
	{
		private SafeUri showUrl, hiddenUrl;
		private Image img;

		/**
		 * @param showUrl
		 *            image for open button
		 * @param hiddenUrl
		 *            image for close button
		 * @param ti
		 *            parent item
		 */
		public OpenButton(SafeUri showUrl,SafeUri hiddenUrl,final TreeItem ti){
			this.showUrl = showUrl;
			this.hiddenUrl = hiddenUrl;
			
			addDomHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					boolean open = ti.getState();
					ti.setState(!open);
					setChecked(!open);
				}
			}, ClickEvent.getType());
			setChecked(true);
		}

		/**
		 * set background-images via HTML
		 * 
		 * @param url
		 *            image url
		 */
		public void setImage(String url)
		{
			//String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
			if(img == null){
				img = new Image(url);
				this.add(img);
			}else{
				img.setUrl(url);
			}
		}

		/**
		 * @param value
		 *            whether it's open
		 */
		public void setChecked(boolean value)
		{
			if (value)
			{
				setImage(showUrl.asString());
				this.setStyleName("arrowBottom");
			}
			else
			{
				setImage(hiddenUrl.asString());
				this.setStyleName("arrowLeft");
			}
			this.getElement().addClassName("algebraOpenButton");
		}

	}

	/**
	 * @param string
	 *            set group name
	 */
	public void setText(String string) {
	    il.setText(string);
	    
    }

	/**
	 * @param value
	 *            whether it's open
	 */
	public void setChecked(boolean value) {
		open.setChecked(value);
	}

	/**
	 * @return sort key
	 */
	public String getLabel() {
		return label;
	}
}
