# Future features and design ideas #

Things that would be nice:

# Native UiBinder support #
Still not sure if this is possible, but here's some thoughts on what we'd like to be able to do: https://wave.google.com/wave/waveref/googlewave.com/w+CE2z1X_0A

# Include some standard Controllers #
I'd like to incorporate standard controllers into Pectin (in the style of Cocoa/[SproutCore](http://www.sproutcore.com/) etc).  Using Controllers in conjunction with Value/ListModel bindings allows some pretty complex behaviour to be built pretty quickly.

The following is a very quick example of a MasterDetail controller that binds a SelectionControllers selected value to an EditorController.  Selection changes are intercepted to prompt the user to save changes if required.
```
public class MasterDetailController {

  SingleSelectionController<Person> selectionController = ...;
  EditorController<Person> editorController = ...;
  Binder binder = new Binder();
  
  public MasterDetailController() {
     
    // bind the selection to our editor
    binder.bind(selectionController.selection()).to(editorController);  

    // intercept selection changes and promt the user if there are 
    // unsaved changes..
    selectionController.selection().interceptUsing(new Interceptor() {
      public void intercept(Invocation invocation) {
        if (editorController.isDirty()) {
          // promt the user in an async/non-blocking way.
          messageService.ask("Do you want to discard you changes?")
            .onChoosingDestructive("Discard Changes")
              .thenExecute(invocation.getProceedCommand())
            .onChoosing("Cancel")
              .thenClose()
            .show();
        } else {
          // no unsaved changes so proceed imeadiatly.  The selection will
          // update and the binding will update the editorController.
          invocation.proceed();
        }
      }
    }
  }
}
// * `messageService` not included.
```

Some example controllers would be:
  * SingleSelectionController & MultiSelectionController
  * EditorController - edits stuff.  Exposes models like `ValueModel<Boolean> dirty()` and `ValueModel<Boolean> editing()` along with `validate()`, `commit()` methods etc.
  * EditorListController - a much needed controller that handles binding of a `ListModel<T>` using a `Provider<EditorController<T>>` as an editor factory.  Could/would use an incremental command to build the UI and expose a "`ValueModel<Boolean> busy()`" that can be used to by the parent controller to show/hide/enable/disable things as required.
  * TaskController - Handles temporal activities, has an `ValueModel<Boolean> active()` model etc.  **Note:**  This one has progressed a bit, I now have `TaskController` and a `TaskContainer`.  `TaskContainers` have various implementations `PopupTaskContainer`, `AnimatedTaskContainer` etc so you can run tasks in various contexts.  Tasks are activated using `taskContainer.activate(taskController)`.

Most likely the first one to tackle will be `EditorController` & `EditorListController` as "how do I bind a `ListModel<T>` to a validated heirarchy of editors" is one of the more commonly asked questions (and currently you have to roll your own).

# Common Event Bindings #
Would like to see binding for common component events.
```
Binder binder = ...;

binder.onClicking(HasClickHandlers).invoke(Command);
binder.onBlurOf(HasBlurHandlers).invoke(Command);

binder.onKeyPress(HasKeyPressHandlers)
      .matching(Predicate<KeyPressEvent>)
      .invoke(Command) // or .focus(FocusWidget) or .focusDeferred(FocusWidget)

// examples
binder.onKeyPress(firstName).matching(KeyPredicates.ENTER).focus(surname);

binder.onKeyPress(surname).matching(KeyPredicates.ENTER).invoke(submitFormCommand);

// could really simplify this to if the enter predicate could be made
// reliable on all platforms....
binder.onTypingEnter(firstName).focus(surname);
```

**Note:** This will be included in 0.9.

There's also scope for bindings like:
```
binder.onSuccessOf(AsyncUiCommand).invoke(Command) // or ParameterisedCommand<T>
binder.onErrorOf(AsyncUiCommand).invoke(Command) // or ParameterisedCommand<T>
```

These are similar to the `binder.displayErrorsOf(..).using(..)` so I'd need to think about consistency.

At some point I'll need to review all the bind methods to see if they should be split into different binders or even if the `FormBinder` functionality should be merged back into Binder.  e.g. the `binder.onABC(...).invoke(...)` might belong in an `EventBinder` or similar.

# Forms #
## Form Bindings ##
Would be nice to add interceptors to the widget bindings.  This would be a more formalised way for watermarks to change the displayed value under certain conditions.

# BeanModelProvider Wish List #

**Note:** In seeing [Bindgen](http://bindgen.org/) it would be really nice to do something similar and have compile time type safety.  Bingen uses annotation processors to generate custom classes.
```
ValueModel<String> name = bindgenStyleProvider.employee().name();
```


## Validation Support ##
It would be nice if BeanModelProvider supported JSR 303 style validation.  This would allow a reflection variant of the provider to be used on the server to perform validation.  This is a fair bit of work but would support unified server side and client side validation.  I'm holding off on this to see how Google's ValueStore proceeds.  I'm also not sure how to handle conditional validation.

## Diffs ##
It might be possible for the provider to support 'diffing' between two beans.  This would allow you to display what has changed to the user when handling stale entity exceptions.

## Compile Time Safety ##
It might also also be possible to support compile time type safety using annotations.  E.g.
```
public class PersonProvider extends BeanModelProvider<Person> {
   // automatically bound to "firstName" 
   MutableValueModel<String> firstName;

   // specifies a property different from the value model name.
   @Path("surname")
   MutableValueModel<String> lastName;
}
```

If this is the case then the following would be possible.
```
// use in a FormModel
fieldOfType(String.class).boundTo(personProvider.firstName);
// or directly bound
binder.bind(personProvider.firstName).to(someField);
```


# Plugins #
## ValidationPlugin enhancements ##
Proper async validation support, see DesignAsynchronousValidation.

Would also nice to support validation triggers like ON\_BLUR, ON\_EDIT etc.  See https://wave.google.com/wave/?nouacheck&pli=1#restored:wave:googlewave.com!w%252BvIXX8JY-A

## SelectionPlugin ##
Bring focus/blur and field selection into the presentation layer.  `selectionModel.addSelectionHandler(...)`, `selectionModel.select(firstName)`.  A typical use case would be focussing a field with validation errors.  I would imagine something like `selectionModel.select(validationManager.getFirstFieldWithErrors())`.  There are issues to consider here with multi-field widgets etc but by using nested form models I think we can get around most cases.

## UndoPlugin ##
Wouldn't be too hard to implement at least basic undo/redo support.  Could also work with the selection model if required.

## Integration with Drag&Drop layout tools ##
And in my dreams... it would be to drag value models onto widgets and have the bind(model.name).to(firstName) automagically done.  I'd get to use visual layout tool (where it makes sense) and keep my OO models...