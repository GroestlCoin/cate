package GRSX.utils;

import GRSX.Contact;
import GRSX.ContactsController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class ContactsSaveManager {
    public static int contactListLength = 7;

    public static String[] contactNames = new String[7];
    public static String[] contactAddresses = new String[7];

    public static final String name = "contact_name_";
    public static final String address = "contact_address_";
    public static final String contactsSaveFileName = "VortexContacts.properties";

    private String saveFileName = "";
    private Properties props = null;

    public ContactsSaveManager(String fileName)
    {
        this.saveFileName = fileName;
        loadProperties();
    }

    public void saveContacts()
    {
        for(int x = 0; x < contactListLength; x++)
        {
            contactNames[x] = ContactsController.contactsList.get(x).getName();
            contactAddresses[x] = ContactsController.contactsList.get(x).getAddress();
            setData(ContactsSaveManager.name + x, contactNames[x]);
            setData(ContactsSaveManager.address + x, contactAddresses[x]);
        }

        saveProperties();
    }

    public void loadContacts()
    {
        for(int x = 0; x < contactListLength; x++) {
            if (getData(ContactsSaveManager.name + x) != null) {
                contactNames[x] = getData(ContactsSaveManager.name + x);
                contactAddresses[x] = getData(ContactsSaveManager.address + x);
                ContactsController.contactsList.set(x, new Contact(contactNames[x], contactAddresses[x]));
            }

            /**
             i only have this here and called each time because im lazy to do a check outside the for loop by writing
             ContactsSaveManager.name + "0" != null so i moved the saving mechanic below into the for loop and i just realized
             i spent all this time writing this paragraph explaining why i cut corners when i spent more time writing this than
             i could have spent actually writing a good program.
             **/
            // saveContacts();
            //  saveProperties();
        }

        //as you can see, i became less lazy and moved it to here. mainly because when loading the contacts popup each time, it kinda lagged a bit.
        if (getData(ContactsSaveManager.name + "0") != null) {
            saveContacts();
            saveProperties();
        }
    }

    public void setData(String prop, String data) {
        props.setProperty(prop, data);
        saveProperties();
    }

    public String getData(String prop) {
        return props.getProperty(prop);
    }

    public Properties getPropsFile()
    {
        return props;
    }

    public void loadProperties() {
        if (props==null) {
            props=new Properties();
            FileInputStream in;

            try {
                File saveFolder = new File("data/");
                saveFolder.mkdir();
                in = new FileInputStream("data/" + saveFileName);
                props.load(in);
                in.close();
            } catch (FileNotFoundException ex) {
                saveProperties();
            } catch (IOException e) {
                // Handle the IOException.
            }
        }
    }

    public void saveProperties() {
        try {
            File saveFolder = new File("data/");
            saveFolder.mkdir();
            FileOutputStream out = new FileOutputStream("data/" + saveFileName);
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException.
        } catch (IOException e) {
            // Handle the IOException.
        }
    }

}