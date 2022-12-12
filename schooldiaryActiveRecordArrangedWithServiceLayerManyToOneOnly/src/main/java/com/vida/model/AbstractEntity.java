package com.vida.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@MappedSuperclass
public class AbstractEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Version
    public Long version;

    public AbstractEntity() {
    }

    public AbstractEntity(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public AbstractEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getVersion() {
        return version;
    }

    public AbstractEntity setVersion(Long version) {
        this.version = version;
        return this;
    }
}
