 # How to Add a New Object Type

 * create a new `GeoXXX` class eg `org.geogebra.common.kernel.geos.GeoPolygon`
   * note that copy() should not set label of the object
 * create a new `DrawXXX` class eg `org.geogebra.common.euclidian.draw.DrawPolygon`
 * check [how to add a new command](./HowToAddANewCommand.md) if the object needs to support dependencies on other objects
 * in GeoClass:
```java
POLYGON("Polygon", 50, false),
```
Default naming, if appropriate...
```java
			else if (isGeoPolygon()) {
				int counter = 0;
				String str;
				do {
					counter++;
					str = app.getPlain("Name.polygon") + kernel.internationalizeDigits(counter+"");;
				} while (!cons.isFreeLabel(str));
				return str;
```
... and in GgbTrans add `Name.polygon=poly` and `Polygon=Polygon` to the menu category (or add to `menu.properties` for local development)
 * in EuclidianDraw
```java
		case GeoElement.GEO_CLASS_POLYGON:
			d = new DrawPolygon(this, (GeoPolygon) geo);
			break;
```
 * `ConstructionDefaults.getDefaultType()`
```java
		case GeoElement.GEO_CLASS_POLYGON: 
			type = DEFAULT_POLYGON;
			break;
```

```java
		// polygon
		GeoPolygon polygon = new GeoPolygon(cons, null);	
		polygon.setLocalVariableLabel("Polygon");
		polygon.setObjColor(colPolygon);
		polygon.setAlphaValue(DEFAULT_POLYGON_ALPHA);
		defaultGeoElements.put(DEFAULT_POLYGON, polygon);
```

## Special behaviors
 * check if change needed in EuclidianController.removeParentPoints()
 * check if change needed in AlgoMacro.initSpecialReferences()
 * check if change needed in GeoElement.hasMoveableInputPoints()
 * check if change needed in Macro.initMacro()

## XML loading

```java
 GeoFactory.createGeoElement()
    		case "polygon":
    			...
    			return new GeoPolygon(cons, null);
```

## Default settings

You can consider adding the default to Options -> Defaults (`OptionsDefaultsD` in Classic 5).
