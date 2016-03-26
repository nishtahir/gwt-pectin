# Validation Plugin #
You use the validation plugin by including static methods from `ValidationPlugin`.

```
// Import the validation methods into your form model..
import static com.pietschy.gwt.pectin.validation.ValidationPlugin.*;

public MyModel() {

  // Create models as normal
  givenName = fieldOfType(String.class).boundTo(provider, "givenName");
  surname = fieldOfType(String.class).boundTo(provider, "surname");
  age = formattedFieldOfType(Integer.class).boundTo(provider, "age");
  likesCheese = fieldOfType(Boolean.class).boundTo(provider, "likesCheese")
  whyILikeCheese = fieldOfType(String.class).boundTo(provider, "whyILikeCheese")

  // and add some validation rules using the ValidationPlugin imported earlier.
  validateField(givenName).using(new NotEmptyValidator("Given name is required"));
  validateField(surname).using(new NotEmptyValidator("Surname is required"));
   
  // formatted fields can also be validated with their formatter.
  validateField(age).usingFieldFormat();

  // or with a text validator that operates on the raw string
  validateField(age).usingTextValidator(new FancyTextValidator());

  // or with a regular validator that operates on the parsed value.
  validateField(age).using(new AgeOver18Validator()); 

  // now we create a conditional validation.
  validateField(whyILikeCheese)
     .using(new NotEmptyValidator("Please share your cheese passion with us."))
     .when(likesCheese);
}

public boolean validate() {
  // validate the form using ValidationPlugin.getValidationManager(FormModel);
  return getValidationManager(this).validate();
}
```

If your widgets implement `ValidationDisplay` then they will automatically notified when ever the validation results change (via the binding process).  Otherwise you can use the `ValidationBinder` to bind the validation results to any widget that implements `ValidationDisplay`.

```
public class MyForm extends Composite {
  
  // define our binder and widgets as normal..
  FormBinder binder = ...;
  ...
  
  // We'll use some additional validation bindings..
  ValidationBinder validation = new ValidationBinder();

  // a widget that implements ValidationDisplay
  ValidationDisplay givenNameValidationMessages = new ValidationDisplayLabel();
 
  public MyForm(MyModel model) {

    // bind our widgets as normal, if the widget implements ValidationDisplay the plugin
    // will ensure it'll automatically notified of validation events.
    binder.bind(model.givenName).to(givenNameField);
    ...

    // We can also use the validation binder to bind the validation state to any
    // instance of ValidationDisplay
    validation.bindValidationOf(model.givenName).to(givenNameValidationMessages); 

    // We can also bind the standard validation style names (e.g. `validationError` 
    // to arbitrary widgets.
    validation.bindValidationOf(model.giveName).toStyleOf(givenNameLabel);

    // bind the rest
    ...
  }
}
```

It's also possible to bind the results of the whole form to anything that implements ValidationDisplay.

```
ValidationBinder validation = ...;
validation.bind(model).to(validationDisplay);
```


## Wishlist & Future Ideas ##
https://wave.google.com/wave/waveref/googlewave.com/w+vIXX8JY-A