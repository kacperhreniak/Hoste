package pl.menyynem.hoste;

import android.os.Bundle;

public abstract class AnalyticsReporter {
    private final IEventSender eventSender;
    private final String categoryName;

    public AnalyticsReporter(IEventSender eventSender, String categoryName) {
        this.eventSender = eventSender;
        this.categoryName = categoryName;
    }


    protected void logEvent(String action) {
        logEvent(action, null);
    }

    protected void logEvent(String action, Bundle bundle) {
        eventSender.logEvent(categoryName, action, bundle);
    }

    protected void logSingleEvent(String eventName, Bundle bundle) {
        eventSender.logEvent(eventName, bundle);
    }
}
