package pl.menyynem.compiler.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import pl.menyynem.annotations.Reporter;
import pl.menyynem.compiler.base.ClassNameUtils;
import pl.menyynem.compiler.base.NameUtils;

public class GenerateAnalyticsReporter {
    private static final String CATEGORY_FIELD_NAME = "CATEGORY";
    private static final String EVENT_SENDER_FIELD_NAME = "eventSender";

    public static TypeSpec generate(TypeElement element) {
        String name = element.getAnnotation(Reporter.class).name();
        FieldSpec fieldSpec = getFieldCategory(name);

        TypeSpec.Builder builder = TypeSpec.classBuilder(getName(name))
                .superclass(ClassNameUtils.getAnalyticsReporterBase())
                .addSuperinterface(ClassName.get(element))
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldSpec)
                .addMethod(constructorMethod(fieldSpec.name));

        List<ExecutableElement> executableElementList = findEventMethods(element);

        for (ExecutableElement executableElement : executableElementList) {
            builder.addMethod(GenerateEventMethod.generate(executableElement));
        }

        return builder.build();
    }

    private static List<ExecutableElement> findEventMethods(TypeElement element) {
        List<? extends Element> elements = element.getEnclosedElements();
        List<ExecutableElement> variableElements = new ArrayList<>();

        for (Element enclosedElement : elements) {
            if (enclosedElement.getKind() == ElementKind.METHOD
                    && enclosedElement instanceof ExecutableElement) {
                variableElements.add((ExecutableElement) enclosedElement);
            }
        }
        return variableElements;
    }

    private static String getName(String name) {
        return NameUtils.getAnalyticsReporter(name);
    }

    private static FieldSpec getFieldCategory(String categoryName) {
        return FieldSpec.builder(String.class, CATEGORY_FIELD_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", categoryName)
                .build();
    }

    private static MethodSpec constructorMethod(String categoryFieldName) {
        ParameterSpec parameterSpec = getEventSenderParameter();
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec)
                .addStatement("super($N, $N)", parameterSpec.name, categoryFieldName)
                .build();
    }

    private static ParameterSpec getEventSenderParameter() {
        return ParameterSpec.builder(ClassNameUtils.getEventSender(), EVENT_SENDER_FIELD_NAME)
                .build();
    }
}
