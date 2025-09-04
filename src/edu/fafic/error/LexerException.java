package edu.fafic.error;

import java.io.Serial;

public class LexerException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    // Construtores

    public LexerException() {

        super("Erro !!!");

    }

    public LexerException(String mensagem) {

        super(mensagem);

    }

}
