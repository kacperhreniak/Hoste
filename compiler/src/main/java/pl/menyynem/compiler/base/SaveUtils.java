package pl.menyynem.compiler.base;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;

public class SaveUtils {

    public static void saveClass(String packageName, TypeSpec typeSpec, Filer filer) throws IOException {
        JavaFile.builder(packageName, typeSpec).build()
                .writeTo(filer);
    }
}
