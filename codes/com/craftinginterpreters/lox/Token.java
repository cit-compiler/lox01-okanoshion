package com.craftinginterpreters.lox;

class Token {
  final TokenType type;
  final String lexeme;
  final Object literal;
  final int line;

  // �R���X�g���N�^
  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  // �g�[�N���𕶎���Ƃ��ĕ\�����邽�߂̃��\�b�h
  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
