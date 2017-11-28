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

import javafx.scene.layout.HBox;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import GRSX.controls.BitcoinAddressValidator;
import GRSX.utils.TextFieldValidator;
import GRSX.utils.WTUtils;

import static com.google.common.base.Preconditions.checkState;
import static GRSX.utils.GuiUtils.*;

public class SendMoneyController {
    public Button sendBtn;
    public Button cancelBtn;
    public TextField address;
    public TextField amountEdit;
    public TextField feeEdit;
    public Label btcLabel;

    public Main.OverlayUI overlayUI;

    private Wallet.SendResult sendResult;

    // Called by FXMLLoader
    public void initialize() {
        Coin balance = Main.bitcoin.wallet().getBalance();
        checkState(!balance.isZero());
        new BitcoinAddressValidator(Main.params, address, sendBtn);
        new TextFieldValidator(amountEdit, text ->
                !WTUtils.didThrow(() -> checkState(Coin.parseCoin(text).compareTo(balance) <= 0)));
        amountEdit.setText(balance.toPlainString());
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }

    public void send(ActionEvent event) {
        // Address exception cannot happen as we validated it beforehand.
        try {
            Coin amount = Coin.parseCoin(amountEdit.getText());
            Address destination = Address.fromBase58(Main.params, address.getText());
            SendRequest req;

            if (amount.equals(Main.bitcoin.wallet().getBalance()))
                req = SendRequest.emptyWallet(destination);
            else
                req = SendRequest.to(destination, amount);

            sendResult = Main.bitcoin.wallet().sendCoins(req);
            Futures.addCallback(sendResult.broadcastComplete, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(Transaction result) {
                    checkGuiThread();
                    overlayUI.done();
                }

                @Override
                public void onFailure(Throwable t) {
                    // We died trying to empty the wallet.
                    crashAlert(t);
                }
            });
            sendResult.tx.getConfidence().addEventListener((tx, reason) -> {
            });
            sendBtn.setDisable(true);
            address.setDisable(true);
            ((HBox)amountEdit.getParent()).getChildren().remove(amountEdit);
            ((HBox)btcLabel.getParent()).getChildren().remove(btcLabel);
        } catch (InsufficientMoneyException e) {
            informationalAlert("Insufficient Funds",
                    "You do not have enough GRS for this transaction.");
            overlayUI.done();
        }
    }
}
