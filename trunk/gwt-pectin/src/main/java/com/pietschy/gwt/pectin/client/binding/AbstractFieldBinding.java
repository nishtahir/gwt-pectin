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

package com.pietschy.gwt.pectin.client.binding;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.pietschy.gwt.pectin.client.FieldModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:53:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFieldBinding<T> extends AbstractBinding
{
   protected FieldModel<T> field;
   protected FieldMonitor fieldMonitor = new FieldMonitor();

   public AbstractFieldBinding(FieldModel<T> field)
   {
      this.field = field;
      registerHandler(field.addValueChangeHandler(fieldMonitor));
   }

   public FieldModel<T> getFieldModel()
   {
      return field;
   }

   public void updateTarget()
   {
      updateWidget(field.getValue());
   }

   protected abstract void updateWidget(T value);

   protected void updateModel(T value)
   {
      fieldMonitor.setIgnoreEvents(true);
      try
      {
         field.setValue(value);
      }
      finally
      {
         fieldMonitor.setIgnoreEvents(false);
      }
   }

   private class FieldMonitor implements ValueChangeHandler<T>
   {
      private boolean ignoreEvents = false;
      
      public void onValueChange(ValueChangeEvent<T> event)
      {
         if (!ignoreEvents)
         {
            T value = event.getValue();
            updateWidget(value);
         }
      }

      public void setIgnoreEvents(boolean ignoreEvents)
      {
         this.ignoreEvents = ignoreEvents;
      }
   }


   
}