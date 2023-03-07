package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;

public class Grupo {
    
    public static void criarGrupo(String nome_grupo, String user, Channel channel) throws Exception{
        channel.exchangeDeclare(nome_grupo, "fanout");
        channel.queueBind(user, nome_grupo, "");
    }
    
    public static void deletarGrupo(String nome_grupo, Channel channel) throws Exception{
        channel.exchangeDelete(nome_grupo);
    }
    
    public static void adicionarUsuario(String usuario, String nome_grupo, Channel channel) throws Exception{
        channel.queueBind(usuario, nome_grupo, "");
    }
    
    public static void removerUsuario(String usuario, String nome_grupo, Channel channel) throws Exception{
        channel.queueUnbind(usuario, nome_grupo, "");
    }

}