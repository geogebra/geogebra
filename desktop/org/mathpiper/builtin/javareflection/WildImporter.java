package org.mathpiper.builtin.javareflection;

/** An Importer that can handle a wildcard, like "java.io.*". **/
public class WildImporter implements Importer {
	String prefix;

	public WildImporter(String name) {
		this.prefix = name.substring(0, name.length() - "*".length());
	}

	public Class<?> classNamed(String name) {
		try {
			return (name.startsWith(prefix)) ? Import.forName(name) : (name
					.indexOf(".") == -1) ? Import.forName(prefix + name) : null;
		} catch (java.lang.SecurityException se) {
			// Can come back from Netscape. Assume the guessed name doesn't
			// exist.
			return null;
		} catch (Throwable t) {
			E.warn(this + " " + name + " " + t);
			return null;
		}
	}

	@Override
	public boolean equals(Object x) {
		return this.getClass() == x.getClass()
				&& this.prefix == ((WildImporter) x).prefix;
	}

	@Override
	public int hashCode() {
		return this.prefix.hashCode();
	}

	@Override
	public String toString() {
		return "(import " + prefix + "*)";
	}

	public void reset() {
	}
}
