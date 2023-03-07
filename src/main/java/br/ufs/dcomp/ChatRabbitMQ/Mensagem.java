package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

public class Mensagem {
    
    private static ZoneId zone = ZoneId.of("America/Sao_Paulo");
    private static DateTimeFormatter formatterDma = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static DateTimeFormatter formatterHms = DateTimeFormatter.ofPattern("HH:mm");
    private static String data = ZonedDateTime.now(zone).format(formatterDma);
    private static String hora = ZonedDateTime.now(zone).format(formatterHms);
    
    public static void enviar(String user, String msg, Channel channel, String destino, String grupo) throws Exception{
        
        ByteString msgBS = ByteString.copyFrom(msg.getBytes("UTF-8"));
        
        MensagemOuterClass.Mensagem.Builder builderMensagem = MensagemOuterClass.Mensagem.newBuilder();
        MensagemOuterClass.Conteudo.Builder builderConteudo = MensagemOuterClass.Conteudo.newBuilder();
        
        builderMensagem.setEmissor(user);
        builderMensagem.setData(data);
        builderMensagem.setHora(hora);
        builderMensagem.setGrupo(grupo);
    
        builderConteudo.setTipo("text/plain");
        builderConteudo.setCorpo(msgBS);
        builderConteudo.setNome("");
        
        builderMensagem.setConteudo(builderConteudo);
        
        MensagemOuterClass.Mensagem msg_final = builderMensagem.build();
        byte[] buffer = msg_final.toByteArray();
        
        channel.basicPublish(grupo, destino, null, buffer);
    
    }
    
    public static String receber(byte[] msg_final, String user) throws Exception{
        
        MensagemOuterClass.Mensagem msg = MensagemOuterClass.Mensagem.parseFrom(msg_final);
        
        String emissor = msg.getEmissor();
        String data = msg.getData();
        String hora = msg.getHora();
        String grupo = msg.getGrupo();
        
        MensagemOuterClass.Conteudo msg_conteudo = msg.getConteudo();
        String tipo = msg_conteudo.getTipo();
        ByteString corpo = msg_conteudo.getCorpo();
        String texto = corpo.toStringUtf8();
        String nome = msg_conteudo.getNome();
        
        if(grupo != null && !grupo.trim().isEmpty()){
            return "(" + data +  " às "  + hora + ") " + emissor + "#" + grupo + " diz: " + texto;  
        }else{
            return "(" + data +  " às "  + hora + ") " + emissor + " diz: " + texto; 
        }
   
    }
    
    public static void tratarCasos(String[] msg_caso, String user, Channel channel) throws Exception{
        Grupo aux_grupo = new Grupo();
        switch(msg_caso[0]){
            case "!addGroup":
                aux_grupo.criarGrupo(msg_caso[1], user, channel);
                System.out.println("Grupo criado com sucesso!");
                break;
            case "!addUser":
                aux_grupo.adicionarUsuario(msg_caso[1], msg_caso[2], channel);
                System.out.println("Usuário inserido com sucesso!");
                break;
            case "!delFromGroup":
                aux_grupo.removerUsuario(msg_caso[1], msg_caso[2], channel);
                System.out.println("Usuário removido com sucesso!");
                break;
            case "!removeGroup":
                aux_grupo.deletarGrupo(msg_caso[1], channel);
                System.out.println("Grupo removido com sucesso!");
                break;
        }
    }

}