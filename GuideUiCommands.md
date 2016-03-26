# UiCommands #

UiCommands are an experimental addition to pectin to model "things that do stuff" in a similar fashion to Swing's `Action` class.  There are two basic types - a basic synchronous version (`UiCommand`) and an async version (`TemporalUiCommand`) of which there are two types - `AsyncUiCommand` and `IncrementalUiCommand`.

All UiCommmands implement GWTs `Command` interface along with providing an `enabled` model.  The async commands also provide an `active` model that's `true` while the activity is in progress.

Checkout the UiCommands & Buttons tab [on the demo](http://scratchpad.pietschy.com/pectin/PectinDemo.html).  You can read [the source code](http://code.google.com/p/gwt-pectin/source/browse/trunk/demo/src/main/java/com/pietschy/gwt/pectin/demo/client/activity/) for more info.

# Concepts #
One of the difficulties in developing a command style infrastructure is that the effects of operations can be wide spread.  The commands often interact with the model both before and after the operation, their errors need to be displayed in the view and the view probably needs to display indication that the activity is in progress.  Supervising controllers may need to know when an activity is complete or has failed.

The UiCommand code has the following types and concepts:
  * **UiCommand** - the base type for all commands.  It extends `Command` and provides an `enabled()` method that returns a `ValueModel<Boolean>` representing the enabled state of the activity.
  * **TemporalUiCommand** - (is there a better name??) an interface that exends `UiCommand` for commands  that execute asynchronously, i.e. execute returns before the command is completed.  `TemporalUiCommands` also provide and `active()` method that returns a `ValueModel<Boolean>` representing the active state of the activity.  The two main implementations are `IncrementalUiCommand` and `AbstractAsyncUiCommand`.
  * **AsyncUiCommand`<R,E>`** - a `TemporalUiCommand` that performs some async operation and then publishes the results (either successes of type R or errors of type E).  These commands can publish errors of any type so can be used to transform Throwables into a specific error type or message or message key.  Implementations typically extend `AbstractAsyncUiCommand`.
  * **Channel`<T>`** - `Channels` are a basic event mechanism that allow simple connection of event produces to consumers.  `AsyncUiCommands` have a result channel and an error channel.
  * **AsyncEvents`<R,E>`** - `AsyncUiCommands` provide event hooks so you can perform actions on start, end and error or send the results or errors to various locations.  There are two variants, `always()` and `onNextCall()` events.  e.g. `save.always().sendResultTo(someDestination)` or  `save.onNextCall().invokeOnSuccess(invocation.getProceedCommand())`.   Behind the scenes this just subscribes to the appropriate channel.
  * **Interceptors** - Allow you to intercept commands before they start.  This allows the view to interact with the user prior to the command executing.  The intercption is callback based so supports asynchronous operation (i.e. prompting via a dialog or other means).
  * **!Binder** - Allows the view to bind UiCommands to buttons (`binder.bind(saveCommand).to(saveButton)`) and various widgets (`HasValue`, `HasText`, `HasHTML` and others) to Channels.  Since activities have an active state you can easily show and hide things while they're running e.g. `binder.show(saveMessage).when(saveCommand.active())`.


# UiCommands #
Your typical basic non-asyn UiCommand should extend `AbstractUiCommand` and implement the `doExecute()` method.

```
public class MyUiCommand extends AbstractUiCommand
{
   @Override
   protected void doExecute()
   {
      // do stuff   
      ...
   }
}
```


Once created you can bind your command to buttons using `FormBinder`.
```
binder.bind(myUiCommand).to(someButton);
```

You can configure your commands `enabled` state using either the `enableWhen(ValueModel<Boolean>)` or ```disableWhen(ValueModel<Boolean>)`` methods.
```
public class MyUiCommand extends AbstractUiCommand
{
   public MyUiCommand(ValueModel<Boolean> someCondition)
   {
      // lets only enable if the condition is met.
      enableWhen(someCondition);
   }

   @Override
   protected void doExecute()
   {
      // do stuff   
      ...
   }
}
```

Executing the command while it's disabled will result in an `ExecutedWhileDisabledException`.  This behaviour can be changed by overriding the `onDisabledExecution()` method.

# IncrementalUiCommand #
IncrementalUiCommands are the UiCommand equivalent of GWTs `IncrementalCommand` (they use an `IncrementalCommand` internally to do the actual work).  I typically use them to build large views in an incremental fashion and then use the `active()` model to disable other parts of the view/UI while the build is in progress.  I also override `onReentrantExecution()` to restart the process from scratch.  This allows the user interupt or change the process before the command has finished (if you don't override it a `ReentrantExecutionException` will be thrown).

```
public class BuildUiCommand extends IncrementalCommand
{
   @Override
   public boolean doIncrementalWork()
   {
      // do a bit of the work
      ...
      return !finished;
   }   
}
```

Other parts of the UI can use the active state to control other parts of the UI.
```
BuildUiCommand buildUiCommand = ...;
someOtherCommand.disableWhile(buildUiCommand.active()); 
```

This particular approach works well with delegating commands described below.


# AsyncUiCommands #

AsyncUiCommands are designed for calling async services.  They fire events for success and errors along with the normal start and finish events of other UiCommands.  AsyncUiCommands are parameterised by their return and error types.  The following is a simple example that save the changes from an editor.

TODO: Update to include `ExceptionManager`...
```
// A basic command that takes a value from and editor and saves it using an
// async service interface
public class SaveCommand extends AbstractAsyncUiCommand<Customer, ErrorMessage>
{
   private EditorModel<Customer> editorModel;
   private CustomerServiceAsync customerService;
   
   public SaveCommand(EditorModel<Customer> editor, CustomerServiceAsync customerService)
   {
      this.editorModel = editorModel;
      this.customerService = customerService;
   }

   @Override
   public void intercept(Invocation invocation)
   {
      if (editorModel.validate())
      {
         editorModel.commit();
         invocation.proceed()
      }
   }

   @Override
   protected void performAsyncOperation(final AsyncCommandCallback<Customer, ErrorMessage> callback)
   {
      // do the work.
      customerService.saveCustomer(editorModel.getValue(), new AsyncCallback<Customer>() 
      {
         public void onSuccess(Customer customer)
         {
            callback.publishSuccess(customer);
         }
          
         public void onFailure(Throwable error)
         {
            ErrorMessage errorMessage = convertExceptionToErrorMessage(error); 
            callback.publishError(errorMessage);
         }
      });
   }
}
```

In real life you'd probably want to ensure the customer update was published to all interested parties.  This is something you could easily use events and channels or an event bus to accomplish.

The view can then bind the command to a button and to an error display as required.

```
FormBinder binder = ...;

binder.bind(saveCommand).to(saveButton);
binder.displayErrorsOf(saveCommand).using(myErrorDisplay);

```

And finally you can easily disable your command while it's active using the following.

```
// our constructor..
public SaveCommand(EditorModel<Customer> editor, CustomerServiceAsync customerService)
{
   this.editorModel = editorModel;
   this.customerService = customerService;

   // disable our selves while we're active..
   disableWhen(active());
}
```

# Events #

All commands allow you to hook additional behaviour into the various lifecycle events generated by commands.

There are two scopes for events, `always()` events and `onNextCall()` events.  The following are some examples.
```
myCommand.always().onStartSend("Starting...").to(aDestination);
myCommand.always().sendResultTo(aValueModel);
myCommand.always().sendResultTo(anUpdateNotificationChannel);
myCommand.always().sendErrorTo(myErrorDisplay);
```

The `onNextCall()` events allow you to handle the cases where you'd like execute some behaviour on only the next call to the command.  For example you might like prompt the user to save before closing.

## How do you use events? ##
Thanks for asking, I tend to have my FormModel define Channels for notifications and/or errors and then have my command send events to those.  The view then binds to those channels.  The main advantage of this is that the view only has to bind to one error channel and not the errors of each command.

```
// e.g. in my save command constructor. 
public SaveCommand(EditorModel<Customer> model, CustomerServiceAsync customerService)
{
   this.editorModel = model;
   this.customerService = customerService;

   // send our errors to an error channel
   always().sendErrorTo(editorModel.errorChannel);

   // and send our result back to the model.
   always().sendResultTo(editorModel);
}
```

Then we we bind the channel in the view.

```
FormBinder binder = ...;

binder.bind(model.errorChannel).to(myErrorDisplay);

```

There are a number of interfaces that you can bind events to, `Destination`, `HasValueSetter`, `Publisher`, `ParameterisedCommand`.  Typically when I want to
send an event to some other entity I'll create an `asDestination()` style method on it that returns one of the above types.

# Interceptors #
`UiCommands` also support the use of interceptors that get invoked prior to the command executing.

```
deleteCustomerCommand.interceptUsing(new Interceptor()
{
   public void intercept(Invocation invocation)
   {
      // ask our view to ask a question.
      dialog.ask("Do you really want to delete this customer?",
                 "This change can't be undone.")
         .onChoosingDestructiveOption("Yes delete the customer").thenExecute(invocation.getProceedCommand())
         .onChoosingDefault("Cancel").justClose()
         .show();
   }
});

// clicking the button will prompt the user.
binder.bind(deleteCustomerCommand).to(deleteCustomerButton);

// and show a message while the activity is in progress.
binder.show(deletingMessage).when(deleteCustomerCommand.active());

// once it completes we can display errors
binder.displayErrorsOf(deleteCustomerCommand).using(myErrorDisplay);
```



# DelegatingUiCommand #
`DelegatingUiCommand` delegates it's behaviour and state to another `UiCommand`.  This is useful when you need one command to have different behaviours based on a changing context within the UI.  In such cases you just set the delegate that's relevant at the time.  The command will automatically track the enabled and active state of the delegate.  Setting the delegate to null will remove all handlers from the delegate (so you won't leak memory).

Another use of delegating commands is to add additional constraints to the commands enabled state.  As an example I have an `PublishCommand` that operates directly on a model but I need the associated button to be disabled while an `IncrementalUiCommand` builds the view.  The following shows how to do this without the publish command having any knowledge of how the view is built.

```
// An IncrementalActivity that builds the view.
BuildUiCommand buildUiCommand = new BuildUiCommand();

// Create our delegate.  We'll disable it while we're building
// the view.
DelegatingUiCommand delegatingPublish = new DelegatingUiCommand();
delegatingPublish.disableWhen(buildUiCommand.active());

// now configure the delegate, we can do this at anytime.
delegatingPublish.setDelegate(realPublishActivity);

// now our publish button will only be enabled when the realPublishActivity
// is enabled and the buildUiActivity is not active. 
binder.bind(delegatingPublish).to(publishButton);
```

TODO: Talk about debug info for delegating commands.