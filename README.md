Welcome to GeoGebra!
--------------------

The primary site for [GeoGebra](https://www.geogebra.org)'s source code is
https://dev.geogebra.org/svn/trunk/geogebra/ which
is a read-only Subversion repository. We maintain a copy of it
at https://github.com/geogebra/geogebra.

Please read http://geogebra.org/license about GeoGebra's
licensing. A copy of this license should also be found in the file
desktop/src/nonfree/resources/org/geogebra/desktop/_license.txt.

To learn more on development of GeoGebra (including suggested compilation
steps) please visit http://dev.geogebra.org/.


Setup the development environment
---------------------------------

* Open IntelliJ. If you don't have IntelliJ on your computer yet 
then you can download and install it from [here](https://www.jetbrains.com/idea/download)
* In the menu select File / New / Project from Version Control / Subversion
* In the new window add the following path: http://dev.geogebra.org/svn/trunk/geogebra
* Click on ‘Checkout’, select your preferred destination folder, select Java 1.8 as the SDK, 
click on OK and wait…
* After the project is checked out, select the root folder of the project, 
open the Run Anything tool (Double ^ on Mac) and run the following command: 
./gradlew :web:runSuperdev
* After a minute or two the GWT UI will appear
* After the Startup URLs are loaded on the UI, select the app that you wish start. For example, 
if you select graphing-canary.html and click on Launch Default Browser 
then the Graphing Calculator app with the newest features 
will load and start in your default browser 
