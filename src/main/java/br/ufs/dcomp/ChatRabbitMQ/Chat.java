package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

public class Chat {
  
  public static String usuario = "";
  public static String grupo = "";
  public static String aux_user_grupo = "";
  
  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("54.175.118.8");
    factory.setUsername("admin");
    factory.setPassword("password");
    factory.setVirtualHost("/");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    
    Scanner sc = new Scanner(System.in);
    Mensagem aux_msg = new Mensagem();
    Grupo aux_grupo = new Grupo();
    
    System.out.print("User: ");
    String user = sc.nextLine();
    String QUEUE_NAME = user;

    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {

        String message = new String(body, "UTF-8");
        System.out.println("");
        
        try {
          System.out.println(aux_msg.receber(body, user));
          
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        
        System.out.print(Chat.aux_user_grupo + ">> ");
        
      }
    };
    
    channel.basicConsume(QUEUE_NAME, true,    consumer);
    
    while(true){
      
      System.out.print(Chat.aux_user_grupo + ">> ");
      String msg = sc.nextLine();
      
      if(msg.charAt(0) == '@'){
        Chat.aux_user_grupo = msg;
        Chat.usuario = msg.replace("@","");
      }
      
      else if(msg.charAt(0) == '!'){
        String[] msg_caso = msg.split(" ");
        aux_msg.tratarCasos(msg_caso, user, channel);
      }
      
      else if(msg.charAt(0) == '#'){
        Chat.aux_user_grupo = msg;
        Chat.grupo = msg.replace("#","");
      }
      
      else if(Chat.aux_user_grupo == ""){
        ;
      }
      
      else{
        if(Chat.aux_user_grupo.charAt(0) == '@'){
          aux_msg.enviar(user, msg, channel, Chat.usuario, "");
        }else if(Chat.aux_user_grupo.charAt(0) == '#'){
          aux_msg.enviar(user, msg, channel, "", Chat.grupo);
        }
      }
    }
  }
}
