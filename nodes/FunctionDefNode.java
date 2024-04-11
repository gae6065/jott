package nodes;

import provided.JottTree;
import provided.JottParser;
import provided.Token;
import provided.TokenType;

import exceptions.*;

import java.util.ArrayList;

public class FunctionDefNode implements JottTree {
    private IdNode idNode;
    private FunctionDefParamsNode functionDefParamsNode;
    private FunctionReturnNode functionReturnNode;
    private FBodyNode fBodyNode;

    // Constructor
    public FunctionDefNode(IdNode idNode, FunctionDefParamsNode functionDefParamsNode,
            FunctionReturnNode functionReturnNode, FBodyNode fBodyNode) {
        this.idNode = idNode;
        this.functionDefParamsNode = functionDefParamsNode;
        this.functionReturnNode = functionReturnNode;
        this.fBodyNode = fBodyNode;
    }

    public static FunctionDefNode parseFunctionDefNode(ArrayList<Token> tokens) throws SyntaxErrorException {
        if (tokens.isEmpty()) {
            throw new SyntaxErrorException("Unexpected EOF", null);
        }

        // Look for Def
        Token defToken = tokens.remove(0);
        if (!defToken.getToken().equals("Def")) {
            throw new SyntaxErrorException("Expected Def but got: " + defToken.getToken(), defToken);
        }

        // Look for <id>
        IdNode idNode = IdNode.parseId(tokens);

        String idString = idNode.toString();

        // Look for [
        Token lbToken = tokens.remove(0);
        if (lbToken.getTokenType() != TokenType.L_BRACKET) {
            throw new SyntaxErrorException("Expected left square bracket but got " + lbToken.getToken(), lbToken);
        }

        FunctionDefParamsNode functionDefParamsNode = FunctionDefParamsNode.parseFunctionDefParamsNode(tokens,
                idString);

        // Look for ]
        Token rbToken = tokens.remove(0);
        if (rbToken.getTokenType() != TokenType.R_BRACKET) {
            throw new SyntaxErrorException("Expected right square bracket but got " + rbToken.getToken(), rbToken);
        }

        // Look for :
        Token colonToken = tokens.remove(0);
        if (colonToken.getTokenType() != TokenType.COLON) {
            throw new SyntaxErrorException("Expected colon but got " + colonToken.getToken(), colonToken);
        }

        FunctionReturnNode functionReturnNode = FunctionReturnNode.parseFunctionReturnNode(tokens, idString);

        // Look for {
        Token lbraceToken = tokens.remove(0);
        if (lbraceToken.getTokenType() != TokenType.L_BRACE) {
            throw new SyntaxErrorException("Expected left brace but got " + lbraceToken.getToken(), lbraceToken);
        }

        FBodyNode fBodyNode = FBodyNode.parseFBodyNode(tokens, idString);

        // Look for }
        Token rbraceToken = tokens.remove(0);
        if (rbraceToken.getTokenType() != TokenType.R_BRACE) {
            throw new SyntaxErrorException("Expected right brace but got " + rbraceToken.getToken(), rbraceToken);
        }

        return new FunctionDefNode(idNode, functionDefParamsNode, functionReturnNode, fBodyNode);
    }

    @Override
    public String convertToJott() {
        return "Def " + idNode.convertToJott() + "[" + functionDefParamsNode.convertToJott() + "]:"
                + functionReturnNode.convertToJott() + "{" + fBodyNode.convertToJott() + "}";
    }

    @Override
    public String convertToJava(String className) {
        String j_return;

        switch (this.functionReturnNode.getReturnType()) {
            case "Integer":
                j_return = "int";
                break;
            case "Double":
                j_return = "double";
                break;
            case "Boolean":
                j_return = "boolean";
                break;
            case "String":
                j_return = "String";
                break;
            case "Void":
                j_return = "void";
                break;
            default:
                System.out.println("Return Type not properly defined");
                return "";
        }
        String java = "public static " + j_return + " " + this.idNode.toString() + "("
                + this.functionDefParamsNode.convertToJava(className);
        java += ") { " + this.fBodyNode.convertToJava(className) + "}";
        return java;

    }

    @Override
    public String convertToC() {
        String c_code;
        if (this.idNode.convertToC("main")) {
            c_code = "int main(void";
        } else {
            String c_return;

            switch (this.functionReturnNode.getReturnType()) {
                case "Integer":
                    c_return = "int";
                    break;
                case "Double":
                    c_return = "double";
                    break;
                case "Boolean":
                    c_return = "bool";
                    break;
                case "String":
                    c_return = "char";
                    break;
                case "Void":
                    c_return = "void";
                    break;
                default:
                    System.out.println("Return Type not properly defined");
                    return "";
            }
            c_code = c_return + " " + this.idNode.convertToC() + "(";
        }
        // Deal with params later
        return c_code;
    }

    @Override
    public String convertToPython() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToPython'");
    }

    @Override
    public boolean validateTree() throws SemanticErrorException {

        // If the func id already exists, throw a semantic error

        if (JottParser.symTable.funcSymTab.containsKey(idNode.toString())) {
            throw new SemanticErrorException("Duplicate function " + idNode.toString(), idNode.getToken());
        }

        JottParser.symTable.funcSymTab.put(idNode.toString(), new ArrayList<>());

        functionDefParamsNode.validateTree();

        functionReturnNode.validateTree();

        if (idNode.toString().equals("main") && !functionReturnNode.getReturnType().equals("Void")) {
            throw new SemanticErrorException("Main must be of return Void", idNode.getToken());
        }

        fBodyNode.validateTree();

        return true;
    }

}
