package GRSX;

import GRSX.utils.ContactsSaveManager;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.bitcoinj.core.Address;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ContactsController
{

    public ListView contacts;
    public Label mode;
    public static ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    public Main.OverlayUI overlayUI;
    public Button close;
    //Modes: 0=viewing, 2=editing/adding, 3=deleting
    public static int currentMode;
    public static int indexToInsertNewContact;

    public void initialize() {
        contactsList = FXCollections.observableArrayList();
        for(int x = 0; x < 7; x++) {
            contactsList.add(x, new Contact("", "Empty"));
        }

        ContactsSaveManager contactsSave = new ContactsSaveManager(ContactsSaveManager.contactsSaveFileName);
        contactsSave.loadContacts();

        //  contactsSave.saveContacts();
        Bindings.bindContent(contacts.getItems(), contactsList);

        contacts.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                if(contact.getAddress().equals("Empty"))
                {
                    return "EMPTY";
                }
                else {
                    return contact.getName() + " | " + contact.getAddress();
                }
            }

            @Override
            public Contact fromString(String string) {
                return null;
            }
        }));
    }

    public void close(ActionEvent event) {
        currentMode=0;
        ContactsSaveManager contactsSave = new ContactsSaveManager(ContactsSaveManager.contactsSaveFileName);
        contactsSave.saveContacts();

        overlayUI.done();
    }

    public void handleMouseClick(MouseEvent arg0) {
        if(currentMode==0) {
            if (contacts.getSelectionModel().getSelectedItem() != null) {

                indexToInsertNewContact = contacts.getSelectionModel().getSelectedIndex();
                Main.instance.overlayUI("contactSelectionScreen.fxml");
            }
        }
    }
}
