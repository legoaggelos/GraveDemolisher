package org.legoaggelos.time;


import java.time.LocalTime;

public class TimerTime {
    LocalTime time;
    public TimerTime(LocalTime initialTime){
        time=initialTime;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
    public String toString(){
        String minutes;
        if(String.valueOf((time.getHour()*60)+time.getMinute()).length()==1){
            minutes="0"+ ((time.getHour() * 60) + time.getMinute());
        } else{
            minutes=String.valueOf((time.getHour()*60)+time.getMinute());
        }
        String seconds;
        if(String.valueOf(time.getSecond()).length()==1){
            seconds="0"+ time.getSecond();
        } else{
            seconds= String.valueOf((time.getSecond()));
        }
        return minutes+":"+seconds;
    }
    public byte getSeconds(){
        return Byte.parseByte(this.toString().split(":")[1]);
    }
    public short getMinutes(){
        return Short.parseShort(this.toString().split(":")[0]);
    }
    public int getInSeconds(){
        return getSeconds()+getMinutes()*60;
    }

}
