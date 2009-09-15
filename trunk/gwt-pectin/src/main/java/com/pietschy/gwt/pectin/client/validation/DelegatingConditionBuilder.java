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

package com.pietschy.gwt.pectin.client.validation;

import com.pietschy.gwt.pectin.client.value.ValueModel;
import com.pietschy.gwt.pectin.client.value.DelegatingValueModel;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Aug 8, 2009
* Time: 1:13:14 PM
* To change this template use File | Settings | File Templates.
*/
class DelegatingConditionBuilder implements ConditionBuilder
{
   protected DelegatingValueModel<Boolean> conditionDelegate;

   public DelegatingConditionBuilder(DelegatingValueModel<Boolean> conditionDelegate)
   {
      this.conditionDelegate = conditionDelegate;
   }

   public void when(ValueModel<Boolean> condition) 
   {
      conditionDelegate.setDelegate(condition);
   }
}