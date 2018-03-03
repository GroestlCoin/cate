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

import GRSX.utils.*;
import com.google.common.util.concurrent.*;
import javafx.scene.image.Image;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.*;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.DeterministicSeed;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import GRSX.controls.NotificationBarPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Security;

import static GRSX.utils.GuiUtils.*;

public class Main extends Application {
    public static NetworkParameters params = MainNetParams.get();
    public static final String APP_NAME = "Vortex";
    private static String WALLET_FILE_NAME = "";
    public static WalletAppKit groestlcoin;
    public static Main instance;

    private StackPane uiStack;
    private Pane mainUI;
    public MainController controller;
    public NotificationBarPane notificationBar;
    public Stage mainWindow;

    public static void main(String[] args) 
    {
        SettingsSaveManager settings = new SettingsSaveManager(SettingsSaveManager.saveFile);
        settings.loadSettings();

        if(SettingsSaveManager.useTestnet)
        {
            params = TestNet3Params.get();
        }
        else
        {
            params = MainNetParams.get();
        }

        WALLET_FILE_NAME = APP_NAME.replaceAll("[^a-zA-Z0-9.-]", "_") + "-" + params.getPaymentProtocolId();

        //this if for Tor support later on, but i cant seem to get the Tor to download the consensus.
       // CryptoRestrictions.removeCryptographyRestrictions();
        launch(args);
    }

    @Override
    public void start(Stage mainWindow) throws Exception {
        try {
            loadWindow(mainWindow);
        } catch (Throwable e) {
            GuiUtils.crashAlert(e);
            throw e;
        }
    }

    private void loadWindow(Stage mainWindow) throws IOException {
        this.mainWindow = mainWindow;
        instance = this;
        GuiUtils.handleCrashesOnThisThread();

        URL location = getClass().getResource("main.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        mainUI = loader.load();
        controller = loader.getController();
        notificationBar = new NotificationBarPane(mainUI);
        mainWindow.setTitle(APP_NAME);
        uiStack = new StackPane();
        Scene scene = new Scene(uiStack);
        TextFieldValidator.configureScene(scene);
        scene.getStylesheets().add(getClass().getResource("wallet.css").toString());
        uiStack.getChildren().add(notificationBar);
        mainWindow.setScene(scene);
        mainWindow.setResizable(false);
        //saving this for later for when i can work out .fxml scaling
        //mainWindow.setMaxWidth(Screen.getPrimary().getBounds().getWidth() / 2);
        //mainWindow.setMaxHeight(Screen.getPrimary().getBounds().getHeight() / 2);
        mainWindow.setMaxWidth(800);
        mainWindow.setMaxHeight(451);

        //using an online image to update the icon on the fly without needing to push new builds, just because i want to.
        mainWindow.getIcons().add(new Image("https://duudl3.xyz/img/vortex_wallet_logo.png"));

        BriefLogFormatter.init();

        Threading.USER_THREAD = Platform::runLater;

        setupWalletKit(null);

        if (groestlcoin.isChainFileLocked()) {
            informationalAlert("Vortex Notification", "Vortex is already running and cannot be started twice.");
            Platform.exit();
            return;
        }

        mainWindow.show();

        groestlcoin.addListener(new Service.Listener() {
            @Override
            public void failed(Service.State from, Throwable failure) {
                GuiUtils.crashAlert(failure);
            }
        }, Platform::runLater);
        groestlcoin.startAsync();
    }

    public void setupWalletKit(DeterministicSeed seed) {
        // If seed is non-null it means we are restoring from backup.
        groestlcoin = new WalletAppKit(params, new File("."), WALLET_FILE_NAME)
        {
            @Override
            protected void onSetupCompleted() {
                groestlcoin.wallet().allowSpendingUnconfirmedTransactions();
                Platform.runLater(controller::onBitcoinSetup);
            }
        };

        //for later
      //  groestlcoin.useTor();

        groestlcoin.setDownloadListener(controller.progressBarUpdater()).setBlockingStartup(false).setUserAgent(APP_NAME, "1.0");

        if (seed != null)
            groestlcoin.restoreWalletFromSeed(seed);
    }

    private Node stopClickPane = new Pane();

    public class OverlayUI<T>
    {
        public Node ui;
        public T controller;

        public OverlayUI(Node ui, T controller)
        {
            this.ui = ui;
            this.controller = controller;
        }

        public void show()
        {
            checkGuiThread();
            if (currentOverlay == null)
            {
                uiStack.getChildren().add(stopClickPane);
                uiStack.getChildren().add(ui);
                blurOut(mainUI);
                fadeIn(ui);
                zoomIn(ui);
            }
            else
            {
                explodeOut(currentOverlay.ui);
                fadeOutAndRemove(uiStack, currentOverlay.ui);
                uiStack.getChildren().add(ui);
                ui.setOpacity(0.0);
                fadeIn(ui, 100);
                zoomIn(ui, 100);
            }
            currentOverlay = this;
        }

        public void done() {
            checkGuiThread();
            if (ui == null) return;  // In the middle of being dismissed and got an extra click.
            explodeOut(ui);
            fadeOutAndRemove(uiStack, ui, stopClickPane);
            blurIn(mainUI);
            this.ui = null;
            this.controller = null;
            currentOverlay = null;
        }
    }

    private OverlayUI currentOverlay;

    //loads given .fxml file and displays it onto the Scene.
    public <T> OverlayUI<T> overlayUI(String name)
    {
        try
        {
            checkGuiThread();
            URL location = GuiUtils.getResource(name);
            FXMLLoader loader = new FXMLLoader(location);
            Pane ui = loader.load();
            T controller = loader.getController();
            OverlayUI<T> pair = new OverlayUI<T>(ui, controller);

            try
            {
                if (controller != null)
                    controller.getClass().getField("overlayUI").set(controller, pair);
            }
            catch (IllegalAccessException | NoSuchFieldException ignored)
            {
                //my program is fucking flawless so i wont need this shit tbh
                ignored.printStackTrace();
            }

            pair.show();
            return pair;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        groestlcoin.stopAsync();
        groestlcoin.awaitTerminated();
        System.exit(0);
    }
}
