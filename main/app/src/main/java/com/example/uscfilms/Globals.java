package com.example.uscfilms;

public class Globals {

    private static Globals instance;

    // Global variable
    private String backendUrl;

    // Restrict the constructor from being instantiated
    private Globals(){ this.backendUrl = "https://csci571-hw9-311618.wl.r.appspot.com"; };

    public String getBackend(){
        return this.backendUrl;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
