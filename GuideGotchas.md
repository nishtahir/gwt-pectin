# Gotcha's to look out for #

## GWT's CheckBox doesn't like nulls ##
GWT's CheckBox will barf if `setValue(Boolean)` is passed a null.  This is an issue since `FieldModel<Boolean>` will almost always contain a null at some point.  The solution is to override `CheckBox.setValue(Boolean)` to convert `null` values to `false`.  Pectin provides a `NullSafeCheckBox` that does just that for you to use.

This issue only surfaces when you're binding a `FieldModel<Boolean>` directly to a `CheckBox`.  I.e.

```
// in our model...
FieldModel<Boolean> field = fieldOfType(Boolean.class).boundTo(...);

// and in our form
CheckBox checkBox = new CheckBox();
binder.bind(model.field).to(checkbox); // this will generally barf 
```

In this case using a `NullSafeCheckBox` will fix the problem.

```
// This will fix it.
NullSafeCheckBox checkBox = new NullSafeCheckBox();
binder.bind(model.field).to(checkbox); // this will work 

```

Cases where you use the `withValue(...)` and `containingValue(...)` methods work fine with regular CheckBoxes as they never apply a null value.

```
// this is ok since the containingValue bindings never use nulls.
CheckBox checkBox = new CheckBox();
binder.bind(model.someListField).containingValue("blah").to(checkbox);
```


## BeanModelProvider currently only support the collections interfaces ##
Currently the default collection converters support the collection interface types (List, Set etc) and not the concrete implementations (ArrayList, HashSet etc).  Until I get around to adding the various concrete types (or you decide to add your own) you'll need to limit your return types from your beans properties to the interfaces.

```
// this will barf unless you register a collection converter for ArrayLists 
public class MyBean 
{
   public ArrayList<String> getTheList() {...}
   public void setTheList(ArrayList<String> thelist) {...}
}
```

And this will work out of the box.
```
// this works out of the box
public class MyBean 
{
   public List<String> getTheList() {...}
   public void setTheList(List<String> thelist) {...}
}
```

See GuideBindingToBeans for more info.

## Using Binder with a FormModel ##

If you forget to use a `FormBinder` with your form models you'll wonder why your plugins aren't working....

```
// Don't use a regular binder in forms!!!
Binder binder = new Binder();
binder.bind(formModel.firstName).to(firstName);
```

```
// This will work!!
FormBinder binder = new FormBinder();
binder.bind(formModel.firstName).to(firstName);
```


## DelegatingUiCommands with missing delegates ##
One of the side effects of declarative coding is that there are cases where stack traces contain no user code.  Invoking a `DelegatingUiCommands` with a null delegate is one such case and can be very painful experience.

To overcome this `DelegatingUiCommands` allow you to configure a debug context that will be included in the stack trace.  So when you create a debug command consider using the following:
```
myDelegatingCommand = new DelegatingUiCommand().withDebugContext("myDelegatingCommand");
```

I'll probably improve this in the future to include the owning class and some debug info.

## Event Ordering ##
TODO: Talk about cases where event order is important.