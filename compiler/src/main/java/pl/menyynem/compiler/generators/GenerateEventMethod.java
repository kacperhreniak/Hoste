package pl.menyynem.compiler.generators;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import pl.menyynem.annotations.Event;
import pl.menyynem.compiler.base.BundleUtils;
import pl.menyynem.compiler.base.ClassNameUtils;

public class GenerateEventMethod {

    private static final String BUNDLE_PARAMETERS_FIELD_NAME = "bundle";

    public static MethodSpec generate(ExecutableElement executableElement) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameters(convertParamteres(executableElement));

        builder.addCode(generateStatement(executableElement));

        return builder.build();
    }

    private static List<ParameterSpec> convertParamteres(ExecutableElement executableElement) {
        List<ParameterSpec> parameterSpecs = new ArrayList<>();
        for (VariableElement variableElement : executableElement.getParameters()) {
            parameterSpecs.add(ParameterSpec.get(variableElement));
        }
        return parameterSpecs;
    }

    private static CodeBlock generateStatement(ExecutableElement executableElement) {
        if (executableElement.getParameters().size() > 0) {
            return generateParametersStatement(executableElement);
        } else {
            return CodeBlock.builder()
                    .addStatement("logEvent($S)", getEventName(executableElement))
                    .build();
        }
    }

    private static CodeBlock generateParametersStatement(ExecutableElement executableElement) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .add(getBundleInitialization());

        for (VariableElement variableElement : executableElement.getParameters()) {
            codeBlockBuilder.addStatement("$N.$N", BUNDLE_PARAMETERS_FIELD_NAME, BundleUtils.createPutCode(variableElement).toString());
        }

        return codeBlockBuilder
                .addStatement("logEvent($S, $N)", getEventName(executableElement), BUNDLE_PARAMETERS_FIELD_NAME)
                .build();
    }

    private static CodeBlock getBundleInitialization() {
        TypeName bundleClassName = ClassNameUtils.getBundle();
        return CodeBlock.builder()
                .addStatement("$T $N = new $T()", bundleClassName, BUNDLE_PARAMETERS_FIELD_NAME, bundleClassName)
                .build();
    }

    private static String getEventName(ExecutableElement executableElement) {
        return executableElement.getAnnotation(Event.class).name();
    }
}
