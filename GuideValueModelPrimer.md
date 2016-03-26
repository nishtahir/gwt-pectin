# ValueModel Primer #


This needs to be filled out but for now it's worth noting that a `FieldModel` is a `ValueModel` that has a reference to it's enclosing form.

```
public interface FieldModel<T> extends Field<T>, MutableValueModel<T>, HasValueChangeHandlers<T> {}
```

So when ever you see a reference to `ValueModel<T>` you can also use any `FieldModel<T>`.