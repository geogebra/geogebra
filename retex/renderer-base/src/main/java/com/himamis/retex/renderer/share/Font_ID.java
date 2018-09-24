package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

public enum Font_ID {

	jlm_msbm10("fonts/maths/jlm_msbm10", 0, -1, 0.462964, 0.300003, 1.0),

	jlm_cmex10("fonts/base/jlm_cmex10", 0, -1, 0.430555, 0.0, 1.000003),

	jlm_cmmi10("fonts/base/jlm_cmmi10", 0, 196, 0.430555, 0.0, 1.000003),

	jlm_cmmib10("fonts/base/jlm_cmmib10", 0, -1, 0.444445, 0.0, 1.149994),

	jlm_moustache("fonts/base/jlm_cmex10", 0, -1, 0.430555, 0.0, 1.000003),

	jlm_cmmi10_unchanged("fonts/base/jlm_cmmi10", 0, 196, 0.430555, 0.0,
			1.000003),

	jlm_cmmib10_unchanged("fonts/base/jlm_cmmib10", 0, -1, 0.444445, 0.0,
			1.149994),

	jlm_stmary10("fonts/maths/jlm_stmary10", 0, -1, 0.430555, 0.0, 1.000003),

	jlm_cmsy10("fonts/maths/jlm_cmsy10", 0, 48, 0.430555, 0.0, 1.000003),

	jlm_msam10("fonts/maths/jlm_msam10", 0, -1, 0.430555, 0.0, 1.000003),

	jlm_cmbsy10("fonts/maths/jlm_cmbsy10", 0, -1, 0.444445, 0.0, 1.149994),

	jlm_dsrom10("fonts/maths/optional/jlm_dsrom10", 0, -1, 0.462964, 0.300003,
			1.0),

	jlm_rsfs10("fonts/maths/jlm_rsfs10", 0, -1, 0.233333, 0.0, 1.000003),

	jlm_eufm10("fonts/euler/jlm_eufm10", 0, -1, 0.475342, 0.333333, 1.0),

	jlm_eufb10("fonts/euler/jlm_eufb10", 0, -1, 0.475342, 0.333333, 1.0),

	jlm_cmti10("fonts/latin/optional/jlm_cmti10", 0, -1, 0.430555, 0.357776,
			1.022217),

	jlm_cmti10_unchanged("fonts/latin/optional/jlm_cmti10", 0, -1, 0.430555,
			0.357776, 1.022217),

	jlm_cmbxti10("fonts/latin/optional/jlm_cmbxti10", 0, -1, 0.444445, 0.414441,
			1.182211),

	jlm_cmr10("fonts/latin/jlm_cmr10", 0, -1, 0.430555, 0.333334, 1.000003),

	jlm_cmss10("fonts/latin/optional/jlm_cmss10", 0, -1, 0.444445, 0.333334,
			1.000003),

	jlm_cmssi10("fonts/latin/optional/jlm_cmssi10", 0, -1, 0.444445, 0.333334,
			1.000003),

	jlm_cmtt10("fonts/latin/optional/jlm_cmtt10", 0, -1, 0.430555, 0.524996,
			1.049991),

	jlm_cmbx10("fonts/latin/optional/jlm_cmbx10", 0, -1, 0.444445, 0.383331,
			1.149994),

	jlm_cmssbx10("fonts/latin/optional/jlm_cmssbx10", 0, -1, 0.458333, 0.366669,
			1.100006),

	jlm_special("fonts/maths/jlm_special", 0, -1, 0.233333, 0.0, 1.000003),

	jlm_jlmr10("fonts/latin/jlm_jlmr10", 0, -1, 0.430555, 0.333334, 1.000003),

	jlm_jlmr10_unchanged("fonts/latin/jlm_jlmr10", 0, -1, 0.430555, 0.333334,
			1.000003),

	jlm_jlmss10("fonts/latin/jlm_jlmss10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmsi10("fonts/latin/jlm_jlmsi10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmi10("fonts/latin/jlm_jlmi10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmbx10("fonts/latin/jlm_jlmbx10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmbi10("fonts/latin/jlm_jlmbi10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmsbi10("fonts/latin/jlm_jlmsbi10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmsb10("fonts/latin/jlm_jlmsb10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_jlmtt10("fonts/latin/jlm_jlmtt10", 0, -1, 0.0, 0.333334, 1.000003),

	jlm_wnr10("cyrillic/fonts/jlm_wnr10", 95, -1, 0.430555, 0.333334, 1.000003),

	jlm_wnti10("cyrillic/fonts/jlm_wnti10", 95, -1, 0.430555, 0.357776,
			1.022217),

	jlm_wntt10("cyrillic/fonts/jlm_wntt10", 95, -1, 0.430555, 0.524996,
			1.049991),

	jlm_wnss10("cyrillic/fonts/jlm_wnss10", 95, -1, 0.444445, 0.333334,
			1.000003),

	jlm_wnssi10("cyrillic/fonts/jlm_wnssi10", 95, -1, 0.444445, 0.333334,
			1.000003),

	jlm_wnssbx10("cyrillic/fonts/jlm_wnssbx10", 95, -1, 0.458333, 0.366669,
			1.100006),

	jlm_wnbx10("cyrillic/fonts/jlm_wnbx10", 95, -1, 0.444445, 0.383331,
			1.149994),

	jlm_wnbxti10("cyrillic/fonts/jlm_wnbxti10", 95, -1, 0.444445, 0.414441,
			1.182211),

	jlm_fcmbipg("greek/fonts/jlm_fcmbipg", 204, -1, 0.451, 0.355, 1.0),

	jlm_fcmbpg("greek/fonts/jlm_fcmbpg", 205, -1, 0.451, 0.319, 1.0),

	jlm_fcmripg("greek/fonts/jlm_fcmripg", 204, -1, 0.441, 0.307, 1.0),

	jlm_fcmrpg("greek/fonts/jlm_fcmrpg", 205, -1, 0.443, 0.278, 1.0),

	jlm_fcsbpg("greek/fonts/jlm_fcsbpg", 204, -1, 0.472, 0.255, 1.0),

	jlm_fcsropg("greek/fonts/jlm_fcsropg", 204, -1, 0.444, 0.239, 1.0),

	jlm_fcsrpg("greek/fonts/jlm_fcsrpg", 204, -1, 0.443, 0.239, 1.0),

	jlm_fctrpg("greek/fonts/jlm_fctrpg", 204, -1, 0.438, 0.525, 1.0);

	private static final Map<Font_ID, Font_ID> boldID = new HashMap<Font_ID, Font_ID>() {
		{
			put(jlm_cmmi10, jlm_cmmib10);
			put(jlm_cmmi10_unchanged, jlm_cmmib10_unchanged);
			put(jlm_cmsy10, jlm_cmbsy10);
			put(jlm_eufm10, jlm_eufb10);
			put(jlm_cmti10, jlm_cmbxti10);
			put(jlm_cmti10_unchanged, jlm_cmbxti10);
			put(jlm_cmr10, jlm_cmbx10);
			put(jlm_cmss10, jlm_cmssbx10);
			put(jlm_cmssi10, jlm_cmssbx10);
			put(jlm_jlmr10, jlm_jlmbx10);
			put(jlm_jlmss10, jlm_jlmsb10);
			put(jlm_jlmsi10, jlm_jlmsbi10);
			put(jlm_jlmi10, jlm_jlmbi10);
			put(jlm_wnr10, jlm_wnbx10);
			put(jlm_wnti10, jlm_wnbxti10);
			put(jlm_wnss10, jlm_wnssbx10);
			put(jlm_wnssi10, jlm_wnssbx10);
			put(jlm_fcmripg, jlm_fcmbipg);
			put(jlm_fcmrpg, jlm_fcmbpg);
			put(jlm_fcsropg, jlm_fcsbpg);
			put(jlm_fcsrpg, jlm_fcsbpg);
		}
	};
	private static final Map<Font_ID, Font_ID> romanID = new HashMap<Font_ID, Font_ID>() {
		{
			put(jlm_cmmi10, jlm_cmr10);
			put(jlm_cmmib10, jlm_cmbx10);
			put(jlm_cmti10, jlm_cmr10);
			put(jlm_cmbxti10, jlm_cmbx10);
			put(jlm_cmss10, jlm_cmr10);
			put(jlm_cmssi10, jlm_cmti10);
			put(jlm_cmssbx10, jlm_cmbx10);
			put(jlm_jlmss10, jlm_jlmr10);
			put(jlm_jlmsi10, jlm_jlmss10);
			put(jlm_jlmi10, jlm_jlmr10);
			put(jlm_jlmbx10, jlm_jlmr10);
			put(jlm_jlmbi10, jlm_jlmr10);
			put(jlm_jlmsbi10, jlm_jlmss10);
			put(jlm_jlmsb10, jlm_jlmbx10);
			put(jlm_wnti10, jlm_wnr10);
			put(jlm_wntt10, jlm_wnr10);
			put(jlm_wnss10, jlm_wnr10);
			put(jlm_wnssi10, jlm_wnti10);
			put(jlm_wnssbx10, jlm_wnbx10);
			put(jlm_wnbxti10, jlm_wnbx10);
			put(jlm_fcmripg, jlm_fcmrpg);
			put(jlm_fcsbpg, jlm_fcmbpg);
			put(jlm_fcsropg, jlm_fcmripg);
		}
	};
	private static final Map<Font_ID, Font_ID> ssID = new HashMap<Font_ID, Font_ID>() {
		{
			put(jlm_cmmi10, jlm_cmss10);
			put(jlm_cmmib10, jlm_cmssbx10);
			put(jlm_cmti10, jlm_cmssi10);
			put(jlm_cmbxti10, jlm_cmssbx10);
			put(jlm_cmr10, jlm_cmss10);
			put(jlm_cmbx10, jlm_cmssbx10);
			put(jlm_jlmr10, jlm_jlmss10);
			put(jlm_jlmi10, jlm_jlmsi10);
			put(jlm_jlmbx10, jlm_jlmsb10);
			put(jlm_jlmbi10, jlm_jlmsbi10);
			put(jlm_wnr10, jlm_wnss10);
			put(jlm_wnti10, jlm_wnssi10);
			put(jlm_wntt10, jlm_wnss10);
			put(jlm_wnbx10, jlm_wnssbx10);
			put(jlm_wnbxti10, jlm_wnssbx10);
			put(jlm_fcmbipg, jlm_fcsbpg);
			put(jlm_fcmbpg, jlm_fcsbpg);
			put(jlm_fcmripg, jlm_fcsropg);
			put(jlm_fcmrpg, jlm_fcsrpg);
		}
	};
	private static final Map<Font_ID, Font_ID> ttID = new HashMap<Font_ID, Font_ID>() {
		{
			put(jlm_cmmi10, jlm_cmtt10);
			put(jlm_cmmib10, jlm_cmtt10);
			put(jlm_cmti10, jlm_cmtt10);
			put(jlm_cmbxti10, jlm_cmtt10);
			put(jlm_cmr10, jlm_cmtt10);
			put(jlm_cmss10, jlm_cmtt10);
			put(jlm_cmssi10, jlm_cmtt10);
			put(jlm_cmbx10, jlm_cmtt10);
			put(jlm_cmssbx10, jlm_cmtt10);
			put(jlm_jlmr10, jlm_jlmtt10);
			put(jlm_jlmss10, jlm_jlmtt10);
			put(jlm_jlmsi10, jlm_jlmtt10);
			put(jlm_jlmi10, jlm_jlmtt10);
			put(jlm_jlmbx10, jlm_jlmtt10);
			put(jlm_jlmbi10, jlm_jlmtt10);
			put(jlm_jlmsbi10, jlm_jlmtt10);
			put(jlm_jlmsb10, jlm_jlmtt10);
			put(jlm_wnr10, jlm_wntt10);
			put(jlm_wnti10, jlm_wntt10);
			put(jlm_wnss10, jlm_wntt10);
			put(jlm_wnssi10, jlm_wntt10);
			put(jlm_wnssbx10, jlm_wntt10);
			put(jlm_wnbx10, jlm_wntt10);
			put(jlm_wnbxti10, jlm_wntt10);
			put(jlm_fcmbipg, jlm_fctrpg);
			put(jlm_fcmbpg, jlm_fctrpg);
			put(jlm_fcmripg, jlm_fctrpg);
			put(jlm_fcmrpg, jlm_fctrpg);
			put(jlm_fcsbpg, jlm_fctrpg);
			put(jlm_fcsropg, jlm_fctrpg);
			put(jlm_fcsrpg, jlm_fctrpg);
		}
	};
	private static final Map<Font_ID, Font_ID> itID = new HashMap<Font_ID, Font_ID>() {
		{
			put(jlm_cmmi10, jlm_cmti10);
			put(jlm_cmmib10, jlm_cmbxti10);
			put(jlm_cmr10, jlm_cmti10);
			put(jlm_cmss10, jlm_cmssi10);
			put(jlm_cmbx10, jlm_cmbxti10);
			put(jlm_cmssbx10, jlm_cmbxti10);
			put(jlm_jlmr10, jlm_jlmi10);
			put(jlm_jlmss10, jlm_jlmsi10);
			put(jlm_jlmbx10, jlm_jlmbi10);
			put(jlm_jlmsb10, jlm_jlmsbi10);
			put(jlm_wnr10, jlm_wnti10);
			put(jlm_wnss10, jlm_wnssi10);
			put(jlm_wnbx10, jlm_wnbxti10);
			put(jlm_fcmbpg, jlm_fcmbipg);
			put(jlm_fcmrpg, jlm_fcmripg);
			put(jlm_fcsbpg, jlm_fcsropg);
			put(jlm_fcsrpg, jlm_fcsropg);
		}
	};

	public final String path;
	public final int unicode;
	public final int skewChar;
	public final double xHeight;
	public final double space;
	public final double quad;

	Font_ID(String path, int unicode, int skewChar, double xHeight,
			double space, double quad) {
		this.path = path;
		this.unicode = unicode;
		this.skewChar = skewChar;
		this.xHeight = xHeight;
		this.space = space;
		this.quad = quad;
	}

	public Font_ID getBoldId() {
		return boldID.containsKey(this) ? boldID.get(this) : this;
	}

	public Font_ID getRomanId() {
		return romanID.containsKey(this) ? romanID.get(this) : this;
	}

	public Font_ID getTtId() {
		return ttID.containsKey(this) ? ttID.get(this) : this;
	}

	public Font_ID getItId() {
		return itID.containsKey(this) ? itID.get(this) : this;
	}

	public Font_ID getSsId() {
		return ssID.containsKey(this) ? ssID.get(this) : this;
	}

	public static Font_ID fromString(String fontId) {
		for (Font_ID f : Font_ID.values()) {
			if (f.name().equals(fontId)) {
				return f;
			}
		}

		return null;
	}
}
