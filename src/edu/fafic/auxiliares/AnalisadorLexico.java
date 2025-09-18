package edu.fafic.auxiliares;

import edu.fafic.error.LexerException;

import java.io.BufferedReader;
import java.io.IOException;

public class AnalisadorLexico {

    private final BufferedReader reader;
    private int estado;
    private int buffer;
    private boolean temBuffer = false;

    public AnalisadorLexico(BufferedReader reader) {
        this.reader = reader;
    }

    public Token pegarProximoToken() throws IOException, LexerException {

        boolean feito = false;
        int tipo = Token.EOF;
        Object valor = null;
        StringBuffer stringBuffer = new StringBuffer();

        estado = 0;

        do {
            int caractere = doBuffer();

            if (Character.isWhitespace(caractere) && tipo == Token.EOF) {
                continue;
            } else if (caractere == -1 && tipo == Token.EOF) {
                return Token.EOF();
            }

            switch (estado) {
                case 0 -> {
                    switch (caractere) {
                        case '<' -> {
                            estado = 1;
                            tipo = Token.RELOP;
                            valor = Token.LT;
                        }

                        case '>' -> {
                            estado = 10;
                            tipo = Token.RELOP;
                            valor = Token.GT;
                        }

                        case '=' -> {
                            estado = 4;
                            tipo = Token.AT;
                            valor = Token.ATR;
                        }

                        case '!' -> {
                            estado = 7;
                            tipo = Token.LOG;
                            valor = Token.NOT;
                        }

                        case '+' -> {
                            tipo = Token.OP;
                            valor = Token.AD;
                            feito = true;
                        }

                        case '-' -> {
                            tipo = Token.OP;
                            valor = Token.SUB;
                            feito = true;
                        }

                        case '*' -> {
                            tipo = Token.OP;
                            valor = Token.MUL;
                            feito = true;
                        }

                        case '/' -> {
                            estado = 24;
                        }

                        case '&' -> {
                            estado = 28;
                        }

                        case '|' -> {
                            estado = 32;
                        }

                        case ',' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.VG;
                            feito = true;
                        }

                        case ';' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.PV;
                            feito = true;
                        }

                        case '(' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.AP;
                            feito = true;
                        }

                        case ')' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.FP;
                            feito = true;
                        }

                        case '{' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.AC;
                            feito = true;
                        }

                        case '}' -> {
                            tipo = Token.PONTUACAO;
                            valor = Token.FC;
                            feito = true;
                        }

                        default -> {
                            paraBuffer(caractere);
                            estado = getProximoEstado();
                        }
                    }
                }

                case 1 -> {
                    if (caractere == '=') {
                        valor = Token.LE;
                        feito = true;
                    } else if (caractere == '>') {
                        valor = Token.NE;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        feito = true;
                    }
                }

                case 4 -> {
                    if (caractere == '=') {
                        tipo = Token.RELOP;
                        valor = Token.EQ;
                    } else {
                        paraBuffer(caractere);
                    }
                    feito = true;//identifiquei um token, nao precisa continuar no while
                }

                case 7 -> {

                    if (caractere == '=') {
                        tipo = Token.RELOP;
                        valor = Token.NE;
                    } else {
                        paraBuffer(caractere);
                    }

                    feito = true;
                }

                case 10 -> {
                    if (caractere == '=') {
                        valor = Token.GE;
                    } else {
                        paraBuffer(caractere);
                    }
                    feito = true;
                }

                case 24 -> {
                    if (caractere == '/') {
                        while (caractere != '\n' && caractere != -1) {
                            caractere = doBuffer();
                        }

                        estado = 0;
                    } else if (caractere == '*') {
                        do {
                            caractere = doBuffer();
                        } while (caractere != '*' && caractere != -1);

                        caractere = doBuffer();

                        if (caractere != '/') {
                            estado = 24;
                        } else {
                            estado = 0;
                        }
                    } else {
                        tipo = Token.OP;
                        valor = Token.DIV;
                        paraBuffer(caractere);
                        feito = true;
                    }
                }

                case 28 -> {
                    if (caractere == '&') {
                        tipo = Token.LOG;
                        valor = Token.AND;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 29 -> {
                    if (caractere == '*' && doBuffer() == '/') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        estado = 29;
                        stringBuffer.append((char) caractere);
                    }
                }

                case 32 -> {
                    if (caractere == '|') {
                        tipo = Token.LOG;
                        valor = Token.OR;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 35 -> {
                    if (Simbolos.isLetra(caractere) || caractere == '_') {
                        estado = 36;
                        tipo = Token.ID;
                        stringBuffer = new StringBuffer();
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 36 -> {
                    if (Character.isLetterOrDigit(caractere) || caractere == '_') {
                        estado = 36;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        if (PalavrasChave.isPalavraChave(stringBuffer)) {
                            tipo = PalavrasChave.tipoPalavraChave(stringBuffer);
                            valor = null;
                        } else {
                            valor = stringBuffer.toString();
                        }
                        feito = true;
                    }
                }

                case 38 -> {
                    if (Character.isDigit(caractere)) {
                        estado = 39;
                        tipo = Token.LITERALNUMERICO;
                        stringBuffer = new StringBuffer();
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 39 -> {
                    if (Character.isDigit(caractere)) {
                        estado = 39;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        valor = Integer.valueOf(stringBuffer.toString());
                        feito = true;
                    }
                }

                case 41 -> {
                    if (caractere == '\'') {
                        estado = 42;
                        tipo = Token.LITERALCHAR;
                        stringBuffer = new StringBuffer();
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 42 -> {
                    if (Simbolos.isLetraOuDigito(caractere)) {
                        estado = 44;
                        stringBuffer.append((char) caractere);
                    } else if (caractere == '\\') {
                        estado = 43;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 43 -> {
                    if (caractere == 'r' || caractere == 'n' || caractere == 't' || caractere == '\\') {
                        estado = 44;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 44 -> {
                    if (caractere == '\'') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 46 -> {
                    if (caractere == '"') {
                        estado = 47;
                        tipo = Token.LITERALSTRING;
                        stringBuffer = new StringBuffer();
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }

                case 47 -> {
                    if (Simbolos.isLetraOuDigito(caractere) || caractere == ' ') {
                        estado = 47;
                        stringBuffer.append((char) caractere);
                    } else if (caractere == '\\') {
                        estado = 48;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        estado = 49;
                    }
                }

                case 48 -> {
                    if (caractere == '\\' || caractere == 'r' || caractere == 't' || caractere == 'n') {
                        estado = 47;
                        stringBuffer.append((char) caractere);
                    } else {
                        estado = getProximoEstado();
                    }
                }

                case 49 -> {
                    if (caractere == '"') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        estado = getProximoEstado();
                    }
                }
                default -> throw new RuntimeException("Estado nao esperado!!!");
            }
        } while (!feito);

        return new Token(tipo, valor);
    }

    private int doBuffer() throws IOException {
        if (temBuffer) {
            temBuffer = false;
            return buffer;
        }

        return reader.read();
    }

    private void paraBuffer(int ch) {
        if (temBuffer) {
            throw new RuntimeException("Buffer cheio!!!");
        }

        buffer = ch;
        temBuffer = true;
    }

    private int getProximoEstado() throws LexerException {
        return switch (estado) {
            case 0 -> 35;
            case 35 -> 38;
            case 38 -> 41;
            case 41 -> 46;
            default -> throw new LexerException("Erro Lexico: Impossível reconhecer token ");
        };
    }

}
