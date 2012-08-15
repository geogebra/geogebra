/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package geogebra.server.fromclient;

import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * An object representing the state parameter passed into this application
 * from the Drive UI integration (i.e. Open With or Create New). Required
 * for Gson to deserialize the JSON into POJO form.
 *
 * @author vicfryzel@google.com (Vic Fryzel)
 */
public class State {
  /**
   * Action intended by the state.
   */
  public String action;

  /**
   * IDs of files on which to take action.
   */
  public Collection<String> ids;

  /**
   * Parent ID related to the given action.
   */
  public String parentId;

  /**
   * Empty constructor required by Gson.
   */
  public State() {}

  /**
   * Create a new State given its JSON representation.
   *
   * @param json Serialized representation of a State.
   */
  public State(String json) {
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    State other = gson.fromJson(json, State.class);
    this.action = other.action;
    this.ids = other.ids;
    this.parentId = other.parentId;
  }
}