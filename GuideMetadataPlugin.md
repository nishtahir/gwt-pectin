# Metadata Plugin #

The `MetadataPlugin` provides adds basic metadata support including enabledness, visibility and watermarks.

The plugin is accessed by importing the static methods of `MetadataPlugin` into your form model.  Once you've defined the metadata for you model the bindings automatically configure your components during the normal binding process (i.e. when you're using `FormBinder`).

## Key Classes ##
The key classes of the plugin are:
  * `MetadataPlugin` - provides static methods controlling and accessing metadata
  * `MetadataBinder` - Still exists but has been mostly made redundant by `Binder` and `FormBinder`.

## Enabled Example ##

To control the enabled state of you fields you use the `enable` and `disable` methods of the plugin.  Each method takes a field and condition.  The condition can be any `ValueModel<Boolean>`.

Static methods defined by `MetadataPlugin`.
```
  enable(FieldModel<?>).when(ValueModel<Boolean>);
  disable(FieldModel<?>).when(ValueModel<Boolean>);
```


In your `FormModel`
```
  // import the plugin methods
  import static com.pietschy.gwt.pectin.metadata.MetadataPlugin.*;

  // create our models
  shipToDifferentAddress = fieldOfType(Boolean.class).boundTo(...);
  shippingAddressLineOne = fieldOfType(String.class).boundTo(...);

  // now use the static methods of MetadataPlugin (imported previously) to
  // bind the enabled state to the value of another field
  enable(shippingAddressLineOne).when(shipToDifferentAddress);  
```

Then in your view you use the `WidgetBinder` as per normal.

```
FormBinder binder = new FormBinder();

binder.bind(model.shipToDifferentAddress).to(shipToDifferentAddressCheckBox);
binder.bind(model.shippingAddressLineOne).to(shippingAddressLineOne);

```

## Visibility Example ##

To control the visibilty of fields you use the `show` and `hide` methods of the plugin.  Each method takes a field and condition.  The condition can be any `ValueModel<Boolean>`.

Static methods defined by `MetadataPlugin`.
```
  show(FieldModel<?>).when(ValueModel<Boolean>);
  hide(FieldModel<?>).when(ValueModel<Boolean>);
```


In your `FormModel`
```
  // import the plugin methods
  import static com.pietschy.gwt.pectin.metadata.MetadataPlugin.*;

  // create our models
  hasComments = fieldOfType(Boolean.class).boundTo(...);
  comments = fieldOfType(String.class).boundTo(...);

  // now use the static methods of MetadataPlugin (imported previously) to
  // bind the enabled state to the value of another field
  show(comments).when(hasComments);  
```

In your View:
```
FormBinder binder = new FormBinder();

binder.bind(model.hasComments).to(commentsCheckBox);
binder.bind(model.comments).to(comments);
```


## Watermarks ##
Watermarks allow you to add a water mark to TextBoxes or instances of !Watermarkable that are bound to `FieldModel<String>` or `FormattedFieldModel<?>`.  Watermarks can be either static strings or values held by other value models.

Static methods defined by `MetadataPlugin`.  All methods also apply to `FormattedFieldModel<?>`

```
  // static watermarks 
  watermark(FieldModel<String>).with(String);

  // dynamic watermarks
  watermark(FieldModel<String>).with(ValueModel<String>);

  // dynamic watermarks from artibrary value models
  watermark(FieldModel<String>).withValueOf(ValueModel<?>);  
  watermark(FieldModel<String>).withValueOf(ValueModel<T>).formattedBy(Function<String, ? super T>);
  watermark(FieldModel<String>).withValueOf(ValueModel<T>).formattedBy(DisplayFormat<? super T>);

  
```

There are also var-arg versions of the `watermark()` method so you can watermark multiple fields with the same value if required.

```
watermark(email, fax).with("Optional");
```


# Using Built-in Binder Support for Enable/Show #
Since Pectin 0.8 both `Binder` and `FormBinder` provide built in methods for enabling and showing widgets based on the state of an arbitrary `ValueModel<Boolean>`.

```
FormBinder binder = ...;
// we can use any value model, not just form fields..
ValueModel<Boolean> loading = ...;

binder.show(loadingMessageWidget).when(loading);
binder.hide(realWidget).when(loading);
```

If you need to apply metadata state to another widget (a label for example) you can use `MetadataPlugin.metadataOf(Field)` to access the value models that reflect the metadata state of the specified field.

```
FormBinder binder = new Formbinder();
StyleBinder style = new StyleBinder();

// hide our label if the field is hidden..
binder.hide(nameLabel).when(metadataOf(model.name).isHidden())

// and style it when it's disabled..
style.style(nameLabel).with("disabled")
     .when(metadataOf(model.name).isDisabled());
```

The `MetadataBinder` also allows you to apply metadata of a field to another widget in one go.
```
MetadataBinder metadata = new MetadataBinder();
metadata.bindMetadataOf(formModel.extraComments).to(extraCommentsLabel);
```