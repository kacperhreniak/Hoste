package pl.menyynem.compiler.base;

import com.squareup.javapoet.ClassName;

public class ClassNameUtils {

    public static ClassName getEventSender() {
        return ClassName.get("pl.menyynem.hoste", "IEventSender");
    }

    public static ClassName getAnalyticsReporterBase() {
        return ClassName.get("pl.menyynem.hoste", "AnalyticsReporter");
    }

    public static ClassName getBundle() {
        return ClassName.get("android.os", "Bundle");
    }
}
