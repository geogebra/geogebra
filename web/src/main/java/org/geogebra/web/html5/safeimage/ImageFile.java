package org.geogebra.web.html5.safeimage;

import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;

/**
 * Class to hold image file information.
 *
 * @author laszlo
 */
public class ImageFile {
	private final String fileName;
	private final String content;
	private final FileExtensions extension;

	/**
	 *
	 * @param fileName file name.
	 * @param content the content.
	 */
	public ImageFile(String fileName, String content) {
		this.fileName = fileName;
		this.content = content;
		extension = StringUtil.getFileExtension(fileName);
	}

	/**
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 *
	 * @return the file content
	 */
	public String getContent() {
		return content;
	}

	/**
	 *
	 * @return the file extension
	 */
	public FileExtensions getExtension() {
		return extension;
	}

	/**
	 *
	 * @return if it is a valid image file.
	 */
	public boolean isValid() {
		return extension.isImage();
	}

	/**
	 *
	 * @return if it is a thumbnail image.
	 */
	public boolean isThumbnail() {
		return MyXMLio.XML_FILE_THUMBNAIL.equalsIgnoreCase(fileName);
	}
}