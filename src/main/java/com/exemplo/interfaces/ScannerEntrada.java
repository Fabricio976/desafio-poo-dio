package com.exemplo.interfaces;

import java.util.Scanner;

public class ScannerEntrada implements LeitorUsuario {

    private final Scanner scanner;

    public ScannerEntrada() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public int nextInt() {
        int value = scanner.nextInt();
        scanner.nextLine(); // limpa o buffer
        return value;
    }

    @Override
    public void close() {
        scanner.close();
    }
}