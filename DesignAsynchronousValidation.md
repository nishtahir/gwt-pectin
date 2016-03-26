# Design thoughs on supporting asynchronous validation #

# Intro #
Pectin curently lacks built in support for asynchronous validation.  I don't think this will be too difficult to add I'm I hoping I can use the new [ValueStore & RequestFactory work at Google](DesignIntegrationWithValueStore.md).  There are a few usecases I'd like to support:

  1. Validation using stand alone AsyncValidators, i.e. the same as the current approach only using async validators (no need for googles new code on this one).  A typical use case might be where you check if an account name is available (on the server) when the user leaves the "Login Id" field.
  1. Validators defined by services/models in the domain layer (i.e. hooking into ValueStore/RequestFactory and friends).

Things to think about:
  1. how to handle async errors.. rpc exceptions != meaningful error messages. There will need to be some means of converting any errors into useful messages and displaying in an application specific manner.  I don't think this will be an issue when dealing with AsyncValidators, but might be tricky if we're trying to use a backend async validation service that's auto-wired.
  1. We're now in the situation where validation is async so our `boolean validate()` method and friends will also need to become async, i.e. `void validate(AsyncCallback<Boolean> callback)` or something similar.  This isn't such an issue if the validation is pushed back from the server but it does futz up the usage somewhat in the synchronous scenario.  Perhaps there's a case for two validate methods or two plugins.

# Adding async validators to the ValidationPlugin #

For the first case of handling basic Async validation I'll need to define some new async version of the validator interfaces:
```
// using async validators
validateField(aValue).using(AsyncValidator<Integer> validator).when(...); 
validateField(aList).using(AsyncListValidator<Integer> validator).when(...); 
```

The async validator interface will likely need to return validation messages rather that take a result collector as an argument.

The Validation plugin would also need to model the fact that validation is in progress, and this would need to be reflected in the rest of the API.  Most notably `ValidationDisplay` would need to be changed to accept notification of the start and end of the validation process.  This way the display knows when to show the "I'm doing stuff" icons and what not.

```
public interface ValidationDisplay {
   public void onValidationStart();
   public void setValidtionResult(ValiationResult r);
   public void onValidationFinsihed();
   public void onValidationFailure();  // for rpc issues..
}
```

It would likely need to also expose the fact that validation is in progress as a value model, e.g.

```
// getValidationManager is a static method provided by the ValidationPlugin
ValueModel<Boolean> inProgress = getValidationManager(myform).getInProgressModel();
```

The view can use this model to display information or react without implementing `ValidationDisplay`.

There are a few gotchas to consider for the plugin, namely that it needs to ensure that async validation results aren't delivered if the validation condition (i.e. `validateField(..).using(..).when(condtion)`) has subsequently become false.


# Interacting with ValueStore #

See DesignIntegrationWithValueStore for more info.