package com.pietschy.gwt.pectin.client;

import com.pietschy.gwt.pectin.client.format.Format;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 21, 2009
 * Time: 10:35:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HasFormat<T>
{
   Format<T> getFormat();

   void setFormat(Format<T> format);
}