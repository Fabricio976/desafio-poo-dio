package com.exemplo.views;

import com.exemplo.entitys.Emprestimo;
import com.exemplo.entitys.Livro;
import com.exemplo.entitys.Usuario;
import com.exemplo.interfaces.LeitorUsuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Livro> livros;
    private List<Usuario> usuarios;
    private List<Emprestimo> emprestimos;
    private LeitorUsuario scanner;

    public Biblioteca(LeitorUsuario entrada) {
        this.livros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
        this.scanner = entrada;
    }

    public void iniciar() {
        carregarDados();
        int opcao;
        do {
            exibirMenu();
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1: cadastrarLivro(); break;
                case 2: cadastrarUsuario(); break;
                case 3: realizarEmprestimo(); break;
                case 4: realizarDevolucao(); break;
                case 5: listarLivros(); break;
                case 6: listarUsuarios(); break;
                case 7: buscarLivro(); break;
                case 8: listarEmprestimos(); break;
                case 9: salvarDados(); break;
                case 0: System.out.println("Saindo do sistema..."); break;
                default: System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("\n=== SISTEMA DE BIBLIOTECA ===");
        System.out.println("1. Cadastrar Livro");
        System.out.println("2. Cadastrar Usuário");
        System.out.println("3. Realizar Empréstimo");
        System.out.println("4. Realizar Devolução");
        System.out.println("5. Listar Livros");
        System.out.println("6. Listar Usuários");
        System.out.println("7. Buscar Livro");
        System.out.println("8. Listar Empréstimos");
        System.out.println("9. Salvar Dados");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    public void cadastrarLivro() {
        System.out.println("\n--- Cadastro de Livro ---");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Ano de Publicação: ");
        int ano = scanner.nextInt();

        Livro livro = new Livro(isbn, titulo, autor, ano);
        livros.add(livro);
        System.out.println("Livro cadastrado com sucesso!");
    }

    public void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuário ---");
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Usuario usuario = new Usuario(id, nome, email);
        usuarios.add(usuario);
        System.out.println("Usuário cadastrado com sucesso!");
    }

    public void realizarEmprestimo() {
        System.out.println("\n--- Realizar Empréstimo ---");
        System.out.print("ID do Usuário: ");
        String userId = scanner.nextLine();
        System.out.print("ISBN do Livro: ");
        String isbn = scanner.nextLine();

        Usuario usuario = buscarUsuarioPorId(userId);
        Livro livro = buscarLivroPorIsbn(isbn);

        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        if (livro == null) {
            System.out.println("Livro não encontrado!");
            return;
        }

        if (!livro.isDisponivel()) {
            System.out.println("Livro não está disponível para empréstimo!");
            return;
        }

        Emprestimo emprestimo = new Emprestimo(livro, usuario);
        emprestimos.add(emprestimo);
        livro.setDisponivel(false);
        usuario.adicionarLivro(livro);

        System.out.println("Empréstimo realizado com sucesso!");
        System.out.println("Data de devolução: " + emprestimo.getDataDevolucao());
    }

    public void realizarDevolucao() {
        System.out.println("\n--- Realizar Devolução ---");
        System.out.print("ID do Usuário: ");
        String userId = scanner.nextLine();
        System.out.print("ISBN do Livro: ");
        String isbn = scanner.nextLine();

        Usuario usuario = buscarUsuarioPorId(userId);
        Livro livro = buscarLivroPorIsbn(isbn);

        if (usuario == null || livro == null) {
            System.out.println("Usuário ou livro não encontrado!");
            return;
        }

        Emprestimo emprestimo = buscarEmprestimoAtivo(usuario, livro);
        if (emprestimo == null) {
            System.out.println("Empréstimo não encontrado!");
            return;
        }

        double multa = emprestimo.calcularMulta();
        emprestimo.setMulta(multa);

        livro.setDisponivel(true);
        usuario.removerLivro(livro);

        System.out.println("Devolução realizada com sucesso!");
        if (multa > 0) {
            System.out.println("Multa a pagar: R$ " + String.format("%.2f", multa));
        }
    }

    public void listarLivros() {
        System.out.println("\n--- Lista de Livros ---");
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }

        for (Livro livro : livros) {
            System.out.println(livro);
        }
    }

    public void listarUsuarios() {
        System.out.println("\n--- Lista de Usuários ---");
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    public void listarEmprestimos() {
        System.out.println("\n--- Lista de Empréstimos ---");
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo realizado.");
            return;
        }

        for (Emprestimo emprestimo : emprestimos) {
            System.out.println(emprestimo);
        }
    }

    public void buscarLivro() {
        System.out.println("\n--- Buscar Livro ---");
        System.out.println("1. Por Título");
        System.out.println("2. Por Autor");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();

        System.out.print("Termo de busca: ");
        String termo = scanner.nextLine().toLowerCase();

        List<Livro> resultados = new ArrayList<>();

        if (opcao == 1) {
            for (Livro livro : livros) {
                if (livro.getTitulo().toLowerCase().contains(termo)) {
                    resultados.add(livro);
                }
            }
        } else if (opcao == 2) {
            for (Livro livro : livros) {
                if (livro.getAutor().toLowerCase().contains(termo)) {
                    resultados.add(livro);
                }
            }
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        if (resultados.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
        } else {
            System.out.println("Resultados da busca:");
            for (Livro livro : resultados) {
                System.out.println(livro);
            }
        }
    }

    public Usuario buscarUsuarioPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    public Livro buscarLivroPorIsbn(String isbn) {
        for (Livro livro : livros) {
            if (livro.getIsbn().equals(isbn)) {
                return livro;
            }
        }
        return null;
    }

    public Emprestimo buscarEmprestimoAtivo(Usuario usuario, Livro livro) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getUsuario().equals(usuario) &&
                    emprestimo.getLivro().equals(livro) &&
                    !livro.isDisponivel()) {
                return emprestimo;
            }
        }
        return null;
    }

    public void salvarDados() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("biblioteca.dat"));
            oos.writeObject(livros);
            oos.writeObject(usuarios);
            oos.writeObject(emprestimos);
            oos.close();
            System.out.println("Dados salvos com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void carregarDados() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("biblioteca.dat"));
            livros = (List<Livro>) ois.readObject();
            usuarios = (List<Usuario>) ois.readObject();
            emprestimos = (List<Emprestimo>) ois.readObject();
            ois.close();
            System.out.println("Dados carregados com sucesso!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Arquivo de dados não encontrado. Iniciando com dados vazios.");
        }
    }

    // Métodos para testes
    public List<Livro> getLivros() { return livros; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Emprestimo> getEmprestimos() { return emprestimos; }
    public void setScanner(LeitorUsuario scanner) { this.scanner = scanner; }
}