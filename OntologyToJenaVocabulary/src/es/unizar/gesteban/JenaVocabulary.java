package es.unizar.gesteban;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class JenaVocabulary {

    public static String STRING_IMPORTS = "import com.hp.hpl.jena.rdf.model.Property;\n"
            + "import com.hp.hpl.jena.rdf.model.Resource;\nimport com.hp.hpl.jena.rdf.model.ResourceFactory;\n\n";
    public static String STRING_METHODS = "  protected static final Resource resource(String local) {\n"
            + "    return ResourceFactory.createResource(uri + local);\n}\n\n"
            + "  protected static final Property property(String local) {\n"
            + "    return ResourceFactory.createProperty(uri, local);\n  }\n\n";

    public final String namespace, prefix;
    public Set<String> classNameSet = new HashSet<String>();
    public Set<String> propertyNameSet = new HashSet<String>();

    public JenaVocabulary(String namespace) {
        this.namespace = namespace;
        // TODO auto-generate a prefix
        this.prefix = "default";
    }

    public JenaVocabulary(String namespace, String prefix) {
        this.namespace = namespace;
        this.prefix = prefix;
    }

    public void addClass(String localName) {
        classNameSet.add(localName);
    }

    public void addProperty(String localName) {
        propertyNameSet.add(localName);
    }

    public void write(String filename, String packagePath) throws IOException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");

        // write package
        writer.printf("package %s;\n\n", packagePath);
        // write imports
        writer.printf(STRING_IMPORTS);
        // write class head
        writer.printf("public class %s {\n\n", prefix.toUpperCase());
        // write uri-related code
        writer.printf("  protected static final String uri = \"%s\";\n\n", namespace);
        writer.printf("  public static String getURI() {\n    return uri;\n  }\n\n");
        // write methods
        writer.printf(STRING_METHODS);
        // write classes
        for (String aClass : classNameSet)
            writer.printf("  public static final Resource %s = resource(\"%s\");\n", aClass, aClass);
        writer.printf("\n");
        // write properties
        for (String aProperty : propertyNameSet)
            writer.printf("  public static final Property %s = property(\"%s\");\n", aProperty, aProperty);
        writer.printf("\n}");
        writer.close();
    }

}
