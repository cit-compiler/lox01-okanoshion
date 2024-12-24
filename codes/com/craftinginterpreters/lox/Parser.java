package com.craftinginterpreters.lox;

import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Expression parsing starts here
    Expr expression() {
        return equality(); // The top-level expression starts from equality
    }

    private Expr equality() {
        Expr expr = comparison(); // Start by parsing comparison expressions

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right); // Combine with binary operator
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term(); // Comparison expressions start with term parsing

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right); // Combine with binary operator
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor(); // Term expressions start with factor parsing

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right); // Combine with binary operator
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary(); // Factor expressions start with unary parsing

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right); // Combine with binary operator
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary(); // Recursively apply unary operator
            return new Expr.Unary(operator, right);
        }

        return primary(); // If no unary operator, process primary expression
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false); // False literal
        if (match(TRUE)) return new Expr.Literal(true);   // True literal
        if (match(NIL)) return new Expr.Literal(null);    // Nil literal

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);  // Number or string literal
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression(); // Parentheses, so parse inside expression
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr); // Group the inner expression
        }

        throw error(peek(), "Expect expression."); // Handle error if no expression is found
    }

    // Utility methods

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true; // If one of the types match, advance the token
            }
        }
        return false; // No match
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance(); // If the token matches, consume it

        throw error(peek(), message); // If not, throw error
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false; // Check if end of token list is reached
        return peek().type == type;  // Check if the current token matches the type
    }

    private Token advance() {
        if (!isAtEnd()) current++; // Move to the next token if not at the end
        return previous(); // Return the previous token after advancing
    }

    private boolean isAtEnd() {
        return peek().type == EOF; // Check if we have reached the end of input
    }

    private Token peek() {
        return tokens.get(current); // Get the current token
    }

    private Token previous() {
        return tokens.get(current - 1); // Get the previous token
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message); // Log the error
        return new ParseError(); // Return a new ParseError
    }

    private void synchronize() {
        advance(); // Move past the current token

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return; // Stop at semicolon

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return; // Stop at keywords that might mark new statements
            }

            advance(); // Keep advancing otherwise
        }
    }

    // Parsing entry point
    Expr parse() {
        try {
            return expression(); // Start parsing from the top-level expression
        } catch (ParseError error) {
            return null; // If there's an error, return null
        }
    }

    // Nested ParseError class
    private static class ParserError extends RuntimeException {}
}
