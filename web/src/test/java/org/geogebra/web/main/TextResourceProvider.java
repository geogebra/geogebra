package org.geogebra.web.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwtmockito.fakes.FakeClientBundleProvider;
import com.google.gwtmockito.fakes.FakeProvider;
import com.himamis.retex.renderer.web.resources.xml.XmlResources;

public final class TextResourceProvider
		implements FakeProvider<XmlResources> {

	public class FakeTextResource implements TextResource {

		private String name;
		private String filename;

		public FakeTextResource(Method method) {
			this.name = method.getName();
			Annotation src = method.getAnnotations()[0];
			filename = src.toString().split("\\[|\\]")[1];
		}

		public String getName() {
			return name;
		}

		public String getText() {
			// TODO Auto-generated method stub
			return read("../retex/renderer-web/src/main/resources/" + filename);
		}

		private String read(String string) {
			File f = new File(string);
			StringBuilder sb = new StringBuilder();
			try {
				InputStreamReader sr = new InputStreamReader(
						new FileInputStream(f));
				BufferedReader br = new BufferedReader(sr);
				String line = br.readLine();
				do {
					sb.append(line);
					sb.append('\n');
					line = br.readLine();
				} while (line != null);
				br.close();
				return sb.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}
	/**
	 * Returns a new instance of the given type that implements methods as
	 * described in the class description.
	 *
	 * @param type
	 *            interface to be implemented by the returned type.
	 */
	@Override
	public XmlResources getFake(Class<?> type) {
		return (XmlResources) Proxy.newProxyInstance(
				TextResourceProvider.class.getClassLoader(),
				new Class<?>[] { type }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Exception {
						Class<?> returnType = method.getReturnType();
						if (TextResource.class
								.isAssignableFrom(returnType)) {
							return new FakeTextResource(method);
						} else {
							return createFakeResource(returnType,
									method.getName());
						}
					}
				});
	}

	/**
	 * Creates a fake resource class that returns its own name where
	 * possible.
	 */
	@SuppressWarnings("unchecked") // safe since the proxy implements type
	private <T> T createFakeResource(Class<T> type, final String name) {
		return (T) Proxy.newProxyInstance(
				FakeClientBundleProvider.class.getClassLoader(),
				new Class<?>[] { type }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Exception {
						Class<?> returnType = method.getReturnType();
						if (returnType == String.class) {
							return name;
						} else if (returnType == SafeHtml.class) {
							return SafeHtmlUtils.fromTrustedString(name);
						} else if (returnType == SafeUri.class) {
							return UriUtils.fromTrustedString(name);
						} else if (returnType == boolean.class) {
							return false;
						} else if (returnType == int.class) {
							return 0;
						} else if (method
								.getParameterTypes()[0] == ResourceCallback.class) {
							// Read the underlying resource type out of the
							// generic parameter
							// in the method's argument
							Class<?> resourceType = (Class<?>) ((ParameterizedType) args[0]
									.getClass().getGenericInterfaces()[0])
											.getActualTypeArguments()[0];
							((ResourceCallback<ResourcePrototype>) args[0])
									.onSuccess(
											(ResourcePrototype) createFakeResource(
													resourceType, name));
							return null;
						} else {
							throw new IllegalArgumentException(
									"Unexpected return type for method "
											+ method.getName());
						}
					}
				});
	}
}