package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.io.file.ZipFile;

public interface EmbedManager {
	public void add(DrawEmbed drawEmbed);

	public void update(DrawEmbed drawEmbed);

	public void removeAll();

	public int nextID();

	/**
	 * Add base64 of embedded files into an archive
	 * 
	 * @param f
	 *            archive
	 */
	public void writeEmbeds(ZipFile f);

	/**
	 * Load all embeds for a slide
	 * 
	 * @param archive
	 *            slide
	 */
	public void loadEmbeds(ZipFile archive);

}
