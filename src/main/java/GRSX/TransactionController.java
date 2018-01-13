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

import GRSX.controls.GroestlcoinAddressValidator;
import GRSX.utils.TextFieldValidator;
import GRSX.utils.WTUtils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.awt.*;
import java.text.DecimalFormat;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import static GRSX.utils.GuiUtils.*;
import static com.google.common.base.Preconditions.checkState;

public class TransactionController {

    public Main.OverlayUI overlayUI;
    public Label transferredAmount;
    public Label feeLabel;
    public Label dateLabel;
    public Label hashLabel;

    public void initialize() {
        Transaction tx = MainController.model.getTransactions().get(MainController.transactionSelected);

        DecimalFormat decimalFormatter = new DecimalFormat("0.00000000");
        String receivedValueStr = MonetaryFormat.BTC.format(tx.getValue(Main.groestlcoin.wallet())).toString();
        receivedValueStr = receivedValueStr.replace("GRS ", "");
        float amtTransferred = Float.parseFloat(receivedValueStr);
        transferredAmount.setText("GRS Transferred: " + decimalFormatter.format(Math.abs(amtTransferred)));

        if(tx.getFee() != null) {
            String feeValueStr = MonetaryFormat.BTC.format(tx.getFee()).toString();
            feeValueStr = feeValueStr.replace("GRS ", "");
            float fee = Float.parseFloat(feeValueStr);
            feeLabel.setText("Fee: " + decimalFormatter.format(fee));
        }
        else {
            feeLabel.setText("Fee: n/a");
        }

        dateLabel.setText("Tx Date: " + tx.getUpdateTime());
        hashLabel.setText("Tx Hash: " + tx.getHash());
    }

    public void close(ActionEvent event) {
        overlayUI.done();
    }

    public void view(ActionEvent event) {
        Transaction tx = MainController.model.getTransactions().get(MainController.transactionSelected);

        try {
            Desktop.getDesktop().browse(new URI("http://groestlsight.groestlcoin.org/tx/" + tx.getHash()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
