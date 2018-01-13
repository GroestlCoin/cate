/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package GRSX;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.Coin;
import org.bitcoinj.utils.MonetaryFormat;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.fxmisc.easybind.EasyBind;
import GRSX.controls.NotificationBarPane;
import GRSX.utils.GroestlcoinUIModel;
import GRSX.utils.easing.EasingMode;
import GRSX.utils.easing.ElasticInterpolator;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import static GRSX.Main.groestlcoin;

/**
 * Gets created auto-magically by FXMLLoader via reflection. The widget fields are set to the GUI controls they're named
 * after. This class handles all the updates and event handling for the main UI.
 */
public class MainController {
    public HBox controlsBox;
    public Label balance;
    public Button sendMoneyOutBtn;
    public Button receiveAddressesBtn;
    public Button contactsBtn;
    public ListView<Transaction> transactionsList;
    public Label connectionStatus;
    public static int transactionSelected;

    public static GroestlcoinUIModel model = new GroestlcoinUIModel();
    private NotificationBarPane.Item syncItem;

    // Called by FXMLLoader.
    public void initialize() {

    }

    public void onBitcoinSetup()
    {
        model.setWallet(groestlcoin.wallet());
        balance.textProperty().bind(EasyBind.map(model.balanceProperty(), coin -> MonetaryFormat.BTC.noCode().format(coin).toString()));

        //disable send  button when balance is 0.00000000, this isnt really needed, but i like it.
        sendMoneyOutBtn.disableProperty().bind(model.balanceProperty().isEqualTo(Coin.ZERO));

        model.syncProgressProperty().addListener(x -> {
            if (model.syncProgressProperty().get() >= 1.0)
            {
                connectionStatus.setText("Connected");
                System.out.println("Wallet is synced");
                readyToGoAnimation();
                if (syncItem != null)
                {
                    syncItem.cancel();
                    syncItem = null;
                }
            }
            else if (syncItem == null)
            {
                connectionStatus.setText("Syncing... " + groestlcoin.wallet().getLastBlockSeenHeight() + "/" + groestlcoin.peerGroup().getDownloadPeer().getBestHeight());
                System.out.println("Wallet is NOT synced");
            }
        });
        Bindings.bindContent(transactionsList.getItems(), model.getTransactions());

        transactionsList.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<Transaction>()
        {
            @Override
            public String toString(Transaction tx) {
                Coin value = tx.getValue(Main.groestlcoin.wallet());
                System.out.println(tx);
                DecimalFormat decimalFormatter = new DecimalFormat("0.00000000");

                if(value.isPositive())
                {
                    String receivedValueStr = MonetaryFormat.BTC.format(value).toString();
                    receivedValueStr = receivedValueStr.replace("GRS ", "");
                    float coinsTransferred = Float.parseFloat(receivedValueStr);
                    String entry = String.format( "▼ %5s GRS", decimalFormatter.format(coinsTransferred));
                    return entry;
                }

                if(value.isNegative())
                {

                    String sentValueStr = MonetaryFormat.BTC.format(value).toString();
                    sentValueStr = sentValueStr.replace("GRS -", "");
                    float coinsTransferred = Float.parseFloat(sentValueStr);
                    String entry = String.format( "▲ %5s GRS", decimalFormatter.format(coinsTransferred));
                    return entry;
                }

                return "TxID " + tx.getHash();
            }

            @Override
            public Transaction fromString(String string) {
                return null;
            }
        }));
    }

    public void sendMoneyOut(ActionEvent event) {
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
        Main.instance.overlayUI("send_money.fxml");
    }

    public void receiveMoney(ActionEvent event) {
        Main.instance.overlayUI("receive_addresses.fxml");
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
    }

    public void openContacts(ActionEvent event) {
        Main.instance.overlayUI("contacts.fxml");
        // Hide this UI and show the send money UI. This UI won't be clickable until the user dismisses send_money.
    }

    public void settingsClicked(ActionEvent event) {
        Main.OverlayUI<WalletSettingsController> screen = Main.instance.overlayUI("wallet_settings.fxml");
        screen.controller.initialize();
    }

    public void restoreFromSeedAnimation() {
        // Buttons slide out ...
        TranslateTransition leave = new TranslateTransition(Duration.millis(1200), controlsBox);
        leave.setByY(80.0);
        leave.play();
    }

    public void readyToGoAnimation() {
        // Buttons slide in and clickable address appears simultaneously.
        TranslateTransition arrive = new TranslateTransition(Duration.millis(1200), controlsBox);
        arrive.setInterpolator(new ElasticInterpolator(EasingMode.EASE_OUT, 1, 2));
        arrive.setToY(0.0);
    }

    public DownloadProgressTracker progressBarUpdater() {
        return model.getDownloadProgressTracker();
    }

    public void openTransaction(MouseEvent mouseEvent)
    {
        if(transactionsList.getSelectionModel().getSelectedItem() != null) {
            transactionSelected = transactionsList.getSelectionModel().getSelectedIndex();
            Main.instance.overlayUI("transaction.fxml");
          //  Desktop.getDesktop().browse(new URI("http://groestlsight.groestlcoin.org/tx/" + transactionsList.getSelectionModel().getSelectedItem().getHash()));
        }

    }
}
