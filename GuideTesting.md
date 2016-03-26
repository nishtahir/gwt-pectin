# Testing #
There are a couple of approaches to testing your view code which depending on your circumstance.  Because Pectin makes use of functions, formats, validators and the like a lot the forms functionality can be moved out of the form and into these classes where they can be tested independently.

Form here you need to decide what level of testing you'd like to do as unit tests and integration tests.  I.e. if you're doing comprehensive integration testing (with the likes of Selenium or some unlucky humans) then you may choose to do limited testing your models (since if the view works then there's a good chance your model does too).  You'll always need to do integration testing if you want to test if things like watermarks and validation messages actually appear on the UI when and where you expected.  Testing the model will only get you so far and you probably don't want to have duplicate testing code.

There is of course also merit in unit testing your models.  You can for instance easily verify that your value models are in fact bound to the correct property of your bean.  You can also test that other aspects like commit/revert, validation and metadata are working correctly (albeit only from the models perspective).

## Testing your model without resorting to GWTTestCase ##

One of the advantages of MVP is that you can reduce the amount of testing that is preformed using GWTTestCase.  Since Pectin uses `GWT.create(...)` for binding to beans a `TestUtil.reflectionBeanModelProvider` utility is provided so you can run all your using your favorite testing tools on the JRE.

**Please Note:** The `TestUtil` class will be in release 0.6.

This utility uses reflection to implement the three abstract methods of `BeanModelProvider`.  The trick to using it is to define a non-public constructor that accepts the provider of required type.

First we modify our model so our test can use a different provider.

```
public MyFormModel extends FormModel
{
   public abstract class PersonProvider extends BeanModelProvider<Person>{}
   private BeanModelProvider<Person> provider;

   // the public constructor
   public MyFormModel()
   {  
      this(GWT.create(PersonProvider.class));
   }

   // constructor for testing so we can use the reflection variant.
   protected MyFormModel(BeanModelProvider<Person> provider)
   {
      this.provider = provider;

      // do the rest
      ...
   }
}
```


Now our unit tests can use a reflection variant of the `BeanModelProvider`.

```
public class MyFormModelTest
{
   private MyFormModel model;

   @BeforeMethod
   public void setUp()
   {
      // we'll use our JRE provider for the tests.
      BeanModelProvider<Person> provider = TestUtil.reflectionBeanModelProvider(Person.class);

      model = new MyFormModel(provider)
   }

   @Test
   public void setPerson()
   {
      Person person = new Person("Joe", "Blogs", "joe@bloggy.com");

      model.setPerson(person);

      assertEquals(model.firstName.getValue(), "Joe");
      assertEquals(model.lastName.getValue(), "Blogs");
      assertEquals(model.emailAddress.getValue(), "joe@bloggy.com");
   }

   @Test
   public void commit()
   {
      Person person = new Person("Joe", "Blogs", "joe@bloggy.com");
      model.setPerson(person);

      model.firstName.setValue("Joseph");
      model.lastName.setValue("Blogs-Smith");
      model.emailAddress.setValue("joseph@blogsmithy.com");

      // values should still be the same on the bean till we commit.
      assertEquals(person.getFirstName(), "Joe");
      assertEquals(person.getLastName(), "Blogs");
      assertEquals(person.getEmailAddress(), "joe@bloggy.com");

      model.commit();

      assertEquals(person.getFirstName(), "Joseph");
      assertEquals(person.getLastName(), "Blogs-Smith");
      assertEquals(person.getEmailAddress(), "joseph@blogsmithy.com");

   }
}

```
