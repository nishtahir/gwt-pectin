package com.google.gwt.sample.contacts.test;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.sample.contacts.client.ContactsServiceAsync;
import com.google.gwt.sample.contacts.client.presenter.ContactsPresenter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExampleJRETest
{
   private ContactsPresenter contactsPresenter;
   private ContactsServiceAsync mockRpcService;
   private HandlerManager eventBus;
   private ContactsPresenter.Display mockDisplay;

   @BeforeMethod
   protected void setUp()
   {
//      mockRpcService = createStrictMock(ContactsServiceAsync.class);
//      eventBus = new HandlerManager(null);
//      mockDisplay = createStrictMock(ContactsPresenter.Display.class);
//      contactsPresenter = new ContactsPresenter(mockRpcService, eventBus, mockDisplay);
   }

   @Test
   public void testContactSort()
   {
//      ArrayList<ContactDetails> contactDetails = new ArrayList<ContactDetails>();
//      contactDetails.add(new ContactDetails("0", "c_contact"));
//      contactDetails.add(new ContactDetails("1", "b_contact"));
//      contactDetails.add(new ContactDetails("2", "a_contact"));
//      contactsPresenter.setContactDetails(contactDetails);
//      contactsPresenter.sortContactDetails();
//      assertTrue(contactsPresenter.getContactDetail(0).getDisplayName().equals("a_contact"));
//      assertTrue(contactsPresenter.getContactDetail(1).getDisplayName().equals("b_contact"));
//      assertTrue(contactsPresenter.getContactDetail(2).getDisplayName().equals("c_contact"));
   }
}
