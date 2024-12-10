package com.craftinginterpreters.lox;

class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;

  // コンストラクタ
  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  // トークンを文字列として表示するためのメソッド
  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
