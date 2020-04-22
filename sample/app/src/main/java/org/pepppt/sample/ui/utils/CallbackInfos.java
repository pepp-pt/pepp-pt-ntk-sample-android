package org.pepppt.sample.ui.utils;

import org.pepppt.core.events.CoreEvent;
import org.pepppt.core.events.EventArgs;

public class CallbackInfos {
    public CoreEvent getEvent() {
        return event;
    }

    public void setEvent(CoreEvent event) {
        this.event = event;
    }

    private CoreEvent event;

    public EventArgs getArgs() {
        return args;
    }

    public void setArgs(EventArgs args) {
        this.args = args;
    }

    private EventArgs args;

    public CallbackInfos(CoreEvent event, EventArgs args) {
        this.event = event;
        this.args = args;
    }
}
