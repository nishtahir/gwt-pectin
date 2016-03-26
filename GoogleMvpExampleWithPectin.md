# Using Pectin with MVP #

&lt;wiki:gadget url="http://gwt-pectin.googlecode.com/svn/trunk/etc/adsense\_gadget.xml" border="0" width="468" height="60" /&gt;

Following on from the [comparison of MVP and Pectin](ComparedToMVP.md), this page shows how Pectin can be used with the [MVP sample](http://code.google.com/webtoolkit/doc/latest/tutorial/mvp-architecture.html) in the GWT documentation.  The example itself is a lousy test case for Pectin however since the customer edit form is so trivial.  But for the sake of the argument I'll do it anyway and add a few features to give a better feel for the features of Pectin.

# The Approach #
There are a couple of ways to approach the design.  In this case I've chosen to embed the presentation model within the view.  You could also have the Presenter own the Presentation Model but in this case there were no clear advantages to that approach and it only increased the complexity of the Presenter.

It's also worth noting that I've only use Pectin on the EditContactPresenter since it's the only form based view.

# Updating the EditContactPresenter #
The changes to the presenter are minimal and main include changes to the Display interface and the pieces of code that interact with it. The changes to the Display interface are:

  1. Replacing the `HasValue` getters with a single setter for the `Contact`.
  1. Add new `commit()` method to write any changes to the `Contact` prior to saving.
  1. Added `validate()` method to support the new validation features.

Once this has been done the `EditContactPresenter` no longer acts as mediator between the `Contact` and the fields in the view.  The Display interface now becomes.

```

public interface Display {

   HasClickHandlers getSaveButton();

   HasClickHandlers getCancelButton();
      
   // replace all the HasValue getters with this
   void setContact(Contact contact);

   // added new commit method to write changes to the 
   // current contact.  
   void commit();

   // new method to validate the current state of the
   // form before committing the changes.
   boolean validate();
     
   Widget asWidget();      
}
```

The other major changes to the `EditContactPresenter` are the Constructor and the `doSave()` method.

The Constructor call to get the contact now becomes:
```
rpcService.getContact(id, new AsyncCallback<Contact>() {

   public void onSuccess(Contact result) {
      // store our contact locally
      EditContactPresenter.this.contact = result;
      // no longer have to update each field, we just pass in the Contact
      // and let the views Presentation Model handle that.
      EditContactPresenter.this.display.setContact(result);
   }

   public void onFailure(Throwable caught) {
      Window.alert("Error retrieving contact");
   }
});

```

And the `doSave()` method now becomes:
```

private void doSave()
{
   // we've added validation so we only save if the form is valid
   if (display.validate()) {

      // all is good so commit the changes to the Bean
      display.commit();

      // and proceed as per normal
      rpcService.updateContact(contact, new AsyncCallback<Contact>() {

         public void onSuccess(Contact result)
         {
            eventBus.fireEvent(new ContactUpdatedEvent(result));
         }

         public void onFailure(Throwable caught)
         {
            Window.alert("Error updating contact");
         }
      });
   }
}
```

The full updated source is available here: [EditContactPresenter.java](http://code.google.com/p/gwt-pectin/source/browse/trunk/contacts-mvp-example/src/main/java/com/google/gwt/sample/contacts/client/presenter/EditContactPresenter.java)


# Updating the EditContactView #

## Create our Presentation Model ##
The first step was to create a Presentation Model for the view.  It defines the three fields displayed by the view.  In order to make things a little bit more realistic I've added validation and watermarks.

```
public class EditContactViewModel extends FormModel
{
   
   // create our bindings to our bean
   public abstract static class ContactProvider extends BeanModelProvider<Contact> {}
   // we're using the base type so we can use a refection variant in our JRE tests.
   private BeanModelProvider<Contact> contactProvider;

   protected final FieldModel<String> firstName;
   protected final FieldModel<String> lastName;
   protected final FieldModel<String> emailAddress;

   public EditContactViewModel()
   {
      this((ContactProvider) GWT.create(ContactProvider.class));
   }

   /**
    * This constructor is provided for our testing.
    */
   protected EditContactViewModel(BeanModelProvider<Contact> contactProvider)
   {
      this.contactProvider = contactProvider;

      // create our fields and bind to our bean
      firstName = fieldOfType(String.class).boundTo(contactProvider, "firstName");
      lastName = fieldOfType(String.class).boundTo(contactProvider, "lastName");
      emailAddress = fieldOfType(String.class).boundTo(contactProvider, "emailAddress");

      // add some validation rules
      validateField(firstName).using(new NotEmptyValidator("Please enter your first name"));
      validateField(lastName).using(new NotEmptyValidator("Please enter your last name"));
      validateField(emailAddress).using(new NotEmptyValidator("Please enter your email address"));

      // add some water marks to denote required fields
      watermark(firstName, lastName, emailAddress).with("Required");
   }

   public void setContact(Contact contact)
   {
      // clear any previous validation state.
      getValidationManager(this).clear();
      // and update all our value models
      contactProvider.setBean(contact);
   }

   public void commit()
   {
      contactProvider.commit();
   }

   public boolean validate()
   {
      return getValidationManager(this).validate();
   }
}
```

The full updated source is available here: [EditContactViewModel.java](http://code.google.com/p/gwt-pectin/source/browse/trunk/contacts-mvp-example/src/main/java/com/google/gwt/sample/contacts/client/view/EditContactViewModel.java)

### And test it... ###
A basic test class using TestNG is here: [EditContactViewModelTest.java](http://code.google.com/p/gwt-pectin/source/browse/trunk/contacts-mvp-example/src/test/java/com/google/gwt/sample/contacts/client/view/EditContactViewModelTest.java)

You can read about testing and the various approaches on the [testing page](GuideTesting.md) on the wiki.



## Now Update the View ##

The changes to the view are relatively simple.  Firstly we update it to implement the new Display interface, namely by removing the widget getters and adding the new methods.  The new methods are simply delegated to the presentation model.  We also add the pectin binders and a widget for displaying the validation messages.


```
// First we update the form to define and create our presentation model and to add
// the Pectin widget binders.
public class EditContactView extends Composite implements EditContactPresenter.Display {

   private EditContactViewModel model = new EditContactViewModel();
   private WidgetBinder binder = new WidgetBinder();
   private ValidationBinder validation = new ValidationBinder();

   ...
}
```

Now we bind the model to our widgets
```
public EditContactView {

   // normal view creation code
   ...

   // create and add our validation messages and add it to the form
   ValidationDisplayPanel validationMessages = new ValidationDisplayPanel();
   contentDetailsPanel.add(validationMessages);
 
   // more code
   ...

   // Now we bind widgets to their models
   binder.bind(model.firstName).to(firstName);
   binder.bind(model.lastName).to(lastName);
   binder.bind(model.emailAddress).to(emailAddress);

   // and bind our validation results to our validaiton display
   validation.bindValidationOf(model).to(validationMessages);
```


And finally we'll remove the old widget getters and add the new methods defined in the Display interface.  The new methods simply delegate to the model.
```
public void setContact(Contact contact)
{
   model.setContact(contact);
}

public void commit()
{
   model.commit();
}

public boolean validate()
{
   return model.validate();
}
```

The full updated source is available here: [EditContactView.java](http://code.google.com/p/gwt-pectin/source/browse/trunk/contacts-mvp-example/src/main/java/com/google/gwt/sample/contacts/client/view/EditContactView.java)