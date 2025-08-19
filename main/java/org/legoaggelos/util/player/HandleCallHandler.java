package org.legoaggelos.util.player;

public class HandleCallHandler {
    private long lastHandleCall = -1;
    public long getTimeSinceLastHandleCall() {
        if (lastHandleCall < 0) {
            return -1;
        }
        return System.currentTimeMillis() - lastHandleCall;
    }

    public long getLastHandleCall() {
        return lastHandleCall;
    }

    public void setLastHandleCall(long lastHandleCall) {
        this.lastHandleCall = lastHandleCall;
    }
    public boolean shouldTick(short targetFPS) {
        if (lastHandleCall == -1) {
            return true;
        }
        return getTimeSinceLastHandleCall() > 1000/targetFPS && getLastHandleCall() >= 0;
    }
}
