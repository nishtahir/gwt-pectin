# Presentation Model Compared to MVP #

## Comparing apples and oranges... ##

One of the difficulties in comparing various standard approaches is that each approach is not so much a pattern in and of itself, but rather a collection of patterns.  For example the presenter in MVP typically performs the roles of both a facade and mediator while the Presentation Model approach employs patterns such as adapter, mediator and decorator.

In reality the two approaches are not mutually exclusive.  In fact using a presentation model can greatly simplify the role of the presenter in an MVP environment.

## What do you mean by Model? ##

One of the key differences between MVP and the Presentation Model approach is the scope of what we define as "the model".  In MVP the model typically refers to just the domain objects that will be displayed, things like a Customer, Contact or Account.  From a Presentation Model perspective the term model encompasses the domain data **plus** all additional state that can change during the normal operation of the UI.  An example would be state to control the requirement that "the comments field should only be visible when the user has selected 'Other'".

Martin Fowler summaries the Presentation Model as follows (from http://martinfowler.com/eaaDev/PresentationModel.html)
> The essence of a Presentation Model is of a fully self-contained class that represents all the data and behavior of the UI window, but without any of the controls used to render that UI on the screen. A view then simply projects the state of the presentation model onto the glass.

> To do this the Presentation Model will have data fields for all the dynamic information of the view. This won't just include the contents of controls, but also things like whether or not they are enabled. In general the Presentation Model does not need to hold all of this control state (which would be lot) but any state that may change during the interaction of the user. So if a field is always enabled, there won't be extra data for its state in the Presentation Model.

Pectin has also been designed so that additional state (such as enabledness,  visibility, validation etc) can be managed by plugins.  Without this you can end up with a boat load of value models in your form that quickly becomes very unreadable.

## Differences with the View ##

Another difference between using MVP and the Presentation Model approach (and specifically the gwt-pectin implementation) and  is the means by which changes in the model are reflected in the view and vice versa.  Within MVP this role is taken by the Presenter (i.e. coded by you) and within Pectin this is automatically handled by bindings (both between the domain classes and the presentation model and between the presentation model and the view).

## Keeping the Presenter ##

The Presenter (also known as the [Supervising Controller](http://martinfowler.com/eaaDev/SupervisingPresenter.html)) typically performs additional roles such as executing RPC calls, responding to application events and performing tasks such as instantiating the view and updating the model.  This role is still useful in and of itself when using the Presentation Model approach.  By moving all the view state to a Presentation Model the Presenters roles can be greatly simplified.


## An Example ##
You can check out GoogleMvpExampleWithPectin for an example of using Pectin in an MVP architecture.  It shows a modified version of the GWT MVP example that uses Pectin for the EditContact form.

## References ##
  * http://martinfowler.com/eaaDev/ModelViewPresenter.html
  * http://martinfowler.com/eaaDev/SupervisingPresenter.html
  * http://martinfowler.com/eaaDev/PresentationModel.html