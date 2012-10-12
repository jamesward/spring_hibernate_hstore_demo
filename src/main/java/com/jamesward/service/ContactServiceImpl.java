package com.jamesward.service;

import com.jamesward.model.Contact;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import java.util.List;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    @PersistenceContext
    EntityManager em;

    @Override
    public void addContact(Contact contact) {
        em.persist(contact);
    }

    @Override
    public List<Contact> getAllContacts() {
        CriteriaQuery<Contact> c = em.getCriteriaBuilder().createQuery(Contact.class);
        c.from(Contact.class);
        return em.createQuery(c).getResultList();
    }
    
    public Contact getContact(Integer id) {
        return em.find(Contact.class, id);
    }

    @Override
    public void addContactMethod(Integer contactId, String name, String value) {
        Contact contact = getContact(contactId);
        contact.contactMethods.put(name, value);
        //em.merge(contact);
    }
    
}
