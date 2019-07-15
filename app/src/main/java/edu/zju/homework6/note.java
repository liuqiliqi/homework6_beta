package edu.zju.homework6;

import android.renderscript.RenderScript;

import java.util.Date;

public class note {
    public static class Note {

        public final long id;
        private Date date;
        private State state;
        private String content;
        private int priority;

        public Note(long id) {
            this.id = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }
}
