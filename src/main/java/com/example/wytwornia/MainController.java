package com.example.wytwornia;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.security.spec.ECField;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;




public  class MainController  implements Initializable {
    public Button btnFilmy;
    public Button btnKoszyk;
    public Button btnUstawienia;
    public Button btnAdminPanel;
    public Button btnWyloguj;
    public Button btnDodajDoKoszyka;
    public Pane paneFilmy;
    public Pane paneUstawienia;
    public Label labelMainPortfelAmount;
    public Label labelLogin = new Label();
    public Label labelNazwaFirmy;
    public Label labelPortfel;
    public Label labelTrybWyswietlania;
    public Label labelJezyk;
    public RadioButton radioJasny;
    public RadioButton radioCiemny;
    public RadioButton radioPolski;
    public RadioButton radioAngielski;
    public TableView<Film> mainFilmyTable;
    public TableColumn<Film, String> colTitle;
    public TableColumn<Film, String> colDirector;
    public TableColumn<Film, String> colGenre;
    public TableColumn<Film, String> colPrice;
    public AnchorPane mainRootPane;
    public TextField searchbar;
    public static ObservableList<Film> listaFilmow = FXCollections.observableArrayList();
    public static ObservableList<User> listaUzytkownikow = FXCollections.observableArrayList(); //lista uzytkownikow tutaj, bo w admin panelu by sie za kazdym razem inicjalizowala przy odpaleniu
    public String fileMenuName;
    private ResourceBundle bundle;
    private Locale locale;



    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        adminVisibility();
        initializeText();
       initializeTable();
       initializeUsers();
       setStyleAndLanguage();

    }



    private void adminVisibility(){
        if(LoginController.user.getAdmin() == 1)  btnAdminPanel.setVisible(true);   // przyznanie adminowi dostepu do "admin panel"
        else btnAdminPanel.setVisible(false);
    }

    private void initializeText() {
        switch(LoginController.user.getSettings()){
            case 11:
                radioPolski.setSelected(true);
                radioJasny.setSelected(true);
                break;
            case 12:
                radioAngielski.setSelected(true);
                radioJasny.setSelected(true);
                break;
            case 21:
                radioPolski.setSelected(true);
                radioCiemny.setSelected(true);
                break;
            case 22:
                radioAngielski.setSelected(true);
                radioCiemny.setSelected(true);
                break;
        }
        labelLogin.setText(LoginController.user.getLogin()); // ustawienie loginu w lewym gornym na login obiektu user
        labelMainPortfelAmount.setText(LoginController.user.getWallet() + " PLN"); // wartosc portfela z obiektu user
        try {
            labelNazwaFirmy.setText(LoginController.connection.returnNazwaFirmy("select * from nazwafirmy")); // ustawienie nazwy firmy na gorze na nazwe firmy z bazy, przyda sie przy zmianach nazy firmy
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        listaFilmow.clear();
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        colDirector.setCellValueFactory(new PropertyValueFactory<>("Director"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("Genre"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));

        try {

            Object[] filmy = LoginController.connection.returnFilmy();
            String[] titleArray = (String[]) filmy[0];
            String[] directorArray = (String[]) filmy[1];
            String[] genreArray = (String[]) filmy[2];
            float[] priceArray = (float[]) filmy[3];
            for (int i = 0; i < titleArray.length; i++){
                listaFilmow.add(new Film(titleArray[i],directorArray[i],genreArray[i],priceArray[i]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mainFilmyTable.setItems(listaFilmow);

        //Wyszukiwanie

        FilteredList<Film> filteredList = new FilteredList<>(listaFilmow, b->true);
        searchbar.textProperty().addListener((observable, oldValue, newValue)-> {
           filteredList.setPredicate(filmSearch -> {
               if(newValue.isEmpty() || newValue.isBlank() || newValue==null){
                   return true;
               }
               String searchKeyword = newValue.toLowerCase();
               if(filmSearch.getTitle().toLowerCase().indexOf(searchKeyword)!=-1){
                   return true;
               } else
                   return false;

           });
        });
        SortedList<Film> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(mainFilmyTable.comparatorProperty());
        mainFilmyTable.setItems(sortedData);

}

    @FXML
    private void btnUstawieniaAction() {
        paneFilmy.setVisible(false);
        paneUstawienia.setVisible(true);

    }

    private void newWindow(String file) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Parent root = FXMLLoader.load(getClass().getResource(file));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
}

    @FXML
    private void openPortfel() {
        try {
            newWindow("portfel.fxml");
        }
        catch (Exception e ) {
            e.printStackTrace();
        }

    }

    @FXML
    private void btnFilmyonAction() {
        paneUstawienia.setVisible(false);
        paneFilmy.setVisible(true);
    }

    @FXML
    private void openAdminPanel(){
        try {
            newWindow("adminPanel.fxml");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public static void btnAnulujOnAction(ActionEvent event) {
        Window window =   ((Node)(event.getSource())).getScene().getWindow();
        if (window instanceof Stage){
            ((Stage) window).close();
        }
    }

    @FXML
    private void updateInfo(){
        this.labelMainPortfelAmount.setText(LoginController.user.getWallet()+" PLN");
    }

    @FXML
    private void openKoszyk() throws IOException {
       newWindow("koszyk.fxml");
    }

    @FXML
    private void changeSceneToLogin(ActionEvent event) {
        try {
            Parent tableViewParent = FXMLLoader.load(getClass().getResource("loginScene.fxml"));
            Scene tableViewScene = new Scene(tableViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(tableViewScene);
            window.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnDodajDoKoszyka(){
        if(mainFilmyTable.getSelectionModel().getSelectedItem()!=null) {
            LoginController.user.koszyk.add(mainFilmyTable.getSelectionModel().getSelectedItem());
            AlertBox.display("Dodano film do koszyka","Sukces");
        }
        else
            AlertBox.display("Nie wybrano filmu", "Błąd");
    }

    private void setLang(ResourceBundle bundle){
        btnFilmy.setText(bundle.getString("buttonMovies"));
        btnKoszyk.setText(bundle.getString("buttonCart"));
        btnAdminPanel.setText(bundle.getString("buttonAdminPanel"));
        btnWyloguj.setText(bundle.getString("buttonLogOut"));
        btnUstawienia.setText(bundle.getString("buttonSettings"));
        btnDodajDoKoszyka.setText(bundle.getString("buttonAddToCart"));
        colTitle.setText(bundle.getString("colTitle"));
        colDirector.setText(bundle.getString("colDirector"));
        colGenre.setText(bundle.getString("colGenre"));
        colPrice.setText(bundle.getString("colPrice"));
        searchbar.setPromptText(bundle.getString("textFieldSearchBar"));
        labelPortfel.setText(bundle.getString("labelWallet"));
        labelJezyk.setText(bundle.getString("labelLanguage"));
        labelTrybWyswietlania.setText(bundle.getString("labelTheme"));
        radioJasny.setText(bundle.getString("radioButtonWhite"));
        radioCiemny.setText(bundle.getString("radioButtonDark"));
        radioPolski.setText(bundle.getString("radioButtonPolish"));
        radioAngielski.setText(bundle.getString("radioButtonEnglish"));

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

    @FXML
    public void polishChosen() throws SQLException {
        if(LoginController.user.getSettings()==12){
            LoginController.user.setSettings(11);
            LoginController.connection.insertQuery("UPDATE `users` SET `Settings` = '11' WHERE `Login` = \""+LoginController.user.getLogin()+"\";");
            AlertBox.display("Zmieniono język na polski, zresetuj aplikację aby zastosować zmiany!","Sukces");
        }
        if(LoginController.user.getSettings()==22) {
            LoginController.user.setSettings(21);
            LoginController.connection.insertQuery("UPDATE `users` SET `Settings` = '21' WHERE `Login` = \""+LoginController.user.getLogin()+"\";");
            AlertBox.display("Zmieniono język na polski, zresetuj aplikację aby zastosować zmiany!","Sukces");
        }
    }

    @FXML
    public void englishChosen() throws SQLException {
        if(LoginController.user.getSettings()==11){
            LoginController.user.setSettings(12);
            LoginController.connection.insertQuery("UPDATE `users` SET `Settings` = '12' WHERE `Login` = \""+LoginController.user.getLogin()+"\";");
            AlertBox.display("Language set to english, please restart the application for the changes to apply","Success");
        }
        if(LoginController.user.getSettings()==21) {
            LoginController.user.setSettings(22);
            LoginController.connection.insertQuery("UPDATE `users` SET `Settings` = '22' WHERE `Login` = \""+LoginController.user.getLogin()+"\";");
            AlertBox.display("Language set to english, please restart the application for the changes to apply","Success");
        }

    }

    private void initializeUsers() {
        if(LoginController.user.getAdmin()==1){
            listaUzytkownikow.clear();
            try {
                Object[] users = LoginController.connection.returnUsers();
                String[] loginArray = (String[]) users[0];
                String[] passwordArray = (String[]) users[1];
                Integer[] adminArray = (Integer[]) users[2];

                for (int i = 0; i < loginArray.length; i++){
                    listaUzytkownikow.add(new User(loginArray[i],passwordArray[i],adminArray[i]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}