package com.tavernasorte.database.entity;

import com.tavernasorte.exception.InvalidItemException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Item {

    public static final int MIN_MULTIPLIER = 1;
    public static final int MAX_MULTIPLIER = 20;
    private static final String INVALID_MULTIPLIER = "O multiplicador do item deve estar entre "
            + MIN_MULTIPLIER + " e " + MAX_MULTIPLIER;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private int price;
    @Column(name = "multiplier")
    @Min(value = MIN_MULTIPLIER, message = INVALID_MULTIPLIER)
    @Max(value = MAX_MULTIPLIER, message = INVALID_MULTIPLIER)
    private int multiplier = MIN_MULTIPLIER;
    @Column(name = "description")
    private String description;

    public Item() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        validateMultiplier(multiplier);
        this.multiplier = multiplier;
    }

    public int prizeMultiplier() {
        validateMultiplier(multiplier);
        return multiplier;
    }

    private static void validateMultiplier(int multiplier) {
        if (multiplier < MIN_MULTIPLIER || multiplier > MAX_MULTIPLIER) {
            throw new InvalidItemException(INVALID_MULTIPLIER);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
