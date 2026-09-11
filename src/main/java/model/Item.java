package model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // gerando o ID do item automaticamente
    private Long id; // Long aceita valores nulos antes do ID ser gerado pelo banco

    private String nome;
    private double preco;
    private int quantidade;

    public Item(){

    }
}