package es.unizar.gesteban;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class OntologyToJenaVocabulary {

    public static Map<String, JenaVocabulary> vocSet = new HashMap<String, JenaVocabulary>();
    public static OntModel model;

    /**
     * 
     * @param args
     *            [0]: ontology file [1]: package path
     * @throws Exception
     */
    public static void main(String[] args) {
        if(args.length!=2) {
            System.err.println("Usage: OntologyToJenaVocabulary URL_or_ontology_file package_path");
            System.err.println("       e.g. java -jar OntologyToJenaVocabulary.jar http://xmlns.com/foaf/0.1/ com.mycompany");
            System.exit(0);
        }
        BasicConfigurator.configure(new NullAppender());
        model = ModelFactory.createOntologyModel();
        RDFDataMgr.read(model, args[0]);
        createVocs();
        writeVocs(args[1]);
    }

    public static void createVocs() {

        // Iterating classes
        for (OntClass aClass : model.listNamedClasses().toSet())
            add(aClass);

        // Iterating object properties
        for (ObjectProperty anObjectProperty : model.listObjectProperties().toSet())
            add(anObjectProperty);

        // Iterating data properties
        for (DatatypeProperty aDatatypeProperty : model.listDatatypeProperties().toSet())
            add(aDatatypeProperty);
    }

    public static void add(OntClass entity) {
        JenaVocabulary voc;
        if ((voc = vocSet.get(entity.getNameSpace())) == null) {
            voc = new JenaVocabulary(entity.getNameSpace(), model.getNsURIPrefix(entity.getNameSpace()));
            vocSet.put(entity.getNameSpace(), voc);
        }
        voc.addClass(entity.getLocalName());
    }

    public static void add(Property entity) {
        JenaVocabulary voc;
        if ((voc = vocSet.get(entity.getNameSpace())) == null) {
            voc = new JenaVocabulary(entity.getNameSpace(), model.getNsURIPrefix(entity.getNameSpace()));
            vocSet.put(entity.getNameSpace(), voc);
        }
        voc.addProperty(entity.getLocalName());
    }

    public static void writeVocs(String packagePath) {
        for (JenaVocabulary voc : vocSet.values())
            try {
                // TODO check filename to be valid
                voc.write(voc.prefix.toUpperCase() + ".java", packagePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }
}
