/*
MIT License

Copyright (c) 2020 Who Write Code

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


package dbController;

import db.DatabaseConnection;
import db.MD5;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Le Nhut Nam
 */
public class LoginController {
    public static boolean Login(String username, String password) throws SQLException, ClassNotFoundException {
        String usernameHashed = new MD5().MD5Digest(username);
        System.out.println(usernameHashed);
        String passwordHased = new MD5().MD5Digest(password);
        System.out.println(passwordHased);
        String SQL = "SELECT * FROM user WHERE username=? AND password=?";
        Connection connection = DatabaseConnection.getDatabaseConnection().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        preparedStatement.setObject(1, usernameHashed);
        preparedStatement.setObject(2, passwordHased);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            if(resultSet.getString(1).equals(usernameHashed) && resultSet.getString(2).equals(passwordHased)){
                return true;
            }
        }
        return false;
    }
}
