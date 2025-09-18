package edu.fafic.auxiliares;

public record Token(int tipo, Object valor) {

    // Constantes para tipos de token

    public static final int CHAR = 0;
    public static final int ELSE = 1;
    public static final int FALSE = 2;
    public static final int IF = 3;
    public static final int INT = 4;
    public static final int MAIN = 5;
    public static final int OUT = 6;
    public static final int PRINTF = 7;
    public static final int RETURN = 8;
    public static final int STATIC = 9;
    public static final int VOID = 10;
    public static final int WHILE = 11;

    public static final int ID = 12;
    public static final int LITERALNUMERICO = 13;
    public static final int AT = 14;
    public static final int OP = 15;
    public static final int LOG = 16;
    public static final int RELOP = 17;
    public static final int LITERALSTRING = 18;
    public static final int LITERALCHAR = 19;
    public static final int PONTUACAO = 20;


    // Valor fim de Arquivo

    public static final int EOF = 100;

    // Valores para tokens RELOP(< > <= >=  == )

    public static final int LT = 1;
    public static final int LE = 2;
    public static final int GT = 3;
    public static final int GE = 4;
    public static final int EQ = 5;
    public static final int NE = 6;

    // Valores para tokens AUX

    public static final int ATR = 1;

    // Valores para tokens OP

    public static final int AD = 1;
    public static final int SUB = 2;
    public static final int DIV = 3;
    public static final int MUL = 4;

    // Valores para tokens LOG

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;

    // Valores para tokens PONTUACAO

    public static final int AP = 1;
    public static final int FP = 2;
    public static final int AC = 3;
    public static final int FC = 4;
    public static final int PV = 5;
    public static final int VG = 6;


    public Token(int tipo) {
        this(tipo, null);
    }

    public static Token EOF() {
        return new Token(EOF);
    }

    public String toString() {
        String valorString = switch (tipo) {
            case RELOP -> RELOP((int) valor);
            case AT -> "ATR";
            case OP -> OP((int) valor);
            case LOG -> tipoLog((int) valor);
            case PONTUACAO -> tipoPontuacao((int) valor);
            default -> "-";
        };

        return "<%s, %s>".formatted(STRING(), valorString);
    }

    private String STRING() {
        return switch (tipo) {
            case CHAR -> "char";
            case ELSE -> "else";
            case FALSE -> "falso";
            case IF -> "if";
            case INT -> "int";
            case MAIN -> "main";
            case OUT -> "out";
            case PRINTF -> "printf";
            case RETURN -> "return";
            case STATIC -> "static";
            case VOID -> "void";
            case WHILE -> "while";
            case ID -> "id";
            case LITERALNUMERICO -> "literalNumerico";
            case LITERALSTRING -> "literalString";
            case LITERALCHAR -> "literalChar";
            case AT -> "at";
            case OP -> "op";
            case LOG -> "log";
            case RELOP -> "relop";
            case PONTUACAO -> "pontuacao";
            default -> "Erro";
        };
    }

    private String RELOP(int tipo) {
        return switch (tipo) {
            case LT -> "LT";
            case LE -> "LE";
            case GT -> "GT";
            case GE -> "GE";
            case EQ -> "EQ";
            case NE -> "NE";
            default -> "Erro";
        };
    }

    private String OP(int tipo) {
        return switch (tipo) {
            case AD -> "+";
            case SUB -> "-";
            case MUL -> "*";
            case DIV -> "/";
            default -> "Erro";
        };

    }

    private String tipoLog(int tipo) {
        return switch (tipo) {
            case AND -> "&&";
            case OR -> "||";
            case NOT -> "!";
            default -> "Erro";
        };
    }

    private String tipoPontuacao(int tipo) {
        return switch (tipo) {
            case AP -> "(";
            case FP -> ")";
            case AC -> "{";
            case FC -> "}";
            case PV -> ";";
            case VG -> ",";
            default -> "Erro";
        };
    }
}
