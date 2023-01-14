package com.example.application.webservices.openfoodfacts.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Images implements Serializable {

    @SerializedName("front")
    private Front front;

    public Front getFront() {
        return front;
    }

    public void setFront(Front front) {
        this.front = front;
    }

    public static class Display implements Serializable {
        @SerializedName("pl")
        private String plURL;

        @SerializedName("en")
        private String enURL;


        public String getPlURL() {
            return plURL;
        }

        public void setPlURL(String plURL) {
            this.plURL = plURL;
        }

        public String getEnURL() {
            return enURL;
        }

        public void setEnURL(String enURL) {
            this.enURL = enURL;
        }
    }

    public static class Front implements Serializable {
        @SerializedName("display")
        private Display display;

        @SerializedName("small")
        private Small small;

        public Display getDisplay() {
            return display;
        }

        public void setDisplay(Display display) {
            this.display = display;
        }

        public Small getSmall() {
            return small;
        }

        public void setSmall(Small small) {
            this.small = small;
        }

    }

    public static class Small implements Serializable {
        @SerializedName("pl")
        private String plURL;

        @SerializedName("en")
        private String enURL;

        public String getPlURL() {
            return plURL;
        }

        public void setPlURL(String plURL) {
            this.plURL = plURL;
        }

        public String getEnURL() {
            return enURL;
        }

        public void setEnURL(String enURL) {
            this.enURL = enURL;
        }
    }
}
