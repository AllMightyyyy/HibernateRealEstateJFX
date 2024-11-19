package org.zakaria.realestatehibernatefx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.zakaria.realestatehibernatefx.viewmodel.RealEstateViewModel;
import org.zakaria.realestatehibernatefx.repositories.RealEstateDao;
import org.zakaria.realestatehibernatefx.model.RealEstate;

/**
 * Controller class for editing Real Estate Properties.
 */
public class RealEstateEditController {

    @FXML
    private Label idLabel;
    @FXML
    private TextField ownerField;
    @FXML
    private TextField realEstateNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField numberOfRoomsField;
    @FXML
    private TextField priceField;
    @FXML
    private Button saveButton;

    private RealEstateViewModel propertyVM;
    private RealEstateController mainController;
    private RealEstateDao realEstateDao;

    /**
     * Initializes the controller by setting up the RealEstateDao.
     */
    @FXML
    private void initialize() {
        realEstateDao = new RealEstateDao();
    }

    /**
     * Sets the property ViewModel to be edited.
     *
     * @param propertyVM the RealEstateViewModel property
     */
    public void setPropertyViewModel(RealEstateViewModel propertyVM) {
        this.propertyVM = propertyVM;
        populateFields();
    }

    /**
     * Sets the main controller to allow refreshing data after edits.
     *
     * @param controller the RealEstateController
     */
    public void setRealEstateController(RealEstateController controller) {
        this.mainController = controller;
    }

    /**
     * Populates the fields with the property's current data from the ViewModel.
     */
    private void populateFields() {
        if (propertyVM != null) {
            idLabel.setText(String.valueOf(propertyVM.idProperty().get()));
            ownerField.setText(propertyVM.ownerProperty().get());
            realEstateNameField.setText(propertyVM.realEstateNameProperty().get());
            addressField.setText(propertyVM.addressProperty().get());
            numberOfRoomsField.setText(String.valueOf(propertyVM.numberOfRoomsProperty().get()));
            priceField.setText(String.valueOf(propertyVM.priceProperty().get()));
        }
    }

    /**
     * Handles the save action to update the property.
     */
    @FXML
    private void handleSave() {
        String owner = ownerField.getText().trim();
        String realEstateName = realEstateNameField.getText().trim();
        String address = addressField.getText().trim();
        String numberOfRoomsText = numberOfRoomsField.getText().trim();
        String priceText = priceField.getText().trim();

        // Validate inputs
        if (owner.isEmpty() || realEstateName.isEmpty() || address.isEmpty() ||
                numberOfRoomsText.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled.");
            return;
        }

        int numberOfRooms;
        double price;
        try {
            numberOfRooms = Integer.parseInt(numberOfRoomsText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Number of Rooms and Price must be valid numbers.");
            return;
        }

        // Update ViewModel properties
        propertyVM.ownerProperty().set(owner);
        propertyVM.realEstateNameProperty().set(realEstateName);
        propertyVM.addressProperty().set(address);
        propertyVM.numberOfRoomsProperty().set(numberOfRooms);
        propertyVM.priceProperty().set(price);

        // Convert ViewModel to Entity
        RealEstate updatedEntity = propertyVM.toEntity();

        // Save changes to database
        try {
            boolean success = realEstateDao.updateRealEstate(updatedEntity);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Property updated successfully.");
                closeWindow();
                mainController.refreshData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update property.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Update Error", "Failed to update property: " + e.getMessage());
        }
    }

    /**
     * Closes the edit window.
     */
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays an alert dialog.
     *
     * @param type    the type of alert
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        Stage stage = (Stage) saveButton.getScene().getWindow();
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
