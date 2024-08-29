package org.legoaggelos.util;

public class Counter {
    long counter;
    long defaultValue;
    public Counter(){
        counter=0;
        defaultValue=0;
    }
    public Counter(long defaultValue){
        counter=defaultValue;
        this.defaultValue=defaultValue;
    }
    public void increaseCounter(){
        if(counter>2160000){
            counter=defaultValue;
        } else{
            counter++;
        }
    }
    public long getCounter(){
        return counter;
    }
    public void resetCounter(){
        counter=defaultValue;
    }
    public void setCounter(long newValue){
        counter=newValue;
    }
}
