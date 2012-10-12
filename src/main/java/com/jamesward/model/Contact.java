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
