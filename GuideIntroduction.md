# Introduction #
Think of something to put here...

Kaspar Fischer has writen a good introduction to the basics of models and binders at http://code.google.com/p/gwt-pectin-docs/wiki/PectinAPIOverview

# The nice thing about ValueModels, ListModels and Commands and things #
One of the really nice things about using ValueModels and ListModels is that you can define all kinds of observable state using these simple interfaces without having to create getters/setters and different event/handler types all over the place.

For example, if we had an object that had a `text` property of type `String`, instead of creating `setText`/`getText`/`addTextChangeHandler` methods and events and what not we can just define a `text()` method (or public final field) that returns a `ValueModel<String>`.

With this approach our bindings only need to work with few simple types, but those types can be use wired together in anyway you need.  It also lends itself to easy reuse of basic building blocks like functions, formats, converters and the like.

## The example that tries to use a ValueModel, ListModel and Command all in one ##
In this exmample we'll create a button that deletes a list of selected items (held in a `ListModel`) and that has some wiz-bang dynamic state.  Lets pretend our UX overlords have stated that the button shall dynamically update as follows:
  1. It must be disabled when the selection is empty
  1. It must display "Delete" for single selections and "Delete All" for multiple.

To do this, we'll use a `UiCommand` along with a `ListModel<T>` for the selection and a `ValueModel<String>` for the button text.  UiCommands come with enable/disable built in so we only need to create a new model for our dynamically changing text.  But first lets define a simple controller we can use.

```
// Our controller that has a selection...
public interface MyController<T> {
  ListModel<T> selection();
  void removeItems(Collection<T> itemsToRemove);
}
```

And now we can define our delete command.
```
public class DeleteCommand extends AbstractUiCommand {

  private ListController<T> controller;
  private ListModel<T> selectedItems;
  private ValueModel<String> text;

  public DeleteCommand(MyController<T> controller) {
    this.controller = controller;
    this.selectedItems = controller.selection();

    // we'll disable when emtpy using a computed model
    disableWhen(Conditions.listOf(selectedItems).isEmtpy());

    // lets compute our text from the size of the selection using a Reduce function...
    text = Functions.computedFrom(selectedItems)
      .using(new Reduce<String, T>() {
         public String compute(List<? extends T> items) {
            return items.size() < 2 "Delete" : "Delete All"; 
         }
       });
  }
 
  public ValueModel<String> text() {
    return text;
  }

  public void doExecute() {
     // do the delete...
     controller.removeItems(selectedItems.asUnmodifiableList());
  }
}
```

Ok, so now that we have our controller/command/models sorted lets bind it to our button.
```
// The binder does the hard work....
Binder binder = new Binder();

// Our unsuspecting button...
Button deleteButton = new Button();

// bind the click and enabled state...
binder.bind(deleteCommand).to(deleteButton);
// bind the text...
binder.bind(deleteCommand.text()).toTextOf(deleteButton);
```

### ..but I don't want to put view specific text in my Command ###
I really don't mind putting things like text in a command, mainly since I see the UiCommand as the "model" for that button.   So if the text changes it needs to be modelled somewhere and the command seems a pretty good place for it.  But if that's not your thing, or if the command needed to be used with multiple buttons with different text you could replace `DeleteCommand.text()` with `DeleteCommand.multipleSelected()` and use a format (or function) in the view.
```
   // define a multipleSelected model instead of text...
   ValueModel<Boolean> multipleSelected = sizeOf(selectedItems).isGreaterThan(1);

   // Please note: this is slightly false advertising here, the sizeOf(..) stuff
   // isn't in the current release.
```

And then define a format...
```
DisplayFormat<Boolean> deleteFormat = new DisplayFormat<Boolean>() {
  public String format(Boolean multiple) {
    return multiple ? "Delete All" : "Delete";
  }
}
```
And use it in our view.
```
binder.bind(deleteCommand.multipleSelected())
  .toTextOf(deleteButton)
  .withFormat(deleteFormat);
```

### Taking the example further ###
To this point, the example has been a little simplistic, we didn't ask the user to confirm the drastic action of deleting things and it's also highly likely the command needs to do it's thing in an async manner.  UiCommands support interceptors so prompting the user is as simple as:
```
deleteCommand.interceptUsing(new Interceptor() {
  public void intercept(Invocation invocation) {
    // prompt the user in an async/non-blocking way.  We should really
    // give different messages based on selectedItems.size() but 
    // I'm too lazy for the demo..
    messageService.ask("Do you really want to delete?")
      .onChoosingDestructive("Yes, destroy them all!!!").thenExecute(invocation.getProceedCommand())
      .onChoosing("Cancel").thenClose()
      .show();
  }
);

// * MessageService not included.
```

In addition to regular UiCommands Pectin also has AsyncUiCommands specifically for async operations.  Async commands also define a `ValueModel<Boolean> active()` method.   It's then easy to disable our button while active by changing our disable logic to be:
```
disableWhen(is(active()).or(listOf(selectedItems).isEmpty()));
```

It also wouldn't be too hard to update the above example to have the button change it's text to to "Deleting..." while the delete was proceeding.

# The nice thing about FormModels #

TODO: Move the front page examples to here.

# Garbage collection and you #

TODO: Talk about `Binder.dispose()`, `Disposable` and `GarbageCollector`.