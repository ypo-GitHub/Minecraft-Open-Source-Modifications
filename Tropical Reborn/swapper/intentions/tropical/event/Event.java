package swapper.intentions.tropical.event;

public abstract class Event {
    private boolean cancelled;

    @SuppressWarnings("all")
    public boolean isCancelled() {
        return this.cancelled;
    }

    @SuppressWarnings("all")
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
