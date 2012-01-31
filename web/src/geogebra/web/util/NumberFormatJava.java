/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geogebra.web.util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class NumberFormatJava implements Serializable
{
    private com.google.gwt.i18n.client.NumberFormat format;

    protected NumberFormatJava()
    {

    }

    protected NumberFormatJava( com.google.gwt.i18n.client.NumberFormat format )
    {
        applyFormat( format );
    }

    protected void applyFormat( com.google.gwt.i18n.client.NumberFormat format )
    {
        this.format = format;
    }

    public final static NumberFormatJava getCurrencyInstance()
    {
        return new NumberFormatJava( com.google.gwt.i18n.client.NumberFormat.getCurrencyFormat() );
    }

    public final static NumberFormatJava getInstance()
    {
        return new NumberFormatJava( com.google.gwt.i18n.client.NumberFormat.getDecimalFormat() );
    }

    public final static NumberFormatJava getIntegerInstance()
    {
        return new NumberFormatJava( com.google.gwt.i18n.client.NumberFormat.getDecimalFormat() );
    }

    public final static NumberFormatJava getNumberInstance()
    {
        return new NumberFormatJava( com.google.gwt.i18n.client.NumberFormat.getDecimalFormat() );
    }

    public final static NumberFormatJava getPercentInstance()
    {
        return new NumberFormatJava( com.google.gwt.i18n.client.NumberFormat.getPercentFormat() );
    }

    public final String format( double number )
    {
        return format.format( number );
    }

    public Number parse(String source) throws ParseException
    {
        try
        {
            return format.parse( source );
        }
        catch ( NumberFormatException e )
        {
            throw new ParseException( e.getMessage(), 0 );
        }
    }
}