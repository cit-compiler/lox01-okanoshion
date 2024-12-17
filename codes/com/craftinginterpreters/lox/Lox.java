package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  // �t�@�C����ǂݍ���Ŏ��s���郁�\�b�h
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // �G���[������΁A��[���ŏI��
    if (hadError) System.exit(65);
  }

  // �Θb�^�v�����v�g�����s���郁�\�b�h
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break; // ���[�U�[��Ctrl-D�ŏI��
      run(line);
      hadError = false; // �V�������͂ŃG���[�t���O�����Z�b�g
    }
  }

  // �\�[�X�R�[�h�����s���郁�\�b�h
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // �g�[�N�����o��
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  // �G���[�n���h�����O���\�b�h
  static void error(int line, String message) {
    report(line, "", message);
  }

  // �G���[���|�[�g�̕\�����\�b�h
  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }
}
