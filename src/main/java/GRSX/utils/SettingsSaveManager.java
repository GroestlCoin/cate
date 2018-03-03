package GRSX.utils;

import GRSX.Contact;
import GRSX.ContactsController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class SettingsSaveManager {

    public static boolean useTestnet;

    public static final String testnet = "testnet";
    public static final String saveFile = "Vortex.properties";

    private String saveFileName = "";
    private Properties props = null;

    public SettingsSaveManager(String fileName)
    {
        this.saveFileName = fileName;
        loadProperties();
    }

    public void loadSettings()
    {
        if(getData(testnet) == null)
        {
            setData(testnet, "false");
            saveProperties();
        }

        useTestnet = Boolean.parseBoolean(getData(testnet));
        saveProperties();
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