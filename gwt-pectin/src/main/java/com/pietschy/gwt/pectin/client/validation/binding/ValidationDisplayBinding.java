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

import com.pietschy.gwt.pectin.client.binding.AbstractBinding;
import com.pietschy.gwt.pectin.client.validation.component.ValidationDisplay;
import com.pietschy.gwt.pectin.client.validation.ValidationHandler;
import com.pietschy.gwt.pectin.client.validation.ValidationEvent;
import com.pietschy.gwt.pectin.client.validation.ValidationResult;
import com.pietschy.gwt.pectin.client.validation.FieldValidator;


/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 17, 2009
 * Time: 1:06:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationDisplayBinding
extends AbstractBinding
implements ValidationHandler
{
   private ValidationDisplay display;
   protected FieldValidator<?> validator;

   public ValidationDisplayBinding(FieldValidator<?> validator, ValidationDisplay display)
   {
      this.display = display;
      this.validator = validator;
      registerHandler(this.validator.addValidationHandler(this));
   }

   public void updateTarget()
   {
      updateDisplay(validator.getValidationResult());
   }

   public ValidationDisplay getTarget()
   {
      return display;
   }

   private void updateDisplay(ValidationResult result)
   {
      display.setValidationResult(result);
   }

   public void onValidate(ValidationEvent event)
   {
      updateDisplay(event.getValidationResult());
   }
}