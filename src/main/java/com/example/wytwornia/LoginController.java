package com.example.wytwornia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public static DatabaseConnection connection;
    public static User user; // static zeby mozna bylo uzyc tego w innej klasie
    public TextField txtFieldLogin;
    public TextField txtFieldPassword;
    public Label labelNazwaFirmy;
    public LoginController() {
        connection = new DatabaseConnection();
    }

    private void setUser(String login)  { // tworzy obiekt klasy User z danymi pobranymi z bazy
        try {
            user = connection.znajdzUzytkownika(login);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
            initializeText();
    }

    private void initializeText()  {
        try {
            labelNazwaFirmy.setText(connection.returnNazwaFirmy("select * from nazwafirmy"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void zaloguj(ActionEvent event) {
        String login = txtFieldLogin.getText();
        String password = txtFieldPassword.getText();
        try {
            boolean validData = connection.checkForResultsQuery("select Login, Password from users where login=\"" + login + "\" and Password=\"" + password + "\";");
            if (validData) { // jezeli connection.query zwroci true, czyli znaleziono takiego usera z takim haslem to przelacz scene na mainScene.fxml
                setUser(login); // przypisanie obiektowi user Klasy User, utworzonemu na samej gorze (line 26) danych z poprawnie zalogowanego uzytkownika
                Parent parent = FXMLLoader.load(getClass().getResource("mainScene.fxml"));
                Scene scene = new Scene(parent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            } else AlertBox.display("Nieprawidłowy login lub hasło", "Błąd");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void zarejestruj() {
        String login = txtFieldLogin.getText();
        String password = txtFieldPassword.getText();
        if (login.isEmpty() || password.isEmpty()) {
            AlertBox.display("Podaj login i hasło", "Błąd");
            return;
        }
        try {
            boolean isNotEmpty = connection.checkForResultsQuery("SELECT * from users"); // sprawdzamy czy jest w ogole jakis uzytkownik, jezeli nie to pierwszy musi byc adminem
            boolean validData = connection.checkForResultsQuery("select login from users where login=\"" + login + "\";");
            int admin;
            if(!isNotEmpty) admin = 1; else admin = 0; // jezeli nie bedzie uzytkownika to ustaw admin na 1, a jezeli bedzie to na 0
            if (!validData) { // jezeli nie ma takiego usera, to go dodaj
                connection.insertQuery("INSERT INTO `users` (`UID`, `Login`, `Password`, `Settings`, `Wallet`, `Admin`) VALUES (NULL, '" + login + "', '" + password + "', '21', '0', '"+admin+"')");
            } else AlertBox.display("Taki użytkownik już istnieje", "Błąd");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
