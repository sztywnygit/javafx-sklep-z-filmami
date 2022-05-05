package com.example.wytwornia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;


public class AdminPanelController implements Initializable {
    public Label labelNazwaFirmy;
    public Label labelFilmy;
    public Label labelUzytkownicy;
    public Label labelTytul;
    public Label labelRezyser;
    public Label labelRokProdukcji;
    public Label labelGatunek;
    public Label labelCena;
    public Label labelLogin;
    public Label labelHaslo;
    public Button buttonDodajUzytkownika;
    public Button buttonAwansujNaAdmina;
    public Button buttonZabierzAdmina;
    public Button buttonAnuluj;
    public Button buttonUsunUzytkownika;
    public Button buttonUsunFilm;
    public Button buttonZmien;
    public TextField txtFieldCompanyName;
    public TextField txtFieldTitle;
    public TextField txtFieldDirector;
    public TextField txtFieldYear;
    public TextField txtFieldGenre;
    public TextField txtFieldPrice;
    public TableView tableViewFilmyAdminPanel;
    public TableView tableViewUsersAdminPanel;
    public TableColumn<Film, String> colTitle;
    public TableColumn<Film, String> colPrice;
    public TableColumn<User, String> colLogin;
    public TableColumn<User, String> colPassword;
    public TableColumn<User, String> colAdmin;
    public TextField txtFieldLogin;
    public TextField txtFieldPassword;
    public CheckBox checkBoxAdmin;
    private ResourceBundle bundle;
    private Locale locale;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeMoviesTable();
        initializeUsersTable();
        setStyleAndLanguage();
    }

    public void initializeUsersTable(){
        colLogin.setCellValueFactory(new PropertyValueFactory<>("Login"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("Password"));
        colAdmin.setCellValueFactory(new PropertyValueFactory<>("Admin"));
        tableViewUsersAdminPanel.setItems(MainController.listaUzytkownikow);

    }

    public void initializeMoviesTable(){
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));
        tableViewFilmyAdminPanel.setItems(MainController.listaFilmow);
    }

    @FXML
    public void btnUsunFilm() throws SQLException {
        if(!tableViewFilmyAdminPanel.getSelectionModel().isEmpty()) {
        Film temp = (Film)tableViewFilmyAdminPanel.getSelectionModel().getSelectedItem();
        MainController.listaFilmow.remove(tableViewFilmyAdminPanel.getSelectionModel().getFocusedIndex());
        LoginController.connection.insertQuery("DELETE FROM `movie` WHERE `title` = \""+temp.getTitle()+"\"");
        AlertBox.display("Usunięto film "+temp.getTitle(), "Sukces");
    } else AlertBox.display("Nie wybrałeś żadnego filmu", "Błąd");
    }

    @FXML
    public void btnAnulujOnAction(ActionEvent event) {
        MainController.btnAnulujOnAction(event);
    }

   @FXML
   public void btnDodajUzytkownika(){
       String login = txtFieldLogin.getText();
       String password = txtFieldPassword.getText();
       if (login.isEmpty() || password.isEmpty()) {
           AlertBox.display("Podaj login i hasło", "Błąd");
           return;
       }
       try {
         boolean isNotEmpty = LoginController.connection.checkForResultsQuery("SELECT * from users"); // sprawdzamy czy jest w ogole jakis uzytkownik, jezeli nie to pierwszy musi byc adminem
           boolean validData = LoginController.connection.checkForResultsQuery("select login from users where login=\"" + login + "\";");
           int admin;
           if(checkBoxAdmin.isSelected() || !isNotEmpty) admin = 1; else admin = 0;
           if (!validData) { // jezeli nie ma takiego usera, to go dodaj
               LoginController.connection.insertQuery("INSERT INTO `users` (`UID`, `Login`, `Password`, `Settings`, `Wallet`, `Admin`) VALUES (NULL, '"+login+"', '"+password+"', '21', '0', '"+admin+"')");
               MainController.listaUzytkownikow.add(new User(login,password,admin));
           } else AlertBox.display("Taki użytkownik już istnieje", "Błąd");

       }
       catch (Exception e) {
           e.printStackTrace();
       }
   }

   @FXML
   public void btnUsunUzytkownika() throws SQLException {

       if(!tableViewUsersAdminPanel.getSelectionModel().isEmpty()) {
           User temp = (User)tableViewUsersAdminPanel.getSelectionModel().getSelectedItem();
           MainController.listaUzytkownikow.remove(tableViewUsersAdminPanel.getSelectionModel().getFocusedIndex());
           LoginController.connection.insertQuery("DELETE FROM `users` WHERE `Login` = \""+temp.getLogin()+"\"");
           AlertBox.display("Usunięto użytkownika "+temp.getLogin(), "Sukces");
       } else AlertBox.display("Nie wybrałeś żadnego użytkownika", "Błąd");
   }


   @FXML
   public void btnZabierzAdmina() throws SQLException {

       if(!tableViewUsersAdminPanel.getSelectionModel().isEmpty()) {
           User temp = (User)tableViewUsersAdminPanel.getSelectionModel().getSelectedItem();
           if(temp.getAdmin()!=0) {
               temp.setAdmin(0);
               MainController.listaUzytkownikow.set(tableViewUsersAdminPanel.getSelectionModel().getFocusedIndex(),temp); // UPDATE `users` SET `Admin` = \"1\" WHERE \"Login\" = \""+temp.getLogin()+"\";
               LoginController.connection.insertQuery(" UPDATE `users` SET `Admin` = '0' WHERE `Login`= \""+temp.getLogin()+"\"");
               AlertBox.display("Pomyślnie zabrano admina", "Sukces");
           } else AlertBox.display("Ten użytkownik nie ma admina", "Błąd");
       } else AlertBox.display("Nie wybrałeś żadnego użytkownika", "Błąd");

   }

    @FXML
    public void btnAwansujAdmin() throws SQLException {
        if(!tableViewUsersAdminPanel.getSelectionModel().isEmpty()) {
            User temp = (User)tableViewUsersAdminPanel.getSelectionModel().getSelectedItem();
            if(temp.getAdmin()!=1) {
                temp.setAdmin(1);
                MainController.listaUzytkownikow.set(tableViewUsersAdminPanel.getSelectionModel().getFocusedIndex(),temp); // UPDATE `users` SET `Admin` = \"1\" WHERE \"Login\" = \""+temp.getLogin()+"\";
                LoginController.connection.insertQuery(" UPDATE `users` SET `Admin` = '1' WHERE `Login`= \""+temp.getLogin()+"\"");
                AlertBox.display("Pomyślnie dodano admina", "Sukces");

            } else AlertBox.display("Ten użytkownik jest już adminem", "Błąd");
        } else AlertBox.display("Nie wybrałeś żadnego użytkownika", "Błąd");


    }


    @FXML
    public void btnDodajFilm() throws SQLException {
String title = txtFieldTitle.getText();
String director = txtFieldDirector.getText();
String year = txtFieldYear.getText();
String genre = txtFieldGenre.getText();
String price = txtFieldPrice.getText();
if(isValidYear(year) && isValidPrice(price)) { // najpierw sprawdzamy czy rok i cena sa prawidlowe
    if (LoginController.connection.checkForResultsQuery("SELECT `Title` FROM `movie` WHERE `Title`=\"" + title + " (" + year + ")\"")) { // jezeli taki film juz istnieje
        AlertBox.display("Taki film już istnieje!", "Błąd");
    } else {
        try {
        LoginController.connection.insertQuery("INSERT INTO `movie` (`ID` ,`Title` , `Director` ,`Genre` ,`Price`) VALUES (NULL, \""+title+" ("+year+")\", \""+director+"\", \""+genre+"\", \""+price+"\");");
        MainController.listaFilmow.add(new Film(title+" ("+year+")",director,genre,Float.parseFloat(price)));
    }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    }

    @FXML
    public void btnChangeCompanyName() {
        try {
            LoginController.connection.insertQuery("UPDATE `nazwafirmy` SET `NazwaFirmy` = '"+txtFieldCompanyName.getText()+"' WHERE `ID` =1;");
            AlertBox.display("Pomyślnie zmieniono nazwę firmy na: "+txtFieldCompanyName.getText()+"!","Sukces");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isValidYear(String s) {
        try{
        if (Integer.parseInt(s) >= 1895 && Integer.parseInt(s) < 2023) return true;
        else {
            AlertBox.display("Podaj prawidłowy rok", "Błąd");
            return false;
        }}
        catch (NumberFormatException e) {
            AlertBox.display("Podaj prawidłowy rok", "Błąd");
            return false;
        }
    }

    private boolean isValidPrice(String s){
                try {
                    if (Float.parseFloat(s) > 0) // sprawdzamy czy float jest dodatni
                        return true;
                } catch (NumberFormatException e) { // jezeli dostaniemy number format exception to zwracamy false
                    AlertBox.display("Podaj prawidłową cenę", "Błąd");
                    return false;
                }
                AlertBox.display("Podaj prawidłową cenę", "Błąd");
                return false; // false jezeli float bedzie ujemny
            }


    private void setLang(ResourceBundle bundle){
        labelNazwaFirmy.setText(bundle.getString("adminCompanyName"));
        labelFilmy.setText(bundle.getString("adminMovies"));
        labelUzytkownicy.setText(bundle.getString("adminUsers"));
        colTitle.setText(bundle.getString("adminColTitle"));
        colPrice.setText(bundle.getString("adminColPrice"));
        colLogin.setText(bundle.getString("adminColLogin"));
        colPassword.setText(bundle.getString("adminColPassword"));
        colAdmin.setText(bundle.getString("adminColAdmin"));
        buttonUsunFilm.setText(bundle.getString("adminDeleteMovie"));
        labelTytul.setText(bundle.getString("adminTitle"));
        labelRezyser.setText(bundle.getString("adminDirector"));
        labelRokProdukcji.setText(bundle.getString("adminYear"));
        labelGatunek.setText(bundle.getString("adminGenre"));
        labelCena.setText(bundle.getString("adminPrice"));
        buttonAwansujNaAdmina.setText(bundle.getString("adminPromoteToAdmin"));
        buttonZabierzAdmina.setText(bundle.getString("adminTakeAdminOff"));
        buttonUsunUzytkownika.setText(bundle.getString("adminDeleteUser"));
        labelLogin.setText(bundle.getString("adminLoginField"));
        labelHaslo.setText(bundle.getString("adminPasswordField"));
        checkBoxAdmin.setText(bundle.getString("adminAdminRights"));
        buttonDodajUzytkownika.setText(bundle.getString("adminAddUser"));
        buttonAnuluj.setText(bundle.getString("walletCancel"));
        buttonZmien.setText(bundle.getString("adminChange"));


    }

    private void setStyleAndLanguage(){
        switch(LoginController.user.getSettings()){
            case 11:
                //  white theme + pl
                locale = new Locale("pl");
                bundle = ResourceBundle.getBundle("com.example.wytwornia.lang", locale);
                setLang(bundle);
                break;
            case 12:
                // white theme + en
                locale = new Locale("en");
                bundle = ResourceBundle.getBundle("com.example.wytwornia.lang", locale);
                setLang(bundle);
                break;
            case 21:
                // dark theme + pl
                locale = new Locale("pl");
                bundle = ResourceBundle.getBundle("com.example.wytwornia.lang", locale);
                setLang(bundle);
                break;
            case 22:
                // dark theme + en
                locale = new Locale("en");
                bundle = ResourceBundle.getBundle("com.example.wytwornia.lang", locale);
                setLang(bundle);
                break;
        }
    }

}
