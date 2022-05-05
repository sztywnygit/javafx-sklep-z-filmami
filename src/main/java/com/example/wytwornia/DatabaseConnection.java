package com.example.wytwornia;
import javafx.application.Platform;

import javax.xml.transform.Result;
import java.sql.*;

public class DatabaseConnection {

    private  Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public DatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://192.166.219.220:3306/sbas", "sbas", "sY.2bUJ.sr");     //("jdbc:mysql://localhost:3306/wytwornia_sbas", "root", "");
            statement = connection.createStatement();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        resultSet = null;

    }


    public  Object[] returnFilmy() throws SQLException { // zwracamy 4 tablice, dlatego musimy zwrocic tablice typu Object


        Statement tempStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // robimy nowy statement typu scroll insensitive, ktory nam  pozwoli sie przenosic na poczatek i koniec wynikow
        //nie uzywamy prywatnej zmiennej statement, bo to ustawi nam wszystkie nastepne statementy na scroll insensitive, i musielibysmy w kazdej metodzie robic statement = connection.createStatement();, zeby znowu byly forward only
        resultSet = tempStatement.executeQuery("SELECT * FROM `movie`");
        if (resultSet.next()) {
            resultSet.last(); // ustawiamy na ostatni element
            int rowCount = resultSet.getRow();// rowCount to numer rzędu ostatniego elementu, stad wiemy ile jest wyników
            String[] titleMovie = new String[rowCount];
            String[] directorMovie = new String[rowCount];
            String[] genreMovie = new String[rowCount];
            float[] priceMovie = new float[rowCount];
            resultSet.first(); // przesuwamy kursor znowu na poczatek


                 for (int i = 0; i < rowCount; i++) {
                    titleMovie[i] = resultSet.getString("Title");
                    directorMovie[i] = resultSet.getString("Director");
                    genreMovie[i] = resultSet.getString("Genre");
                    priceMovie[i] = resultSet.getFloat("Price");
                    resultSet.next();
            }
                 Object[] filmy = new Object[4];
                 filmy[0] = titleMovie;  // pierwszy element tablicy typu Object to tablica typu String titleMovie zawierajaca tytuly
                 filmy[1] = directorMovie; // drugi element tablicy typu Object to tablica typu String directorMovie zawierajaca rezyserow
                 filmy[2] = genreMovie; // trzeci element tablicy typu Object to tablica typu String genreMovie zawierajaca gatunek filmu
                 filmy[3] = priceMovie; // czwarty element tablicy typu Object to tablica typu Float genreMovie zawierajaca cene
            return filmy;
        }
        return null;
    }

    public Object[] returnUsers() throws SQLException {
        Statement tempStatement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE); // robimy nowy statement typu scroll insensitive, ktory nam  pozwoli sie przenosic na poczatek i koniec wynikow
        //nie uzywamy prywatnej zmiennej statement, bo to ustawi nam wszystkie nastepne statementy na scroll insensitive, i musielibysmy w kazdej metodzie robic statement = connection.createStatement();, zeby znowu byly forward only
        resultSet = tempStatement.executeQuery("SELECT * FROM `users`");
        if (resultSet.next()) {
            resultSet.last(); // ustawiamy na ostatni element
            int rowCount = resultSet.getRow();// rowCount to numer rzędu ostatniego elementu, stad wiemy ile jest wyników
            String[] userLogin = new String[rowCount];
            String[] userPassword = new String[rowCount];
            Integer[] userAdmin = new Integer[rowCount];
            resultSet.first(); // przesuwamy kursor znowu na poczatek

            for (int i = 0; i < rowCount; i++) {
                userLogin[i] = resultSet.getString("Login");
                userPassword[i] = resultSet.getString("Password");
                userAdmin[i] = resultSet.getInt("Admin");
                resultSet.next();
            }
            Object[] uzytkownicy = new Object[3];
            uzytkownicy[0] = userLogin;
            uzytkownicy[1] = userPassword;
            uzytkownicy[2] = userAdmin;

            return uzytkownicy;
        }
        return null;

    }


    public String returnNazwaFirmy(String wantedquery) throws SQLException {
         resultSet = statement.executeQuery(wantedquery);
        if(resultSet.next()) return resultSet.getString("NazwaFirmy");
        else return null;
    }

    public boolean checkForResultsQuery(String wantedquery) throws SQLException { // sprawdza czy dane zapytanie cos zwroci
     resultSet = statement.executeQuery(wantedquery);
    if(resultSet.next()) return true; // jezeli cos znajdzie -> true
    else return false; // jezeli nic nie znajdzie -> false

}
    public boolean insertQuery(String wantedquery) throws SQLException { //wykonujemy insert wantedquery , czyli wpisujemy cos do bazy
        statement.executeUpdate(wantedquery);
        return true;
    }

    public User znajdzUzytkownika(String login) throws SQLException { // znajdujemy uzytkownika, tworzymy obiekt i go zwracamy
         resultSet = statement.executeQuery("SELECT Login, Password, Settings, Wallet, Admin FROM users WHERE Login=\""+login+"\";");
        if(resultSet.next()) {
            User tempUser = new User();
            tempUser.setLogin(resultSet.getString("Login"));
            tempUser.setPassword(resultSet.getString("Password"));
            tempUser.setAdmin(resultSet.getInt("Admin"));
            tempUser.setSettings(resultSet.getInt("Settings"));
            tempUser.setWallet(resultSet.getFloat("Wallet"));
            return tempUser;
        }
        return null;
    }


}