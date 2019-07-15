package edu.zju.homework6;
public enum State {
    TODO(0), DONE(1);

    public final int intValue;

    State(int i) {
        this.intValue = i;
    }

    public static State from(int intValue) {
        for (State state : State.values()) {
            if (state.intValue == intValue) {
                return state;
            }
        }
        return TODO; // default
    }
}
