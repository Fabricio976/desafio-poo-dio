package com.exemplo.views;

import com.exemplo.entitys.Emprestimo;
import com.exemplo.entitys.Livro;
import com.exemplo.entitys.Usuario;
import com.exemplo.interfaces.LeitorUsuario;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Classe Biblioteca")
public class BibliotecaTest {

    @Mock
    private LeitorUsuario scanner;

    @InjectMocks
    private Biblioteca biblioteca;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private Livro livroPadrao;
    private Usuario usuarioPadrao;


    private void mockarEntradaUsuario(String... entradas) {
        // Configura o mock para retornar os valores na ordem fornecida
        when(scanner.nextLine()).thenReturn(entradas[0], Arrays.copyOfRange(entradas, 1, entradas.length));
    }

    @BeforeEach
    public void setUp() {
        // Redireciona System.out para capturar a saída do console
        System.setOut(new PrintStream(outContent));

        // Instâncias padrão para serem usadas nos testes, evitando repetição
        livroPadrao = new Livro("123-455-789-0", "Java Poo", "João Jones", 2025);
        usuarioPadrao = new Usuario("U001", "Ana Beatriz", "ana.b@example.com");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Nested
    @DisplayName("Testes de Cadastro")
    class CadastroTests {

        @Test
        @DisplayName("Deve cadastrar um livro com sucesso")
        public void testCadastrarLivro_Success() {
            when(scanner.nextLine())
                    .thenReturn(livroPadrao.getIsbn())
                    .thenReturn(livroPadrao.getTitulo())
                    .thenReturn(livroPadrao.getAutor());
            when(scanner.nextInt()).thenReturn(livroPadrao.getAnoPublicacao());

            biblioteca.cadastrarLivro();

            List<Livro> livros = biblioteca.getLivros();
            assertEquals(1, livros.size(), "A lista de livros deveria conter 1 livro.");
            Livro livroAdicionado = livros.getFirst();
            assertEquals("Java Poo", livroAdicionado.getTitulo());
            assertTrue(livroAdicionado.isDisponivel());
            assertTrue(outContent.toString().contains("Livro cadastrado com sucesso!"));

            // Verifica se os métodos do scanner foram chamados corretamente
            verify(scanner, times(3)).nextLine();
            verify(scanner, times(1)).nextInt();
        }

        @Test
        @DisplayName("Deve cadastrar um usuário com sucesso")
        public void testCadastrarUsuario_Success() {

            mockarEntradaUsuario(usuarioPadrao.getId(), usuarioPadrao.getNome(), usuarioPadrao.getEmail());

            biblioteca.cadastrarUsuario();

            List<Usuario> usuarios = biblioteca.getUsuarios();
            assertEquals(1, usuarios.size(), "A lista de usuários deveria conter 1 usuário.");
            Usuario usuarioAdicionado = usuarios.getFirst();
            assertEquals("Ana Beatriz", usuarioAdicionado.getNome());
            assertTrue(outContent.toString().contains("Usuário cadastrado com sucesso!"));
            verify(scanner, times(3)).nextLine();
        }
    }

    @Nested
    @DisplayName("Testes de Empréstimo e Devolução")
    class EmprestimoDevolucaoTests {

        @BeforeEach
        void setupEmprestimo() {
            biblioteca.getLivros().add(livroPadrao);
            biblioteca.getUsuarios().add(usuarioPadrao);
        }

        @Test
        @DisplayName("Deve realizar um empréstimo com sucesso")
        public void testRealizarEmprestimo_Success() {

            mockarEntradaUsuario(usuarioPadrao.getId(), livroPadrao.getIsbn());

            biblioteca.realizarEmprestimo();

            assertEquals(1, biblioteca.getEmprestimos().size(), "Um empréstimo deveria ter sido registrado.");
            assertFalse(livroPadrao.isDisponivel(), "O livro deveria estar marcado como indisponível.");
            assertTrue(outContent.toString().contains("Empréstimo realizado com sucesso!"));
        }

        @Test
        @DisplayName("Não deve realizar empréstimo se o livro não estiver disponível")
        public void testRealizarEmprestimo_LivroIndisponivel() {

            livroPadrao.setDisponivel(false); // Prepara o livro como já emprestado
            mockarEntradaUsuario(usuarioPadrao.getId(), livroPadrao.getIsbn());

            biblioteca.realizarEmprestimo();

            assertTrue(biblioteca.getEmprestimos().isEmpty(), "Nenhum empréstimo deveria ter sido criado.");
            assertTrue(outContent.toString().contains("Livro não está disponível para empréstimo!"));
        }

        @Test
        @DisplayName("Deve realizar uma devolução com sucesso")
        public void testRealizarDevolucao_Success() {

            Emprestimo emprestimo = new Emprestimo(livroPadrao, usuarioPadrao);
            livroPadrao.setDisponivel(false);
            biblioteca.getEmprestimos().add(emprestimo);
            mockarEntradaUsuario(usuarioPadrao.getId(), livroPadrao.getIsbn());

            biblioteca.realizarDevolucao();

            assertTrue(livroPadrao.isDisponivel(), "O livro deveria estar disponível após a devolução.");

            assertTrue(outContent.toString().contains("Devolução realizada com sucesso!"));
        }

        @Test
        @DisplayName("Não deve realizar devolução se o empréstimo não for encontrado")
        public void testRealizarDevolucao_EmprestimoNaoEncontrado() {

            mockarEntradaUsuario(usuarioPadrao.getId(), livroPadrao.getIsbn());

            biblioteca.realizarDevolucao();

            assertTrue(outContent.toString().contains("Empréstimo não encontrado!"));
        }
    }

    @Nested
    @DisplayName("Testes de Busca e Listagem")
    class BuscaListagemTests {

        @BeforeEach
        void setupBusca() {
            biblioteca.getLivros().add(livroPadrao);
        }

        @Test
        @DisplayName("Deve buscar livro por título com sucesso (insensível a maiúsculas)")
        public void testBuscarLivro_PorTitulo_CaseInsensitive() {

            when(scanner.nextInt()).thenReturn(1);
            when(scanner.nextLine()).thenReturn("JAVA");

            biblioteca.buscarLivro();

            String output = outContent.toString();
            assertTrue(output.contains("Resultados da busca:"));
            assertTrue(output.contains("Java Poo"));
        }

        @Test
        @DisplayName("Deve exibir mensagem quando a busca por livro não encontrar resultados")
        public void testBuscarLivro_SemResultados() {

            when(scanner.nextInt()).thenReturn(1);
            when(scanner.nextLine()).thenReturn("Python para Leigos");

            biblioteca.buscarLivro();

            assertTrue(outContent.toString().contains("Nenhum livro encontrado."));
        }

        @Test
        @DisplayName("Deve listar os livros quando a lista não está vazia")
        public void testListarLivros_ComItens() {

            biblioteca.listarLivros();

            String output = outContent.toString();
            assertTrue(output.contains("123-455-789-0"));
            assertTrue(output.contains(" Java Poo"));
            assertFalse(output.contains("Nenhum livro cadastrado."));
        }

        @Test
        @DisplayName("Deve exibir mensagem correta ao listar livros com a lista vazia")
        public void testListarLivros_Vazio() {

            biblioteca.getLivros().clear();
            biblioteca.listarLivros();

            assertTrue(outContent.toString().contains("Nenhum livro cadastrado."));
        }
    }
}
