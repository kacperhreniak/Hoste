# Hoste
Android library to generate analytics reporter
```java
public class DefaultEventSender implements IEventSender {

    @Override
    public void logEvent(String category, String action, Bundle parameters) {
        // log event to server or 3rd platform
    }

    @Override
    public void logEvent(String eventName, Bundle parameters) {
        // log event to server or 3rd platform
    }
}
```

```java
@Reporter(name = "example_view")
public interface ExampleAnalyticsReporter {

    @Event(name = "show")
    void sendShowEvent();

    @Event(name = "logged_user")
    void sendLoggedUserEvent(@Parameter(key = "is_vip") boolean isVipUser);

}
```

```java
public ExampleAnalyticsReporter provideExampleAnalyticsReporter(IEventSender eventSender) {
        return new HosteExampleAnalyticsReporter(eventSender);
}
```

```gradle
    implementation 'com.github.menyyy.Hoste:compiler:0.0.3'
    implementation 'com.github.menyyy.Hoste:android-heart:0.0.3'
    annotationProcessor 'com.github.menyyy.Hoste:annotations:0.0.3'
   ```
   
 ```gradle
   allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```
