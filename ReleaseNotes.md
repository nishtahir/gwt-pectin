# Pectin Release Notes #
## Release 0.8 ##

The theme of this release is a move towards a much more general binding solution and UI framework.  Most notably this realease supports binding to regular value models, basic property paths in `BeanModelProvider`, more command enhancements plus various other odds and sods.

#### Painful Form Changes: ####
  * All form releated classes (including plugins) have moved to a new `form` subpackage.
  * `FormBinder` has replaced `WidgetBinder`.

#### Binding Changes: ####
  * New `Binder` class **supports binding regular** `ValueModels` and `ListModels` directly to `HasValue<T>`, `ValueTarget<T>` and `MutableValueModel<T>` without the need of a `FormModel`.  This means you can use `BeanModelProvider` (or any `ValueModel` for that matter) directly in cases where you don't need the features of the form.  E.g.
```
// Using a ValueModel<T> of your choice...
MutableValueModel<ContactDetails> contactDetails = provider.getValueModel("department.manager.contactDetails", ContactDetails.class) 

// ...and our new binder...
Binder binder = new Binder();

// ...you can bind to anything that implements HasValue<T> or
// MutableValueModel<T> or ValueTarget<T> or ParameteriseCommand<T> etc. 
binder.bind(contactDetails).to(contactDetailsEditor);
```
  * `FormBinder` (formally `WidgetBinder`) extends the new `Binder` so the following changes apply to both.
  * New `Binder.bind(ValueModel).toTextOf(HasText)` and `Binder.bind(ValueModel).toHtmlOf(HasHTML)` to replace the deprecated `Binder.bind(ValueModel).toLabel(HasText)`.  Formats are also support (i.e. `bind(model).toTextOf(hasText).withFormat(format)`) and there are variants that work with list models.
  * New `Binder.onTransitionOf(ValueModel<T>).to(T).invoke(Command)` bindings.
  * New `Functions` class that has static methods for creating computed models from regular value and list models using functions and converters etc.  E.g. `ValueModel<T> result = Functions.computedFrom(ValueModel<S>).using(Function<T,S>)` and `ValueModel<T> result = Functions.computedFrom(ListModel<S>).using(Reduce<T,S>)` etc.
  * New text conditions that test for empty and blank string.  i.e. `Conditions.textOf(source).isBlank()`.
  * New (initial) support for list based conditions e.g. `Conditions.listOf(favoriteWines).isEmpty()`.

#### BeanModelProvider Changes: ####
  * BeanModelProviders now supports basic nested property paths.  So you can go `fieldOfType(T).boundTo(provider, "a.b.c")`.
    * There are two mechanisms.
      * Beans can expose properties by placing a `@NestedBean` annotation on the corresponding getter..  I.e. bean 'a' would annotate getB() with `@NestedBean` etc.
      * The provider can be annotated with a `@NestedTypes` annotation. I.e. the above provider would be annotated using `@NestedTypes({B.class, C.class}}`.  See GuideBindingToBeans for more information.
    * The property path only supports list models as the final property.  Thus `bean.bean.list` is ok while `bean.list.bean` isn't.
    * The value models returned from the provider are only mutable if the property is mutable (i.e. it has a setter) and the parent bean is non null.  You can always read from a value model even if it's souce bean is null, i.e. reading from "a.b.c" will simply return `null` if either `a` and/or `b` is `null`.  Attempting to write to `c` while `a` or `b` is null will throw a `SourceBeanIsNullException`.
  * BeanModelProviders now implement `ValueModel`  and the `setBean(T)` method has been changed to `BeanModelProvider.setValue(T)`.
  * Renamed `TestUtil` to `ReflectionProviders`, simplified method names and moved from `util` to a new `reflect` package..
  * Renamed `BeanModelProvider.getDirtyModel()` to `dirty()` to be consistent with the rest of the library and added a `HasDirtyModel` interface to denote it.  The interface is also implemented by the value & list models created by providers.
  * **Casualties**: `CopyingBeanModelProvider` and related classes are no more due to the changes to support nested beans.  The ability to "commit without losing dirty state" is now supported by the standard `BeanModelProvider` by using `provider.commit(false)`.

#### Formatted Field Changes ####
  * `FormattedFieldModel` and `FormattedFieldListModel` both now provide a `sanitiseText()` method that can be used to run the current text value through the formatter.  This method doesn't affect the current value and will leave invalid text values alone.  There is also a `sanitiseTextCommand()` method that returns a command that invokes `sanitiseText()`.
  * Fixed for [issue 29](https://code.google.com/p/gwt-pectin/issues/detail?id=29).  The above can be triggered onBlur using the Binder.  e.g. `binder.bind(formattedField).to(widget).sanitiseTextOnBlur()`.  An alternate `sanitiseTextOnBlurOf(HasBlurHandlers)` can also be used.
  * `FormatExceptionPolicy` can now be configured using the builder API ([issue 24](https://code.google.com/p/gwt-pectin/issues/detail?id=24)).
  * Binding formatted fields and formatted list fields to `HasText` targets now uses the fields default format if the builder doesn't specifiy one ([issue 23](https://code.google.com/p/gwt-pectin/issues/detail?id=23))..
  * Cleanups and fixes to FormattedFieldBindings (see [issue 23](https://code.google.com/p/gwt-pectin/issues/detail?id=23)).

#### Interceptor Changes: ####
  * Moved interceptors from `command` package to new `interceptor` package.
  * New `InterceptedValueModel` & `InterceptedMutableValueModel` interfaces and `InterceptedValueHolder` implementation thereof.  Intercepters can be added to intercept and prevent value changes.  E.g.
```
selectionModel.interceptUsing(new Interceptor(){
   public void intercept(Invocation invocation) {
      if (editor.hasUnsavedChanges()) {
         messageDisplay.ask("Do you want to discard your changes?")
            .onChoosing("Yes, Discard changes").thenExcecute(invocation.getProceedCommand())
            .onChoosing("Cancel").thenClose()
            .show();
      } else {
         invocation.proceed();
      }
   }
});
```

#### Command Changes: ####
  * Added ability for `AsyncUiCommands` to `abort()` without publishing either a result or error.
  * Updated `DelegatingCommand` and `DelegatingUiCommand` to throw `MissingDelegateExceptions` instead of `NullPointerExceptions` if executed when the delegate isn't configured.  Also added a debugContext to DelegatingCommands to help locate the command in question (since stack traces are generated during a listener callback and may have no reference to appliation classes).
  * New `ExceptionManager` class for simplifying exception handling in AsyncUiCommands.  The usage style is in the form of:
```
// create our manager, the String is the error type we're going to publish
ExceptionManager<String> exceptionManager = new ExceptionManager<String>();

// basic convert exception to message...
exceptionManager.onCatching(SomeException.class)
  .publishError(constants.anErrorMessage());

// or do some additional work..
exceptionManager.onCatching(SomeException.class)
  .invoke(new ExceptionHandler<StaleEntityException>() { 
     public void handle(StaleEntityException error) {
        // refresh the entity...
        theModel.setValue(error.getFreshEntity());
        // and publish a message
        publishError(constants.stateEntityMessage());
     }
  });

// we can install a default handler too for all the common RPC errors
exceptionManager.onUnregisteredExceptionsInvoke(myDefaultHandler);
```
  * `AbstractAsyncUiCommand` now has an `asAsyncCallback(..)` convenience method that returns a regular GWT `AsyncCallback` given a `AsyncCommandCallback` and `ExceptionManager`.
```
public void performAsyncOperation(AsyncCommandCallback<R,String> callback) {
  // our convenience method creates a regular callback we can use directly
  // with our RPC service.
  service.doStuff(asAsyncCallback(callback, exceptionManager);
}

```
  * The `ExceptionManager.containsHandlerFor(..)` method can be used to see if a handler has been registred for a particular exception.  This is useful for unit tests to pick up when a new exception has been added to an RPC service without adding an appropriate handler to your commands.

#### Other changes: ####
  * New `Watermarkable` interface so watermarks can be applied to widgets other than TextFields.
  * New `ReducingValueModel` that operates on ListModels and corresponding builder methods.  E.g. `fieldOfType(T).computedFrom(ListModel<S>).using(Reduce<T,S>)` and `Functions.computedFrom(ListModel<S>).using(Reduce<T,S>)`.
  * New `FormModel.allFieldsExcept(..)` method for cases where you want to apply a plugin to all but a few fields.
  * Large refactoring and clean up of the bindings and binding builders.
  * Removed deprecated `toLabel(HasText, DisplayFormat)` binding methods.
  * Fixed bug in `AbstractDynamicList` where `setValue(T)` and `setValue(T, false)` were incorrectly firing ValueChangeEvents.
  * Renamed `HasValueGetter` to `ValueSource` and `HasValueSetter` to `ValueTarget`.
  * Added `MutableListModel.clear()` method.
  * Various binder methods of the form `.to(T)` have been updated to `.to(<? super T>)`.


## Release 0.7 ##
  * Initial release of [UiCommands and Channels](GuideUiCommands.md).
  * Added a new `Conditions.valueOf(ValueModel<T>).isIn(T...)` and `.isNotIn(T...)`.  Supports var-args, `Iterables` and `ListModels`.
  * Introduced new interfaces `HasValueGetter` and `HasValueSetter` that allow the reading and writing of a value without requiring a full `ValueModel`.
  * Updated `CopyingBeanModelProvider` to support the `HasValueGetter` and `HasValueSetter`.
  * Updated `AbstractBeanModelProvider`, `BeanPropertyValueModel` and `BeanPropertyListModel` to support read only bean properties (i.e. those with with no setter).  The mutablility of the underlying property is exposed in the value returned by `BeanPropertyValueModel.getMutableModel()`. ([Issue 15](https://code.google.com/p/gwt-pectin/issues/detail?id=15))
  * Fixed defect where BeanModelProviders weren't picking up inherited properties. ([Issue 16](https://code.google.com/p/gwt-pectin/issues/detail?id=16))
  * Updated `AbstractBeanModelProvider` so that updates are made to the models in the order they are created.
  * Updated `AbstractComputedValueModel` to cache the computed value.
  * Fixed bug where `DelegatingValueModel` would cache its `HandlerManager` if a null delegate was used thus causing a barf on the next non null `setDelegate(delegate)` call.
  * `ArrayListModel` now supports and array version of setElements(T[.md](.md)) and also now provides an `Destination<T[]> asDesitination()` method that allows you to send the output of a channel diretly to the list.
  * Moved `MetadataBinder` show/hide methods to `WidgetBinder`.  `MetadataBinder` is only now required if you want to manually bind all the metadata of a field to a widget.
  * Added bindings for activities and channels to `WidgetBinder`,
  * Added support for binding fields to `HasValueSetter`.
  * Added `EnhancedPasswordTextBox` that fires value change events on key up events and not just blur events.
  * Cleaned up validation interfaces and updated `ValidationBinder` to support binding of anything that implements `HasValidationResult` to a `ValidationDisplay`.
  * Fixed bug in `ValidationDisplayPanel` where it was displaying 1 less validation message than it should.
  * Assorted updates and clean ups.
  * More tests added.

And finally there's a new discussion group at http://groups.google.com/group/gwt-pectin-discuss


## Release 0.6 ##
This release has quite a few changes, let me know if you have any problems.

There have been some breaking changes to `BeanModelProvider`, see below.
  * Refactored `BeanModelProvider` code into three separate types.
    * `BeanModelProvider` - as before but without the autocommit capabilities
    * `AutoCommitBeanModelProvider` - new provider that always writes value changes to the underlying bean
    * `CopyingBeanModelProvider` -  a new provider to copies state to and from beans without keeping a reference to any particular instance.  This provider is useful for updating cloned instances.  It also allows the dirty state to be updated at some time after the update i.e. after a RPC call returns successfully.  Initial support for checkpointing has been added, although at this time it's only to clear the dirty state.  It could easily be updated support restore operations (using mementos) if required.
  * All BeanModelProviders now check and enforce collection elements types ([issue 13](https://code.google.com/p/gwt-pectin/issues/detail?id=13)).  This means calls like `fieldOfType(String.class).boundTo(bean, "property")` and `listOfType(String.class).boundTo(bean, "listProperty")` will barf if either properties don't exist or are the wrong type.  A unit test that instantiates the form can now guarantee the properties exist and have the correct type.
  * All BeanModelProviders now barf if you try and write to a read only property.
  * The dirty models to no longer fire events when `dirtyModel.setValue(..)` is called with the same value.
  * Updated `ValueModelProvider` and `ListModelProvider` interfaces to support arbitrary key types.  The bean providers now implement `ValueModelProvider<String>` and `ListModelProvider<String>` (i.e. for use with string property names).


Other changes:
  * Deprecated `MetadataBinder.bindValueOf(...)` and friends in favour of simple `show(widget).when(condition)` and `show(widget).usingMetadataOf(field)`.  There are methods for show/hide/enable/disable.
  * Updated `WidgetBinder.toLabel(HasText)` methods to use default formats.  E.g. `binder.bind(model.gender).toLabel(myLabel)` will use the default `ToStringFormat`.  To specify a different format you can call `binder.bind(model.gender).toLabel(myLabel).withFormat(genderFormat)`.
  * Fixed the generics on  `ReducingValueModel`, `Reduce` and `ListDisplayFormat` so creating default `ToString` style functions and formats works on collections without casting.  See [this blog entry if you're interested](http://pietschy.com/blog/2010/02/using-extends-t-and-super-t-for-reduce-style-functions-that-operate-on-collections/).
  * Updated `ReducingValueModel` to support run time changes to the `Reduce` function.
  * Added `ReducingValueModel.recomputeAfterRunning(Runnable)` to allow for multiple changes to the source models or function while triggering only one value change event.
  * Moved `CollectionToStringFormat` and `ToStringFormat` to the format package.
  * Cleaned and refactored `WidgetBinder` infrastructure.  `BindingCallback` has been simplified and various builders updated to be more consistent.
  * Made the addition of external validation messages easier to access by making `ValidationManager.getValidator(field)` return `FieldValidator` instead of `HasValidation`.
  * Fixed bug where `FormattedListFieldModelImpl` extended `ListFieldModelImpl` instead of `AbstractListFieldModelBase`.  This was causing class cast exceptions in the convenience methods of the validation plugin.
  * Updated `AbstractDynamicList` constructor to take the text of the remove link.
  * Added `TestUtil` class and `RefectionBeanModelProvider` to facilitate JVM based unit tests.

Known Bugs
  * Changed the way `BeanPropertyListMode`l computes it's dirty state to include the list order (fixing an issue when using `AbstractDynamicList`).  This change introduces an issue with `bind(model.list).containingValue(T).to(checkbox)` ([issue 14](https://code.google.com/p/gwt-pectin/issues/detail?id=14)).  Unchecking and re-checking the checkboxes in a different order leaves the form in a dirty state, which is very confusing for users if you're displaying the dirty state on the UI.  See [issue 14](https://code.google.com/p/gwt-pectin/issues/detail?id=14) for more info.  This bug is preferred however since it gives false dirties rather than false cleans.  This bug will only affect you if you're displaying the dirty state on the UI.


## Release 0.5 ##
  * Removed var-arg methods from `AbstractDynamicList` to work around GWT 2 compiler problem ([issue 10](https://code.google.com/p/gwt-pectin/issues/detail?id=10)).
  * Updated computed fields to support functions that convert from any super of the source model type.  I.e. this allows for the use of a generic `ToStringFunction` for any type of source model.
  * Added default implementation of a `ToStringFunction`.
  * Updated `ComputedValueModel` to expose getters and setters for the function.
  * Added support for watermarks derived from arbitrary value models as per [issue 9](https://code.google.com/p/gwt-pectin/issues/detail?id=9).
  * Added var-args watermark methods so you can do things like `watermark(name, email).with("Required")`.
  * Introducing new `FieldModelBase` and `ListFieldModelBase` types so that the API can safely distinguish between formatted and regular variants of each model type.
  * Simplified validation plugin API based on the above changes.
  * Added `FormValidator` class that can be used to track the validation messages of all the fields in the form.  So now you can bind the formModel to a `ValidationDisplay`.  E.g. `validationBinder.bind(formModel).to(errorMessageWidget)`.
  * Updated `FormattedFieldValidatorImpl` to only validate the value if there were no errors logged by the text validators.  This can be overridden if required.
  * Fixed a bug where `ValidationDisplayLabel` wasn't clearing existing validation styles.


## Release 0.4 ##
  * Changed version numbering to something more sane.
  * Added new `FormattedListFieldModel` that allows binding of non-string `ListModels` to `HasValue<Collection<String>>`.
  * Added `TextSplitter` component that converts a `TextBox/TextArea` into a `HasValue<Collection<String>>` based on a regex.
  * Added support for auto commit on `BeanModelProvider` ([issue 5](https://code.google.com/p/gwt-pectin/issues/detail?id=5)).
  * Added support for dirty tracking on `BeanModelProvider` and the value models it creates ([issue 7](https://code.google.com/p/gwt-pectin/issues/detail?id=7)).
  * Added `FormModel.allFields()` to support bindings like `enable(allFields()).when(..)` ([issue 6](https://code.google.com/p/gwt-pectin/issues/detail?id=6)).
  * Added support for binding metadata visibility to DOM Elements.  This improves integration with `UiBinder`.
  * Added more var-args methods for binding to multiple widgets with one binding.
  * Removed deprecated `BeanPropertySource` interface.
  * Added `ValidationDisplayPanel` and `ValidationDisplayLabel` classes with default css for error, warning and info messages.
  * Renamed `StyleApplicator` to `ValidationStyles` and provided two static istances, one that uses `UIObject.addStyleName` and one that uses `UIObject.addStyleDependentName`.
  * Changed validation style names (as applied by the `ValidationStyles`) from the `validation-error` form to the `validationError` form.
  * Changed default watermark style from `watermark` to `pectin-Watermark` and added default css for it.
  * Added some basic validators for lists including `NotEmptyListValidator`, `NoEmptyElementsValidator`, `NoNullElementsValidator`.
  * Fixed bug in `ListFieldValidatorImpl` where validation messages weren't always displayed in the order they were added.
  * Changed `Severity.compareTo` so the natural sort order puts the highest severity first.


## Release 0.1.3 ##
  * Added initial support for watermarks in TextBoxes  using the `MetadataPlugin`.
  * Simplified `StyleBinder` interface in inline with [issue 3](https://code.google.com/p/gwt-pectin/issues/detail?id=3)
  * Changed `MetadataBinder.bindVisibility(..)` (and related methods) to `MetadataBinder.bindVisibilityOf(..)`.
  * Updated `MetadataBinder.bindValueOf(..)` to support both enabled and visibility variants.
  * Changed `Function` to operate on a single value and added `ReducingFunction` to operate on a collection of values.
  * Added `ComputedValueModel` that takes a source `ValueModel` and `Function`.
  * Added `ReducingValueModel` that takes a `ReducingFunction` and a collection of source models.
  * General clean up of the computed value models.
  * Simplified computed fields by using `Function` for single source and `ReducingFunction` for multiple source models.
  * Fixed a bug where calling `AbstractBinder.dispose()` twice was failing
  * Updated build environment to work with Snow Leopard

## Release 0.1.2 ##
  * Added support for GWT JUnit tests:
    * src/gwt-test/java are run using GWT JUnit infrastructure.
    * src/test/java are run using TestNG.
  * Updated `BeanModelProvider` to throw more specific exceptions when accessing bean properties instead of the generic `IllegalStateException` and friends.
  * Deprecated `BeanPropertySource` interface as it's no longer required for deferred binding (a hangover from earlier prototypes).
  * Exposed the `validate` and `runValidators` methods on the various `FieldValidator` interfaces and implementations.
  * Renamed `PluginCallback` to `BindingCallback`.
  * Javadoc updates.

## Release 0.1.1 ##
  * Removed inner classes from builders to simplify code completion options.
  * Added support for bindings values, validation and meta data state to the css style of arbitrary widgets.
  * `IndexedValidationResult` now supports validation messages without an index.
  * `ValueHolder` now always fires `ValueChangeEvents`.
  * Various bits and pieces.

## Release 0.1.0 ##
  * Initial Release