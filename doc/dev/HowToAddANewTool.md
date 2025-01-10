# How to add a new Mode to GeoGebra
 
* in `geogebra.euclidian.EuclidianConstants.java` define a onstant
  ```
  public static final int MODE_MIRROR_AT_CIRCLE = 54;
  ```
  and in `getModeText` define the translation key used for the tool name
  ```
  case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
    return "MirrorAtCircle";
  ```

 
* in `geogebra.euclidian.EuclidianController.java`
 
  * add code to allowSelectionRectangle(), processSelectionRectangle() and mouseReleased() if appropriate
 
  * add to processMode():
    ```java
    case EuclidianView.MODE_MIRROR_AT_CIRCLE:
      changedKernel = mirrorAtCircle(view.getTopHits(hits));
      break;
    ```

 * add method `mirrorAtCircle(ArrayList<GeoElement> hits)`

* in `org.geogebra.common.gui.toolbar.Toolbar` add the tool to toolbar definition for Classic
  ```
  sb.append(EuclidianView.MODE_MIRROR_AT_CIRCLE);
  ```
  Similar changes are needed in the unbundled apps, e.g. `common.gui.toolcategorization.impl.GraphingToolCollectionFactory`
 
* Add the name and help to translation system (for local testing you can also add them to `menu.properties` for Android and Classic5 and `properties_keys_en.js` for web)
  ```
  MirrorAtCircle=Mirror point at circle
  MirrorAtCircle.Help=Point and Circle
  ```

* Make a new icon: 
  * SVG needed for web (see `ToolbarSvgResources` for list of SVGs and `GGWToolbar` for ID -> SVG mapping)
  * 32x32px and 64x64px PNG files needed for desktop/Android/iOS. If you don't want to add the tool to Classic 5, an exception in `ResourceAvailability` test is needed.

**Note:** when testing with Classic5, don't forget Options -> Restore Default Settings so that you can see the new Tool :)
