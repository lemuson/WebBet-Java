package bets.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet(urlPatterns = "/updateAccount")
public class UpdateAccountServlet extends HttpServlet
{
    String DB_URL = "jdbc:sqlserver://localhost:1433;;encrypt=false;databaseName=SportsBets";
    String DB_USER = "user";
    String DB_PASSWORD = "0000";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        resp.setContentType("text/html");

        String fullName = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        Integer userID = (Integer) req.getSession().getAttribute("userId");

        if(userID == null)
        {
            resp.sendRedirect("login");
            return;
        }

        if (fullName == null || fullName.isEmpty() || phone == null || phone.isEmpty() || email == null || email.isEmpty())
        {
            return;
        }

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD))
        {
            String updateQuery = "UPDATE Пользователи " +
                    "SET Имя = ?, НомерТелефона = ?, Почта = ? " +
                    "WHERE ID_Пользователь = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, userID);

            preparedStatement.executeUpdate();
        }
        catch (Exception e)
        {
            resp.getWriter().println("Ошибка: "+ e.getMessage());
        }
        resp.sendRedirect("account");
    }
}
