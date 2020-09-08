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
import java.sql.ResultSetMetaData;



/**
 *
 * @author Le Nhut Nam
 */
public class RegisterController {
    // String username, String password
    public static boolean Register(String username, String password) throws SQLException, ClassNotFoundException {
        if (username != null && password != null){
            if(username.length() > 0 && password.length() > 0){
                String SQL = "SELECT * FROM user";
                Connection connection = DatabaseConnection.getDatabaseConnection().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                ResultSet resultSet = preparedStatement.executeQuery();
                ResultSetMetaData rsmd = resultSet.getMetaData();
                while (resultSet.next()){
                    if(resultSet.getString(1).equals(username)){
                        System.out.println("This username has already existed.");
                        return false;
                    }
                }                
                String insertToUser = "INSERT INTO user VALUE(?,?)";
                String insertToRecoverySQL = "INSERT INTO recovery_user VALUE(?,?)";
                try {
                // insert into user table
                    preparedStatement = connection.prepareStatement(insertToUser);
                    String hashedUsername = new MD5().MD5Digest(username);
                    String hashedPassword = new MD5().MD5Digest(password);
                    preparedStatement.setObject(1, hashedUsername);
                    preparedStatement.setObject(2, hashedPassword);
                    int n = preparedStatement.executeUpdate();
                    if (n == 1){
                        System.out.println("[INSERT OBJECT] Successful insert to database.");
                    } else {
                        System.out.println("[INSERT OBJECT] Fail insert to database.");
                        return false;
                    }
                    
                    // insert into recovery_user table
                    preparedStatement = connection.prepareStatement(insertToRecoverySQL);
                    preparedStatement.setObject(1, username);
                    preparedStatement.setObject(2, password);
                    n = preparedStatement.executeUpdate();
                    if (n == 1){
                        System.out.println("[INSERT OBJECT] Successful insert to database.");
                    } else {
                        System.out.println("[INSERT OBJECT] Fail insert to database.");
                        return false;
                    }      
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
}
