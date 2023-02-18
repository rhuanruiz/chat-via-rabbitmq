package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;  
import java.util.Date;  

public class Chat {
  
  public static String usuario = "";
  
  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("52.207.78.110");
    factory.setUsername("admin");
    factory.setPassword("password");
    factory.setVirtualHost("/");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    
    System.out.print("User: ");
    Scanner sc = new Scanner(System.in);
    String user = sc.nextLine();
    String QUEUE_NAME = user;

    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {

        String message = new String(body, "UTF-8");
        
        System.out.println("");
        System.out.println(message);
        
        if(Chat.usuario != null && !Chat.usuario.trim().isEmpty()){
          System.out.print("@" + Chat.usuario + ">> ");
 
        }else{
          System.out.print(">> ");
        }
        
      }
    };
    
    SimpleDateFormat formatterDma = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatterHms = new SimpleDateFormat("HH:mm"); 
    Date data = new Date(); 
    
    while(true){
    
      channel.basicConsume(QUEUE_NAME, true,    consumer);
    
      System.out.print(">> ");
      String msg = sc.nextLine();
      
      if(msg.charAt(0) == '@'){
        while(true){
    
          Chat.usuario = msg.replace("@","");
        
          do{
          
          System.out.print("@" + Chat.usuario + ">> "); 
          msg = sc.nextLine();
        
          if(msg.charAt(0) != '@'){
            String message = "(" + formatterDma.format(data) +  " às "  + formatterHms.format(data) + ") " + user + " diz: " + msg;
            channel.basicPublish("",Chat.usuario, null,  message.getBytes("UTF-8"));
          }
          
          }while(msg.charAt(0) != '@');
        }
      }
    }
  }
}