package com.example.ac2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText edtTitulo, edtAutor, edtAnoPublicacao;
    private Spinner spinnerGenero, spinnerStatusLeitura, spinnerFiltroGenero;
    private CheckBox checkboxFavorito;
    private Button btnCadastrar;
    private TextView txtTotalLivros;
    private RecyclerView recyclerLivros;

    private ArrayList<Livro> listaLivros = new ArrayList<>();
    private ArrayList<Livro> listaLivrosFiltrados = new ArrayList<>();

    private LivroAdapter adapter;

    private Livro livroEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o Firebase
        db = FirebaseFirestore.getInstance();

        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        edtAnoPublicacao = findViewById(R.id.edtAnoPublicacao);

        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerStatusLeitura = findViewById(R.id.spinnerStatusLeitura);
        spinnerFiltroGenero = findViewById(R.id.spinnerFiltroGenero);

        checkboxFavorito = findViewById(R.id.checkboxFavorito);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        txtTotalLivros = findViewById(R.id.txtTotalLivros);
        recyclerLivros = findViewById(R.id.recyclerLivros);

        recyclerLivros.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LivroAdapter(
                listaLivrosFiltrados,
                new LivroAdapter.OnLivroClickListener() {

                    @Override
                    public void onLivroClick(Livro livro) {
                        carregarLivroParaEdicao(livro);
                    }

                    @Override
                    public void onLivroLongClick(Livro livro) {
                        confirmarExclusao(livro);
                    }
                }
        );

        recyclerLivros.setAdapter(adapter);
        btnCadastrar.setOnClickListener(v -> salvarLivro());

        spinnerFiltroGenero.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        atualizarLista();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        carregarLivros();
    }

    private void salvarLivro() {

        String titulo = edtTitulo.getText().toString().trim();
        String autor = edtAutor.getText().toString().trim();
        String anoTexto = edtAnoPublicacao.getText().toString().trim();

        if (titulo.isEmpty()) {
            edtTitulo.setError("Informe o título do livro");
            edtTitulo.requestFocus();
            return;
        }

        if (autor.isEmpty()) {
            edtAutor.setError("Informe o autor do livro");
            edtAutor.requestFocus();
            return;
        }

        if (spinnerGenero.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione um gênero",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (anoTexto.isEmpty()) {
            edtAnoPublicacao.setError("Informe o ano de publicação");
            edtAnoPublicacao.requestFocus();
            return;
        }

        int ano;

        try {
            ano = Integer.parseInt(anoTexto);
        } catch (NumberFormatException e) {
            edtAnoPublicacao.setError("Informe um ano válido");
            edtAnoPublicacao.requestFocus();
            return;
        }

        if (ano <= 0) {
            edtAnoPublicacao.setError("Informe um ano válido");
            edtAnoPublicacao.requestFocus();
            return;
        }

        if (spinnerStatusLeitura.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione um status de leitura",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String genero = spinnerGenero.getSelectedItem().toString();
        String status = spinnerStatusLeitura.getSelectedItem().toString();

        boolean favorito = checkboxFavorito.isChecked();

        Livro livro = new Livro(
                titulo,
                autor,
                genero,
                ano,
                status,
                favorito
        );

        if (livroEditando == null) {

            DocumentReference novoDocumento = db.collection("livros").document();

            livro.setId(novoDocumento.getId());

            novoDocumento.set(livro).addOnSuccessListener(aVoid -> {

                Toast.makeText(this, "Livro cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                    carregarLivros();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(this,
                                "Erro ao cadastrar: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        } else {

            db.collection("livros")
                    .document(livroEditando.getId())
                    .set(livro)
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(this,
                                "Livro atualizado com sucesso!",
                                Toast.LENGTH_SHORT).show();

                        limparCampos();
                        carregarLivros();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(this,
                                "Erro ao atualizar: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void carregarLivros() {

        db.collection("livros")
                .get()
                .addOnSuccessListener(query -> {

                    listaLivros.clear();

                    for (QueryDocumentSnapshot documento : query) {

                        Livro livro = documento.toObject(Livro.class);

                        // Armazena o ID do documento Firebase
                        livro.setId(documento.getId());

                        listaLivros.add(livro);
                    }

                    atualizarLista();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Erro ao carregar livros: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void atualizarLista() {

        listaLivrosFiltrados.clear();

        String generoSelecionado =
                spinnerFiltroGenero.getSelectedItem().toString();

        for (Livro livro : listaLivros) {

            if (generoSelecionado.equals("Todos os gêneros")
                    || livro.getGenero().equals(generoSelecionado)) {

                listaLivrosFiltrados.add(livro);
            }
        }

        adapter.notifyDataSetChanged();

        txtTotalLivros.setText(
                "Total de livros cadastrados: " + listaLivros.size()
        );
    }

    private void carregarLivroParaEdicao(Livro livro) {

        edtTitulo.setText(livro.getTitulo());
        edtAutor.setText(livro.getAutor());

        selecionarItemSpinner(spinnerGenero, livro.getGenero());

        edtAnoPublicacao.setText(
                String.valueOf(livro.getAnoPublicacao())
        );

        selecionarItemSpinner(
                spinnerStatusLeitura,
                livro.getStatusLeitura()
        );

        checkboxFavorito.setChecked(livro.isFavorito());

        livroEditando = livro;

        btnCadastrar.setText("Atualizar");

        Toast.makeText(this,
                "Livro carregado para edição",
                Toast.LENGTH_SHORT).show();
    }

    private void selecionarItemSpinner(Spinner spinner, String valor) {

        for (int i = 0; i < spinner.getCount(); i++) {

            if (spinner.getItemAtPosition(i).toString().equals(valor)) {

                spinner.setSelection(i);
                return;
            }
        }
    }

    private void confirmarExclusao(Livro livro) {

        new AlertDialog.Builder(this)
                .setTitle("Excluir livro")
                .setMessage(
                        "Tem certeza de que deseja excluir \""
                                + livro.getTitulo() + "\"?"
                )
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Excluir", (dialog, which) -> {
                    excluirLivro(livro);
                })
                .show();
    }

    private void excluirLivro(Livro livro) {

        if (livro.getId() == null) {
            Toast.makeText(this,
                    "Não foi possível identificar o livro",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("livros")
                .document(livro.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(this,
                            "Livro excluído com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    if (livroEditando != null
                            && livro.getId().equals(livroEditando.getId())) {

                        limparCampos();
                    }

                    carregarLivros();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Erro ao excluir: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void limparCampos() {

        edtTitulo.setText("");
        edtAutor.setText("");
        edtAnoPublicacao.setText("");

        spinnerGenero.setSelection(0);
        spinnerStatusLeitura.setSelection(0);

        checkboxFavorito.setChecked(false);

        livroEditando = null;

        btnCadastrar.setText("Cadastrar");
    }
}