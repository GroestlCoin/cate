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
import GRSX.utils.ContactsSaveManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.bitcoinj.wallet.Wallet;

import static com.google.common.base.Preconditions.checkState;

public class AddContactController {
    public Button sendBtn;
    public Button cancelBtn;
    public TextField address;
    public TextField amountEdit;
    public Label btcLabel;
    public Label addLabel;

    public Main.OverlayUI overlayUI;

    private Wallet.SendResult sendResult;

    // Called by FXMLLoader
    public void initialize() {
        String selectedAddress = ContactsController.contactsList.get(ContactsController.indexToInsertNewContact).getAddress();
        String selectedName = ContactsController.contactsList.get(ContactsController.indexToInsertNewContact).getName();

        if(selectedAddress.equals("Empty"))
        {
            addLabel.setText("Add Contact");
        }
        else
        {
            addLabel.setText("Edit Contact");
            amountEdit.setText(selectedName);
            address.setText(selectedAddress);
            sendBtn.setText("Confirm");
        }

        new GroestlcoinAddressValidator(Main.params, address, sendBtn);
    }

    public void cancel(ActionEvent event) {
        Main.instance.overlayUI("contacts.fxml");
        ContactsController.currentMode = 0;
    }

    public void add(ActionEvent event) {
        Main.instance.overlayUI("contacts.fxml");
        ContactsController.currentMode = 0;
        ContactsController.contactsList.set(ContactsController.indexToInsertNewContact, new Contact(amountEdit.getText(), address.getText()));
        ContactsSaveManager contactsSave = new ContactsSaveManager(ContactsSaveManager.contactsSaveFileName);
        contactsSave.saveContacts();
    }
}
