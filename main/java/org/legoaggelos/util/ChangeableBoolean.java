package org.legoaggelos.util;

import java.util.Objects;

public class ChangeableBoolean {
    private boolean bool;

    public ChangeableBoolean(boolean bool){
        this.bool=bool;
    }

    public void setBool(boolean bool) {
        if(this.bool!=bool) {
            this.bool = bool;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangeableBoolean that)) return false;
        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bool);
    }
    public boolean bool(){
        return this.bool;
    }
}
