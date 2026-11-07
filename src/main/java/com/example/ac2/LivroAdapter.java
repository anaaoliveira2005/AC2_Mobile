package com.example.ac2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.LivroViewHolder> {

    private ArrayList<Livro> listaLivros;
    private OnLivroClickListener listener;

    public interface OnLivroClickListener {
        void onLivroClick(Livro livro);
        void onLivroLongClick(Livro livro);
    }
    public LivroAdapter(ArrayList<Livro> listaLivros,
                        OnLivroClickListener listener) {

        this.listaLivros = listaLivros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LivroViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_livro, parent, false);

        return new LivroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LivroViewHolder holder, int position) {

        Livro livro = listaLivros.get(position);

        holder.txtTituloLivro.setText(livro.getTitulo());
        holder.txtAutorLivro.setText(livro.getAutor());
        holder.txtGeneroLivro.setText(livro.getGenero());
        holder.txtAnoLivro.setText(String.valueOf(livro.getAnoPublicacao()));
        holder.txtStatusLivro.setText(livro.getStatusLeitura());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLivroClick(livro);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLivroLongClick(livro);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaLivros.size();
    }

    public static class LivroViewHolder extends RecyclerView.ViewHolder {

        TextView txtTituloLivro;
        TextView txtAutorLivro;
        TextView txtGeneroLivro;
        TextView txtAnoLivro;
        TextView txtStatusLivro;

        public LivroViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTituloLivro = itemView.findViewById(R.id.txtTituloLivro);
            txtAutorLivro = itemView.findViewById(R.id.txtAutorLivro);
            txtGeneroLivro = itemView.findViewById(R.id.txtGeneroLivro);
            txtAnoLivro = itemView.findViewById(R.id.txtAnoLivro);
            txtStatusLivro = itemView.findViewById(R.id.txtStatusLivro);
        }
    }
}