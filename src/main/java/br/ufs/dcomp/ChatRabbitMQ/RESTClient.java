package br.ufs.dcomp.ChatRabbitMQ;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.Gson; 
import com.google.gson.reflect.TypeToken;

public class RESTClient {
    
    public static void listarUsuarios(String caminho){
        try {
            
            String username = "admin";
            String password = "password";
     
            String usernameAndPassword = username + ":" + password;
            String authorizationHeaderName = "Authorization";
            String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
     
            String restResource = "http://RabbitMQ-LB-73b5836bff8bc00d.elb.us-east-1.amazonaws.com";
            Client client = ClientBuilder.newClient();
            Gson gson = new Gson();
            Response resposta = client.target(restResource)
                .path(caminho)
                .request(MediaType.APPLICATION_JSON)
                .header( authorizationHeaderName, authorizationHeaderValue ) 
                .get();
           
            if(resposta.getStatus() == 200){
            	String json = resposta.readEntity(String.class);
            	Type type_token = new TypeToken<Collection<Usuario>>() {}.getType();
            	List<Usuario> usuarios = gson.fromJson(json, type_token);
            	for(Usuario aux : usuarios){
                    String user = aux.getDestination();
            		if(!user.isEmpty()){
            		    System.out.print(user + ", ");
            		}
            	}
                System.out.println();
            } else {
                System.out.println(resposta.getStatus());
            }   
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void listarGrupos(String caminho){
        try {
            
            String username = "admin";
            String password = "password";
     
            String usernameAndPassword = username + ":" + password;
            String authorizationHeaderName = "Authorization";
            String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
     
            String restResource = "http://RabbitMQ-LB-73b5836bff8bc00d.elb.us-east-1.amazonaws.com";
            Client client = ClientBuilder.newClient();
            Gson gson = new Gson();
            Response resposta = client.target(restResource)
                .path(caminho)
                .request(MediaType.APPLICATION_JSON)
                .header( authorizationHeaderName, authorizationHeaderValue ) 
                .get();
           
            if(resposta.getStatus() == 200){
            	String json = resposta.readEntity(String.class);
            	Type type_token = new TypeToken<Collection<Usuario>>() {}.getType();
            	List<Usuario> usuarios = gson.fromJson(json, type_token);
            	for(Usuario aux : usuarios){
                    String user = aux.getSource();
            		if(!user.isEmpty()){
            		    System.out.print(user + ", ");
            		}
            	}
                System.out.println();
            } else {
                System.out.println(resposta.getStatus());
            }   
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}