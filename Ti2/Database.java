import static spark.Spark.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Classe para gerenciar conexão com o banco de dados
public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/seuBancoDeDados";
    private static final String USER = "seuUsuario";
    private static final String PASSWORD = "suaSenha";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

// CRUD de disciplina usando Spark e PostgreSQL
public class Main {
    public static void main(String[] args) {
        port(8080); // Define a porta

        // Página inicial
        get("/", (req, res) -> "CRUD para Disciplinas com Spark e PostgreSQL");

        // Formulário HTML para criar uma nova disciplina
        get("/disciplina/new", (req, res) -> {
            return "<form method='POST' action='/disciplina'>"
                    + "Código: <input type='text' name='coddisc'><br>"
                    + "Nome: <input type='text' name='nomedisc'><br>"
                    + "Período: <input type='number' name='periododisc'><br>"
                    + "Nota: <input type='number' name='notadisc'><br>"
                    + "Aprovado: <input type='checkbox' name='aprovdisc'><br>"
                    + "<input type='submit' value='Criar'>"
                    + "</form>";
        });

        // Rota para criar uma nova disciplina (CREATE)
        post("/disciplina", (req, res) -> {
            String coddisc = req.queryParams("coddisc");
            String nomedisc = req.queryParams("nomedisc");
            int periododisc = Integer.parseInt(req.queryParams("periododisc"));
            int notadisc = Integer.parseInt(req.queryParams("notadisc"));
            boolean aprovdisc = req.queryParams("aprovdisc") != null;

            try (Connection conn = Database.connect()) {
                String sql = "INSERT INTO disciplina (coddisc, nomedisc, periododisc, notadisc, aprovdisc) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, coddisc);
                pstmt.setString(2, nomedisc);
                pstmt.setInt(3, periododisc);
                pstmt.setInt(4, notadisc);
                pstmt.setBoolean(5, aprovdisc);
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            res.redirect("/disciplinas");
            return null;
        });

        // Rota para listar todas as disciplinas (READ)
        get("/disciplinas", (req, res) -> {
            StringBuilder resultado = new StringBuilder("<h1>Lista de Disciplinas</h1>");
            resultado.append("<ul>");

            try (Connection conn = Database.connect()) {
                String sql = "SELECT * FROM disciplina";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    resultado.append("<li>");
                    resultado.append("Código: " + rs.getString("coddisc") + " - Nome: " + rs.getString("nomedisc"));
                    resultado.append(" - Período: " + rs.getInt("periododisc"));
                    resultado.append(" - Nota: " + rs.getInt("notadisc"));
                    resultado.append(" - Aprovado: " + (rs.getBoolean("aprovdisc") ? "Sim" : "Não"));
                    resultado.append(" - <a href='/disciplina/edit/" + rs.getString("coddisc") + "'>Editar</a>");
                    resultado.append(" - <a href='/disciplina/delete/" + rs.getString("coddisc") + "'>Deletar</a>");
                    resultado.append("</li>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            resultado.append("</ul>");
            return resultado.toString();
        });

        // Formulário para editar uma disciplina
        get("/disciplina/edit/:coddisc", (req, res) -> {
            String coddisc = req.params(":coddisc");
            StringBuilder form = new StringBuilder();

            try (Connection conn = Database.connect()) {
                String sql = "SELECT * FROM disciplina WHERE coddisc = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, coddisc);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    form.append("<form method='POST' action='/disciplina/update'>");
                    form.append("<input type='hidden' name='coddisc' value='" + rs.getString("coddisc") + "'><br>");
                    form.append("Nome: <input type='text' name='nomedisc' value='" + rs.getString("nomedisc") + "'><br>");
                    form.append("Período: <input type='number' name='periododisc' value='" + rs.getInt("periododisc") + "'><br>");
                    form.append("Nota: <input type='number' name='notadisc' value='" + rs.getInt("notadisc") + "'><br>");
                    form.append("Aprovado: <input type='checkbox' name='aprovdisc'" + (rs.getBoolean("aprovdisc") ? " checked" : "") + "><br>");
                    form.append("<input type='submit' value='Atualizar'>");
                    form.append("</form>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return form.toString();
        });

        // Rota para atualizar uma disciplina (UPDATE)
        post("/disciplina/update", (req, res) -> {
            String coddisc = req.queryParams("coddisc");
            String nomedisc = req.queryParams("nomedisc");
            int periododisc = Integer.parseInt(req.queryParams("periododisc"));
            int notadisc = Integer.parseInt(req.queryParams("notadisc"));
            boolean aprovdisc = req.queryParams("aprovdisc") != null;

            try (Connection conn = Database.connect()) {
                String sql = "UPDATE disciplina SET nomedisc = ?, periododisc = ?, notadisc = ?, aprovdisc = ? WHERE coddisc = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nomedisc);
                pstmt.setInt(2, periododisc);
                pstmt.setInt(3, notadisc);
                pstmt.setBoolean(4, aprovdisc);
                pstmt.setString(5, coddisc);
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            res.redirect("/disciplinas");
            return null;
        });

        // Rota para deletar uma disciplina (DELETE)
        get("/disciplina/delete/:coddisc", (req, res) -> {
            String coddisc = req.params(":coddisc");

            try (Connection conn = Database.connect()) {
                String sql = "DELETE FROM disciplina WHERE coddisc = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, coddisc);
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            res.redirect("/disciplinas");
            return null;
        });
    }
}

