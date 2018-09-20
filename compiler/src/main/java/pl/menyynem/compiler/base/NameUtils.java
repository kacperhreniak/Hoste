package pl.menyynem.compiler.base;

public class NameUtils {

    private static final String PREFIX_ANALYTICS_REPORTER = "Hoste";

    public static String getAnalyticsReporter(String className) {
        return PREFIX_ANALYTICS_REPORTER + className;
    }
}
