; Java Launcher
;--------------
 
;You want to change the next four lines
Name "GeoGebra"
Caption "Java Launcher"
Icon "geogebra.ico"
OutFile "GeoGebraPrim.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
;You want to change the next two lines too
;!define CLASSPATH ".;lib;lib\myJar"
;!define CLASS "org.me.myProgram"
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" -Xms32m -Xmx512m -jar geogebra.jar --primary=true --showSplash=false --settingsfile=geogebraprim.properties'
 
 
  SetOutPath $EXEDIR
  ExecWait $0
SectionEnd
 
Function GetJRE
;
;  Find JRE (javaw.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"
 
  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"
 
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
