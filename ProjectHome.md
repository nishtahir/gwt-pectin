# Pectin #
## Introduction ##
Pectin is a library for building user interfaces in GWT based on the [ValueModel](http://www.mimuw.edu.pl/~sl/teaching/00_01/Delfin_EC/Patterns/ValueModel.htm), [PresentationModel](http://martinfowler.com/eaaDev/PresentationModel.html) and [Passive View](http://martinfowler.com/eaaDev/PassiveScreen.html) patterns.  It uses a declarative style API (guice style) for defining the models, commands and forms as well as binding them to widgets.  Pectins form infrastructure also supports a plugin mechanism to add additional features such as validation and metadata (enabled, visible & watermarks).

## Key Features ##
  * Declarative API for defining and binding models.
  * Support for basic concepts such as values, lists, conditions, functions, [commands](http://code.google.com/p/gwt-pectin/wiki/GuideUiCommands), interceptors and channels.
  * Advanced forms API with support for plugins that extend the basic functionality to suite particular needs.  Plugins give you the means to model the from in the language of the domain.
  * Binds to bean properties (including collections).
  * No special widgets required, works with any `HasValue<T>` or `HasText` widget.
  * Ready built plugins for Metadata (enabled, visible, watermarks) and Validation.
  * Additional bindings for controlling CSS styles based on model state.

## Future Directions ##
Pectin is reminiscent of [Cocoa's Key-Value](http://developer.apple.com/mac/library/documentation/Cocoa/Conceptual/KeyValueCoding/Concepts/BasicPrinciples.html) style of developement and I'm aiming to add various standard controllers in the spirit of [SproutCore](http://www.sproutcore.com/).  You can read more about various ideas and what-not at [FutureFeaturesAndDesignIdeas](http://code.google.com/p/gwt-pectin/wiki/FutureFeaturesAndDesignIdeas) on the wiki.

## Docs ##
The docs are a little bit behind at the minute but you can checkout the form based examples below, the [Introduction](GuideIntroduction.md) and the rest of the [wiki](http://code.google.com/p/gwt-pectin/w/list).

## Demo ##
You can see a demo at http://scratchpad.pietschy.com/pectin/PectinDemo.html

## Background & Inspiration ##

Pectin of came out of developing various Swing applications including those with highly demanding UX/UI requirements.  The main inspiration was an application developed while working for [this guy](http://ca.linkedin.com/in/stevespenceley) (it's pretty much a given that your car was styled with the software he designed).

Some of the requirements included obfuscated fields that un-obfuscate and become editable on supervisor override, automatic focus transfer on valid data entry, as well as various fields that were computed or converted from other values.  In cases such as these developing a custom value model framework (that modeled the domain requirements) allowed complex forms to be built quickly and cleanly.  Pectin grew from this and from the desire to not have to write one of those from scratch again.

Pectin (and it's various somewhat ugly ancestors) were inspired from the excellent [JGoodies Binding](https://binding.dev.java.net/) and [Spring Rich Client](http://spring-rich-c.sourceforge.net/1.1.0/index.html) libraries.  The API style was inspired by [Guice](http://code.google.com/p/google-guice/), [EasyMock](http://easymock.org/) and [Mockito](http://mockito.org/).


## Compared to MVP ##
For a brief discussion on the relationship between MVP and Presentation Models check out [ComparedToMVP](ComparedToMVP.md).  This is a little old, but there's also an example of [using Pectin with the GWT MVP example](GoogleMvpExampleWithPectin.md).

## Contributing ##
I'd love your help and feedback.  Let me know if you'd like to contribute.

I'm also always on the lookout for interesting projects so if you'd like assistence with integrating pectin into your project or would like to see pectin's capabilities and features expand to fit your project give me a call.

Otherwise, if you've found pectin to be of value and you'd like to say thanks then feel free to donate funds for a bottle of wine, or send me a [book from my Amazon wishlist](http://amzn.com/w/143JD22KTE9X6), it's always nice to get a surprise in the mail.

&lt;wiki:gadget url="http://gwt-pectin.googlecode.com/svn/trunk/etc/donate\_gadget.xml" border="0" width="140" height="45" /&gt;

## Release Info ##
**GWT Version**: Pectin is compatible with GWT 2.0, 1.7.1, 1.7.0 and should also work on 1.6.4.

Check out the ReleaseNotes for the change history.

# Examples #

## The quick Form example ##
One of the goals is to provide an API that describes the form in English like terms.  The aim is to end up with a `FormModel` that looks something like:

```
// create some fields for our form.
FieldModel<Boolean> likesCheese = fieldOfType(Boolean.class).boundTo(someBean, "cheeseLover");
FieldModel<String> whyILikeCheese = fieldOfType(String.class).boundTo(someBean, "reasonForLikingCheese");

// and configure the behaviour.
enable(whyILikeCheese).when(likesCheese);
watermark(whyILikeCheese).with("Tell us why you like cheese!");
validateField(whyILikeCheese)
   .using(new NotEmptyValidator("Please share your cheese passion with us."))
   .when(likesCheese);

```

Once the model is defined we can then bind it to our view.

```
// works well with UiBinder
@UiField CheckBox likerOfCheese;
@UiField TextArea cheeseLikingReason;

FormBinder binder = new FormBinder();
binder.bind(model.likesCheese).to(likerOfCheese);
binder.bind(model.whyILikeCheese).to(cheeseLikingReason);

```

The language elements such as enable(..), watermark(..) and validateField(..) are defined by plugins (see below) which get the chance to configure widgets during the binding process.

## The longer Form example ##

Inherit Pectin in your module:
```
<module>
   <inherits name='com.pietschy.gwt.pectin.Pectin'/>
</module>
```

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

  private TextBox givenNameField = new TextBox();
  private TextBox surnameField = new TextBox();
  private TextBox ageField = new TextBox();

  private String buttonGroupId = DOM.createUniqueId();
  private RadionButton maleRadio = new RadioButton(buttonGroupId, "Male");
  private RadionButton femaleRadio = new RadioButton(buttonGroupId, "Female");

  private CheckBox cabSav = new CheckBox("Cab Sav");
  private CheckBox merlot = new CheckBox("Merlot");
  private CheckBox shiraz = new CheckBox("Shiraz");

  public MyForm(MyFormModel model) {

    // bind our fields to our models.
    binder.bind(model.givenName).to(givenNameField);
    binder.bind(model.surname).to(surnameField);
    binder.bind(model.age).to(age);

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

# Plugins #

Plugins allow you to create a business level language for your forms that you can deploy across your projects.  For example, if your forms required obfucated fields (such as limiting the display of credit card numbers or account details) you would create a plugin that provides an `obfuscate(creditCard).when(...)` style methods and bindings to support it.  This way the complex behaviour required for obfuscation is contained within the plugin and there's no need to worry that every developer has implemented obfuscation correctly (the DRY principle).

By using this approach Pectin doesn't force you to use a one-size-fits-all solution.  Plugins are accessed using static methods so they won't clutter the API if you're not using them.  If you don't like the plugins provided or need more advanced functionality you can develop your own (or feel free to hire me to do it for you).

Pectin provides basic plugins for validation and one for metadata (enabled, visible & watermarks).

## Metadata Plugin ##

The metadata plugin adds metadata to fields to support enabledness, visibility and watermarks.

In our `FormModel`:
```
  // import the plugin methods
  import static com.pietschy.gwt.pectin.metadata.MetadataPlugin.*;

  // create our models
  shipToDifferentAddress = fieldOfType(Boolean.class).boundTo(...);
  shippingAddressLineOne = fieldOfType(String.class).boundTo(...);

  // now use the static methods of MetadataPlugin (imported previously) to
  // bind the enabled state to the value of another field
  enable(shippingAddressLineOne).when(shipToDifferentAddress);  

  // lets add a water mark, currently this only works for FieldModel<String> and 
  // FormattedFieldModels bound to `TextBox`s or `Watermarkable`s. 
  watermark(shippingAddressLineOne).with("Enter your shipping address");

```

Now if your component implements GWT's `Focusable` or the plugins `HasEnabled` interface it will be automatically enabled and disabled when ever `shipToDifferentAddress` changes.  Any `TextBox` will also automatically have the watermark applied when it isn't focused and it's value is empty.

In our Form/Widget we just bind as normal, the plugin takes care of the rest:
```

  // bind our check box for shipping to a different address
  binder.bind(model.shipToDifferentAddress).to(shipToDifferentAddressCheckBox);

  // the plugin will automatically install metadata bindings our widget
  // will only be enabled when shipToDifferentAddress is selected. 
  binder.bind(model.shippingAddressLineOne).to(shipAddressLineOneTextBox);   
```

Clicking the check box will automatically enable and disable the text box, and whenever the text box is empty and not focused the watermark will be displayed.

You can also manually configure the metadata.  In our `FormModel`:
```
  // use the MetadataPlugin.getMetadata(FieldModel) method to manually configure the metadata
  getMetadata(shippingAddressLineOne).setEnabled(false);
```

**Please note** that both `Binder` and `FormBinder` now directly supports show/hide and enable/disable methods for cases where you want to configure widgets from arbitrary value models.  This allows you to do things like the following (without having to use the metadata plugin or even a FormModel for that matter).
```
FormBinder binder = new FormBinder();
// lets show and hide things based on the value of a ValueModel<State>
// which is updated in our model layer as things are loaded...
binder.show(loadingMessage).when(valueOf(model.state).is(State.LOADING));
binder.show(mainWidget).when(valueOf(model.state).is(State.LOADED));
```


## Validation Plugin ##
You use the validation plugin by including static methods from `ValidationPlugin`.

```
// Import the validation methods into your form model..
import static com.pietschy.gwt.pectin.validation.ValidationPlugin.*;

public MyModel() {
  // Create models as normal
  givenName = fieldOfType(...);
  surname = fieldOfType(...);
  age = formattedFieldOfType(...);

  // and add some validation rules using the ValidationPlugin imported earlier.
  validateField(givenName).using(new NotEmptyValidator("Given name is required"));
  validateField(surname).using(new NotEmptyValidator("Surname is required"));
   
  // formatted fields can also be validated with their formatter.
  validateField(age).usingFieldFormat();
  // or with a text validator that operates on the raw string
  validateField(age).usingTextValidator(new FancyTextValidator());
  // or with a regular validator that operates on the parsed value.
  validateField(age).using(new AgeOver18Validator()); 
}

public boolean validate() {
  // validate the form using ValidationPlugin.getValidationManager(FormModel);
  return getValidationManager(this).validate();
}
```

If your widgets implement `ValidationDisplay` then they will automatically notified when ever the validation results change (via the binding process).  Otherwise you can use the `ValidationBinder` to bind the validation results to any widget that implements `ValidationDisplay`.

```
public class MyForm extends Composite {
  
  // define our binder and widgets as before..
  FormBinder binder = ...;
  ...
  
  // We'll use some additional validation bindings..
  ValidationBinder validation = new ValidationBinder();

  // a widget that implements ValidationDisplay
  ValidationDisplay givenNameValidationMessages = new ValidationDisplayLabel();
 
  public MyForm(MyModel model) {

    // bind our widgets as normal, if the widget implements ValidationDisplay the plugin
    // will ensure it'll automatically notified of validation events.
    binder.bind(model.givenName).to(givenNameField);
    ...

    // We can also use the validation binder to bind the validation state to any
    // instance of ValidationDisplay
    validation.bindValidationOf(model.givenName).to(givenNameValidationMessages); 

    // We can also bind the standard validation style names (e.g. `validationError` 
    // to arbitrary widgets.
    validation.bindValidationOf(model.giveName).toStyleOf(givenNameLabel);

    // do the layout as normal..
    ...
  }
}

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

### Dirty Models ###

The `BeanModelProvider` also supports dirty tracking.  The dirty model will be `true` if any of the models it creates have a value different from the bean.

```
ValueModel<Boolean> dirty = personProvider.dirty();
```

If you have more than one provider you can use conditions to create a global dirty model.

```
ValueModel<Boolean> dirty = Conditions.or(personProvider.dirty(), addressProvider.dirty());
```


Then the dirty model can be easily used to enabled and disable buttons in the view.

```
FormBinder binder = new FormBinder();
binder.enable(saveButton).when(model.dirty);
```


# Building and running the demo #

Pectin uses [Gradle](http://gradle.org) as its build system.  You don't need to have gradle installed, it will download itself the first time you run the build script.

**Running the Demo** (from the **demo** sub directory):
```
./gradlew demo
```


**Building the JAR** (from the **gwt-pectin** sub directory):
```
./gradlew libs
```

To see the list of available targets type:
```
./gradlew -t
```

You can find more information in the wiki page BuildingFromSource