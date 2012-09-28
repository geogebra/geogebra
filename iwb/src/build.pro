#
# Proguard config file for GeoGebra
#
#
-ignorewarnings

-injars build/geogebra.jar
-injars build/geogebra_main.jar
-injars build/geogebra_gui.jar
-injars build/geogebra_export.jar
-injars build/geogebra_algos.jar
-injars build/geogebra_cas.jar
-injars build/jlatexmath.jar

-outjars build/temp

# libraries
-libraryjars <java.home>/lib/rt.jar
-libraryjars lib/build/lib_jsobject.jar
-libraryjars lib/build/lib_mac_extensions.jar
-libraryjars lib/sbsdk.jar

#-libraryjars ../java150-rt.jar

# Rhino Javascript is not obfuscated
-libraryjars build/geogebra_javascript.jar


-dontoptimize
-allowaccessmodification
-overloadaggressively

# needed for eg StringBuilder.setLength()
# see http://proguard.sourceforge.net/manual/troubleshooting.html
-dontskipnonpubliclibraryclasses

-printmapping geogebra.map 	 
#-applymapping geogebra3-2-0-0.map	 

# Keep GeoGebra application
-keep class geogebra.GeoGebra {
    public static void main(java.lang.String[]);
}
-keep class geogebra.iwb.GeoGebraIWB {
    public static void main(java.lang.String[]);
}


## enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep GeoGebra applet
-keep class geogebra.GeoGebraApplet {
    public <methods>;
}

# Keep GeoGebraAppletPreloader
-keep class geogebra.GeoGebraAppletPreloader {
    public <methods>;
}

# Keep GeoGebraPanel
-keep class geogebra.GeoGebraPanel {
    public <methods>;
}

# Keep Moodle plugin
-keep class geogebra.GeoGebraMoodleApplet {
    public <methods>;
}

# see META-INF/services
-keep class org.freehep.graphicsio.raw.RawImageWriterSpi { <methods>; }

# JLaTeXMath uses reflection
-keep class org.scilab.forge.jlatexmath.* { <methods>; }

# MathPiper / JAS interaction
-keep class org.mathpiper.builtin.library.jas.* { <methods>; }
-keep class org.mathpiper.builtin.javareflection.* { <methods>; }
-keep class edu.jas.poly.* { <methods>; }


-keep class geogebra.gui.virtualkeyboard.VirtualKeyboard { public static void main(java.lang.String[]); }


#####
# Plugin part
####

# So that Jython can access GeoElement methods
-keep class geogebra.plugin.jython.PythonAPI { <methods>; <fields>; }
-keep class geogebra.plugin.jython.PythonFlatAPI { <methods>; <fields>; }
-keep class geogebra.plugin.jython.CommonsMathLinearAPI { <methods>; <fields>; }

# for the inner class 'Geo'
-keepattributes InnerClasses
-keep class geogebra.plugin.jython.PythonAPI$Geo { <methods>; }
-keep class geogebra.plugin.jython.PythonFlatAPI$Geo { <methods>; }
-keep class geogebra.plugin.jython.PythonFlatAPI$Expression { <methods>; }

-keep class geogebra.plugin.jython.PythonScriptInterface { <methods>; <fields>; }
# allow eg Color.Red
-keep class geogebra.awt.GColorD { <fields>; }
-keep class geogebra.common.plugin.EuclidianStyleConstants { <fields>; }
-keep class geogebra.common.plugin.GeoClass { <fields>; }
-keep class geogebra.common.plugin.Operation { <fields>; }

-keep class geogebra.plugin.GgbAPI { <methods>; }
-keep class geogebra.common.plugin.GgbAPI { <methods>; }

# so that it can be accessed from Jython
-keep class jd2xx.JD2XX { <methods>; <fields>; }
-keep class jd2xx.JD2XX$ProgramData { <methods>; <fields>; }
-keep class jd2xx.JD2XX$DeviceInfo { <methods>; <fields>; }
-keep class jd2xx.JD2XXEvent { <methods>; <fields>; }
-keep class jd2xx.JD2XXEventListener { <methods>; <fields>; }
-keep class jd2xx.JD2XXInputStream { <methods>; <fields>; }
-keep class jd2xx.JD2XXOutputStream { <methods>; <fields>; }


