package swapper.intentions.tropical.event;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;


/**
 * Makes google event bus print exceptions in console!
 */
public class TropicalEventBus extends com.google.common.eventbus.EventBus implements SubscriberExceptionHandler {

    public TropicalEventBus() {
    }

    public TropicalEventBus(String identifier) {
        super(identifier);
    }

    @Override
    public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
        if (throwable instanceof RuntimeException) {
            throwable.printStackTrace();
            throw (RuntimeException) throwable;
        }
    }
}