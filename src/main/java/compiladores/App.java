package compiladores;

import org.antlr.v4.runtime.tree.ParseTree;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

// Las diferentes entradas se explicaran oportunamente
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("...inicializando... ");
        // create a CharStream that reads from file
        CharStream input = CharStreams.fromFileName("./input/CodigoPrueba.txt");

        // create a lexer that feeds off of input CharStream
        idLexer lexer = new idLexer(input);
        
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // create a parser that feeds off the tokens buffer
        idParser parser = new idParser(tokens);

      
        // // create Listener
        idBaseListener listener = new MiListener();
        parser.addParseListener(listener);

         System.out.println("\n");
        // Conecto el objeto con Listeners al parser
        
        // Solicito al parser que comience indicando una regla gramatical
        // En este caso la regla es el simbolo inicial
       
        ParseTree tree = parser.prog();
        // Conectamos el visitor
        // Caminante visitor = new Caminante();
        // visitor.visit(tree);
        // System.out.println(visitor);
        // System.out.println(visitor.getErrorNodes());
        // Imprime el arbol obtenido
        System.out.println(tree.toStringTree(parser));
        // System.out.println(escucha);
        
    }
}