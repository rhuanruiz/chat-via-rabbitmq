package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileOutputStream;

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
    
        builderConteudo.setTipo("");
        builderConteudo.setCorpo(msgBS);
        builderConteudo.setNome("");
        
        builderMensagem.setConteudo(builderConteudo);
        
        MensagemOuterClass.Mensagem msg_final = builderMensagem.build();
        byte[] buffer = msg_final.toByteArray();
        
        channel.basicPublish(grupo, destino, null, buffer);
    
    }
    
    public static String receber(byte[] msg_final, String user) throws Exception{
        
        MensagemOuterClass.Mensagem msg = MensagemOuterClass.Mensagem.parseFrom(msg_final);
        MensagemOuterClass.Conteudo msg_conteudo = msg.getConteudo();
        String tipo = msg_conteudo.getTipo();
        
        if(tipo.equals("")){
            String emissor = msg.getEmissor();
            String data = msg.getData();
            String hora = msg.getHora();
            String grupo = msg.getGrupo();
        
            ByteString corpo = msg_conteudo.getCorpo();
            String texto = corpo.toStringUtf8();
            String nome = msg_conteudo.getNome();
            
            if(grupo != null && !grupo.trim().isEmpty()){
                return "(" + data +  " às "  + hora + ") " + emissor + "#" + grupo + " diz: " + texto;  
            }else{
                return "(" + data +  " às "  + hora + ") " + emissor + " diz: " + texto; 
            }   
        }else{
            return download(msg_final, user); 
        }
        
    }
    
    public static void upload(String dir, String user, String user_destino, String grupo, Channel channel_upload, String aux) throws Exception{
        
        Path caminho = Paths.get(dir);
        byte[] arquivo = Files.readAllBytes(caminho);
        String tipo = Files.probeContentType(caminho);
        
        ByteString arqBS = ByteString.copyFrom(arquivo);
        
        MensagemOuterClass.Mensagem.Builder builderMensagem = MensagemOuterClass.Mensagem.newBuilder();
        MensagemOuterClass.Conteudo.Builder builderConteudo = MensagemOuterClass.Conteudo.newBuilder();
        
        builderMensagem.setEmissor(user);
        builderMensagem.setData(data);
        builderMensagem.setHora(hora);
        builderMensagem.setGrupo(grupo);
    
        builderConteudo.setTipo(tipo);
        builderConteudo.setCorpo(arqBS);
        builderConteudo.setNome((caminho.getFileName()).toString());
        
        builderMensagem.setConteudo(builderConteudo);
        
        MensagemOuterClass.Mensagem msg_final = builderMensagem.build();
        byte[] buffer = msg_final.toByteArray();
        
        if(aux.charAt(0) == '@'){
            System.out.println("Enviando \"" + dir + "\" para @" + user_destino + ".");
            channel_upload.basicPublish("", user_destino + "Upload", null, buffer);
        }else if(aux.charAt(0) == '#'){
            System.out.println("Enviando \"" + dir + "\" para #" + grupo + ".");
            channel_upload.basicPublish(grupo + "Upload", "", null, buffer);
        }
        
        if(aux.charAt(0) == '@'){
            System.out.println("Arquivo \"" + dir + "\" foi enviado para @" + user_destino + "!");
        }else if(aux.charAt(0) == '#'){
            System.out.println("Arquivo \"" + dir + "\" foi enviado para #" + grupo + "!");;
        }
        
    }
    
    public static String download(byte[] msg_final, String user) throws Exception{
        
        MensagemOuterClass.Mensagem msg = MensagemOuterClass.Mensagem.parseFrom(msg_final);
        
        String emissor = msg.getEmissor();
        String data = msg.getData();
        String hora = msg.getHora();
        String grupo = msg.getGrupo();
        
        MensagemOuterClass.Conteudo msg_conteudo = msg.getConteudo();
        
        String tipo = msg_conteudo.getTipo();
        ByteString corpo = msg_conteudo.getCorpo();
        byte[] buffer = corpo.toByteArray();
        String nome = msg_conteudo.getNome();
        
        FileOutputStream outputStream = new FileOutputStream("downloads/" + user + "_downloads" + "/" + nome); 
        outputStream.write(buffer);
        outputStream.close();
        
        if(grupo.equals("")){
            return "(" + data +  " às "  + hora + ") Arquivo \"" + nome + "\"" + " recebido de @" + emissor + "!";
        }else{
            return "(" + data +  " às "  + hora + ") Arquivo \"" + nome + "\"" + " recebido de " + emissor + "#" + grupo + "!";
        }
        
    }
    
    public static void tratarCasos(String[] msg_caso, String user, Channel channel, Channel channel_upload, String user_destino, 
                                                                                    String grupo, String aux_user_grupo) throws Exception{
        
        Grupo aux_grupo = new Grupo();
        RESTClient aux_rest = new RESTClient();
        String path = "";
        
        switch(msg_caso[0]){
            case "!addGroup":
                aux_grupo.criarGrupo(msg_caso[1], user, channel, channel_upload);
                System.out.println("Criado!");
                break;
            case "!addUser":
                aux_grupo.adicionarUsuario(msg_caso[1], msg_caso[2], channel, channel_upload);
                System.out.println("Adicionado!");
                break;
            case "!delFromGroup":
                aux_grupo.removerUsuario(msg_caso[1], msg_caso[2], channel, channel_upload);
                System.out.println("Removido!");
                break;
            case "!removeGroup":
                aux_grupo.deletarGrupo(msg_caso[1], channel, channel_upload);
                System.out.println("Deletado!");
                break;
            case "!upload":
                upload(msg_caso[1], user, user_destino, grupo, channel_upload, aux_user_grupo);
                break;
            case "!listUsers":
                path = "/api/exchanges/%2f/" + msg_caso[1] + "/bindings/source";
                aux_rest.listarUsuarios(path);
                break;
            case "!listGroups":
                path = "/api/queues/%2f/" + user + "/bindings";
                aux_rest.listarGrupos(path);
                break;
        }
    }

}