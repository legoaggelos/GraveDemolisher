package org.legoaggelos.time;

public class NanoTime {
    long nanoTime;
    public NanoTime(long initialTime){
        nanoTime=initialTime;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public void setNanoTime(long nanoTime) {
        this.nanoTime = nanoTime;
    }
}
