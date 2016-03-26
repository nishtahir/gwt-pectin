# Pectin Forms #

**TODO**

## Example ##
Create your model:
```
public class MyFormModel extends FormModel {

  // Use deferred binding to bind to bean properties.
  public static abstract class PersonModelProvider extends BeanModelProvider<Person>{}
  private PersonModelProvider personProvider = GWT.create(PersonModelProvider.class);

  // define some simple fields, I'm using protected final fields for convenience.
  protected final FieldModel<String> givenName;
  protected final FieldModel<String> surname;
  protected final FieldModel<Gender> gender;

  // ...and a formatted field 
  protected final FormattedFieldModel<Integer> age;

  // ..and a list of wines
  protected final ListFieldModel<Wine> favoriteWines;

  public MyForm() {

    // create our fields and bind them to our bean.
    givenName = fieldOfType(String.class).boundTo(personProvider, "givenName");
    surname = fieldOfType(String.class).boundTo(personProvider, "surname");
    gender = fieldOfType(Gender.class).boundTo(personProvider, "gender");

    // formatted fields allow us to bind HasValue<String> widgets to non string value models.
    age = formattedFieldOfType(Integer.class).using(new AgeFormat()).boundTo(personProvider, "age");
    moneyInBank = formattedFieldOfType(Double.class).using(new CurrencyFormat()).boundTo(personProvider, "moneyInBank");

    // we can bind to bean properties of the various collection types.
    // i.e. person.get/setFavoriteWines(List<Wine> wines)
    favoriteWines = listOfType(Wine.class).boundTo(personProvider, "favoriteWines");

  }

  public void setPerson(Person person) {
    // setting the bean on the provider will update the model and
    // all widgets that are bound to it.
    personProvider.setBean(person);
  }
}

```

Now bind it to our widgets.  The binder binds `FieldModel<T>` instances to any `HasValue<T>` widget.  It also supports binding fields with specific values to `HasValue<Boolean>`.

```
public class MyForm extends Composite {

  private FormBinder binder = new FormBinder();

  private TextBox givenName = new TextBox();
  private TextBox surname = new TextBox();
  private TextBox age = new TextBox();
  private TextBox moneyInBank = new TextBox();

  private String buttonGroupId = DOM.createUniqueId();
  private RadionButton maleRadio = new RadioButton(buttonGroupId, "Male");
  private RadionButton femaleRadio = new RadioButton(buttonGroupId, "Female");

  private CheckBox cabSav = new CheckBox("Cab Sav");
  private CheckBox merlot = new CheckBox("Merlot");
  private CheckBox shiraz = new CheckBox("Shiraz");

  public MyForm(MyFormModel model) {

    // bind our fields to our models.
    binder.bind(model.givenName).to(givenName);
    binder.bind(model.surname).to(surname);
    binder.bind(model.age).to(age);
    binder.bind(model.moneyInBank).to(moneyInBank);

    // bind our gender to some radio buttons
    binder.bind(model.gender).withValue(Gender.MALE).to(maleRadio);
    binder.bind(model.gender).withValue(Gender.FEMALE).to(femaleRadio);

    // bind to our list to a bunch of checkboxes.  Selecting and unselecting a
    // checkbox will add and remove the value from the list.  We could also 
    // bind to any component that implements HasValue<Collection<Wine>>.
    binder.bind(model.favoriteWines).containingValue(Wine.CAB_SAV).to(cabSav);
    binder.bind(model.favoriteWines).containingValue(Wine.MERLOT).to(merlot);
    binder.bind(model.favoriteWines).containingValue(Wine.SHIRAZ).to(shiraz);

    doLayout();
  }

  protected void doLayout() {
    ....
  }
}

```

## Formatted Field Bits ##
Pectin doesn't automatically reformat user entered text (since there's no one way that will always work) but does provide a mechanism to do it.

If you need to do it from you model you can call the following:
```
// in my form model
moneyInBank.sanitiseText();

// using a command
moneyInBank.sanitiseTextCommand().execute();
```

Alternatively you can do it directly in your view.
```
FormBinder binder = new FormBinder();

binder.bind(model.moneyInBank).to(monyInBank).sanitiseTextOnBlur();
```


## Other Stuff ##
FormModels also support:

### Computed Fields ###
```
  moneyInMattress = fieldOfType(Double.class).boundTo(...);
  moneyInBank = fieldOfType(Double.class).boundTo(...);
  
  // create a computed field.  You can also compute values of a different type from the source values.
  netWorth = fieldOfType(Double.class)
    .computedFrom(moneyInMattress, moneyInBank)
    .using(new SumDoubles());
```

### Converted Fields ###
```
  // create a converted field.  You can also convert between different types.
  netWorthInCents = fieldOfType(Double.class)
    .convertedFrom(netWorth)
    .using(new MultiplyBy(100));
```

### Form only values ###
```
  // create field that isn't bound to anything..
  backgroundColor = fieldOfType(Color.class).create();

  // and this time with an initial value.
  color = fieldOfType(Color.class).createWithValue(MyColors.THE_NEW_BLACK);
```

### Conditions ###
```
// import our condition builder methods like valueOf(..) etc
import static com.pietschy.gwt.pectin.client.condition.Conditions.*;

ValueModel<String> sourceModel = ...;

// changes in the source model will change the state of the condition.
Condition sourceEqualsAbc = valueOf(sourceModel).is("abc");

// you can also match to other Models
ValueModel<String> otherModel = ...;
Condition sourceEqualsOther = valueOf(sourceModel).isSameAs(otherModel);

// there's also regex functionality for sources of type ValueModel<String> 
Condition sourceMatchesRegex = textOf(sourceModel).matches("^pectin rocks$");


// conditions can also be chained.
Condition multi = valueOf(sourceModel).is("abc")
   .and(valueOf(otherModel).matches("^pectin rocks$"));
```

Conditions implement `ValueModel<Boolean>` so you can use them where ever you'd use a boolean value model.  For example they are very useful when combined with plugins.

```
enable(aField).when(valueOf(thisField).isSameAs(thatField));
```

### Functions ###
Pectin also supports stand alone functions using the the static methods of the Functions class.  The functionality is equivalent to the converted/computed fields above but creates regular value models.

```
ValueModel<Double> sum = Functions.computedFrom(aListOfNumbers).using(new SumDoubles());
```