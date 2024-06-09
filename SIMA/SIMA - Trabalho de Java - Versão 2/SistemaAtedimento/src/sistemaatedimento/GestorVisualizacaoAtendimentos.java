package sistemaatedimento;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorVisualizacaoAtendimentos {
    private Connection conexao;
    private Statement stmt;

    public GestorVisualizacaoAtendimentos() {
        conectar();
    }

    private void conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/Banco";
            String user = "root";
            String password = "";
            conexao = DriverManager.getConnection(url, user, password);
            stmt = conexao.createStatement();
            JOptionPane.showMessageDialog(null, "Conectado ao banco de dados!");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void visualizarAtendimentos() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM atendimento");
            StringBuilder result = new StringBuilder("Lista de atendimentos:\n");
            while (rs.next()) {
                int id = rs.getInt("id");
                String descricao = rs.getString("descricao");
                int idEquipe = rs.getInt("id_equipe");
                result.append("ID: ").append(id).append(", Descrição: ").append(descricao)
                      .append(", ID da Equipe: ").append(idEquipe).append("\n");
            }
            JOptionPane.showMessageDialog(null, result.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar atendimentos: " + e.getMessage());
        }
    }

    public void autorizarAcesso(String nomeUsuario) {
        try {
            PreparedStatement verificaUsuario = conexao.prepareStatement("SELECT * FROM usuarios_autorizados WHERE nome = ?");
            verificaUsuario.setString(1, nomeUsuario);
            ResultSet rs = verificaUsuario.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "O usuário já está autorizado para acessar o sistema.");
            } else {
                PreparedStatement autorizaUsuario = conexao.prepareStatement("INSERT INTO usuarios_autorizados (nome) VALUES (?)");
                autorizaUsuario.setString(1, nomeUsuario);
                autorizaUsuario.executeUpdate();
                JOptionPane.showMessageDialog(null, "Usuário autorizado com sucesso.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao autorizar acesso: " + e.getMessage());
        }
    }

    private void exibirMenu() {
        StringBuilder menu = new StringBuilder();
        menu.append("Escolha uma opção:\n");
        menu.append("1. Visualizar atendimentos\n");
        menu.append("2. Autorizar acesso de usuário\n");
        menu.append("3. Visualizar usuários autorizados\n");
        menu.append("4. Cadastrar novo usuário\n");
        menu.append("5. Excluir membro da equipe\n");
        menu.append("0. Sair\n");
        menu.append("Opção: ");

        while (true) {
            try {
                int opcao = Integer.parseInt(JOptionPane.showInputDialog(null, menu.toString()));
                switch (opcao) {
                    case 1:
                        visualizarAtendimentos();
                        break;
                    case 2:
                        String nomeUsuario = JOptionPane.showInputDialog(null, "Digite o nome do usuário que deseja autorizar:");
                        autorizarAcesso(nomeUsuario);
                        break;
                    case 3:
                        visualizarUsuariosAutorizados();
                        break;
                    case 4:
                        cadastrarUsuario();
                        break;
                    case 5:
                        excluirDados();
                        break;
                    case 0:
                        JOptionPane.showMessageDialog(null, "Encerrando o sistema.");
                        System.exit(0);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Opção inválida. Por favor, escolha uma opção válida.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Opção inválida. Por favor, digite um número.");
            }
        }
    }

    public void executar() {
        exibirMenu();
    }

private void cadastrarUsuario() {
    try {
        String nomeUsuario = JOptionPane.showInputDialog(null, "Digite o nome do novo usuário:");
        String senhaUsuario = JOptionPane.showInputDialog(null, "Digite a senha do novo usuário:");
        String tipoUsuario = JOptionPane.showInputDialog(null, "Digite o tipo do novo usuário (gestor/funcionario):");

        if (nomeUsuario != null && !nomeUsuario.trim().isEmpty() &&
            senhaUsuario != null && !senhaUsuario.trim().isEmpty() &&
            tipoUsuario != null && (tipoUsuario.equalsIgnoreCase("gestor") || tipoUsuario.equalsIgnoreCase("funcionario"))) {

            PreparedStatement statement = conexao.prepareStatement("INSERT INTO usuarios (nome, senha, tipo) VALUES (?, ?, ?)");
            statement.setString(1, nomeUsuario);
            statement.setString(2, senhaUsuario);
            statement.setString(3, tipoUsuario.toLowerCase());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso.");
        } else {
            JOptionPane.showMessageDialog(null, "Dados inválidos. Verifique e tente novamente.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário: " + e.getMessage());
    }
}

    private void consultarDados() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM equipe");
            StringBuilder result = new StringBuilder("Lista de membros da equipe:\n");
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cargo = rs.getString("cargo");
                result.append("Nome: ").append(nome).append(", Cargo: ").append(cargo).append("\n");
            }
            JOptionPane.showMessageDialog(null, result.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar dados: " + e.getMessage());
        }
    }

    private void excluirDados() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Digite o ID do membro da equipe que deseja excluir:"));

            String sql = "DELETE FROM equipe WHERE id = ?";
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Membro da equipe excluído com sucesso.");
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum membro da equipe encontrado com o ID especificado.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir membro da equipe: " + e.getMessage());
        }
    }

    private void visualizarUsuariosAutorizados() {
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios_autorizados");
            StringBuilder result = new StringBuilder("Lista de usuários autorizados:\n");
            while (rs.next()) {
                String nome = rs.getString("nome");
                result.append("Nome: ").append(nome).append("\n");
            }
            JOptionPane.showMessageDialog(null, result.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar usuários autorizados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GestorVisualizacaoAtendimentos controle = new GestorVisualizacaoAtendimentos();
        controle.executar();
    }
}
