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


package db;

import client.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JDBC {
    // Properties
    private static java.sql.Connection connection = null;
    private java.sql.PreparedStatement preparedStatement = null;
    private java.sql.ResultSet result = null;
    private String SELECTALL = "SELECT * FROM user";
    private String username;
    private String password;
    java.sql.Statement statement = null;
    java.sql.ResultSet resultSet = null;  
    List<User> users = null;
    
    /**
     * JDBC constructor
     */
    public JDBC(){
        try {
            connection = DatabaseConnection.getDatabaseConnection().getConnection();
        } catch (ClassNotFoundException classNotFoundException) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, classNotFoundException);
        } catch (SQLException sQLException) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, sQLException);
        }
        
        try {
            preparedStatement = connection.prepareStatement(SELECTALL);
            resultSet = preparedStatement.executeQuery();
            User user;
            this.users = new ArrayList<>();
            System.out.println("Start to get data from database ...");
            while(resultSet.next()){
                user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                System.out.println(user.toString());
                users.add(user);
            }
            System.out.println("Finish get data from database.");
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadData(){
        try {
            preparedStatement = connection.prepareStatement(SELECTALL);
            resultSet = preparedStatement.executeQuery();
            User user;
            this.users.clear();
            System.out.println("Start to get data from database ...");
            while(resultSet.next()){
                user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                System.out.println(user.toString());
                users.add(user);
            }
            System.out.println("Finish get data from database.");
        } catch (SQLException ex) {
            Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     */
    public void closeConnection(){
        try {
            resultSet.close();
            statement.close();
            connection.close();
	} catch (SQLException sQLException) {
            // TODO Auto-generated catch block
            sQLException.printStackTrace();
	}        
    }
    
    /**
     * insert function
     * @param username
     * @param password 
     */
    public void insert(String username, String password){
        if (username != null && password != null){
            if(username.length() > 0 && password.length() > 0){
                String SQL = "INSERT INTO user VALUE(?,?)";
                String recoverySQL = "INSERT INTO recovery_user VALUE(?,?)";
                try {
                    // insert into user table
                    preparedStatement = connection.prepareStatement(SQL);
                    String hashedPassword = new MD5().MD5Digest(password);
                    preparedStatement.setObject(1, username);
                    preparedStatement.setObject(2, hashedPassword);
                    int n = preparedStatement.executeUpdate();
                    if (n == 1){
                        System.out.println("[INSERT OBJECT] Successful insert to database.");
                    } else {
                        System.out.println("[INSERT OBJECT] Fail insert to database.");
                    }
                    
                    // insert into recovery_user table
                    preparedStatement = connection.prepareStatement(recoverySQL);
                    preparedStatement.setObject(1, username);
                    preparedStatement.setObject(2, password);
                    n = preparedStatement.executeUpdate();
                    if (n == 1){
                        System.out.println("[INSERT OBJECT] Successful insert to database.");
                    } else {
                        System.out.println("[INSERT OBJECT] Fail insert to database.");
                    }
                    
                } catch (SQLException ex) {
                    Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
