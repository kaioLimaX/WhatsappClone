package com.skydevices.watsappclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydevices.watsappclone.R;
import com.skydevices.watsappclone.adapter.MensagensAdapter;
import com.skydevices.watsappclone.config.ConfiguracaoFirebase;
import com.skydevices.watsappclone.helper.Base64Custom;
import com.skydevices.watsappclone.helper.UsuarioFirebase;
import com.skydevices.watsappclone.model.Conversa;
import com.skydevices.watsappclone.model.Mensagem;
import com.skydevices.watsappclone.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;

    private Usuario usuarioSelecionado;

    private EditText editMensagem;

    private ImageView imageCamera;

    //identificador usuario que envia e o que recebe a mensagem
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    private RecyclerView recyclerMensagens;
    private MensagensAdapter adapter;

    final private List<Mensagem> mensagens = new ArrayList<>();

    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;
    private  static final int SELECAO_CAMERA = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.verde));
        }

        Toolbar toolbar = findViewById(R.id.textViewNomeChat);
        toolbar.setTitle( "" );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configuracoes iniciais

        textViewNome = findViewById(R.id.TextNomeSelecionado);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);


        //recuperar id usuario que remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();


        //Recuperar dados do usario Selecionado
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("chatcontato");
            textViewNome.setText(usuarioSelecionado.getNome());

            String foto = usuarioSelecionado.getFoto();
            if(foto != null){
                Uri url = Uri.parse(usuarioSelecionado.getFoto());
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewFoto);
            }else {
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }

            idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioSelecionado.getEmail());

        }

        //configuaração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //Configuração recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());


        recyclerMensagens.setLayoutManager(layoutManager);


        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);







        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);




        //evento de clique camera

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if(imagem != null){
                    //recuperando dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    String nomeImagem = UUID.randomUUID().toString();


                    //salvar imagem no Firebase
                    StorageReference imagemRef = storage
                            .child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(  nomeImagem + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("erro", "erro ao fazer upload");
                            Toast.makeText(ChatActivity.this,
                                    "erro ao fazer upload",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                   String url = task.getResult().toString();

                                   Mensagem mensagem = new Mensagem();
                                   mensagem.setIdUsuario(idUsuarioRemetente);
                                   mensagem.setMensagem("imagem.jpg");
                                   mensagem.setImagem(url);
                                    //salvar mensagem remetente
                                   salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                    //salvar mensagem destinatario
                                    salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);

                                    Toast.makeText(ChatActivity.this,
                                            "Sucesso ao enviar imagem",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });


                }


            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    public void EnviarMensagem(View view){
        String textomensagem = editMensagem.getText().toString();

        if(!textomensagem.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario( idUsuarioRemetente);
            mensagem.setMensagem( textomensagem );
            //salvar mensagem remetente
            salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);

            //salvar mensagem destinatario
            salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);

            //salvar conversa
            salvarConversa(mensagem);



        }else {
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enviar!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarConversa(Mensagem msg){

        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem() );
        conversaRemetente.setUsuarioExibicao(usuarioSelecionado);
        conversaRemetente.salvar();


    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        DatabaseReference mensagensRef = database.child("mensagens");

                mensagensRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);


        //Limpar Texto
        editMensagem.setText("");
        recyclerMensagens.smoothScrollToPosition(mensagens.size());

    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);

    }



    private void recuperarMensagens(){

        mensagens.clear();

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);



                adapter.notifyDataSetChanged();
                recyclerMensagens.smoothScrollToPosition(mensagens.size());



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {



            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }




}