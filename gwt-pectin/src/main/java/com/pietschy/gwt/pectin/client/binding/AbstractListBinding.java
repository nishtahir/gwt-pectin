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

import com.pietschy.gwt.pectin.client.list.ListModelChangedHandler;
import com.pietschy.gwt.pectin.client.list.ListModelChangedEvent;
import com.pietschy.gwt.pectin.client.list.MutableListModel;
import com.pietschy.gwt.pectin.client.ListFieldModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:53:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListBinding<T> 
extends AbstractBinding 
implements Disposable
{
   protected ListFieldModel<T> model;
   protected ListMonitor listMonitor = new ListMonitor();

   public AbstractListBinding(ListFieldModel<T> field)
   {
      this.model = field;
      registerHandler(field.addListModelChangedHandler(listMonitor));
   }
   
   public ListFieldModel<T> getFieldModel()
   {
      return model;
   }

   protected void updateModel(ModelUpdater<T> runnable)
   {
      listMonitor.setIgnoreEvents(true);
      try
      {
         runnable.update(model);
      }
      finally
      {
         listMonitor.setIgnoreEvents(false);
      }
   }
   
   public interface ModelUpdater<T> {
      public void update(MutableListModel<T> model);
   }

   private class ListMonitor implements ListModelChangedHandler<T>
   {
      private boolean ignoreEvents = false;

      public void onListDataChanged(ListModelChangedEvent<T> event)
      {
         if (!ignoreEvents)
         {
            updateTarget();
         }
      }

      public void setIgnoreEvents(boolean ignoreEvents)
      {
         this.ignoreEvents = ignoreEvents;
      }
   }
}