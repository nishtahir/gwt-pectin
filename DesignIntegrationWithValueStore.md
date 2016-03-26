# Initial thoughts on integration with ValueStore #

Integration with [Googles ValueStore proposal](http://code.google.com/p/google-web-toolkit/wiki/ValueStoreAndRequestFactory) would give pectin CRUD and JSR-303 support out of the box.

From the initial design proposal it looks like I should be to build an adaption layer for ValueStores that can act as a drop in replacement for pectins BeanModelProviders:

```
[ValueStore] <-[ValueStoreAdapter]-> [Presentation Model] <-[Bindings]-> [View]
                                              |
                                     [Validation Plugin]
```

(See GuidePectinArchitectureOverview for more detail on the parts above)

From the code examples given I'm not quite sure if DetaValueStore will remain unchanged for the life of the view or not (the examples have a setStore method).  If so I'm hoping be provide native integration in the form model with pectin creating the adapter behind the scenes.  If not then the adatper will need to be created in the same vein as `BeanModelProvider` and friends.

```
// pectin will automatically create the `ValueStoreAdapter` behind the scenes.
FieldModel<String> name = fieldBoundTo(valueStore, TroopId.name());
FieldModel<String> serialNumber = fieldBoundTo(valueStore, TroopId.serialNumber());
```

This concise syntax (i.e. `fieldBoundTo(...)` instead of `fieldOfType(Class<T>).boundTo(..)` will only be possible if the `Property` returned by `TroopId.name()` has a method such as `Class getPropertyType()` in addition to the generic declaration.  Some plugins need this information during binding.  i.e. pectin needs to be able to do this:
```
// return String.class so plugins can make runtime checks if they need to.
Class nameClass = TroopId.name().getPropertyType()
```

If this isn't supported then I'll have to revert to the existing `fieldOfType(String.class).boundTo(valueStore, TroopId.name())` semantics.


## Tracking Dirty State ##
The design proposal include a `store.isChanged()` method but it's unclear if this state is observable.  This is not a big issue with pectin as dirty state is always provided by the adaption layer anyway, if it's not supported there then it's not supported.

# ValueStore Validation #

**See** DesignAsynchronousValidation for changes to the ValidationPlugin so support async validators outside of ValueStores

Pectin already supports the injection of external validation results so hopefully I'll be able to hook that up to the ValueStore events. I hope to create a `ValueStoreValueModel` that exposes the validation capabilities of the store (both `store.validate()` and callbacks for validation errors).  From here the ValidationPlugin would query the `ValueStoreValueModel` and install the appropriate listeners etc.  E.g.

```
// the validation plugin would query the underlying value model
// and automatically install the appropriate validation hooks.
validateField(name).usingValueStore();
```

It would probably be worth supporting an "all ValueStore fields variant", e.g.
```
validateField(allFields()).usingValueStore();
```

or something like:
```
autoWireValueStoreValidation();
```


If the `Error` is a message key then the above method would probably need to take a message catalog.

```
// we may have to supply a message catalog.
validateField(name).usingValueStoreWith(messageCatalog);

autoWireValueStoreValidationUsing(messageCatalog);
```

At this stage I'm not sure if ValueStore supports field level validation.  Pectin could probably work around this to provide field level validation if it didn't, but that would be a bit of a pain.

It may be worth making this a little less bound to `ValueStore` and have our `ValueStoreValueModel` implement interfaces like `HasValidationService`,  `HasValidators`,  `HasAsynValidators` or even some thing like just `HasValidationErrorHandlers`.  That way if you want to use something else other than `ValueStore` you can write your own adapter and still use all of pectins functionality.  In this case I'd probably name the validation method something like `validateField(name).usingDomainValidationServices()` or something.