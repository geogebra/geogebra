; This script will detect which Version of Windows is running. And display
; its version in a messagebox
; Email: dragonbals@hotmail.com
 
;You want to change the next four lines
Name "GeoGebra"
Caption "Java Launcher"
Icon "geogebra.ico"
OutFile "GeoGebra.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" -Xms32m -Xmx512m -jar geogebra.jar --settingsfile=geogebra.properties'
 
  SetOutPath $EXEDIR
  ExecWait $0
SectionEnd


Function GetJRE

  Push $R0
  Push $R1
 
  ClearErrors
 
  ReadRegStr $R0 HKLM \
  "SOFTWARE\Microsoft\Windows NT\CurrentVersion" CurrentVersion
 
  IfErrors 0 lbl_winnt
 
  ; we are not NT
  ReadRegStr $R0 HKLM \
  "SOFTWARE\Microsoft\Windows\CurrentVersion" VersionNumber
 
  StrCpy $R1 $R0 1
  StrCmp $R1 '4' 0 lbl_error
 
  StrCpy $R1 $R0 3
 
  StrCmp $R1 '4.0' lbl_win32_95
  StrCmp $R1 '4.9' lbl_win32_ME lbl_win32_98
 
; OLD WINDOWS: 95, 98, ME
; use Java 5 to start GeoGebra
  lbl_win32_95:
  lbl_win32_98:
  lbl_win32_ME:
    StrCpy $R0 "$EXEDIR\jre5\bin\javaw.exe"
    Goto lbl_done
 
; NEW WINDOWS: NT, 2000, XP, 7
; use Java 6 or later to start GeoGebra 
  lbl_winnt:
  lbl_error:
    StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
    Goto lbl_done
    
  lbl_done:
  Pop $R1
  Exch $R0
FunctionEnd