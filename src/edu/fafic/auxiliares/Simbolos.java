package edu.fafic.auxiliares;

public class Simbolos {

    public static boolean isLetra(int caractere) {

        return Character.isLetter(caractere) && caractere != '_';

    }

    public static boolean isLetraOuDigito(int caractere) {

        return Character.isLetterOrDigit(caractere) && caractere != '_';

    }

}
