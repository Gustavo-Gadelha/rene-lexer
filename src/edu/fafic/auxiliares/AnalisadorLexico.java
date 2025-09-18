package edu.fafic.auxiliares;

import edu.fafic.error.LexerException;

import java.io.BufferedReader;
import java.io.IOException;

public class AnalisadorLexico {

    private final BufferedReader reader;
    private boolean temBuffer;
    private int buffer;
    private int estado;

    public AnalisadorLexico(BufferedReader reader) {
        this.reader = reader;
        this.temBuffer = false;
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
                // Considera '\n' e '\r' como whitespace, causando a leitura incorreta durante a transição de estados
                // Ex: Estado 0 para 28 (que reconhece /, // e /*), desconsidera espaço e quebra de linha entre um / e outro / ou *
                // Ex: Estado 0 para 28 (que reconhece &&), desconsidera espaço e quebra de linha entre um & e outro
                // Ex: Estado 0 para 32 (que reconhece ||), desconsidera espaço e quebra de linha entre um | e outro
                continue;
            } else if (caractere == -1 && tipo == Token.EOF) {
                // Causa a leitura incorreta durante a transição de estados, poís se o próximo caractere for -1, termina prematuramente a leitura
                // Ex: Estado 0 para 28 (que reconhece /, // e /*), não reconhece / como Token DIV se / estiver na última linha (ignora whitespace)
                // Ex: Estado 0 para 28 (que reconhece &&), não levanta erro se & estive na última linha (ignora whitespace)
                // Ex: Estado 0 para 32 (que reconhece ||), não levanta erro se | estive na última linha (ignora whitespace)
                return Token.EOF();
            }

            switch (estado) {
                // Estado inicial
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

                        // Caso o caractere não seja um operador, atribuição ou pontuação, vá para o estado 35
                        default -> {
                            paraBuffer(caractere);
                            // 0 -> 35
                            estado = getProximoEstado();
                        }
                    }
                }

                // TODO: Não deveria reconhecer <> como Not Equals
                // Vem do estado 0, lendo '<'
                // Reconhece <, <= e <>
                case 1 -> {
                    if (caractere == '=') {
                        valor = Token.LE;
                    } else if (caractere == '>') {
                        valor = Token.NE;
                    } else {
                        paraBuffer(caractere);
                    }

                    feito = true;
                }

                // Vem do estado 0, lendo '='
                // Reconhece = e ==
                case 4 -> {
                    if (caractere == '=') {
                        tipo = Token.RELOP;
                        valor = Token.EQ;
                    } else {
                        paraBuffer(caractere);
                    }

                    feito = true;
                }

                // Vem do estado 0, lendo '!'
                // Reconhece ! e !=
                case 7 -> {
                    if (caractere == '=') {
                        tipo = Token.RELOP;
                        valor = Token.NE;
                    } else {
                        paraBuffer(caractere);
                    }

                    feito = true;
                }

                // Vem do estado 0, lendo '>'
                // Reconhece > e >=
                case 10 -> {
                    if (caractere == '=') {
                        valor = Token.GE;
                    } else {
                        paraBuffer(caractere);
                    }

                    feito = true;
                }

                // TODO: Deveria reconhecer o espaçamento e quebra de linha entre um operador e outro
                // TODO: Não deveria reconhecer o segundo caractere após /** como um Token DIV
                // Vem do estado 0, lendo '/'
                // Reconhece DIV, comentários de linhas e comentários de bloco
                // Possui loops internos para consumir caracteres sem produzir tokens de comentários
                case 24 -> {
                    if (caractere == '/') {
                        while (caractere != '\n' && caractere != -1) {
                            caractere = doBuffer();
                        }

                        estado = 0;
                    } else if (caractere == '*') {
                        // Reconheceu *

                        do {
                            caractere = doBuffer();
                        } while (caractere != '*' && caractere != -1);
                        // Reconheceu * ... *

                        caractere = doBuffer();

                        if (caractere == '/') {
                            // Reconheceu * ... */
                            estado = 0;
                        } else {
                            // Se o proximo caractere for um *, ignora tudo até achar outro * ou EOF
                            // Se não, ignora o caractere atual e reconhece o proximo como DIV
                            // Ex: /* ... * a, /* ... **a, "/* ... *  "
                            estado = 24;
                        }
                    } else {
                        tipo = Token.OP;
                        valor = Token.DIV;
                        // Reconhece o segundo caractere após o ultimo astérico /* ... * como DIV (ignorando whitespace)
                        // Falsamente mostra o caractere como / no token e retorna o caractere para o buffer
                        // Ex: /* a comment *
                        paraBuffer(caractere);
                        feito = true;
                    }
                }

                // TODO: Deveria reconhecer o espaçamento e quebra de linha entre um operador e outro
                // Vem do estado 0, lendo '&'
                case 28 -> {
                    if (caractere == '&') {
                        tipo = Token.LOG;
                        valor = Token.AND;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // TODO: Não existe nenhum transição para este estado, provavelmente foi integrado ao estado 24
                // Reconhece o final do comentário em bloco */, mas não levanta erro se não encontrar
                case 29 -> {
                    if (caractere == '*' && doBuffer() == '/') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        estado = 29;
                        stringBuffer.append((char) caractere);
                    }
                }

                // TODO: Deveria reconhecer o espaçamento e quebra de linha entre um operador e outro
                // Vem do estado 0, lendo '|'
                case 32 -> {
                    if (caractere == '|') {
                        tipo = Token.LOG;
                        valor = Token.OR;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // Se reconhecer letra ou underline, vá para estado 36
                // Se não, vá para estado 38
                case 35 -> {
                    if (Simbolos.isLetra(caractere) || caractere == '_') {
                        estado = 36;
                        tipo = Token.ID;
                        stringBuffer = new StringBuffer();
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        // 35 -> 38
                        estado = getProximoEstado();
                    }
                }

                // IDENTIFICADOR ou PALAVRA_RESERVADA
                // Consome (avança o buffer sem alterar o estado) letras, dígitos e underline
                // Termina quando encontrar qualquer outro caractere
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

                // Se reconhecer digito, vá para o estado 39
                // Se não, vá para estado 41
                case 38 -> {
                    if (Character.isDigit(caractere)) {
                        estado = 39;
                        tipo = Token.LITERALNUMERICO;
                        stringBuffer = new StringBuffer();
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        // 38 -> 41
                        estado = getProximoEstado();
                    }
                }

                // LITERAL_INTEIRO
                // Consome (avança o buffer sem alterar o estado) dígitos
                // Termina quando encontrar qualquer outro caractere
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

                // Se reconhecer aspás simples, vá para o estado 42
                // Se não, vá para estado 46
                case 41 -> {
                    if (caractere == '\'') {
                        estado = 42;
                        tipo = Token.LITERALCHAR;
                        stringBuffer = new StringBuffer();
                    } else {
                        paraBuffer(caractere);
                        // 41 -> 46
                        estado = getProximoEstado();
                    }
                }

                // Inicio do LITERAL_CHAR
                // Se reconhecer letra ou digito, vá para o estado 44
                // Se reconhecer escape (\), vá para o estado 43
                // Se não, levanta erro
                case 42 -> {
                    if (Simbolos.isLetraOuDigito(caractere)) {
                        estado = 44;
                        stringBuffer.append((char) caractere);
                    } else if (caractere == '\\') {
                        estado = 43;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // Só reconhece '\r', '\n', '\t' ou '\\'
                // Então se não encontrar 'r', 'n', 't' ou '\', levanta erro'
                case 43 -> {
                    if (caractere == 'r' || caractere == 'n' || caractere == 't' || caractere == '\\') {
                        estado = 44;
                        stringBuffer.append((char) caractere);
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // Fim do LITERAL_CHAR
                // Se não reconhecer aspas simples, levanta erro
                case 44 -> {
                    if (caractere == '\'') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // Inicio do LITERAL_STRING
                // Se reconhecer aspas duplas, vá para o estado 47
                // Se não, levanta erro
                case 46 -> {
                    if (caractere == '"') {
                        estado = 47;
                        tipo = Token.LITERALSTRING;
                        stringBuffer = new StringBuffer();
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // TODO: Não reconhece pontuação, operadores, ou aspas simples dentro da string
                // Consome (avança o buffer sem alterar o estado) letra, digito ou espaço em branco
                // Se reconhecer escape (\), vá para o estado 48
                // Se não, vá para o estado 49
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

                // Só reconhece '\r', '\n', '\t' ou '\\'
                // Se reconhecer 'r', 'n', 't' ou '\', volte para o estado 47
                // Se não, levanta erro
                case 48 -> {
                    if (caractere == '\\' || caractere == 'r' || caractere == 't' || caractere == 'n') {
                        estado = 47;
                        stringBuffer.append((char) caractere);
                    } else {
                        // Não existe transição para este estado, então levanta erro
                        estado = getProximoEstado();
                    }
                }

                // Fim do LITERAL_STRING
                // Se não reconhecer aspas duplas, levanta erro
                case 49 -> {
                    if (caractere == '"') {
                        valor = stringBuffer;
                        feito = true;
                    } else {
                        paraBuffer(caractere);
                        // Não existe transição para este estado, então levanta erro
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

    // TODO: Esse método não é necessário e pode ser substituído pela atribuição direta do estado
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
