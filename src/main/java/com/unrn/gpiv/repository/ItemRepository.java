package com.unrn.gpiv.repository;

import com.unrn.gpiv.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // Con extender JpaRepository ya tenés el findAll(), save() y delete() gratis!
}
