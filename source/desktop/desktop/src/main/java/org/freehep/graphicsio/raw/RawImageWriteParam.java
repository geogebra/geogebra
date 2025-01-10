// Copyright 2003, FreeHEP
package org.freehep.graphicsio.raw;

import java.awt.Color;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageWriteParam;

import org.freehep.graphicsio.ImageParamConverter;
import org.freehep.util.UserProperties;

/**
 * 
 * @version $Id: RawImageWriteParam.java,v 1.4 2009-08-17 21:44:46 murkle Exp $
 */
public class RawImageWriteParam extends ImageWriteParam
		implements ImageParamConverter {

	private final static String rootKey = RawImageWriteParam.class.getName();

	public final static String BACKGROUND = rootKey + ".Background";

	public final static String CODE = rootKey + ".Code";

	public final static String PAD = rootKey + ".Pad";

	private Color bkg;

	private String code;

	private int pad;

	public RawImageWriteParam(Locale locale) {
		super(locale);
		bkg = null;
		code = "ARGB";
		pad = 1;
	}

	@Override
	public ImageWriteParam getWriteParam(Properties properties) {
		UserProperties p = new UserProperties(properties);
		setBackground(p.getPropertyColor(BACKGROUND, bkg));
		setCode(p.getProperty(CODE, code));
		setPad(p.getPropertyInt(PAD, pad));
		return this;
	}

	public Color getBackground() {
		return bkg;
	}

	public void setBackground(Color bkg) {
		this.bkg = bkg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getPad() {
		return pad;
	}

	public void setPad(int pad) {
		this.pad = pad;
	}
}
