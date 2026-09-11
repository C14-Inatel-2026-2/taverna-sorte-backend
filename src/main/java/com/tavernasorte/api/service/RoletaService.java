package com.tavernasorte.api.service;

import org.springframework.stereotype.Service;

@Service
public class RoletaService {

    public int girar() {
        return (int) (Math.random() * 10) + 1;
    }
}