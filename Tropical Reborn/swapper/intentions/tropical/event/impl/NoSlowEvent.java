package swapper.intentions.tropical.event.impl;

import swapper.intentions.tropical.event.Event;

public class NoSlowEvent extends Event {

    private Type type;

    public NoSlowEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        ITEM, KEEPSPRINT, SNEAK, WATER, SOULSAND
    }

}
