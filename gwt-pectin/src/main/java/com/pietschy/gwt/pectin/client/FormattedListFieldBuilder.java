/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.pietschy.gwt.pectin.client;

import com.pietschy.gwt.pectin.client.format.Format;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 12:20:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedListFieldBuilder<T>
{
   private FormModel formModel;
   private Class<T> valueType;

   protected FormattedListFieldBuilder(FormModel formModel, Class<T> valueType)
   {
      this.formModel = formModel;
      this.valueType = valueType;
   }

   public FormattedListFieldBindingBuilder<T> using(Format<T> formatter)
   {
      return new FormattedListFieldBindingBuilder<T>(formModel, valueType, formatter, new DefaultListFormatExceptionPolicy<T>());
   }

   public FormattedListFieldBindingBuilder<T> using(Format<T> formatter, ListFormatExceptionPolicy<T> exceptionPolicy)
   {
      return new FormattedListFieldBindingBuilder<T>(formModel, valueType, formatter, exceptionPolicy);
   }

}