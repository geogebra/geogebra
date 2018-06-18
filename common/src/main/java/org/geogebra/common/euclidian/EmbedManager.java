package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoEmbed;

public interface EmbedManager {
	public void add(DrawEmbed drawEmbed);

	public void update(DrawEmbed drawEmbed);

	public void removeAll();

	/**
	 * @return unused ID for new embed
	 */
	public int nextID();

	/**
	 * Add base64 of embedded files into an archive
	 * 
	 * @param construction
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

	public void backgroundAll();

	public void play(GeoEmbed lastVideo);

}
