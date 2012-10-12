package com.jamesward.service;


import java.util.List;

import com.jamesward.model.Contact;

public interface ContactService {
    
    public void addContact(Contact contact);
    public List<Contact> getAllContacts();
    public void addContactMethod(Integer contactId, String name, String value);
    
}
