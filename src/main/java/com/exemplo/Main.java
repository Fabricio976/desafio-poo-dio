package com.exemplo;

import com.exemplo.interfaces.ScannerEntrada;
import com.exemplo.views.Biblioteca;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca(new ScannerEntrada());
        biblioteca.carregarDados();
        biblioteca.iniciar();
    }
}