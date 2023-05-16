package com.skydevices.watsappclone.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.skydevices.watsappclone.config.ConfiguracaoFirebase;
import com.skydevices.watsappclone.model.Usuario;

public class UsuarioFirebase {


    public static String getIdentificadorUsuario(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth(); // recuperar usuario
        String email = usuario.getCurrentUser().getEmail(); // recuperar email Usuario
        String identificadorUsuario = Base64Custom.codificarBase64(email); // Codificar email para BaseCustom64

        return identificadorUsuario;

    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth(); // recuperar usuario
        return usuario.getCurrentUser();

    }
    public static boolean atualizarNomeUsuario(String nome){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "erro ao nome de perfil");
                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean atualizarFotoUsuario(Uri url){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "erro ao atualizar foto de perfil");
                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName() );

        if(firebaseUser.getPhotoUrl() == null){
            usuario.setFoto("");
        }else {
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }

}
