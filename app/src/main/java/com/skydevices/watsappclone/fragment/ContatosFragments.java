package com.skydevices.watsappclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skydevices.watsappclone.R;
import com.skydevices.watsappclone.activity.ChatActivity;
import com.skydevices.watsappclone.adapter.ContatosAdapter;
import com.skydevices.watsappclone.config.ConfiguracaoFirebase;
import com.skydevices.watsappclone.helper.RecyclerItemClickListener;
import com.skydevices.watsappclone.helper.UsuarioFirebase;
import com.skydevices.watsappclone.model.Usuario;

import java.util.ArrayList;

public class ContatosFragments extends Fragment {

    private RecyclerView recyclerViewListaContato;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listacontatos = new ArrayList<>();

    private DatabaseReference usuariosRef;
    private  ValueEventListener valueEventListenerContatos;
    private  FirebaseUser usuarioAtual;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contratos, container, false);


        recyclerViewListaContato = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioAtual =  UsuarioFirebase.getUsuarioAtual();

        //configurar adapter
        adapter = new ContatosAdapter(listacontatos, getActivity());

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaContato.setLayoutManager( layoutManager );
        recyclerViewListaContato.setHasFixedSize( true );
        recyclerViewListaContato.setAdapter( adapter );

        //configurar evento de click no recyclerView

        recyclerViewListaContato.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContato,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Usuario usuarioSelecionado = listacontatos.get(position);

                                Intent i = new Intent(getActivity(), ChatActivity.class);
                                i.putExtra("chatcontato", usuarioSelecionado);
                                startActivity( i );

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){
        listacontatos.clear();

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados : snapshot.getChildren()){



                    Usuario usuario = dados.getValue(Usuario.class);

                    String emailUsuarioAtual = usuarioAtual.getEmail(); // recupera usuario atual
                    if(!emailUsuarioAtual.equals(usuario.getEmail()) ){// lista apenas os contatos, e tira o usuario atual

                        listacontatos.add(usuario);
                    }



                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}