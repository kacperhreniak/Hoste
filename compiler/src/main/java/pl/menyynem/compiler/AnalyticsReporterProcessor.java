package pl.menyynem.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import pl.menyynem.annotations.Event;
import pl.menyynem.annotations.Parameter;
import pl.menyynem.annotations.Reporter;
import pl.menyynem.compiler.base.SaveUtils;
import pl.menyynem.compiler.generators.GenerateAnalyticsReporter;

@AutoService(Processor.class)
public class AnalyticsReporterProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(Reporter.class.getCanonicalName());
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        List<TypeElement> typeElements = findTypesAnnotatedWIthReporter(roundEnvironment.getElementsAnnotatedWith(Reporter.class));

        for (TypeElement typeElement : typeElements) {
            TypeSpec typeSpec = GenerateAnalyticsReporter.generate(typeElement);
            saveTypeSpec(typeSpec, typeElement);
        }
        return true;
    }

    private List<TypeElement> findTypesAnnotatedWIthReporter(Set<? extends Element> annotatedElements) {
        List<TypeElement> typeElements = new ArrayList<>();
        for (Element element : annotatedElements) {
            if (isCorrectnessCreated(element)) {
                typeElements.add((TypeElement) element);
            }
        }
        return typeElements;
    }

    private boolean isCorrectnessCreated(Element element) {
        boolean result = verifyInterfaceElement(element);
        result = result && verifyMethods((TypeElement) element);
        return result;
    }

    private boolean verifyInterfaceElement(Element element) {
        if (element.getKind() != ElementKind.INTERFACE
                || !(element instanceof TypeElement)) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "@" + Reporter.class.getSimpleName() + " is available only for Interface");

            return false;
        }
        return true;
    }

    private boolean verifyMethods(TypeElement typeElement) {
        for (Element element : typeElement.getEnclosedElements()) {
            if (!verifyExecitableElement(element)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "In " + typeElement.getSimpleName() + " only methods are available");
                return false;
            }

            if (!existEventAnnotation((ExecutableElement) element)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "All methods in Reporter required @" + Event.class.getSimpleName());
                return false;
            }

            if (!areParameterCorrected((ExecutableElement) element)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "All Paramteres required @" + Parameter.class.getSimpleName());
                return false;
            }
        }
        return true;
    }

    private boolean verifyExecitableElement(Element element) {
        return element.getKind() == ElementKind.METHOD
                && element instanceof ExecutableElement;
    }

    private boolean existEventAnnotation(ExecutableElement element) {
        return element.getAnnotation(Event.class) != null;
    }

    private boolean areParameterCorrected(ExecutableElement element) {
        for (Element parameterElement : element.getParameters()) {
            if (!isCorrectnessParameter(parameterElement)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectnessParameter(Element element) {
        return element.getKind() == ElementKind.PARAMETER
                && element instanceof VariableElement
                && element.getAnnotation(Parameter.class) != null;
    }

    private void saveTypeSpec(TypeSpec typeSpec, TypeElement typeElement) {
        try {
            SaveUtils.saveClass(((PackageElement) typeElement.getEnclosingElement()).getQualifiedName().toString(),
                    typeSpec, processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Cannot save " + typeElement.getSimpleName().toString());
        }
    }
}
