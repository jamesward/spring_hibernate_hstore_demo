NoSQL Inside SQL with Java, Spring, Hibernate, and PostgreSQL
=============================================================

There are many benefits to schema-less NoSQL datastores, but there are always tradeoffs.  The primary gift that NoSQL movement has given us is that we are now more open to variety of options for how we persist data, instead of always trying to shoehorn everything into a relational model.  Now the challenge is in deciding which persistence model fits best with each domain in a system and then combining those models in a cohesive way.  The general term to describe this is [Polyglot Persistence](http://martinfowler.com/bliki/PolyglotPersistence.html) and there are many ways to accomplish it.  Lets walk through how you can combine a regular SQL model with a key-value NoSQL model using Java, Spring, Hibernate, and PostgreSQL.

This article will walk through the pieces of a simple web application that uses regular SQL and [PostgreSQL's hstore](http://www.postgresql.org/docs/9.1/static/hstore.html) for key value pairs.  This method is a mix of NoSQL inside SQL.  One benefit of this approach is that the same datastore can be used for both the SQL and the NoSQL data.
 
In this example the server technologies will be Java, Spring, and Hibernate.  (The same thing can also be done with [Rails](http://schneems.com/post/19298469372/you-got-nosql-in-my-postgres-using-hstore-in-rails), [Django](http://craigkerstiens.com/2012/06/11/schemaless-django/), and many other technologies.)  To add Hibernate support for `hstore` I found a fantastic blog about [Storing sets of key/value pairs in a single db column with Hibernate using PostgreSQL hstore type](http://backtothefront.net/2011/storing-sets-keyvalue-pairs-single-db-column-hibernate-postgresql-hstore-type/).  I won't go through that code here but you can find everything in the [GitHub repo for my demo project](https://github.com/jamesward/spring_hibernate_hstore_demo).

This demo app uses Maven to define [the dependencies](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/pom.xml).  Embedded Jetty is started via a [plain 'ole Java application](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/Webapp.java) that sets up Spring MVC.  Spring is configured via Java Config for the [main stuff](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/config/RootConfig.java), the [web stuff](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/config/WebConfig.java), and the [database stuff](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/config/DataConfig.java).

The client technologies will be jQuery and Bootstrap and there is a strict seperation between the client and server via RESTful JSON services.  The whole client-side is in a [plain 'ole HTML file](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/resources/META-INF/resources/index.html).  Via jQuery / Ajax the client communicates to JSON services exposed via a [Spring MVC Controller](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/controller/ContactController.java).

Ok.  Now onto the PartialSQL stuff.  This application stores "Contacts" that have a name but also can have many "Contact Methods" (e.g. phone numbers and email addresses).  This is a good place for a schema-less, key-value pair column because it avoids the cumbersome alternatives: putting that information into a separate table or trying to create a model object that has all of the possible "Contact Methods".  So lets take a look at the simple [Contact Entity](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/model/Contact.java):

```
package com.jamesward.model;

import net.backtothefront.HstoreUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
@TypeDef(name = "hstore", typeClass = HstoreUserType.class)
public class Contact {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(nullable = false)
    public String name;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    public Map<String, String> contactMethods = new HashMap<String, String>();

}
```

If you are familiar with Hibernate / JPA then most of this should look pretty familiar to you.  The new / interesting stuff is the `contactMethods` property.  It is a `Map<String, String>` and it uses PostgreSQL's hstore datatype.  In order for that to work, the type has to be defined and the `columnDefinition` set.  Thanks again to [Jakub Głuszecki](http://backtothefront.net/) for putting together the [HstoreHelper](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/net/backtothefront/HstoreHelper.java) and [HstoreUserType](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/net/backtothefront/HstoreUserType.java) that make this possible.

Now the rest is simple because it's just plain Hibernate / JPA.  Here is the [ContactService](https://github.com/jamesward/spring_hibernate_hstore_demo/blob/master/src/main/java/com/jamesward/service/ContactServiceImpl.java) that does the basic query and updates.

```
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
    }
    
}
```

Now that you understand how it all works, check out a [live demo on Heroku](http://immense-crag-5799.herokuapp.com/).

If you want to run this app locally or on Heroku, then first you need to grab the source code and continue working inside the newly created `spring_hibernate_hstore_demo` directory:

        $ git clone https://github.com/jamesward/spring_hibernate_hstore_demo.git
        $ cd spring_hibernate_hstore_demo

To run locally:

1. Setup your PostgreSQL database to support hstore by opening a `psql` connection to it:

        $ psql -U username -W -h localhost database

2. Then enable hstore:

        => create extension hstore;
        => \q

3. Build the app (depends on having [Maven installed](http://maven.apache.org):

        $ mvn package

4. Set the `DATABASE_URL` environment variable to point to your PostgreSQL server:

        $ export DATABASE_URL=postgres://username:password@localhost/databasename

5. Start the app:

        $ java -cp target/classes:target/dependency/* com.jamesward.Webapp

6. [Try it out](http://localhost:8080)

Cool!  Now you can run it on the cloud with Heroku.  Here is what you need to do:

1. [Install the Heroku Toolbelt](http://toolbelt.heroku.com)

2. Login to Heroku:

        $ heroku login

3. Create a new app:

        $ heroku create

4. Add Heroku Postgres:

        $ heroku addons:add heroku-postgresql:dev

5. Tell Heroku to set the `DATABASE_URL` environment variable based on the database that was just added (replace `YOUR_HEROKU_POSTGRESQL_COLOR_URL` with your own):

        $ heroku pg:promote YOUR_HEROKU_POSTGRESQL_COLOR_URL

6. Open a `psql` connection to the database:

        $ heroku pg:psql

7. Enable hstore support in your database:

        => create extension hstore;
        => \q

8. Deploy the app:

        $ git push heroku master

9. View the app on the cloud:

        $ heroku open

Fantastic!  Let me know if you have any questions.
