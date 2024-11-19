package org.zakaria.realestatehibernatefx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.zakaria.realestatehibernatefx.model.RealEstate;
import org.zakaria.realestatehibernatefx.repositories.RealEstateDao;

/**
 * Main application class for the Real Estate Manager.
 */
public class RealEstateApplication extends Application {
    RealEstateDao realEstateDao = new RealEstateDao();

    @Override
    public void start(Stage stage) throws Exception {
        // Check if 'Test Property' already exists to prevent duplicate entries
        RealEstate existing = realEstateDao.getRealEstateByName("Test Property").orElse(null);
        if (existing == null) {
            RealEstate realEstate = new RealEstate();
            realEstate.setRealEstateName("Test Property");
            realEstate.setAddress("123 Test Street");
            realEstate.setOwner("Test Owner");
            realEstate.setPrice(100000.00);
            realEstate.setNumberOfRooms(3);

            boolean success = realEstateDao.saveRealEstate(realEstate);
            System.out.println("Save successful: " + success);
        } else {
            System.out.println("Test Property already exists.");
        }

        // Load the main UI from FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("real-estate-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        stage.setTitle("Real Estate Manager");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main entry point for the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
