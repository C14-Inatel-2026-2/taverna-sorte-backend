package com.tavernasorte.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // gerando o ID do item automaticamente
    private Long id; // Long aceita valores nulos antes do ID ser gerado pelo banco

    @Column(name = "nome")
    @JsonProperty("nome")
    private String name;
    @Column(name = "preco")
    @JsonProperty("preco")
    private int price;
    @Column(name = "multiplicador")
    @JsonProperty("multiplicador")
    private double multiplier;
    @Column(name = "sorte")
    @JsonProperty("sorte")
    private double luck;
    @Column(name = "descricao")
    @JsonProperty("descricao")
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

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLuck() {
        return luck;
    }

    public void setLuck(double luck) {
        this.luck = luck;
    }
}
