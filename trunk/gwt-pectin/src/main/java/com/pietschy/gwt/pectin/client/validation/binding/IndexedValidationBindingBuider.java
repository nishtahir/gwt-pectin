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

package com.pietschy.gwt.pectin.client.validation.binding;

import com.google.gwt.user.client.ui.UIObject;
import com.pietschy.gwt.pectin.client.validation.ListFieldValidator;
import com.pietschy.gwt.pectin.client.validation.component.IndexedValidationDisplay;
import com.pietschy.gwt.pectin.client.validation.component.StyleApplicator;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Sep 15, 2009
* Time: 12:09:52 PM
* To change this template use File | Settings | File Templates.
*/
public class IndexedValidationBindingBuider 
{
   private ListFieldValidator<?> validator;
   private ValidationBinder binder;
   private StyleApplicator styleApplicator;

   public IndexedValidationBindingBuider(ValidationBinder binder, ListFieldValidator<?> validator, StyleApplicator styleApplicator)
   {
      this.binder = binder;
      this.validator = validator;
      this.styleApplicator = styleApplicator;
   }
   
   public void to(final IndexedValidationDisplay validationDisplay)
   {
      binder.registerBinding(new IndexedValidationDisplayBinding(validator, validationDisplay));
   }
   
   public void toStyle(UIObject widget)
   {
      binder.registerBinding(new ValidationStyleBinding(validator, widget, styleApplicator));
   }
   
   
}