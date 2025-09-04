package edu.fafic.principal;

import edu.fafic.auxiliares.AnalisadorLexico;
import edu.fafic.auxiliares.Token;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class TestaAnalisadorLexico {

    public static void main(String[] args) {

        BufferedReader bufferedReader;
        AnalisadorLexico analisadorLexico;
        Token token;

        System.out.println("Analise Léxica: \n");

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("Programa.lex")));

            analisadorLexico = new AnalisadorLexico(bufferedReader);
            token = analisadorLexico.pegarProximoToken();

            while (token.getTipo() != Token.EOF) {

                System.out.println("Token: " + token);

                token = analisadorLexico.pegarProximoToken();

            }

        } catch (Exception e) {

            System.err.println("Exceção: " + e.getMessage());

        }

    }

}
