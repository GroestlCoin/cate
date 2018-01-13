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

import GRSX.utils.ContactsSaveManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bitcoinj.wallet.Wallet;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ContactActionsController {
    public Button copy;
    public Button cancelBtn;
    public Button editBtn;
    public TextField address;
    public TextField amountEdit;
    public TextField feeEdit;
    public Label btcLabel;

    public Main.OverlayUI overlayUI;

    private Wallet.SendResult sendResult;

    // Called by FXMLLoader
    public void initialize() {
        String address = ContactsController.contactsList.get(ContactsController.indexToInsertNewContact).getAddress();

        if(address.equals("Empty"))
        {
            copy.setDisable(true);
            editBtn.setText("Add");
        }
    }

    public void cancel(ActionEvent event) {
        Main.instance.overlayUI("contacts.fxml");
        ContactsController.currentMode = 0;
    }

    public void add(ActionEvent event) {
        Main.instance.overlayUI("add_contact.fxml");
    }

    public void copyAddress(ActionEvent event) {
        StringSelection stringSelection = new StringSelection(ContactsController.contactsList.get(ContactsController.indexToInsertNewContact).getAddress());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
        overlayUI.done();
    }

    public void delete(ActionEvent event) {
        Main.instance.overlayUI("contacts.fxml");
        ContactsController.currentMode = 0;
        ContactsController.contactsList.set(ContactsController.indexToInsertNewContact, new Contact("", "Empty"));
        ContactsSaveManager contactsSave = new ContactsSaveManager(ContactsSaveManager.contactsSaveFileName);
        contactsSave.saveContacts();
    }
}
