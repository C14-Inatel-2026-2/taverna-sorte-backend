package com.tavernasorte.database.repository;

import com.tavernasorte.database.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
