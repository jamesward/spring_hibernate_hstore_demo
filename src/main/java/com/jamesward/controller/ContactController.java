package com.jamesward.controller;

import com.jamesward.model.Contact;
import com.jamesward.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }

    @RequestMapping(method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public final void create(@RequestBody final Contact contact) {
        contactService.addContact(contact);
    }

    @RequestMapping(value="/{id}/contact-method", method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public final void addContactMethod(@PathVariable("id") final Integer contactId, @RequestBody final Map<String, String> data) {
        contactService.addContactMethod(contactId, data.get("name"), data.get("value"));
    }

}
