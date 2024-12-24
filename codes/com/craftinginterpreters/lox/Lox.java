package com.craftinginterpreters.lox;

import java.util.List;
import com.craftinginterpreters.lox.Interpreter;
import com.craftinginterpreters.lox.RuntimeError;
import com.craftinginterpreters.lox.Token;
import com.craftinginterpreters.lox.Scanner;
import com.craftinginterpreters.lox.Parser;
import com.craftinginterpreters.lox.Expr;
import com.craftinginterpreters.lox.AstPrinter;
import com.craftinginterpreters.lox.TokenType;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

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
    if (hadRuntimeError) System.exit(70);
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

    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();

    // Stop if there was a syntax error.
    if (hadError) return;
    interpreter.interpret(expression);

    System.out.println(new AstPrinter().print(expression));

    // �g�[�N�����o��
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  // �G���[�n���h�����O���\�b�h
  static void error(int line, String message) {
    report(line, "", message);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
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
