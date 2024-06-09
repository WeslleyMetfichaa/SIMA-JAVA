package sistemaatedimento;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sistemaatedimento {
    private Connection conexao;

    public sistemaatedimento() {
        conectarAoBanco();
    }

    private void conectarAoBanco() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/Banco";
            String user = "root";
            String password = "";
            conexao = DriverManager.getConnection(url, user, password);
            exibirMensagem("Conectado ao banco de dados!");
        } catch (ClassNotFoundException | SQLException e) {
            exibirErro("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void cadastrarUsuario() {
        try {
            String usuario = obterInput("Digite o nome do usuário:");
            String senha = obterInput("Digite a senha:");
            String tipo = obterInput("Digite o tipo de usuário (gestor/funcionario):");

            if (usuario == null || usuario.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty() ||
                tipo == null || (!tipo.equalsIgnoreCase("gestor") && !tipo.equalsIgnoreCase("funcionario"))) {
                exibirErro("Dados inválidos. Verifique e tente novamente.");
                return;
            }

            String sql = "INSERT INTO usuarios (nome, senha, tipo) VALUES (?, ?, ?)";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setString(1, usuario);
                statement.setString(2, senha);
                statement.setString(3, tipo.toLowerCase());
                statement.executeUpdate();
                exibirMensagem("Usuário cadastrado com sucesso.");
            }
        } catch (SQLException e) {
            exibirErro("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public String fazerLogin() {
        try {
            String usuario = obterInput("Digite o nome do usuário:");
            String senha = obterInput("Digite a senha:");

            if (usuario == null || usuario.trim().isEmpty() ||
                senha == null || senha.trim().isEmpty()) {
                exibirErro("Usuário e senha são obrigatórios.");
                return null;
            }

            String sql = "SELECT senha, tipo FROM usuarios WHERE nome = ?";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setString(1, usuario);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    String senhaArmazenada = rs.getString("senha");
                    String tipo = rs.getString("tipo");

                    if (senha.equals(senhaArmazenada)) {
                        return tipo;
                    } else {
                        exibirErro("Senha incorreta.");
                        return null;
                    }
                } else {
                    exibirErro("Usuário não encontrado.");
                    return null;
                }
            }
        } catch (SQLException e) {
            exibirErro("Erro ao fazer login: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        sistemaatedimento controle = new sistemaatedimento();
        String menu = "Bem-vindo(a) ao SIMA\nEscolha uma opção:\n1. Login\n2. Cadastrar usuário\n0. Sair\nOpção: ";

        int opcao;
        do {
            String input = obterInput(menu);
            opcao = Integer.parseInt(input);
            switch (opcao) {
                case 1:
                    String tipoUsuario = controle.fazerLogin();
                    if (tipoUsuario != null) {
                        if ("gestor".equals(tipoUsuario)) {
                            new GestorVisualizacaoAtendimentos().executar();
                        } else if ("funcionario".equals(tipoUsuario)) {
                            new FuncionarioAtendimento().exibirMenu();
                        } else {
                            exibirErro("Tipo de usuário desconhecido.");
                        }
                    }
                    break;
                case 2:
                    controle.cadastrarUsuario();
                    break;
                case 0:
                    exibirMensagem("Encerrando o sistema.");
                    break;
                default:
                    exibirErro("Opção inválida. Por favor, escolha uma opção válida.");
            }
        } while (opcao != 0);
    }

    private static String obterInput(String mensagem) {
        return JOptionPane.showInputDialog(null, mensagem);
    }

    private static void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }

    private static void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
