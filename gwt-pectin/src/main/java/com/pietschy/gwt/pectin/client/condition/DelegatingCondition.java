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

package com.pietschy.gwt.pectin.client.condition;

import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Aug 5, 2009
* Time: 1:36:29 PM
* To change this template use File | Settings | File Templates.
*/
public class DelegatingCondition
implements ValueModel<Boolean>, HasValueChangeHandlers<Boolean>
{
   private HandlerManager handlerManager = new HandlerManager(this);
   
   private ValueModel<Boolean> delegate;

   public void setDelegate(ValueModel<Boolean> delegate)
   {
      if (this.delegate != null)
      {
         throw new IllegalStateException("delegate already set");
      }
      
      this.delegate = delegate;
      
      delegate.addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {                                                    
            ValueChangeEvent.fire(DelegatingCondition.this, event.getValue());
         }
      });
      
      ValueChangeEvent.fire(DelegatingCondition.this, delegate.getValue());
   }

   public Boolean getValue()
   {
      if (delegate == null)
      {
         return true;
      }
      else
      {
         Boolean value = delegate.getValue();
         return value != null ? value : false;
      }
   }

   public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler)
   {
      return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
   }

   public void fireEvent(GwtEvent<?> event)
   {
      handlerManager.fireEvent(event);
   }
}