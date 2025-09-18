package edu.fafic.auxiliares;

public class PalavrasChave {

    private static final String CHAR = "char";
    private static final String ELSE = "else";
    private static final String FALSE = "false";
    private static final String INT = "int";
    private static final String IF = "if";
    private static final String MAIN = "main";
    private static final String OUT = "out";
    private static final String PRINTF = "printf";
    private static final String RETURN = "return";
    private static final String STATIC = "static";
    private static final String VOID = "void";
    private static final String WHILE = "while";

    public static boolean isPalavraChave(StringBuffer stringBuffer) {
        String palavra = stringBuffer.toString();
        return palavra.equals(CHAR)
               || palavra.equals(ELSE)
               || palavra.equals(FALSE)
               || palavra.equals(INT)
               || palavra.equals(IF)
               || palavra.equals(MAIN)
               || palavra.equals(OUT)
               || palavra.equals(PRINTF)
               || palavra.equals(RETURN)
               || palavra.equals(STATIC)
               || palavra.equals(VOID)
               || palavra.equals(WHILE);
    }

    public static int tipoPalavraChave(StringBuffer palavra) {
        return switch (palavra.toString()) {
            case CHAR -> Token.CHAR;
            case ELSE -> Token.ELSE;
            case FALSE -> Token.FALSE;
            case INT -> Token.INT;
            case IF -> Token.IF;
            case MAIN -> Token.MAIN;
            case OUT -> Token.OUT;
            case PRINTF -> Token.PRINTF;
            case RETURN -> Token.RETURN;
            case STATIC -> Token.STATIC;
            case VOID -> Token.VOID;
            default -> Token.WHILE;
            // TODO: O default deveria levantar um erro caso não reconheça a palavra chave, ou retorna -1
        };
    }
}
