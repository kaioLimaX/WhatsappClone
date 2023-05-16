package com.skydevices.watsappclone.model;

public class Mensagem {

    private String idUsuario;
    private String Mensagem;
    private String imagem;

    public Mensagem() {
    }

    public String getIdUsuario() {  return idUsuario;   }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return Mensagem;
    }

    public void setMensagem(String mensagem) {
        Mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}
