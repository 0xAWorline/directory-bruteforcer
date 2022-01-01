/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebsiteBruteforcer;

import java.io.IOException;
import java.net.HttpURLConnection; 
import java.net.URL;
/**
 *
 * @author Austin Worline
 */
public class Requester {
    
    private int responseCode;
    private String URL,protocol;
    private String responseMessage;
    private String fullURL;
    
    //premade constructor
    public Requester(String URL,String protocol) {
        this.URL = URL;
        this.protocol = protocol;
    }
    //empty constructor for future use
    public Requester() {
        
    }
    //sets protocol 
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    //sets URL
    public void setURL(String URL) {
        this.URL = URL;
    }
    //sends GET request to the selected website with the added slug
    public void sendRequest(String slug) throws IOException {
        if(slug == null) {
            return;
        }
        this.fullURL = this.protocol + "://" + this.URL + "/" + slug;
        System.out.println("--- Sending request to " + this.fullURL + " ---");
        try {
        URL url = new URL(fullURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
        con.setRequestMethod("GET");
         
        this.responseCode = con.getResponseCode();
        this.responseMessage = con.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //returns specific response codes/messages from request
    public String getResponse() {
        if(this.responseCode >= 200 && this.responseCode <= 403) {
            return this.fullURL + " " + this.responseCode + " " + this.responseMessage;
        }
        else {
            return "";
        }
    }    
}