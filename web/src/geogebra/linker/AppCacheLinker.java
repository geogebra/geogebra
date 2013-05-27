/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package geogebra.linker;

import com.google.gwt.core.ext.linker.Shardable;

/**
 * A custom linker that generates an app cache manifest with the files generated
 * by the GWT compiler and the static files used by this application.
 * <p>
 * Before using this approach with production code be sure that you understand
 * the limitations of {@link SimpleAppCacheLinker}, namely that it sends all
 * permutations to the client.
 * 
 * @see SimpleAppCacheLinker
 */
@Shardable
public class AppCacheLinker extends SimpleAppCacheLinker {
  @Override
  protected String[] otherCachedFiles() {
    return new String[] {
    		"css/mathquill.css",
    		"font/Symbola.eot",
    		"font/Symbola.otf",
    		"font/Symbola.svg",
    		"font/Symbola.ttf",
    		"font/Symbola.woff",
    		"gwt/clean/clean-2.css",
    		"gwt/clean/clean_rtl.css",
    		"gwt/clean/images/circles.png",
    		"gwt/clean/images/circles_ie6.png",
    		"gwt/clean/images/corner.png",
    		"gwt/clean/images/corner_ie6.png",
    		"gwt/clean/images/ggb-logo-16x16-white.png",
    		"gwt/clean/images/hborder.png",
    		"gwt/clean/images/hborder_ie6.png",
    		"gwt/clean/images/inputhelp_left_16x16.png",
    		"gwt/clean/images/inputhelp_left_18x18.png",
    		"gwt/clean/images/inputhelp_left_20x20.png",
    		"gwt/clean/images/inputhelp_right_16x16.png",
    		"gwt/clean/images/inputhelp_right_18x18.png",
    		"gwt/clean/images/inputhelp_right_20x20.png",
    		"gwt/clean/images/thumb_horz.png",
    		"gwt/clean/images/thumb_vertical.png",
    		"gwt/clean/images/vborder.png",
    		"gwt/clean/images/vborder_ie6.png",
    		"images/10x1.png",
    		"images/axes.gif",
    		"images/bold.png",
    		"images/ggb4-splash-h120.png",
    		"images/ggbSplash.html",
    		"images/grid.gif",
    		"images/italic.png",
    		"images/nav_pause.png",
    		"images/nav_play.png",
    		"images/spinner.gif",
    		"images/spinner.html",
    		"images/splash-ggb4.svg",
    		"images/triangle-down.png",
    		"images/view-refresh.png",
    		"js/giac.js",
    		"js/jquery-1.7.2.min.js",
    		"js/loadCAS.js",
    		"js/mathml_concat.js",
    		"js/mathquill.js",
    		"js/properties_keys.js",
    		"js/properties_keys_af.js",
    		"js/properties_keys_ar.js",
    		"js/properties_keys_ar_MA.js",
    		"js/properties_keys_ar_TN.js",
    		"js/properties_keys_bg.js",
    		"js/properties_keys_br.js",
    		"js/properties_keys_bs.js",
    		"js/properties_keys_ca.js",
    		"js/properties_keys_ca_XV.js",
    		"js/properties_keys_cs.js",
    		"js/properties_keys_cy.js",
    		"js/properties_keys_da.js",
    		"js/properties_keys_de.js",
    		"js/properties_keys_de_AT.js",
    		"js/properties_keys_el.js",
    		"js/properties_keys_en.js",
    		"js/properties_keys_en_AU.js",
    		"js/properties_keys_en_GB.js",
    		"js/properties_keys_eo.js",
    		"js/properties_keys_es.js",
    		"js/properties_keys_et.js",
    		"js/properties_keys_eu.js",
    		"js/properties_keys_fa.js",
    		"js/properties_keys_fi.js",
    		"js/properties_keys_fr.js",
    		"js/properties_keys_ga.js",
    		"js/properties_keys_gl.js",
    		"js/properties_keys_hi.js",
    		"js/properties_keys_hr.js",
    		"js/properties_keys_hu.js",
    		"js/properties_keys_hy.js",
    		"js/properties_keys_in.js",
    		"js/properties_keys_is.js",
    		"js/properties_keys_it.js",
    		"js/properties_keys_iw.js",
    		"js/properties_keys_ja.js",
    		"js/properties_keys_ji.js",
    		"js/properties_keys_ka.js",
    		"js/properties_keys_kk.js",
    		"js/properties_keys_km.js",
    		"js/properties_keys_kn.js",
    		"js/properties_keys_ko.js",
    		"js/properties_keys_la.js",
    		"js/properties_keys_lt.js",
    		"js/properties_keys_mk.js",
    		"js/properties_keys_ml.js",
    		"js/properties_keys_mn.js",
    		"js/properties_keys_mr.js",
    		"js/properties_keys_ms.js",
    		"js/properties_keys_ne.js",
    		"js/properties_keys_nl.js",
    		"js/properties_keys_nl_BE.js",
    		"js/properties_keys_no.js",
    		"js/properties_keys_no_NB.js",
    		"js/properties_keys_no_NN.js",
    		"js/properties_keys_os.js",
    		"js/properties_keys_pl.js",
    		"js/properties_keys_pt.js",
    		"js/properties_keys_pt_PT.js",
    		"js/properties_keys_ro.js",
    		"js/properties_keys_ru.js",
    		"js/properties_keys_si.js",
    		"js/properties_keys_sk.js",
    		"js/properties_keys_sl.js",
    		"js/properties_keys_sq.js",
    		"js/properties_keys_sr.js",
    		"js/properties_keys_sv.js",
    		"js/properties_keys_ta.js",
    		"js/properties_keys_te.js",
    		"js/properties_keys_tg.js",
    		"js/properties_keys_th.js",
    		"js/properties_keys_tl.js",
    		"js/properties_keys_tr.js",
    		"js/properties_keys_ty.js",
    		"js/properties_keys_uk.js",
    		"js/properties_keys_ur.js",
    		"js/properties_keys_vi.js",
    		"js/properties_keys_xh.js",
    		"js/properties_keys_zh.js",
    		"js/properties_keys_zh_CN.js",
    		"js/properties_keys_zh_TW.js",
    		"js/workercheck.js",
    		"js/zipjs/arraybuffer.js",
    		"js/zipjs/base64.js",
    		"js/zipjs/dataview.js",
    		"js/zipjs/deflate.js",
    		"js/zipjs/inflate.js",
    		"js/zipjs/zip-2.js",
    		"js/zipjs/zip-fs.js",
    		"js/zipjs/zip.js"
    		
    };
  }
}