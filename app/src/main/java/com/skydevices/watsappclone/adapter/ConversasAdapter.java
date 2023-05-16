package com.skydevices.watsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skydevices.watsappclone.R;
import com.skydevices.watsappclone.helper.Base64Custom;
import com.skydevices.watsappclone.helper.UsuarioFirebase;
import com.skydevices.watsappclone.model.Conversa;
import com.skydevices.watsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyviewHolder> {

    private List<Conversa> conversas;
    private Context context;


    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);

        return new MyviewHolder(itemlista);


    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        Conversa conversa = conversas.get(position);
        Usuario usuario = conversa.getUsuarioExibicao();
        holder.nome.setText(usuario.getNome());
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        if (usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome,ultimaMensagem;


        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            foto  = itemView.findViewById(R.id.imageViewContato);
            nome  = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);

        }
    }
}
