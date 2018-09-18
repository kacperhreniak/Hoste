package pl.menyynem.compiler.base;

public class NameUtils {

    private static final String SUFFIX_ANALYTICS_REPORTER = "$AnalyticsReporter";

    public static String getAnalyticsReporter(String className) {
        return className.substring(0, 1).toUpperCase() + className.substring(1)
                + SUFFIX_ANALYTICS_REPORTER;
    }
}
