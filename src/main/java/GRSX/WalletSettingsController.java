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

import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.Service;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import GRSX.utils.TextFieldValidator;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static GRSX.utils.GuiUtils.informationalAlert;
import static GRSX.utils.WTUtils.didThrow;
import static GRSX.utils.WTUtils.unchecked;

public class WalletSettingsController {
    private static final Logger log = LoggerFactory.getLogger(WalletSettingsController.class);

    @FXML DatePicker datePicker;
    @FXML TextArea wordsArea;
    @FXML Button restoreButton;

    public Main.OverlayUI overlayUI;


    public void initialize()
    {
        DeterministicSeed seed = Main.bitcoin.wallet().getKeyChainSeed();

        Instant creationTime = Instant.ofEpochSecond(seed.getCreationTimeSeconds());
        LocalDate origDate = creationTime.atZone(ZoneId.systemDefault()).toLocalDate();
        datePicker.setValue(origDate);

        final List<String> mnemonicCode = seed.getMnemonicCode();
        checkNotNull(mnemonicCode);    // Already checked for encryption.
        String origWords = Utils.join(mnemonicCode);
        wordsArea.setText(origWords);

        MnemonicCode codec = unchecked(MnemonicCode::new);
        TextFieldValidator validator = new TextFieldValidator(wordsArea, text ->
            !didThrow(() -> codec.check(Splitter.on(' ').splitToList(text)))
        );
    }

    public void closeClicked(ActionEvent event)
    {
        overlayUI.done();
    }

    public void restoreClicked(ActionEvent event) {
        if (Main.bitcoin.wallet().getBalance().value > 0)
        {
            informationalAlert("Non-Empty Wallet Detected!",
                    "You must empty this wallet out before attempting to restore an older one, as mixing wallets " +
                            "together can lead to invalidated backups.");
            return;
        }

        overlayUI.done();
        Main.instance.controller.restoreFromSeedAnimation();

        long birthday = datePicker.getValue().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        DeterministicSeed seed = new DeterministicSeed(Splitter.on(' ').splitToList(wordsArea.getText()), null, "", birthday);
        Main.bitcoin.addListener(new Service.Listener()
        {
            @Override
            public void terminated(Service.State from)
            {
                Main.instance.setupWalletKit(seed);
                Main.bitcoin.startAsync();
            }
        }, Platform::runLater
        );

        Main.bitcoin.stopAsync();
    }

    public void openDonation(ActionEvent actionEvent) {
        try
        {
                Desktop.getDesktop().browse(new URI("http://donate.duudle.xyz"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
