/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.io;



/**
 * A specialized {@link Reader} that reads characters from a {@code String} in
 * a sequential manner.
 * 
 * @see StringWriter
 */
public class BufferedReader extends Reader {


    /**
     * Construct a new {@code BufferredReader} with {@code str} as source. The size
     * of the reader is set to the {@code length()} of the string and the Object
     * to synchronize access through is set to {@code str}.
     * 
     * @param str
     *            the source string for this reader.
     */
    public BufferedReader(Reader r) {
      
    }
    
    public int read(char[] chars, int from, int to){
    	return -1;
    }
    
    public void close(){
    	
    }
}
