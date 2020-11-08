# Welcome to GeoGebra!
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fgeogebra%2Fgeogebra&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=PAGE+VIEWS&edge_flat=false)](https://hits.seeyoufarm.com)

The primary site for [GeoGebra](https://www.geogebra.org)'s source code is
https://dev.geogebra.org/svn/trunk/geogebra/ which
is a read-only Subversion repository. We maintain a copy of it
at https://github.com/geogebra/geogebra.

Please read https://www.geogebra.org/license about GeoGebra's
licensing. A copy of this license should also be found in the file
desktop/src/nonfree/resources/org/geogebra/desktop/_license.txt.

To learn more on development of GeoGebra (including suggested compilation
steps) please visit https://dev.geogebra.org/.

## Running the web version
To start the web version from command line, run

```
./gradlew :web:run
```

This will start a development server on your machine where you can test the app. 
If you need to access the server from other devices, you can specify a binding address

```
./gradlew :web:run -Pgbind=A.B.C.D
```

where `A.B.C.D` is your IP address. 
Then you can access the dev server through `http://A.B.C.D:8888`.
You can also run `./gradlew :web:tasks` to list other options.

## Running the desktop version (Classic 5)
To start the desktop version from command line, run

```
./gradlew :desktop:run
```
You can also run `./gradlew :desktop:tasks` to list other options.

## Setup the development environment

* Open IntelliJ. If you don't have IntelliJ on your computer yet 
then you can download and install it from [here](https://www.jetbrains.com/idea/download)
* In the menu select File / New / Project from Version Control / Git
* In the new window add the following path: `https://git.geogebra.org/ggb/geogebra.git`
* Click on ‘Checkout’, select your preferred destination folder, select Java 1.8 as the SDK, 
click on OK and wait…
* After the project is checked out, select the root folder of the project, 
open the Run Anything tool (Double ^ on Mac) and run the following command: 
`./gradlew :web:run`
* After a minute or two the GWT UI will appear
* After the Startup URLs are loaded on the UI, select the app that you wish start. For example, 
if you select `graphing.html` and click on Launch Default Browser 
then the Graphing Calculator app with the newest features 
will load and start in your default browser 
