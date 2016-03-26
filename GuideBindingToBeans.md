# Binding to Beans #

## Overview ##
Pectin providers a `BeanModelProvider` class for creating ValueModels and ListModels from regular Java Beans.

The providers can be used directly or can be used with forms as follows:
```
// providers are created using GWT.create(...)
public class PersonProvider extends BeanModelProvider<Person>{}
PersonProvider provider = GWT.create(PersonProvider.class);

// use it directly..
ValueModel<PostCode> postCode = provider.getValueModel("address.postCode", PostCode.class);

// or use it within a form
FieldModel<PostCode> postCode = fieldOfType(PostCode.class).boundTo(provider, "address.postCode");
```


## 1. Define our Bean ##

The only requirement is that our bean follows the standard Java bean getter/setter method naming convention.

```
public class Person {
   private String givenName;   
   private String surname;

   // define our standard the getters and setters.
   public String getGivenName() {return givenName;}
   public String setGivenName(String givenName) {this.givenName = givenName;}

   public String getSurname {return surname;}
   public String setSurame(String surname) {this.surname = surname;}
}
```


## 2. Create a `BeanModelProvider<T>` for our bean ##

The `BeanModelProvider<T>` acts as a `ValueModel` factory for our bean.  To use it we define an abstract subclass for our bean and instantiate it using `GWT.create()`

```
// define our abstract sub class for our bean type.
public abstract class PersonProvider extends BeanModelProvider<Person>{};

// and let GWT create an instance for us.
PersonProvider personProvider = GWT.create(PersonProvider.class);
```

## 3. Set the bean on the provider ##

Once all the wiring has been done all that is left is to configure the provider with the bean we wish to display or edit.

```
// configure the providers bean.  All the fields bound to the provider will 
// automatically update. 
personProvider.setBean(new Person());
```

## 4. Committing changes ##
By default changes are not automatically propegated back to the bean.  To update the bean you need to call `commit()` on the provider.

```
// write the change back to the underlying bean.
personProvider.commit();
```

You can also call revert
```
// revert all models back to the bean state.
personProvider.revert();
```


## 5. AutoCommit ##
Pectin also supports auto commit features.  There are 2 options, you can either use the `AutoCommitBeanModelProvider` or call `BeanModelProvider.setAutoCommit(true)`.  In both cases the changes will be writen directly to the bean as they happen and the provider will never be dirty (see below).

## 6. Dirty Tracking ##
The BeanModelProvider also provides a `ValueModel<Boolean> dirty()` model that tracks the dirty state of the data.  If any value is changed from the state defined by the bean then the value model will change to `true`.  If the change is reverted (either by calling revert or by a user change) the dirty state will change back to `false`.  Likewise committing the changes will also revert the dirty state back to `false`.

```
// expose our providers dirty state from our FormModel
public class MyForm extends FormModel {

   PersonProvider personProvider = GWT.create(PersonProvider);

   public final ValueModel<Boolean> dirty = personProvider.dirty()
}
```

The dirty model can be easily used to enabled and disable buttons in the view.

```
Binder binder = new Binder();
binder.enable(saveButton).when(myForm.dirty);
```

# Binding to nested beans #

Since version 0.8 Pectin supports binding to nested beans.  Because GWT doens't provide runtime reflection you need to tell Pectin which nested beans are to be exposed.  This can be done by either annotating bean getters with `@NestedBean` or by annotating the `BeanModelProvider` with `@NestedTypes`.  The latter is useful if you don't have access or don't want to polute your beans with pectin types.

Lets extend our person example to include a nested Address property.

```
public class Person 
{
   // other properties...
   ...

   // our new address property
   private Address address;
   public Address getAddress() {return address;}
   public void setAddress(Address address) {this.address = address;}
}

public class Address
{
   private String addressLineOne;
   private String addressLineTwo;
   private String city;
   private String state;
   private PostCode postCode;
  
   public String setAddressLineOne(String text) {addressLineOne = text;}
   public String getAddressLineOne() {return addressLineOne;}
   public String setAddressLineTwo(String text) {addressLineTwo = text;}
   public String getAddressLineTwo() {return addressLineTwo;}
   ....etc
}
```

## Using the @NestedBean Annotation ##
Using this approach, we would annotate the `Person.getAddress()` method directly with `@NestedBean` annotation as follows:
```
public class Person {
   // mark our address as a nested bean. 
   @NestedBean
   public Address getAddress();
}
```

Now we can access the nested property from our provider.

```
public class PersonProvider extends BeanModelProvider<Person>{}

PersonProvider provider = GWT.create(PersonProvider.class);

ValueModel<String> addressLineOne = provider.getValueModel("address.addressLineOne", String.class);
```


## Using @NestedTypes Annotation ##
In cases where you can't or don't want to annotate your bean directly you can use the `@NestedTypes` annotion to provide a list of types to be made available as nested properties.
```
// our address class is a nested type.
@NestedTypes({Address.class})
public class PersonProvider extends BeanModelProvider<Person>{};

// create as normal..
PersonProvider provider = GWT.create(PersonProvider.class);

ValueModel<String> addressLineOne = provider.getValueModel("address.addressLineOne", String.class);

```

### Handling Recursive Bean Paths ###
When using the `@NestedTypes` annotation there is the case where a nested type returns an instance of itself either directly or from one of it's nested types.  Such an example would be an `Employee` type that has a `getBoss()` method that also returns an `Employee`.  Because pectin computes all possible paths at compile time this would result in an endless loop.  In order to avoid this you need to add the `@LimitPropertyDepth` annotation to the provider to tell pectin how deep you need the property paths to be processed.

```
@NestedTypes({Employee.class})
@LimitPropertyDepth(3)
public class EmployeeProvider extends BeanModelProvider<Employee>{}

EmployeeProvider provider = GWT.create(EmployeeProvider.class);
ValueModel<Employee> bigBoss = provider.getValueModel("boss.boss.boss", Employee.class)
```

**Note:** Pectin will barf during the rebind process if it finds a recursive path without a `@LimitPropertyDepth` annotation.  The moral is you don't need to worry about using the annotation until the compile process fails.

**Note:** There is no need to be exact in the depth as unused paths generated by the rebind process will be stripped during the GWT optimisation process.

## The Models are Linked ##
When using nested properties pectin ensures they are wired together as you'd expect.  Specifically if changes are made to a parent value model, any nested properties will update appropriately.
```
@NestedTypes({Address.class})
public class PersonProvider extends BeanModelProvider<Person>{};

// create as normal..
PersonProvider provider = GWT.create(PersonProvider.class);

ValueModel<Address> address = provider.getValueModel("address", Address.class);
ValueModel<PostCode> postCode = provider.getValueModel("address.postCode", String.class);

// changing the address model will cause the post code to 
// update and fire a value change event.
address.setValue(new Address()); 
```

**Note:**  Please note that directly modifying the Address bean above won't cause change events to fire.  There are hooks in pectin to achive this but the default `BeanModelProvider` doesn't support it.  If you want to do this send me a note.

### Mutability ###
The value models returned from the provider are mutable if and only if the underlying property is mutable (i.e. it has a setter) and the parent bean is non null.  You can always read from a value model even if it's souce bean is null, i.e. reading from `address.postCode` will simply return `null` if either `address` and/or `postCode` is `null`.  Attempting to write to `postCode` while `address` is null will throw a `SourceBeanIsNullException`.


# Binding to collection properties #

Pectin also allows you to bind collection properties to `ListModel`s.  In this case the bean must define getters and setters for the collection properties.  Please note Pectin doesn't support indexed Java bean properties.

Lets add a simple collection to our `Person` model.  We use copy on read and write to ensure any modifications are made on copies.

```
public class Person {
   private String givenName;   
   private String surname;

   // our collection property
   private List<Wine> favoriteWines = new ArrayList<Wine>();

   // getters and setters for our collection.  We copy on read and write to 
   // prevent the collection being changed underneath us. 
   public List<Wine> getFavoriteWines {return new ArrayList(favoriteWines);}
   public void setFavoriteWines(List<Wine> surname) {this.favoriteWines = new ArrayList(favoriteWines);}
}
```

Now we can bind `ListModel` fields to our collection property.  Pectin supports the basic java collection interfaces out of the box.

```
public class PersonFormModel extends FormModel {

   protected final FieldModel<String> givenName;
   protected final FieldModel<String> surname;

   protected final ListFieldModel<Wine> favoriteWines;

   public PersonFormModel(PersonProvider personProvider) {
      
      // bind our regular models.
      ...

      // bind our collection to a list model.
      favoriteWines = listOfType(Wine.class).boundTo(personProvider, "favoriteWines");
   }
}
```

## Handling alternate collection types ##
If you need to bind to a collection type other than the standard `Collection`, `List`, `Set` or `SortedSet` you can add a `CollectionConverter` to the `BeanModelProvider`.

The following converter can be used for bean properties whose type is `HashSet`.

```
CollectionConverter converter = new CollectionConverter<HashSet>() {

   public Collection<?> fromBean(HashSet<?> source)
   {
      return source;
   }

   public HashSet toBean(List<?> source)
   {
      return new HashSet<Object>(source);
   }
}

// now register the converter
personProvider.registerCollectionConverter(HashSet.class, converter);
```