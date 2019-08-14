package org.geogebra.common.euclidian;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;
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
	public void writeEmbeds(Construction construction, ZipFile f);

	/**
	 * Load all embeds for a slide
	 * 
	 * @param archive
	 *            slide
	 */
	public void loadEmbeds(ZipFile archive);

	/**
	 * Save state of all widgets.
	 */
	void persist();

	/**
	 * Move all embeds to background.
	 */
	public void backgroundAll();

	/**
	 * Activates embedded applet
	 * 
	 * @param embed
	 *            active embed
	 */
	public void play(GeoEmbed embed);

	/**
	 * Removes embedded applet
	 * 
	 * @param drawEmbed
	 *            drawable
	 */
	public void remove(DrawEmbed drawEmbed);

	/**
	 * Add new embed.
	 * 
	 * @param dataUrl
	 *            base64 content
	 */
	public void embed(String dataUrl);

	/**
	 * @param drawEmbed
	 *            applet drawble
	 * @return preview image
	 */
	public MyImage getPreview(DrawEmbed drawEmbed);

	/**
	 * Executes an action in all embedded elements.
	 *
	 * @param action
	 *            event type
	 */
	public void executeAction(EventType action);

	/**
	 * @param action
	 *            event type
	 * @param embedId
	 *            ID of embedded element
	 */
	public void executeAction(EventType action, int embedId);

	/**
	 * Move embeds to chache so that they don't need rebuilding during undo
	 */
	public void storeEmbeds();

	/**
	 * Permanently remove cached embeds
	 */
	public void clearStoredEmbeds();

	/**
	 * opens the  Graspable math tool
	 */
	public void openGraspableMTool();
}
