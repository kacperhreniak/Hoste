package pl.menyynem.compiler.base;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;

import pl.menyynem.annotations.Parameter;

public class BundleUtils {

    public static CodeBlock createPutCode(VariableElement variableElement) {
        return CodeBlock.builder()
                .add("$N($S, $N)", getMethodName(variableElement),
                        getKeyValue(variableElement), variableElement.getSimpleName().toString())
                .build();
    }

    private static String getMethodName(VariableElement variableElement) {
        String classPath = variableElement.getEnclosingElement().asType().toString();
        if (classPath.equals(String.class.getName())) {
            return "putString";
        } else if (classPath.equals(int.class.getName())) {
            return "putInt";
        } else if (classPath.equals(boolean.class.getName())) {
            return "putBoolean";
        } else {
            return "putSerializable";
        }
    }

    private static String getKeyValue(VariableElement variableElement) {
        return variableElement.getAnnotation(Parameter.class).key();
    }
}
