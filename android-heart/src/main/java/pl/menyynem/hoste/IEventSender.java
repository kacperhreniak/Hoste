package pl.menyynem.hoste;

import android.os.Bundle;

public interface IEventSender {

    void logEvent(String category, String action, Bundle parameters);

    void logEvent(String eventName, Bundle parameters);
}
