package com.example.wytwornia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class KoszykController implements Initializable {
    public TableView<Film> tableViewKoszyk;
    public TableColumn<Film, String> colTitle;
    public TableColumn<Film, String> colDirector;
    public TableColumn<Film, String> colGenre;
    public TableColumn<Film, String> colPrice;
    public Label lblDoZaplatyAmount;
    public Label labelInitial;
    public Label labelPodsumowanie;
    public Label labelDoZaplaty;
    public Label labelMetodaPlatnosci;
    public Button buttonUsunZKoszyka;
    public Button buttonZaplac;
    public Button buttonAnuluj;
    public Label lblDostepneSrodki;
    public RadioButton radioPortfel;
    public RadioButton radioVisa;
    private  float sum = 0;
    private ResourceBundle bundle;
    private Locale locale;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTable();
        setSumOfPrices();
        initializeText();
        setStyleAndLanguage();
    }

    public void setSumOfPrices(){
         sum = 0;
        for(int i = 0; i<LoginController.user.koszyk.size();i++){
            sum = sum + LoginController.user.koszyk.get(i).getPrice();
        }
        sum = BigDecimal.valueOf(sum)
                .setScale(2, BigDecimal.ROUND_HALF_DOWN)
                .floatValue();
        lblDoZaplatyAmount.setText(String.valueOf(sum)+" PLN");
    }

    public void initializeTable(){
        if(LoginController.user.koszyk.size()!=0) {
            try {
                colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
                colDirector.setCellValueFactory(new PropertyValueFactory<>("Director"));
                colGenre.setCellValueFactory(new PropertyValueFactory<>("Genre"));
                colPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));
                tableViewKoszyk.setItems(LoginController.user.koszyk);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void initializeText(){
        lblDostepneSrodki.setText(LoginController.user.getWallet()+" PLN)");
    }

    @FXML
    public void btnAnulujOnAction(ActionEvent event) {
        MainController.btnAnulujOnAction(event);
    }

    @FXML
    public void btnUsunZKoszyka() {
        if(!tableViewKoszyk.getSelectionModel().isEmpty()) {
            LoginController.user.koszyk.remove(tableViewKoszyk.getSelectionModel().getSelectedItem());
            setSumOfPrices();
        }
    }@FXML
    public void btnZaplac() throws SQLException {
        if(LoginController.user.koszyk.size()!=0) {
            if (radioPortfel.isSelected()) {
                if (LoginController.user.getWallet() >= sum) {
                    float roznica = LoginController.user.getWallet() - sum;
                    LoginController.user.setWallet(roznica);
                    LoginController.connection.insertQuery("UPDATE `users` SET `Wallet` =\"" + roznica + "\" WHERE `users`.`Login` =\"" + LoginController.user.getLogin() + "\" ;");
                    LoginController.user.koszyk.clear();
                    setSumOfPrices();
                    initializeText();
                    AlertBox.display("Dziękujemy za zakupy w naszym sklepie", "Sukces");
                } else {
                    AlertBox.display("Nie masz wystarczających środków na koncie", "Błąd");
                }
            } else //nie robimy przypadkow gdy ktos wybierze karte visa albo przelewy, zakladamy ze jak to wybiera to konczy pomyslnie platnosc
                AlertBox.display("Dziękujemy za zakupy w naszym sklepie", "Sukces");
                LoginController.user.koszyk.clear();
                setSumOfPrices();
                initializeText();
        } else {
            AlertBox.display("Koszyk jest pusty", "Błąd");
        }
    }

    private void setLang(ResourceBundle bundle){
        labelInitial.setText(bundle.getString("cartLabelInitial"));
        colTitle.setText(bundle.getString("cartColTitle"));
        colDirector.setText(bundle.getString("cartColDirector"));
        colGenre.setText(bundle.getString("cartColGenre"));
        colPrice.setText(bundle.getString("cartColPrice"));
        buttonUsunZKoszyka.setText(bundle.getString("cartButtonDelete"));
        labelPodsumowanie.setText(bundle.getString("cartLabelSummary"));
        labelDoZaplaty.setText(bundle.getString("cartLabelTotal"));
        labelMetodaPlatnosci.setText(bundle.getString("cartLabelPaymentMethod"));
        radioPortfel.setText(bundle.getString("cartWallet"));
        radioVisa.setText(bundle.getString("radioVisa"));
        buttonZaplac.setText(bundle.getString("cartPay"));
        buttonAnuluj.setText(bundle.getString("walletCancel"));

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
