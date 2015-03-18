package org.chrisv.gwtaptoracle.processors;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;

@SupportedOptions("module")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RootProcessor extends AbstractProcessor {
    private Messager messager;
    private Map<String, Class<?>> generators;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();

        // TODO: support multiple modules, comma-separated
        resolveModules();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return generators.keySet();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        messager.printMessage(Kind.NOTE, "Process call");

        return true;
    }

    private void resolveModules() {
        generators = new HashMap<>();

        String module = processingEnv.getOptions().get("module");

        if (!Strings.isNullOrEmpty(module)) {
            URL resource = getClass().getResource("/" + module.replace(".", "/") + ".gwt.xml");

            if (resource == null) {
                messager.printMessage(Kind.ERROR, "Unable to find module '" + module + "' in classpath.");
            } else {
                try {
                    resolveModules(resource);
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    messager.printMessage(Kind.ERROR, "An error happened while parsing '" + module + "'.");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void resolveModules(URL resource)
            throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(resource.openStream());

        NodeList generatorRules = document.getElementsByTagName("generate-with");

        for (int i = 0; i < generatorRules.getLength(); i++) {
            Node generatorNode = generatorRules.item(i);
            Node generatorClassNode = generatorNode.getAttributes().getNamedItem("class");

            if (generatorClassNode != null) {
                NodeList ruleNodes = generatorNode.getChildNodes();
                for (int j = 0; j < ruleNodes.getLength(); j++) {
                    Node ruleNode = ruleNodes.item(j);
                    if ("when-annotation-is".equals(ruleNode.getNodeName())) {
                        Node annotationClassNode = ruleNode.getAttributes().getNamedItem("class");

                        if (annotationClassNode != null) {
                            // TODO: Class doesn't exist... we are before compilation
                            Class<?> generatorClass = Class.forName(generatorClassNode.getTextContent());
                            Class<? extends Annotation> annotationClass =
                                    Class.forName(annotationClassNode.getTextContent()).asSubclass(Annotation.class);

                            generators.put(annotationClass.getName(), generatorClass);
                        }
                    }
                }
            }
        }
    }
}
