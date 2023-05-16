package com.skydevices.watsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.skydevices.watsappclone.config.ConfiguracaoFirebase;
import com.skydevices.watsappclone.helper.UsuarioFirebase;

public class Conversa {

    private String idRemetente;
    private  String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;

    private Usuario usuarioAtual;


    public Conversa() {

    }

    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

                 conversaRef.child(this.getIdRemetente())
                         .child(this.getIdDestinatario())
                         .setValue(this);
                 salvarConversaDestinatario();



    }
    public void salvarConversaDestinatario(){
        DatabaseReference ref = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaParearOutro = ref.child("conversas");
        usuarioAtual = UsuarioFirebase.getDadosUsuarioLogado();
        setUsuarioExibicao(usuarioAtual);
        conversaParearOutro.child(getIdDestinatario())
                .child(getIdRemetente())
                .setValue(this);
    }


    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatarion) {
        this.idDestinatario = idDestinatarion;
    }



    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }


    }

