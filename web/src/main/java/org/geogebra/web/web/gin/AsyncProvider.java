/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geogebra.web.web.gin;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An object capable of providing an instance of type T asynchronously via
 * {@link AsyncCallback}. The instance is created within a GWT.runAsync block
 * using the following template:
 * 
 * <pre style=code>
 *      public void get(final AsyncCallback<T> callback) {
 *        GWT.runAsync(new RunAsyncCallback() {
 *          public void onSuccess() {
 *            callback.onSuccess(Provider<T>.get());
 *          }
 *          public void onFailure(Throwable ex) {
 *            callback.onFailure(ex);
 *          }
 *        }
 *      }
 * </pre>
 * 
 */
public interface AsyncProvider<T> {

	/**
	 * @param callback
	 *            Callback used to pass the instance of T or an exception if
	 *            there is an issue creating that instance.
	 */
	void get(AsyncCallback<T> callback);
}