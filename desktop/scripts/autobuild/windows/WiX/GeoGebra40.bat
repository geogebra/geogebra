:: GeoGebra 4.0 startup file

:: Copyright (C) 2011 GeoGebra Inc., All rights reserved

:: @author Zoltan Kovacs <kovzol@geogebra.org>



@echo off

:: Searching for the Java Runtime Environment (JRE)

:: (http://stackoverflow.com/questions/1339910/how-can-i-detect-the-installed-sun-jre-on-windows)

FOR /F "tokens=2* delims=	 " %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Runtime Environment" /v CurrentVersion') DO SET CurrentVersion=%%B
if "%CurrentVersion%"=="" goto NoJava
:: Here additional tests could be done for exact version (TODO)

java -Xms32m -Xmx1024m -jar geogebra.jar --settingsfile=geogebra.properties

if errorlevel 1 goto BadDirectory
exit /b 0

:NoJava
call :Message "You need (Sun/Oracle) Java version 1.5 or above to run GeoGebra 4.0.
 You will be redirected to the downloading page immediately."
:: Waiting 5 seconds
PING -n 5 127.0.0.1 > NUL
start http://java.com/en/download/index.jsp
exit /b 1

:BadDirectory
call :Message "Probably you run this batch from the wrong directory. Please reinstall GeoGebra."
exit /b 2

:Message
SET MF=%Temp%.\GeoGebraMessage.html
echo ^<html^>^<center^>^<table width=500 height=500^>^<tr^>^<td width=500 height=500^>^<center^> > %MF%
echo ^<img src=geogebra.png align=center^>^<br^>^<b^>Error occurred on GeoGebra 4.0 startup^</b^>^<br^> >> %MF%
echo %~1 >> %MF%
echo ^</td^>^</tr^>^</html^> >> %MF%
copy geogebra.png %Temp%
start %MF%
