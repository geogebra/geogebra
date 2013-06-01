/**
 * @(#)geogebra.nsi
 * 
 *    Version: 1.02.20100108
 *    Version: 1.01.20100107
 *    Version: 1.00.20100105
 * Written by: Yves Kreis <mailto:yves@geogebra.org>
 * 
 * All GeoGebra installers are subject to the following Creative Commons 
 * Attribution-Noncommercial-Share Alike License; either version 3.0 of 
 * the License, or (at your option) any later version 
 * (see http://creativecommons.org/licenses/by-nc-sa/3.0/):
 * 
 * You are free to copy, distribute, transmit, and adapt GeoGebra 
 * installers under the following conditions:
 *  + Attribution. You must attribute the work
 *      (i.e. by linking to http://www.geogebra.org/)
 *  + Noncommercial. You may not use this work for commercial purposes 
 *      (unless you have approval from GeoGebra, please contact us
 *       at office@geogebra.org).
 *  + Share Alike. If you alter, transform, or build upon this work, 
 *      you may distribute the resulting work only under the same, 
 *      similar or a compatible license.
 * 
 * GeoGebra installers are ...
 *   installation packages that let you install the GeoGebra 
 *   application and/or documentation files on your system 
 *   (MS Windows, Mac OS X, Linux, etc.) including GeoGebra WebStart.
 * 
 */

/**
 * Installer Attributes
 */

Name "GeoGebra 5.0"
OutFile "${outfile}"
Caption "GeoGebra Installer" # see line marked with CAPTION
BrandingText "GeoGebra ${fullversion} (${builddate})"


RequestExecutionLevel highest
SetCompressor /SOLID /FINAL lzma
XPStyle on

!ifdef uninstaller
  SilentInstall silent
!endif

/**
 * Installer Types
 */

!ifndef uninstaller
  InstType $(UMUI_TEXT_SETUPTYPE_STANDARD_TITLE)
!endif

/**
 * Variables
 */

Var UMUI_DEFAULT_SHELLVARCONTEXT
Var STARTMENU_FOLDER

!ifndef uninstaller
  
  Var ADMINISTRATOR
  Var VERSION
  Var WORKSTATION
  Var ARCHITECTURE
  
  Var ADDITIONALTASKS_INI
  Var ADDITIONALTASKS_DESKTOP
  Var ADDITIONALTASKS_DESKTOP_ALL
  Var ADDITIONALTASKS_DESKTOP_CURRENT
  Var ADDITIONALTASKS_ASSOCIATE_GGB
  Var ADDITIONALTASKS_ASSOCIATE_GGB_ALL
  Var ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
  Var ADDITIONALTASKS_ASSOCIATE_GGT
  Var ADDITIONALTASKS_ASSOCIATE_GGT_ALL
  Var ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
  
!endif

Var DESKTOP_ALL
Var DESKTOP_CURRENT
Var QUICK_LAUNCH
Var ASSOCIATE_ALL
Var ASSOCIATE_CURRENT
Var ASSOCIATE_GGB
Var ASSOCIATE_GGT

/**
 * Ultra Modern User Interface (http://ultramodernui.sourceforge.net/)
 */

!include UMUI.nsh

!define MUI_ICON geogebra.ico
!define MUI_UNICON geogebra.ico

!ifndef uninstaller
  !define MUI_ABORTWARNING
  !define UMUI_PAGEBGIMAGE
  !define UMUI_LANGUAGE_ALWAYSSHOW
  
  !define UMUI_SETUPTYPEPAGE_STANDARD "$(UMUI_TEXT_SETUPTYPE_STANDARD_TITLE)"
  !define UMUI_SETUPTYPEPAGE_DEFAULTCHOICE ${UMUI_STANDARD}
  
  !define MUI_STARTMENUPAGE_DEFAULTFOLDER "GeoGebra 5.0"
  !define UMUI_ALTERNATIVESTARTMENUPAGE_USE_TREEVIEW
  !define UMUI_ALTERNATIVESTARTMENUPAGE_SETSHELLVARCONTEXT
!else
  !define MUI_UNABORTWARNING
  !define UMUI_UNPAGEBGIMAGE
  !define UMUI_UNLANGUAGE_ALWAYSSHOW
!endif

!define UMUI_LEFTIMAGE_BMP left.bmp
!define UMUI_USE_INSTALLOPTIONSEX
!define UMUI_DEFAULT_SHELLVARCONTEXT $UMUI_DEFAULT_SHELLVARCONTEXT

/**
 * Macros
 */

!include LogicLib.nsh
!include WordFunc.nsh

!include ZipDLL.nsh

!macro PushShellVarContext
  Push $R0
  StrCpy $R0 $SMPROGRAMS
  SetShellVarContext current
  ${If} $R0 == $SMPROGRAMS
    Push current
  ${Else}
    Push all
    SetShellVarContext all
  ${EndIf}
  Exch
  Pop $R0
!macroend

!macro PopShellVarContext
  Push $R0
  Exch
  Pop $R0
  ${If} $R0 == all
    SetShellVarContext all
  ${Else}
    SetShellVarContext current
  ${EndIf}
  Pop $R0
!macroend

/**
 * Functions
 */

!ifndef uninstaller
  
  Function Administrator
    Push $R0
    
    ClearErrors
    UserInfo::GetName
    IfErrors Win95
    Pop $R0
    UserInfo::GetAccountType
    Pop $R0
    
    ${If} "Admin" == $R0
    ${OrIf} "Power" == $R0
    ${OrIf} "" == $R0
      StrCpy $ADMINISTRATOR "true"
    ${Else}
      StrCpy $ADMINISTRATOR "false"
    ${EndIf}
    Goto End
    
    Win95:
      StrCpy $ADMINISTRATOR "true"
    
    End:
      Pop $R0
  FunctionEnd
  
  Function PushShellVarContext
    !insertmacro PushShellVarContext
  FunctionEnd
  
  Function PopShellVarContext
    !insertmacro PopShellVarContext
  FunctionEnd
  
!else
  
  Function un.PushShellVarContext
    !insertmacro PushShellVarContext
  FunctionEnd
  
  Function un.PopShellVarContext
    !insertmacro PopShellVarContext
  FunctionEnd
  
!endif

/**
 * Pages
 */

!ifndef uninstaller
  !insertmacro UMUI_PAGE_MULTILANGUAGE
  !insertmacro MUI_PAGE_LICENSE ..\geogebra\gui\_license.txt
  !define MUI_PAGE_CUSTOMFUNCTION_SHOW SetupTypeShow
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE SetupTypeLeave
  !insertmacro UMUI_PAGE_SETUPTYPE
  !insertmacro MUI_PAGE_DIRECTORY
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE AlternativeStartMenuLeave
  !insertmacro UMUI_PAGE_ALTERNATIVESTARTMENU StartMenu $STARTMENU_FOLDER
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE AdditionalTasksLeave
  !insertmacro UMUI_PAGE_ADDITIONALTASKS AdditionalTasks
  !define MUI_PAGE_CUSTOMFUNCTION_SHOW ConfirmShow
  !define UMUI_CONFIRMPAGE_TEXTBOX Confirm
  !insertmacro UMUI_PAGE_CONFIRM
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_FINISHPAGE_RUN $INSTDIR\GeoGebra-JOGL2.exe
  !insertmacro MUI_PAGE_FINISH
!else
  !insertmacro UMUI_UNPAGE_MULTILANGUAGE
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH
!endif

/**
 * Page Functions
 */

!ifndef uninstaller
  
  Function SetupTypeShow
    !insertmacro INSTALLOPTIONS_READ $R0 SetupType.ini "Field 2" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    !insertmacro INSTALLOPTIONS_WRITE SetupType.ini "Field 2" Flags $R0
    !insertmacro INSTALLOPTIONS_READ $R0 SetupType.ini "Field 5" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    !insertmacro INSTALLOPTIONS_WRITE SetupType.ini "Field 5" Flags $R0
    !insertmacro INSTALLOPTIONS_READ $R0 SetupType.ini "Field 8" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    !insertmacro INSTALLOPTIONS_WRITE SetupType.ini "Field 8" Flags $R0
    !insertmacro INSTALLOPTIONS_READ $R0 SetupType.ini "Field 11" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    !insertmacro INSTALLOPTIONS_WRITE SetupType.ini "Field 11" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE SetupType.ini Settings NextButtonText "$(^InstallBtn)"
  FunctionEnd
  
  Function SetupTypeLeave
    !insertmacro INSTALLOPTIONS_READ $R0 SetupType.ini Settings State
    ${IfNot} 0 = $R0
      GetDlgItem $R0 $HWNDPARENT 1
      !insertmacro INSTALLOPTIONS_READ $R1 SetupType.ini Settings NumFields
      IntOp $R1 $R1 - 2
      !insertmacro INSTALLOPTIONS_READ $R1 SetupType.ini "Field $R1" State
      ${If} 0 = $R1
        SendMessage $R0 ${WM_SETTEXT} 0 "STR:$(^InstallBtn)"
      ${Else}
        SendMessage $R0 ${WM_SETTEXT} 0 "STR:$(^NextBtn)"
      ${EndIf}
      Abort
    ${EndIf}
  FunctionEnd
  
  Function AlternativeStartMenuLeave
    !insertmacro INSTALLOPTIONS_READ $R0 AlternativeStartMenuStartMenu.ini Settings State
    ${If} 3 = $R0
      !insertmacro INSTALLOPTIONS_READ $R0 AlternativeStartMenuStartMenu.ini "Field 2" HWND
      !insertmacro INSTALLOPTIONS_READ $R1 AlternativeStartMenuStartMenu.ini "Field 3" State
      !ifdef UMUI_ALTERNATIVESTARTMENUPAGE_USE_TREEVIEW
        !insertmacro UMUI_STRREPLACE $R1 "{" "\" $R1
        !insertmacro UMUI_STRREPLACE $R1 "}" "" $R1
      !endif
      SendMessage $R0 ${WM_SETTEXT} 0 "STR:$R1"
    ${EndIf}
  FunctionEnd
  
  Function AdditionalTasks
    ${If} "true" == $ADMINISTRATOR
      StrCpy $0 1
      StrCpy $1 0
    ${Else}
      StrCpy $0 0
      StrCpy $1 1
    ${EndIf}
    StrCpy $ADDITIONALTASKS_INI $MUI_TEMP1
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_LABEL $(UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS)
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK DESKTOP 0 $(UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON)
    StrCpy $ADDITIONALTASKS_DESKTOP $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP" Flags NOTIFY
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO DESKTOP_ALL $0 $(UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS)
    StrCpy $ADDITIONALTASKS_DESKTOP_ALL $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_ALL" Flags
    ${WordAdd} "$R0" "|" "+DISABLED" $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_ALL" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_ALL" Left 20
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO DESKTOP_CURRENT $1 $(UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER)
    StrCpy $ADDITIONALTASKS_DESKTOP_CURRENT $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_CURRENT" Flags
    ${WordAdd} "$R0" "|" "+DISABLED" $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_CURRENT" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_DESKTOP_CURRENT" Left 20
    ${IfNot} "6.1" == $VERSION   # Windows 7 | Windows Server 2008 R2
    ${AndIfNot} "" == $VERSION
    ${AndIfNot} "" == $WORKSTATION
      !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK QUICK_LAUNCH 0 $(UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON)
    ${EndIf}
    
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_EMPTYLINE
    
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_LABEL $(UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION)
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK ASSOCIATE_GGB 1 "$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH)GGB$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END)"
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGB $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB" Flags NOTIFY
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO ASSOCIATE_GGB_ALL $0 $(UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS)
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGB_ALL $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_ALL" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    ${If} "false" == $ADMINISTRATOR
      ${WordAdd} "$R0" "|" "+DISABLED" $R0
    ${EndIf}
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_ALL" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_ALL" Left 20
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO ASSOCIATE_GGB_CURRENT $1 $(UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER)
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    ${If} "false" == $ADMINISTRATOR
      ${WordAdd} "$R0" "|" "+DISABLED" $R0
    ${EndIf}
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT" Left 20
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK ASSOCIATE_GGT 1 "$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH)GGT$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END)"
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGT $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT" Flags NOTIFY
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO ASSOCIATE_GGT_ALL $0 $(UMUI_TEXT_SHELL_VAR_CONTEXT_FOR_ALL_USERS)
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGT_ALL $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_ALL" Flags
    ${WordAdd} "$R0" "|" "+GROUP" $R0
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    ${If} "false" == $ADMINISTRATOR
      ${WordAdd} "$R0" "|" "+DISABLED" $R0
    ${EndIf}
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_ALL" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_ALL" Left 20
    !insertmacro UMUI_ADDITIONALTASKSPAGE_ADD_TASK_RADIO ASSOCIATE_GGT_CURRENT $1 $(UMUI_TEXT_SHELL_VAR_CONTEXT_ONLY_FOR_CURRENT_USER)
    StrCpy $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT $UMUI_TEMP3
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT" Flags
    ${WordAdd} "$R0" "|" "+NOTIFY" $R0
    ${If} "false" == $ADMINISTRATOR
      ${WordAdd} "$R0" "|" "+DISABLED" $R0
    ${EndIf}
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT" Flags $R0
    !insertmacro INSTALLOPTIONS_WRITE $ADDITIONALTASKS_INI "Field $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT" Left 20
  FunctionEnd
  
  Function AdditionalTasksLeave
    !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI Settings State
    ${If} $R0 = $ADDITIONALTASKS_DESKTOP
    ${OrIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB
    ${OrIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT
      ${If} "true" == $ADMINISTRATOR
        ${If} $R0 = $ADDITIONALTASKS_DESKTOP
          StrCpy $0 $ADDITIONALTASKS_DESKTOP
          StrCpy $1 $ADDITIONALTASKS_DESKTOP_ALL
          StrCpy $2 $ADDITIONALTASKS_DESKTOP_CURRENT
        ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB
          StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGB
          StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGB_ALL
          StrCpy $2 $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
        ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT
          StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGT
          StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGT_ALL
          StrCpy $2 $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
        ${EndIf}
        !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $0" State
        !insertmacro INSTALLOPTIONS_READ $R1 $ADDITIONALTASKS_INI "Field $1" HWND
        !insertmacro INSTALLOPTIONS_READ $R2 $ADDITIONALTASKS_INI "Field $2" HWND
        EnableWindow $R1 $R0
        EnableWindow $R2 $R0
        !insertmacro INSTALLOPTIONS_READ $R1 $ADDITIONALTASKS_INI "Field $1" Flags
        !insertmacro INSTALLOPTIONS_READ $R2 $ADDITIONALTASKS_INI "Field $2" Flags
        ${If} 0 = $R0
          ${WordAdd} "$R1" "|" "+DISABLED" $R1
          ${WordAdd} "$R2" "|" "+DISABLED" $R2
        ${Else}
          ${WordAdd} "$R1" "|" "-DISABLED" $R1
          ${WordAdd} "$R2" "|" "-DISABLED" $R2
        ${EndIf}
        !insertmacro INSTALLOPTIONS_WRITE "$ADDITIONALTASKS_INI" "Field $1" Flags $R1
        !insertmacro INSTALLOPTIONS_WRITE "$ADDITIONALTASKS_INI" "Field $2" Flags $R2
      ${EndIf}
      Abort
    ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB_ALL
    ${OrIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
    ${OrIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT_ALL
    ${OrIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
      ${If} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB_ALL
        StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGT_ALL
        StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
      ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
        StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
        StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGT_ALL
      ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT_ALL
        StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGB_ALL
        StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
      ${ElseIf} $R0 = $ADDITIONALTASKS_ASSOCIATE_GGT_CURRENT
        StrCpy $0 $ADDITIONALTASKS_ASSOCIATE_GGB_CURRENT
        StrCpy $1 $ADDITIONALTASKS_ASSOCIATE_GGB_ALL
      ${EndIf}
      !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $0" HWND
      SendMessage $R0 ${BM_SETCHECK} ${BST_CHECKED} 0
      !insertmacro INSTALLOPTIONS_READ $R0 $ADDITIONALTASKS_INI "Field $1" HWND
      SendMessage $R0 ${BM_SETCHECK} ${BST_UNCHECKED} 0
      Abort
    ${ElseIfNot} 0 = $R0
      Abort
    ${EndIf}
  FunctionEnd
  
  Function Confirm
    !insertmacro UMUI_GET_CHOOSEN_SETUP_TYPE_TEXT
    Pop $R0
    ${If} "Custom" == $R0
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED DESKTOP_ALL
        StrCpy $DESKTOP_ALL 1
        StrCpy $DESKTOP_CURRENT 0
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED DESKTOP_CURRENT
        StrCpy $DESKTOP_ALL 0
        StrCpy $DESKTOP_CURRENT 1
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF
      !insertmacro UMUI_ADDITIONALTASKS_IF_NOT_CKECKED DESKTOP
        StrCpy $DESKTOP_ALL 0
        StrCpy $DESKTOP_CURRENT 0
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF
      StrCpy $QUICK_LAUNCH 0
      ${IfNot} "6.1" == $VERSION   # Windows 7 | Windows Server 2008 R2
      ${AndIfNot} "" == $VERSION
      ${AndIfNot} "" == $WORKSTATION
        !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED QUICK_LAUNCH
          StrCpy $QUICK_LAUNCH 1
        !insertmacro UMUI_ADDITIONALTASKS_ENDIF 
      ${EndIf}
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED ASSOCIATE_GGB_ALL
        StrCpy $ASSOCIATE_ALL 1
        StrCpy $ASSOCIATE_CURRENT 0
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED ASSOCIATE_GGB_CURRENT
        StrCpy $ASSOCIATE_ALL 0
        StrCpy $ASSOCIATE_CURRENT 1
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF
      StrCpy $ASSOCIATE_GGB 0
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED ASSOCIATE_GGB
        StrCpy $ASSOCIATE_GGB 1
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF 
      StrCpy $ASSOCIATE_GGT 0
      !insertmacro UMUI_ADDITIONALTASKS_IF_CKECKED ASSOCIATE_GGT
        StrCpy $ASSOCIATE_GGT 1
      !insertmacro UMUI_ADDITIONALTASKS_ENDIF 
    ${Else}
      ${If} "true" == $ADMINISTRATOR
        StrCpy $DESKTOP_ALL 1
        StrCpy $DESKTOP_CURRENT 0
      ${Else}
        StrCpy $DESKTOP_ALL 0
        StrCpy $DESKTOP_CURRENT 1
      ${EndIf}
      StrCpy $QUICK_LAUNCH 0
      ${IfNot} "6.1" == $VERSION   # Windows 7 | Windows Server 2008 R2
      ${AndIfNot} "" == $VERSION
      ${AndIfNot} "" == $WORKSTATION
        StrCpy $QUICK_LAUNCH 1
      ${EndIf}
      ${If} "true" == $ADMINISTRATOR
        StrCpy $ASSOCIATE_ALL 1
        StrCpy $ASSOCIATE_CURRENT 0
      ${Else}
        StrCpy $ASSOCIATE_ALL 0
        StrCpy $ASSOCIATE_CURRENT 1
      ${EndIf}
      StrCpy $ASSOCIATE_GGB 1
      StrCpy $ASSOCIATE_GGT 1
      Abort
    ${EndIf}
    
    !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE $(UMUI_TEXT_INSTCONFIRM_TEXTBOX_DESTINATION_LOCATION)
    !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "      $INSTDIR"
    !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE ""
    !insertmacro MUI_STARTMENU_WRITE_BEGIN StartMenu
      !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE $(UMUI_TEXT_INSTCONFIRM_TEXTBOX_START_MENU_FOLDER)
      !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "      $SMPROGRAMS\$STARTMENU_FOLDER"
      !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE ""
    !insertmacro MUI_STARTMENU_WRITE_END
    
    ${If} 1 = $DESKTOP_ALL
    ${OrIf} 1 = $DESKTOP_CURRENT
    ${OrIf} 1 = $QUICK_LAUNCH
    ${OrIf} 1 = $ASSOCIATE_GGB
    ${OrIf} 1 = $ASSOCIATE_GGT
      !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE $(UMUI_TEXT_ADDITIONALTASKS_TITLE):
      ${If} 1 = $DESKTOP_ALL
      ${OrIf} 1 = $DESKTOP_CURRENT
      ${OrIf} 1 = $QUICK_LAUNCH
        !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "      $(UMUI_TEXT_ADDITIONALTASKS_ADDITIONAL_ICONS)"
        ${If} 1 = $DESKTOP_ALL
        ${OrIf} 1 = $DESKTOP_CURRENT
          Call PushShellVarContext
          ${If} 1 = $DESKTOP_ALL
            SetShellVarContext all
          ${ElseIf} 1 = $DESKTOP_CURRENT
            SetShellVarContext current
          ${EndIf}
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "            $(UMUI_TEXT_ADDITIONALTASKS_CREATE_DESKTOP_ICON):"
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "                  $DESKTOP"
          Call PopShellVarContext
        ${EndIf}
        ${If} 1 = $QUICK_LAUNCH
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "            $(UMUI_TEXT_ADDITIONALTASKS_CREATE_QUICK_LAUNCH_ICON):"
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "                  $QUICKLAUNCH"
        ${EndIf}
      ${EndIf}
      ${If} 1 = $ASSOCIATE_GGB
      ${OrIf} 1 = $ASSOCIATE_GGT
        !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "      $(UMUI_TEXT_ADDITIONALTASKS_FILE_ASSOCIATION)"
        ${If} 1 = $ASSOCIATE_ALL
          StrCpy $0 " (HKLM)"
        ${ElseIf} 1 = $ASSOCIATE_CURRENT
          StrCpy $0 " (HKCU)"
        ${Else}
          StrCpy $0 ""
        ${EndIf}
        ${If} 1 = $ASSOCIATE_GGB
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "            $(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH)GGB$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END)$0"
        ${EndIf}
        ${If} 1 = $ASSOCIATE_GGT
          !insertmacro UMUI_CONFIRMPAGE_TEXTBOX_ADDLINE "            $(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH)GGT$(UMUI_TEXT_ADDITIONALTASKS_ASSOCIATE_WITH_END)$0"
        ${EndIf}
      ${EndIf}
    ${EndIf}
  FunctionEnd
  
  Function ConfirmShow
    !insertmacro INSTALLOPTIONS_READ $R0 "confirm.ini" "Field 3" Flags
    ${WordAdd} "$R0" "|" "+HSCROLL" $R0
    ${WordAdd} "$R0" "|" "-VSCROLL" $R0
    !insertmacro INSTALLOPTIONS_WRITE "confirm.ini" "Field 3" Flags $R0
  FunctionEnd

Function Architecture
  Push $R0
  Push $R1

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors 0 ArchAmd64  ;; Java found in the registry (64 bit entry, launch4j prefers it)
   ;; see http://stackoverflow.com/questions/2688932/configure-launch4j-to-use-32-bit-jvm-only
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors 0 ArchI586  ;; Java found it in the registry (32 bit entry)

  ClearErrors
  messageBox MB_OK "No Java is detected on your system. You will be redirected to the Java download page after installing GeoGebra.\n\
    By default, GeoGebra installs a 32 bit version of the JOGL1 DLLs if no Java is preinstalled. \
    If you are going to install the 64 bit version of Java, you must re-run the GeoGebra installer \
    to have the correct version of the JOGL1 DLLs."

  ArchI586:
   StrCpy $ARCHITECTURE "i586"
   Goto JavaFound

  ArchAmd64:
   StrCpy $ARCHITECTURE "amd64"

  JavaFound:
  Pop $R1
  Pop $R0
FunctionEnd

!endif

/**
 * Languages
 */

!insertmacro MUI_LANGUAGE English
!insertmacro MUI_LANGUAGE Arabic
!insertmacro MUI_LANGUAGE Basque
!insertmacro MUI_LANGUAGE Bosnian
!insertmacro MUI_LANGUAGE Bulgarian
!insertmacro MUI_LANGUAGE Catalan
!insertmacro MUI_LANGUAGE TradChinese
!insertmacro MUI_LANGUAGE SimpChinese
!insertmacro MUI_LANGUAGE Croatian
!insertmacro MUI_LANGUAGE Czech
!insertmacro MUI_LANGUAGE Danish
!insertmacro MUI_LANGUAGE Dutch
!insertmacro MUI_LANGUAGE Estonian
!insertmacro MUI_LANGUAGE Farsi
!insertmacro MUI_LANGUAGE Finnish
!insertmacro MUI_LANGUAGE French
!insertmacro MUI_LANGUAGE Galician
#!insertmacro MUI_LANGUAGE Georgian
!insertmacro MUI_LANGUAGE German
!insertmacro MUI_LANGUAGE Greek
!insertmacro MUI_LANGUAGE Hebrew
!insertmacro MUI_LANGUAGE Hungarian
!insertmacro MUI_LANGUAGE Icelandic
!insertmacro MUI_LANGUAGE Indonesian
!insertmacro MUI_LANGUAGE Italian
!insertmacro MUI_LANGUAGE Japanese
!insertmacro MUI_LANGUAGE Korean
!insertmacro MUI_LANGUAGE Lithuanian
!insertmacro MUI_LANGUAGE Macedonian
!insertmacro MUI_LANGUAGE Norwegian
#!insertmacro MUI_LANGUAGE Persian
!insertmacro MUI_LANGUAGE Polish
!insertmacro MUI_LANGUAGE Portuguese
!insertmacro MUI_LANGUAGE PortugueseBR
!insertmacro MUI_LANGUAGE Russian
!insertmacro MUI_LANGUAGE Serbian
!insertmacro MUI_LANGUAGE Slovak
!insertmacro MUI_LANGUAGE Slovenian
!insertmacro MUI_LANGUAGE Swedish
!insertmacro MUI_LANGUAGE Spanish
!insertmacro MUI_LANGUAGE Turkish
#!insertmacro MUI_LANGUAGE Vietnamese
!insertmacro MUI_LANGUAGE Welsh

/**
 * File Properties
 */

VIAddVersionKey CompanyName "International GeoGebra Institute"
VIAddVersionKey FileDescription "GeoGebra Installer"
VIAddVersionKey FileVersion ${fullversion}
VIAddVersionKey InternalName GeoGebra_Installer_${versionname}
VIAddVersionKey LegalCopyright "(C) 2001-2012 International GeoGebra Institute"
VIAddVersionKey OriginalFilename GeoGebra_Installer_${versionname}.exe
VIAddVersionKey ProductName "GeoGebra 5.0"
VIAddVersionKey ProductVersion ${fullversion}

VIProductVersion ${fullversion}

!define LVM_GETITEMCOUNT 0x1004
!define LVM_GETITEMTEXT 0x102D
 
Function DumpLog
  Exch $5
  Push $0
  Push $1
  Push $2
  Push $3
  Push $4
  Push $6
 
  FindWindow $0 "#32770" "" $HWNDPARENT
  GetDlgItem $0 $0 1016
  StrCmp $0 0 exit
  FileOpen $5 $5 "w"
  StrCmp $5 "" exit
    SendMessage $0 ${LVM_GETITEMCOUNT} 0 0 $6
    System::Alloc ${NSIS_MAX_STRLEN}
    Pop $3
    StrCpy $2 0
    System::Call "*(i, i, i, i, i, i, i, i, i) i \
      (0, 0, 0, 0, 0, r3, ${NSIS_MAX_STRLEN}) .r1"
    loop: StrCmp $2 $6 done
      System::Call "User32::SendMessageA(i, i, i, i) i \
        ($0, ${LVM_GETITEMTEXT}, $2, r1)"
      System::Call "*$3(&t${NSIS_MAX_STRLEN} .r4)"
      FileWrite $5 "$4$\r$\n"
      IntOp $2 $2 + 1
      Goto loop
    done:
      FileClose $5
      System::Free $1
      System::Free $3
  exit:
    Pop $6
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
    Exch $5
FunctionEnd

/**
 * Installer
 */

Section Install Install
  
  !ifndef uninstaller

    SectionIn 1 2

    SetOutPath $INSTDIR
    File forum.ico
    File "${build.dir}\installer\windows\GeoGebra-no3D.exe"
    File "${build.dir}\installer\windows\GeoGebra-JOGL1.exe"
    File "${build.dir}\installer\windows\GeoGebra-JOGL2.exe"
    File "${build.dir}\unpacked\OpenGeoProver.jar"
    File "${build.dir}\unpacked\geogebra*.jar"
    File "${build.dir}\unpacked\gluegen-rt-jogl1.jar"
    File "${build.dir}\unpacked\*win*.jar"
    File "${build.dir}\unpacked\jogl.jar"
    File "${build.dir}\unpacked\gluegen-rt.jar"
    File "${build.dir}\unpacked\jd2*.jar"
    File "${build.dir}\unpacked\jl*.jar"
    File "${build.dir}\unpacked\jogl.all.jar"
    File "${build.dir}\unpacked\jython.jar"
    File "${build.dir}\..\lib\javagiac.dll"
    File "${build.dir}\..\lib\javagiac64.dll"
    File gpl-3.0.txt
    File by-nc-sa-3.0.txt
    File /r "${build.dir}\..\installer\jre\win32\jre"

    Call Architecture

    ZipDLL::extractall "$INSTDIR\jogl1-windows-$ARCHITECTURE.jar" "$INSTDIR"
    RMDir /r $INSTDIR\META-INF

    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
    SetOutPath ""
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (3D with JOGL1).lnk" $INSTDIR\GeoGebra-JOGL1.exe "" $INSTDIR\GeoGebra-JOGL1.exe 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (3D with JOGL2).lnk" $INSTDIR\GeoGebra-JOGL2.exe "" $INSTDIR\GeoGebra-JOGL2.exe 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (without 3D).lnk" $INSTDIR\GeoGebra-no3D.exe "" $INSTDIR\GeoGebra-no3D.exe 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra Forum.lnk" http://www.geogebra.org/forum/ "" $INSTDIR\forum.ico 0
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebraTube.lnk" http://www.geogebratube.org/ "" $INSTDIR\GeoGebra.exe 0
    ${If} 1 = $DESKTOP_ALL
    ${OrIf} 1 = $DESKTOP_CURRENT
      Call PushShellVarContext
      ${If} 1 = $DESKTOP_ALL
        SetShellVarContext all
      ${ElseIf} 1 = $DESKTOP_CURRENT
       SetShellVarContext current
      ${EndIf}
      CreateShortCut "$DESKTOP\GeoGebra (3D with JOGL2).lnk" $INSTDIR\GeoGebra-JOGL2.exe "" $INSTDIR\GeoGebra-JOGL2.exe 0
      Call PopShellVarContext
    ${EndIf}
    ${If} 1 = $QUICK_LAUNCH
      CreateShortCut "$QUICKLAUNCH\GeoGebra (3D with JOGL2).lnk" $INSTDIR\GeoGebra-JOGL2.exe "" $INSTDIR\GeoGebra-JOGL2.exe 0
    ${EndIf}
    
    Call PushShellVarContext
    ${If} 1 = $ASSOCIATE_ALL
      SetShellVarContext all
    ${ElseIf} 1 = $ASSOCIATE_CURRENT
      SetShellVarContext current
    ${EndIf}
    
    ${If} 1 = $ASSOCIATE_GGB
      WriteRegStr SHCTX Software\Classes\.ggb "" GeoGebra.File
      WriteRegStr SHCTX Software\Classes\.ggb "Content Type" application/vnd.geogebra.file
      WriteRegStr SHCTX Software\Classes\GeoGebra.File "" "GeoGebra File"
      WriteRegStr SHCTX Software\Classes\GeoGebra.File\DefaultIcon "" $INSTDIR\GeoGebra-JOGL2.exe,0
      WriteRegStr SHCTX Software\Classes\GeoGebra.File\shell\open\command "" '"$INSTDIR\GeoGebra-JOGL2.exe" "%1"'
      WriteRegStr SHCTX "Software\Classes\MIME\Database\Content Type\application/vnd.geogebra.file" Extension .ggb
    ${EndIf}
    ${If} 1 = $ASSOCIATE_GGT
      WriteRegStr SHCTX Software\Classes\.ggt "" GeoGebra.Tool
      WriteRegStr SHCTX Software\Classes\.ggt "Content Type" application/vnd.geogebra.tool
      WriteRegStr SHCTX Software\Classes\GeoGebra.Tool "" "GeoGebra Tool"
      WriteRegStr SHCTX Software\Classes\GeoGebra.Tool\DefaultIcon "" $INSTDIR\GeoGebra-JOGL2.exe,0
      WriteRegStr SHCTX Software\Classes\GeoGebra.Tool\shell\open\command "" '"$INSTDIR\GeoGebra-JOGL2.exe" "%1"'
      WriteRegStr SHCTX "Software\Classes\MIME\Database\Content Type\application/vnd.geogebra.tool" Extension .ggt
    ${EndIf}
    ${If} 1 = $ASSOCIATE_GGB
    ${OrIf} 1 = $ASSOCIATE_GGT
      System::Call "shell32::SHChangeNotify(i 0x08000000, i 0x1000, i 0, i 0)"
    ${EndIf}
    
    System::Call "*(&i2, &i2, &i2, &i2, &i2, &i2, &i2, &i2) i .r0"
    System::Call "kernel32::GetLocalTime(i r0)"
    System::Call "*$0(&i2 .r1, &i2 .r2, &i2, &i2 .r3, &i2, &i2, &i2, &i2)"
    IntCmp $2 9 0 0 +2
    StrCpy $2 "0$2"
    IntCmp $3 9 0 0 +2
    StrCpy $3 "0$3"
    SectionGetSize ${Install} $0
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" Contact office@geogebra.org
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" DisplayIcon $INSTDIR\GeoGebra-JOGL2.exe,0
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" DisplayName "GeoGebra 5.0"
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" DisplayVersion ${fullversion}
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" EstimatedSize $0
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" HelpLink http://www.geogebra.org/forum/
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" InstallDate $1$2$3
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" InstallLocation $INSTDIR
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" Language $LANGUAGE
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" NoModify 1
    WriteRegDWORD SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" NoRepair 1
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" Publisher "International GeoGebra Institute"
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" SettingsIdentifier Software\JavaSoft\Prefs\geogebra
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" UninstallString '"$INSTDIR\uninstaller.exe"'
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" URLInfoAbout http://www.geogebra.org/
    WriteRegStr   SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0" URLUpdateInfo http://www.geogebra.org/download/
    Call PopShellVarContext
    
    SetOutPath $INSTDIR
    Call PushShellVarContext
    Pop $R0
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks DESKTOP_ALL       $DESKTOP_ALL
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks DESKTOP_CURRENT   $DESKTOP_CURRENT
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks QUICK_LAUNCH      $QUICK_LAUNCH
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_ALL     $ASSOCIATE_ALL
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_CURRENT $ASSOCIATE_CURRENT
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_GGB     $ASSOCIATE_GGB
    WriteINIStr $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_GGT     $ASSOCIATE_GGT
    WriteINIStr $INSTDIR\uninstaller.ini Paths           INSTDIR           $INSTDIR
    WriteINIStr $INSTDIR\uninstaller.ini Paths           STARTMENU_FOLDER  $STARTMENU_FOLDER
    WriteINIStr $INSTDIR\uninstaller.ini Permissions     ShellVarContext   $R0
    
    File "${build.dir}\installer\windows\uninstaller.exe"

    /* Logging */
    StrCpy $0 "$INSTDIR\install.log"
    Push $0
    Call DumpLog

    
  !endif
  
SectionEnd

/**
 * Installer Functions
 */

Function .onInit

  !ifndef uninstaller
    !insertmacro UMUI_MULTILANG_GET
    System::Call 'kernel32::CreateMutexA(i 0, i 0, t "GeoGebraInstaller") ?e'
    Pop $R0
    ${IfNot} 0 = $R0
      System::Call "kernel32::GetCurrentProcessId() i .R0"
      System::Call "kernel32::CreateToolhelp32Snapshot(i 0x00000002, i 0) i .r0"
      System::Call "*(i, i, i, i, i, i, i, i, i, &t1024) i .r1"
      System::Call "*$1(_, &l0 .r2)"
      System::Call "*$1(i r2, i, i, i, i, i, i, i, i, &t1024)"
      System::Call "kernel32::Process32First(i r0, i r1) i .r2"
      ${DoWhile} 1 = $2
        System::Call "*$1(i, i, i .R1, i, i, i, i .R2, i, i, &t1024)"
        ${If} $R0 = $R1
          ${Break}
        ${EndIf}
        System::Call "kernel32::Process32Next(i r0, i r1) i .r2"
      ${Loop}
      System::Free $1
      System::Call "kernel32::CloseHandle(i r0)"
      
      StrCpy $0 0
      StrCpy $1 0
      ${Do}
        FindWindow $0 "#32770" "" 0 $0
        ${If} 0 = $0
        ${OrIfNot} $R0 = $R1
          ${If} 0 = $1
            MessageBox MB_OK|MB_ICONEXCLAMATION "The installer is already running!"
          ${Else}
            System::Call "user32::ShowWindow(i r1, i 9)"
            System::Call "user32::SetForegroundWindow(i r1)"
          ${EndIf}
          Abort
        ${Else}
          System::Call "user32::GetWindowText(i r0, t .r2, i 1024) i .r3"
          IntOp $3 $3 - 1
          StrCpy $2 $2 $3
          ${If} "GeoGebra Installer" == $2 # CAPTION!
            System::Call "user32::GetWindowThreadProcessId(i r0, *i .r2)"
            ${If} $2 = $R2
              ${Break}
            ${ElseIf} 0 = $1
              StrCpy $1 $0
            ${EndIf}
          ${EndIf}
        ${EndIf}
      ${Loop}
    ${EndIf}
    
    Call Administrator
    ${If} "true" == $ADMINISTRATOR
      SetShellVarContext all
      StrCpy $UMUI_DEFAULT_SHELLVARCONTEXT all
      StrCpy $INSTDIR "$PROGRAMFILES\GeoGebra 5.0"
    ${Else}
      SetShellVarContext current
      StrCpy $UMUI_DEFAULT_SHELLVARCONTEXT current
      StrCpy $INSTDIR "$PROFILE\GeoGebra 5.0"
    ${EndIf}
    
    StrCpy $VERSION ""
    StrCpy $WORKSTATION ""
    System::Call "*(i, i, i, i, i, &t128, &i2, &i2, &i2, &i1, &i1) i .r0"
    System::Call "*$0(_, &l0 .r1)"
    System::Call "*$0(i r1, i, i, i, i, &t128, &i2, &i2, &i2, &i1, &i1)"
    System::Call "kernel32::GetVersionExA(i r0) i .r1"
    ${IfNot} 0 = $1
      System::Call "*$0(i, i .R0, i .R1, i, i, &t128, &i2, &i2, &i2, &i1 .R2, &i1)"
      /**
       * Windows Versions
       *
       * 5.0 -> Windows 2000  | Windows Server 2000
       * 5.1 -> Windows XP
       * 5.2 ->                 Windows Server 2003 (R2)
       * 6.0 -> Windows Vista | Windows Server 2008
       * 6.1 -> Windows 7     | Windows Server 2008 R2
       */
      StrCpy $VERSION $R0.$R1
      ${If} 1 = $R2
        StrCpy $WORKSTATION 1
      ${Else}
        StrCpy $WORKSTATION 0
      ${EndIf}
    ${EndIf}
  !else
    WriteUninstaller "${uninstaller}uninstaller.exe"
  !endif
FunctionEnd

/**
 * Uninstaller Macros
 */

!macro RemoveUninstaller
  DeleteRegKey SHCTX "Software\Microsoft\Windows\CurrentVersion\Uninstall\GeoGebra 5.0"
  Delete $INSTDIR\uninstaller.ini
  Delete $INSTDIR\uninstaller.exe
!macroend

/**
 * Uninstaller
 */

!ifdef uninstaller
  
  Section Uninstall
    SectionIn 1
    
    !insertmacro RemoveUninstaller
    
    Delete $INSTDIR\forum.ico
    Delete $INSTDIR\GeoGebra-no3D.exe
    Delete $INSTDIR\GeoGebra-JOGL1.exe
    Delete $INSTDIR\GeoGebra-JOGL2.exe
    Delete $INSTDIR\*.jar
    Delete $INSTDIR\*.dll
    Delete $INSTDIR\gpl-3.0.txt
    Delete $INSTDIR\by-nc-sa-3.0.txt
    Delete $INSTDIR\install.log
    RMDir /r $INSTDIR
    
    Delete "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (3D with JOGL1).lnk"
    Delete "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (3D with JOGL2).lnk"
    Delete "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra (without 3D).lnk"
    Delete "$SMPROGRAMS\$STARTMENU_FOLDER\GeoGebra Forum.lnk"
    Delete $SMPROGRAMS\$STARTMENU_FOLDER\GeoGebraTube.lnk
    RMDir $SMPROGRAMS\$STARTMENU_FOLDER
    
    ${If} 1 = $DESKTOP_ALL
    ${OrIf} 1 = $DESKTOP_CURRENT
      Call un.PushShellVarContext
      ${If} 1 = $DESKTOP_ALL
        SetShellVarContext all
      ${ElseIf} 1 = $DESKTOP_CURRENT
        SetShellVarContext current
      ${EndIf}
      Delete "$DESKTOP\GeoGebra (3D with JOGL1).lnk"
      Delete "$DESKTOP\GeoGebra (3D with JOGL2).lnk"
      Delete "$DESKTOP\GeoGebra (without 3D).lnk"
      Call un.PopShellVarContext
    ${EndIf}
    ${If} 1 = $QUICK_LAUNCH
      Delete "$QUICKLAUNCH\GeoGebra (3D with JOGL2).lnk"
    ${EndIf}
    
    Call un.PushShellVarContext
    ${If} 1 = $ASSOCIATE_ALL
      SetShellVarContext all
    ${ElseIf} 1 = $ASSOCIATE_CURRENT
      SetShellVarContext current
    ${EndIf}
    
    ${If} 1 = $ASSOCIATE_GGB
      DeleteRegKey SHCTX Software\Classes\.ggb
      DeleteRegKey SHCTX Software\Classes\GeoGebra.File
      DeleteRegKey SHCTX "Software\Classes\MIME\Database\Content Type\application/vnd.geogebra.file"
    ${EndIf}
    ${If} 1 = $ASSOCIATE_GGT
      DeleteRegKey SHCTX Software\Classes\.ggt
      DeleteRegKey SHCTX Software\Classes\GeoGebra.Tool
      DeleteRegKey SHCTX "Software\Classes\MIME\Database\Content Type\application/vnd.geogebra.tool"
    ${EndIf}
    ${If} 1 = $ASSOCIATE_GGB
    ${OrIf} 1 = $ASSOCIATE_GGT
      System::Call "shell32::SHChangeNotify(i 0x08000000, i 0x1000, i 0, i 0)"
    ${EndIf}
    Call un.PopShellVarContext
  SectionEnd
  
!endif

/**
 * Uninstaller Functions
 */

!ifdef uninstaller
  
  Function un.onInit
    !insertmacro UMUI_MULTILANG_GET
    
    ReadINIStr $DESKTOP_ALL       $INSTDIR\uninstaller.ini AdditionalTasks DESKTOP_ALL
    ReadINIStr $DESKTOP_CURRENT   $INSTDIR\uninstaller.ini AdditionalTasks DESKTOP_CURRENT
    ReadINIStr $QUICK_LAUNCH      $INSTDIR\uninstaller.ini AdditionalTasks QUICK_LAUNCH
    ReadINIStr $ASSOCIATE_ALL     $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_ALL
    ReadINIStr $ASSOCIATE_CURRENT $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_CURRENT
    ReadINIStr $ASSOCIATE_GGB     $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_GGB
    ReadINIStr $ASSOCIATE_GGT     $INSTDIR\uninstaller.ini AdditionalTasks ASSOCIATE_GGT
    ReadINIStr $R1                $INSTDIR\uninstaller.ini Paths           INSTDIR
    ReadINIStr $STARTMENU_FOLDER  $INSTDIR\uninstaller.ini Paths           STARTMENU_FOLDER
    ReadINIStr $R0                $INSTDIR\uninstaller.ini Permissions     ShellVarContext
    
    ${If} "all" == $R0
      SetShellVarContext all
      StrCpy $UMUI_DEFAULT_SHELLVARCONTEXT all
    ${ElseIf} "current" == $R0
      SetShellVarContext current
      StrCpy $UMUI_DEFAULT_SHELLVARCONTEXT current
    ${EndIf}
    
    IfErrors 0 NoErrors
      !insertmacro RemoveUninstaller
      MessageBox MB_OK|MB_ICONEXCLAMATION "No valid uninstaller data found!"
      Abort
      
    NoErrors:
      StrCpy $INSTDIR $R1
  FunctionEnd
  
!endif
 
