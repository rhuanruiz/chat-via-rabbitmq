package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;

public class Grupo {
    
    public static void criarGrupo(String nome_grupo, String user, Channel channel, Channel channel_upload)  throws Exception{
        channel.exchangeDeclare(nome_grupo, "fanout");
        channel.queueBind(user, nome_grupo, "");
        
        channel_upload.exchangeDeclare(nome_grupo + "Upload", "fanout");
        channel_upload.queueBind(user + "Upload", nome_grupo + "Upload", "");
    }
    
    public static void deletarGrupo(String nome_grupo, Channel channel, Channel channel_upload) throws Exception{
        channel.exchangeDelete(nome_grupo);
        channel_upload.exchangeDelete(nome_grupo + "Upload");
    }
    
    public static void adicionarUsuario(String usuario, String nome_grupo, Channel channel, Channel channel_upload) throws Exception{
        channel.queueBind(usuario, nome_grupo, "");
        channel_upload.queueBind(usuario + "Upload", nome_grupo + "Upload", "");
    }
    
    public static void removerUsuario(String usuario, String nome_grupo, Channel channel, Channel channel_upload) throws Exception{
        channel.queueUnbind(usuario, nome_grupo, "");
        channel_upload.queueUnbind(usuario + "Upload", nome_grupo + "Upload", "");
    }
    
}