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

import java.io.Reader;

import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * An object representing a File and its content, for use while interacting
 * with a DrEdit JavaScript client. Can be serialized and deserialized using
 * Gson.
 *
 * @author vicfryzel@google.com (Vic Fryzel)
 */
public class ClientFile {
  /**
   * ID of file.
   */
  public String resource_id;

  /**
   * Title of file.
   */
  public String title;

  /**
   * Description of file.
   */
  public String description;

  /**
   * MIME type of file.
   */
  public String mimeType;

  /**
   * Content body of file.
   */
  public String content;

  /**
   * Empty constructor required by Gson.
   */
  public ClientFile() {}

  /**
   * Creates a new ClientFile based on the given File and content.
   */
  public ClientFile(File file, String content) {
    this.resource_id = file.getId();
    this.title = file.getTitle();
    this.description = file.getDescription();
    this.mimeType = file.getMimeType();
    this.content = content;
  }

  /**
   * Construct a new ClientFile from its JSON representation.
   *
   * @param in Reader of JSON string to parse.
   */
  public ClientFile(Reader in) {
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    ClientFile other = gson.fromJson(in, ClientFile.class);
    this.resource_id = other.resource_id;
    this.title = other.title;
    this.description = other.description;
    this.mimeType = other.mimeType;
    this.content = other.content;
  }

  /**
   * @return JSON representation of this ClientFile.
   */
  public String toJson() {
    return new Gson().toJson(this).toString();
  }

  /**
   * @return Representation of this ClientFile as a Drive file.
   */
  public File toFile() {
    File file = new File();
    file.setId(this.resource_id);
    file.setTitle(this.title);
    file.setDescription(this.description);
    file.setMimeType(this.mimeType);
    return file;
  }
}