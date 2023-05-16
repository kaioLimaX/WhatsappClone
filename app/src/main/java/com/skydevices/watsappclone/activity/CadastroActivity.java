package com.skydevices.watsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.skydevices.watsappclone.R;
import com.skydevices.watsappclone.config.ConfiguracaoFirebase;
import com.skydevices.watsappclone.helper.Base64Custom;
import com.skydevices.watsappclone.helper.UsuarioFirebase;
import com.skydevices.watsappclone.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void cadastrarUsuario(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(CadastroActivity.this,
                            "Usuario criado com Sucesso",
                            Toast.LENGTH_SHORT).show();
                    UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                    finish();
                    try{
                        String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setuId(identificadorUsuario );
                        usuario.salvar();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String excesao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excesao = "Insira uma senha mais forte";

                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excesao = "insira um email valido";

                    }catch (FirebaseAuthUserCollisionException e){
                        excesao = "Email ja cadastrado";
                    }
                    catch (Exception e){
                        excesao = "Erro cadastrar " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excesao, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void validarCadastroUsuario(View view){
        //recuperar texto dos campos
        String textoNome = campoNome.getText().toString();
        String textEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        //verificação se o TextBox esta vazio

        if(!textoNome.isEmpty()){//verifica nome
            if(!textEmail.isEmpty()){//verifica email
                if(!textoSenha.isEmpty()){//verifica senha
                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textEmail );
                    usuario.setSenha( textoSenha );

                    cadastrarUsuario(usuario);


                }else {
                    Toast.makeText(CadastroActivity.this,"Preencha o email!",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this,"Preencha o email!",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this,"Preencha o nome!",Toast.LENGTH_SHORT).show();
        }
    }
}