package GRSX;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.bitcoinj.core.Address;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class ReceiveAddressesController
{

    public ListView addresses;
    private ObservableList<Address> addressesList = FXCollections.observableArrayList();
    public Main.OverlayUI overlayUI;
    public Button close;

    public void initialize() {
        addressesList.setAll(Main.groestlcoin.wallet().getIssuedReceiveAddresses());

        Bindings.bindContent(addresses.getItems(), addressesList);

        addresses.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<Address>() {
            @Override
            public String toString(Address addr) {
                return addr.toString();
            }

            @Override
            public Address fromString(String string) {
                return null;
            }
        }));
    }

    public void close(ActionEvent event) {
        overlayUI.done();
    }

    public void handleMouseClick(MouseEvent arg0) {
        if(addresses.getSelectionModel().getSelectedItem() != null) {
            StringSelection stringSelection = new StringSelection(addresses.getSelectionModel().getSelectedItem().toString());
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        }
    }
}
