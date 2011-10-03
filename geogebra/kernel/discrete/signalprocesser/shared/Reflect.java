package geogebra.kernel.discrete.signalprocesser.shared;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Reflect {
    
    /* ****************************************************** */
    // Constants
    
    // you don't need to clone any more than about 6 depth for any dataobject
    // so I wouldn't suggest increasing this value by much.
    
    private static final int DEPTH_LIMIT = 16;
    
    
    /* ****************************************************** */
    // General Commands
    
    public static Integer getinteger( Object obj , String fieldname) {
        return (Integer) getobject( obj , fieldname );
    }
    public static String getstring( Object obj , String fieldname) {
        return (String) getobject( obj , fieldname );
    }
    
    public static Object getobject( Object obj , String fieldname) {
        try {
            return getfield( obj , fieldname ).get( obj );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }
    
    public static void setinteger( Object obj , String fieldname , Object value) {
        try {
            getfield( obj , fieldname ).set( obj , value );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }
    
    
    /* ****************************************************** */
    // More General Commands
    
    public static void migrateobj( Object obj ,  Class template , Object newobj ) {
        translate( obj , template , newobj );
    }

    
    /* ****************************************************** */
    // Get Field
    
    public static Field getfield( Object obj , String fieldname) {
        try {
            Class objclass = obj.getClass();
            Field tmpfield=null;
            do {
                try {
                    tmpfield = objclass.getDeclaredField( fieldname );
                    break;
                } catch ( NoSuchFieldException _e ) { 

                    // TODO Log an error message, or do some other action.
                    
                }
                objclass=objclass.getSuperclass();
            } while ( objclass != null );
            if ( tmpfield==null ) throw new NoSuchFieldException(fieldname);
            
            // Because probably protected or private
            tmpfield.setAccessible(true);
            
            return tmpfield;
        } catch ( NoSuchFieldException e ) {
            throw new RuntimeException("NoSuchFieldException: " + e.getMessage());
        }
    }
    
    
    /* ****************************************************** */
    // Set Field
    
    public static void setfield( Object obj , String field , Object obj2 , String field2 ) {
        setfield( obj , field , getobject( obj2 , field2 ) );
    }
    
    public static void setfield( Object obj , String fieldname , Object value ) {
        try {
            Class currclass = obj.getClass();
            Field myfield = null;
            do {
                try {
                    myfield = currclass.getDeclaredField( fieldname );
                    break;
                } catch ( NoSuchFieldException _e ) { 

                    // TODO Log error message, or do some other action.    
                }
                
                currclass=currclass.getSuperclass();
            } while ( currclass != null );
            if ( myfield==null ) throw new NoSuchFieldException(fieldname);
            
            myfield.setAccessible(true);
            myfield.set( obj , value );
            myfield.setAccessible(false);
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        } catch ( NoSuchFieldException e ) {
            throw new RuntimeException("NoSuchFieldException: " + e.getMessage());
        }
    }
    
    
    /* ****************************************************** */
    // Translate : copy everything from one object to another
    //             using the template of fields provided.
    
    private static void translate( Object previous , Class template , Object newobject ) {
        if ( previous==null ) return;
        if ( newobject==null ) {
            throw new RuntimeException("Argument may not be null; please check that this argument is not null before using this method");
        }
        try {
            Class tmplclass = template;
            do {
                Field[] tmplfields = tmplclass.getDeclaredFields();
                
                for ( int x=0 ; x < tmplfields.length ; x++ ) {
                    Field field = tmplfields[x];
                    
                    // Determine whether this field should be edited
                    if ( isEditable(field)==false ) continue;
                    
                    // Otherwise, set the field
                    field.setAccessible(true);
                    
                    String name  = field.getName();
                    Object value = field.get(previous);
                    setfield( newobject , name , value );
                    
                    field.setAccessible(false);
                }
                tmplclass=tmplclass.getSuperclass();
            } while ( tmplclass != null );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }
    
    
    /* ****************************************************** */
    // Deep Clone an object and all the objects within it
    
    public static Object cloneObject( Object obj ) {
        return cloneObject( obj , 0 , DEPTH_LIMIT );
    }
    
    public static Object cloneObject( Object obj , int maxdepth ) {
        return cloneObject( obj , 0 , maxdepth );
    }
    
    private static Object cloneObject( Object obj , int count , int maxdepth) {
        if ( obj==null ) return null;
        if ( maxdepth>0 && count>=maxdepth ) {
            throw new RuntimeException("Depth cloning limit of " + maxdepth + " reached; clone failed");
        }
        
        try {
            Object newobj = newInstance( obj );
            
            Class objclass = obj.getClass();
            do {
                Field[] objfields = objclass.getDeclaredFields();
                
                for ( int x=0 ; x < objfields.length ; x++ ) {
                    Field field = objfields[x];
                    
                    // Determine whether this field should be edited
                    if ( isEditable(field)==false ) continue;
                    
                    // Otherwise, set the field
                    field.setAccessible(true);
                    
                    String name  = field.getName();
                    Object value = field.get(obj);
                    
                    if ( value!=null ) {
                	if ( value instanceof Number || value instanceof String ) {
                		// do nothing
                        } else if ( value instanceof Collection ) {
                            value = cloneCollection( (Collection)value , count+1 , maxdepth );
                        } else {
                            value = cloneObject( value , count+1 , maxdepth );
                        }
                    }
                    setfield( newobj , name , value );
                    
                    field.setAccessible(false);
                }
                objclass=objclass.getSuperclass();
            } while ( objclass != null );
            
            return newobj;
        } catch ( InstantiationException e ) {
            throw new RuntimeException("InstantiationException: " + e.getMessage());
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }

    public static Vector cloneVector( Vector oldcollection ) {
        return (Vector) cloneCollection( oldcollection , 0 , DEPTH_LIMIT );
    }
    public static Vector cloneVector( Vector oldcollection, int maxdepth ) {
        return (Vector) cloneCollection( oldcollection , 0 , maxdepth );
    }
    
    public static Collection cloneCollection( Collection oldcollection ) {
        return cloneCollection( oldcollection , 0 , DEPTH_LIMIT );
    }
    public static Collection cloneCollection( Collection oldcollection, int maxdepth ) {
        return cloneCollection( oldcollection , 0 , maxdepth );
    }
    
    private static Collection cloneCollection( Collection oldcollection , int count , int maxdepth ) {
        if ( oldcollection==null ) return null;
        if ( maxdepth>0 && count>=maxdepth ) {
            throw new RuntimeException("Depth cloning limit of " + maxdepth + " reached; clone failed");
        }
        
        try {
            Collection newcollection;
            
            // You can't use the instanceof operator in this case;
            //  because there are many OJB classes that extend Vector.
            if ( oldcollection.getClass() == Vector.class ) {
                // For efficency reasons, set the vector to the correct size
                newcollection = new Vector( oldcollection.size() );
            } else {
                newcollection = (Collection) newInstance( oldcollection );
            }
            
            Iterator iter = oldcollection.iterator();
            while ( iter.hasNext() ) {
                Object vobj = iter.next();
                
                if ( vobj instanceof Number || vobj instanceof String ) {
                    // do nothing; leave the object as is
                } else if ( vobj instanceof Collection ) {
                    vobj = cloneCollection( (Collection)vobj , count+1 , maxdepth );
                } else {
                    vobj = cloneObject( vobj , count+1 , maxdepth );
                }
                
                newcollection.add( vobj );
            }
            
            // Return the cloned collection
            return newcollection;
        } catch ( InstantiationException e ) {
            throw new RuntimeException("InstantiationException: " + e.getMessage());
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException("IllegalAccessException: " + e.getMessage());
        }
    }
    
    
    /* ****************************************************** */
    // Determines whether the field should be copied or not; also returns that static variables
    //  are not editable - though in our case we don't want to edit them so we return false.
    
    private static boolean isEditable( Field field ) {
        // Check that the field is not final or static
        int modifiers = field.getModifiers();
        if ( Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers) ) {
            return false;
        } else {
            return true;
        }
    }
    
    
    /* ****************************************************** */
    // Create a new instance of a given Class/Object
    
    public static Object newInstance( Class myclass ) throws IllegalAccessException, InstantiationException {
        return myclass.newInstance();
    }
    public static Object newInstance( Object myobject ) throws IllegalAccessException, InstantiationException {
        return myobject.getClass().newInstance();
    }
    
    
    /* ****************************************************** */
}
