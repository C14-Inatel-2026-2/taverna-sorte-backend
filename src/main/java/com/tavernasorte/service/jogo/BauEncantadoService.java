package com.tavernasorte.service.jogo;

import com.tavernasorte.dto.ResultadoBau;
import com.tavernasorte.exception.ApostaInvalidaException;
import com.tavernasorte.model.jogo.Resultado;
import com.tavernasorte.model.jogo.bau.DetalheBauEncantado;
import com.tavernasorte.model.jogo.bau.Simbolo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class BauEncantadoService implements Jogo<Void> {

    private static final int QUANTIDADE_SIMBOLOS = 3;
    private final SorteadorSimbolo sorteadorSimbolo;

    public BauEncantadoService(SorteadorSimbolo sorteadorSimbolo) {
        this.sorteadorSimbolo = sorteadorSimbolo;
    }

    @Override
    public String id() {
        return "bau-encantado";
    }

    @Override
    public Class<Void> tipoEscolha() {
        return Void.class;
    }

    @Override
    public Resultado jogar(Void escolha) {
        return calcularResultado(sortearSimbolos());
    }

    /** Mantém a operação usada pela rota POST /bau/abrir. */
    public ResultadoBau abrir(double valorAposta) {
        if (!Double.isFinite(valorAposta) || valorAposta <= 0) {
            throw new ApostaInvalidaException("O valor da aposta deve ser maior que zero");
        }

        List<Simbolo> simbolos = sortearSimbolos();
        return new ResultadoBau(simbolos, calcularPremio(simbolos, valorAposta));
    }

    public double calcularPremio(List<Simbolo> simbolos, double valorAposta) {
        return valorAposta * calcularResultado(simbolos).multiplicador();
    }

    private List<Simbolo> sortearSimbolos() {
        List<Simbolo> simbolos = new ArrayList<>();
        for (int i = 0; i < QUANTIDADE_SIMBOLOS; i++) {
            simbolos.add(sorteadorSimbolo.sortear());
        }
        return simbolos;
    }

    private Resultado calcularResultado(List<Simbolo> simbolos) {
        DetalheBauEncantado detalhe = new DetalheBauEncantado(simbolos);

        int simbolosDistintos = new HashSet<>(simbolos).size();
        if (simbolosDistintos == 1) {
            Simbolo simbolo = simbolos.get(0);
            return new Resultado(simbolo.getMultiplicador(), "Três " + simbolo + "! Prêmio de " + simbolo.getMultiplicador() + "x.", detalhe);
        }
        if (simbolosDistintos == 2) {
            return new Resultado(1, "Um par! Sua aposta foi devolvida.", detalhe);
        }
        return new Resultado(0, "Nenhum símbolo combinou.", detalhe);
    }
}
