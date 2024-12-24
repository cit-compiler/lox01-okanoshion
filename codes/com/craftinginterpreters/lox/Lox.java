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

  // ファイルを読み込んで実行するメソッド
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // エラーがあれば、非ゼロで終了
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  // 対話型プロンプトを実行するメソッド
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break; // ユーザーがCtrl-Dで終了
      run(line);
      hadError = false; // 新しい入力でエラーフラグをリセット
    }
  }

  // ソースコードを実行するメソッド
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();

    // Stop if there was a syntax error.
    if (hadError) return;
    interpreter.interpret(expression);

    System.out.println(new AstPrinter().print(expression));

    // トークンを出力
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  // エラーハンドリングメソッド
  static void error(int line, String message) {
    report(line, "", message);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

  // エラーレポートの表示メソッド
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
