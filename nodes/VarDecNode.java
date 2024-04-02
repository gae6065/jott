package nodes;

import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

import exceptions.SemanticErrorException;
import exceptions.SyntaxErrorException;

public class VarDecNode implements JottTree{
    private TypeNode type;
    private IdNode id;

    /**
     *  Grammar: <Var_Dec> -> < type > < id>;
     * 
     * @param type  child node that is a type
     * @param id child node that is an id
     */
    public VarDecNode(TypeNode type, IdNode id){
        this.type = type;
        this.id = id;
    }

    /**
     * parses a Variable Declaration Node given the list of remaining tokens
     * 
     * @param tokens                    arraylist of tokens
     * @return                          Defined Variable Declaratio Node
     * @throws SyntaxErrorException     one of Child Nodes was incorrect
     */
    public static VarDecNode parseVarDecNode(ArrayList<Token> tokens) throws SyntaxErrorException {
        if(tokens.isEmpty()) {
            throw new SyntaxErrorException("Unexpected EOF", JottParser.lastToken);
        }

        TypeNode typeNode = TypeNode.parseTypeNode(tokens);
        IdNode idNode = IdNode.parseId(tokens);

        
        
        Token semiColon = tokens.remove(0);
        if(semiColon.getTokenType()==TokenType.SEMICOLON){
            return new VarDecNode(typeNode, idNode);        
        } else {
            throw new SyntaxErrorException("Expected SemiColon", semiColon );
        }
    }

    @Override
    public String convertToJott() {
        return type.convertToJott() + " " + id.convertToJott() + ";";
    }

    @Override
    public String convertToJava(String className) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToJava'");
    }

    @Override
    public String convertToC() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToC'");
    }

    @Override
    public String convertToPython() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToPython'");
    }

    @Override
    public boolean validateTree() throws SemanticErrorException {
        if(JottParser.symTable.varSymTab.containsKey(id.toString())){
            throw new SemanticErrorException("Variable already declared", id.getToken());
        }
        
        JottParser.symTable.varSymTab.put(id.toString(), type.toString());
        return true;
    }
    
    
}
