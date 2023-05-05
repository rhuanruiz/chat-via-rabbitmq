package br.ufs.dcomp.ChatRabbitMQ;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    
    @SerializedName("source")
    private String source;
    
    @SerializedName("destination")
    private String destination;
    
    public String getSource() {
        return source;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
}