package org.geogebra.common.euclidian;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.EventType;

/**
 * Updates, adds and removes embedded applets.
 * 
 * @author Zbynek
 */
public interface EmbedManager {
	/**
	 * Add new applet.
	 * 
	 * @param drawEmbed
	 *            embedded applet
	 */
	public void add(DrawEmbed drawEmbed);

	/**
	 * Update an embedded applet.
	 * 
	 * @param drawEmbed
	 *            embedded applet
	 */
	public void update(DrawEmbed drawEmbed);

	/**
	 * Remove all widgets.
	 */
	public void removeAll();

	/**
	 * @return unused ID for new embed
	 */
	public int nextID();

	/**
	 * Add base64 of embedded files into an archive
	 * 
	 * @param construction
	 *            construction
	 * 
	 * @param f
	 *            archive
	 */
	void writeEmbeds(Construction construction, ZipFile f);

	/**
	 * Load all embeds for a slide
	 * 
	 * @param archive
	 *            slide
	 */
	void loadEmbeds(ZipFile archive);

	/**
	 * Save state of all widgets.
	 */
	void persist();

	/**
	 * Move all embeds to background.
	 */
	void backgroundAll();

	/**
	 * Activates embedded applet
	 * 
	 * @param embed
	 *            active embed
	 */
	void play(GeoEmbed embed);

	/**
	 * Removes embedded applet
	 * 
	 * @param drawEmbed
	 *            drawable
	 */
	void remove(DrawEmbed drawEmbed);

	/**
	 * Add new embedded applet and store undo info.
	 * 
	 * @param material
	 *            online material
	 */
	void embed(Material material);

	/**
	 * @param drawEmbed
	 *            applet drawble
	 * @return preview image
	 */
	MyImage getPreview(DrawEmbed drawEmbed);

	/**
	 * Executes an action in all embedded elements.
	 *
	 * @param action
	 *            event type
	 */
	void executeAction(EventType action);

	/**
	 * Move embeds to chache so that they don't need rebuilding during undo
	 */
	void storeEmbeds();

	/**
	 * Permanently remove cached embeds
	 */
	void clearStoredEmbeds();

	/**
	 * opens the  Graspable math tool
	 */
	void openGraspableMTool();

	void initAppEmbed(GeoEmbed ge);

	/**
	 * @param embed drawable
	 * @param layer z-index
	 */
	void setLayer(DrawWidget embed, int layer);

	/**
	 * @param embedID embed ID
	 * @return embed content as JSON
	 */
	String getContent(int embedID);

	/**
	 * @param embedID embed ID
	 * @param content embed content as JSON
	 */
	void setContent(int embedID, String content);

	/**
	 * @param action
	 *            action to be executed
	 * @param id
	 *            embed ID
	 */
	void embeddedAction(EventType action, String id);
}
