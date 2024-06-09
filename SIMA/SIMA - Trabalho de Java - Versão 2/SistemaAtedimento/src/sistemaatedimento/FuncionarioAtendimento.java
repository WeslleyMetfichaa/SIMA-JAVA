package sistemaatedimento;


import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FuncionarioAtendimento {
    private Connection conexao;

    public FuncionarioAtendimento() {
        conectar();
    }

    private void conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/Banco";
            String user = "root";
            String password = "";
            conexao = DriverManager.getConnection(url, user, password);
            JOptionPane.showMessageDialog(null, "Conectado ao banco de dados!");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void exibirMenu() {
        String menu = "Escolha uma opção:\n"
                    + "1 - Cadastrar atendimento\n"
                    + "2 - Consultar atendimento\n"
                    + "3 - Editar atendimento\n"
                    + "4 - Excluir atendimento\n"
                    + "0 - Sair";
        
        int opcao;
        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(menu));
            switch (opcao) {
                case 1:
                    cadastrarAtendimento();
                    break;
                case 2:
                    consultarAtendimento();
                    break;
                case 3:
                    editarAtendimento();
                    break;
                case 4:
                    excluirAtendimento();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Saindo do sistema.");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }
        } while (opcao != 0);
    }

    public void cadastrarAtendimento() {
        try {
            String descricao = JOptionPane.showInputDialog(null, "Digite a descrição do atendimento:");
            if (descricao == null || descricao.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "A descrição do atendimento não pode estar vazia.");
                return;
            }
            
            String idEquipeStr = JOptionPane.showInputDialog(null, "Digite o ID da equipe responsável pelo atendimento:");
            int idEquipe;
            try {
                idEquipe = Integer.parseInt(idEquipeStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "O ID da equipe deve ser um número inteiro.");
                return;
            }
            
            String sql = "INSERT INTO atendimento (descricao, id_equipe) VALUES (?, ?)";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setString(1, descricao);
                statement.setInt(2, idEquipe);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Atendimento cadastrado com sucesso.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar atendimento: " + e.getMessage());
        }
    }

    public void consultarAtendimento() {
        try {
            String idStr = JOptionPane.showInputDialog(null, "Digite o ID do atendimento:");
            int id = Integer.parseInt(idStr);

            String sql = "SELECT * FROM atendimento WHERE id = ?";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "ID: " + resultSet.getInt("id") +
                                    "\nDescrição: " + resultSet.getString("descricao") +
                                    "\nID da Equipe: " + resultSet.getInt("id_equipe"));
                } else {
                    JOptionPane.showMessageDialog(null, "Atendimento não encontrado.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar atendimento: " + e.getMessage());
        }
    }

    public void editarAtendimento() {
        try {
            String idStr = JOptionPane.showInputDialog(null, "Digite o ID do atendimento que deseja editar:");
            int id = Integer.parseInt(idStr);

            String descricao = JOptionPane.showInputDialog(null, "Digite a nova descrição do atendimento:");
            if (descricao == null || descricao.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "A descrição do atendimento não pode estar vazia.");
                return;
            }

            String idEquipeStr = JOptionPane.showInputDialog(null, "Digite o novo ID da equipe responsável pelo atendimento:");
            int idEquipe;
            try {
                idEquipe = Integer.parseInt(idEquipeStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "O ID da equipe deve ser um número inteiro.");
                return;
            }

            String sql = "UPDATE atendimento SET descricao = ?, id_equipe = ? WHERE id = ?";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setString(1, descricao);
                statement.setInt(2, idEquipe);
                statement.setInt(3, id);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, "Atendimento atualizado com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(null, "Atendimento não encontrado.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao editar atendimento: " + e.getMessage());
        }
    }

    public void excluirAtendimento() {
        try {
            String idStr = JOptionPane.showInputDialog(null, "Digite o ID do atendimento que deseja excluir:");
            int id = Integer.parseInt(idStr);

            String sql = "DELETE FROM atendimento WHERE id = ?";
            try (PreparedStatement statement = conexao.prepareStatement(sql)) {
                statement.setInt(1, id);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Atendimento excluído com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(null, "Atendimento não encontrado.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir atendimento: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        FuncionarioAtendimento funcionario = new FuncionarioAtendimento();
        funcionario.exibirMenu();
    }
}