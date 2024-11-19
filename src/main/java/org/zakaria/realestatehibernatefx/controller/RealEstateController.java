package org.zakaria.realestatehibernatefx.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.zakaria.realestatehibernatefx.RealEstateApplication;
import org.zakaria.realestatehibernatefx.model.RealEstate;
import org.zakaria.realestatehibernatefx.repositories.RealEstateDao;
import org.zakaria.realestatehibernatefx.utility.HibernateUtil;
import org.zakaria.realestatehibernatefx.viewmodel.RealEstateViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Real Estate Properties.
 */
public class RealEstateController {

    // --- UI Components ---

    // Menu Items
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem addPropertyMenuItem;

    // TabPane
    @FXML
    private TabPane mainTabPane;

    // Properties Tab Components
    @FXML
    private TextField generalFilterField;
    @FXML
    private TextField filterOwnerField;
    @FXML
    private TextField filterAddressField;
    @FXML
    private TextField filterMinPriceField;
    @FXML
    private TextField filterMaxPriceField;

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
    private Button addPropertyButton;

    @FXML
    private TableView<RealEstateViewModel> propertyTable;
    @FXML
    private TableColumn<RealEstateViewModel, Number> idColumn;
    @FXML
    private TableColumn<RealEstateViewModel, String> ownerColumn;
    @FXML
    private TableColumn<RealEstateViewModel, String> realEstateNameColumn;
    @FXML
    private TableColumn<RealEstateViewModel, String> addressColumn;
    @FXML
    private TableColumn<RealEstateViewModel, Number> numberOfRoomsColumn;
    @FXML
    private TableColumn<RealEstateViewModel, Number> priceColumn;

    @FXML
    private Pagination pagination;

    // --- Data Models ---
    private ObservableList<RealEstateViewModel> propertyList = FXCollections.observableArrayList();

    private FilteredList<RealEstateViewModel> filteredData;
    private SortedList<RealEstateViewModel> sortedData;

    private static final int ROWS_PER_PAGE = 20;

    private RealEstateDao realEstateDao; // Data Access Object

    // --- Initialization ---
    @FXML
    private void initialize() {
        realEstateDao = new RealEstateDao();
        loadProperties();

        // Initialize Table Columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        ownerColumn.setCellValueFactory(cellData -> cellData.getValue().ownerProperty());
        realEstateNameColumn.setCellValueFactory(cellData -> cellData.getValue().realEstateNameProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        numberOfRoomsColumn.setCellValueFactory(cellData -> cellData.getValue().numberOfRoomsProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        // Setup Filtered and Sorted Lists
        filteredData = new FilteredList<>(propertyList, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(propertyTable.comparatorProperty());
        propertyTable.setItems(sortedData);

        // Add Listeners for Filters
        addFilterListeners();

        // Setup Row Factory for Property Table
        propertyTable.setRowFactory(tv -> {
            TableRow<RealEstateViewModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    RealEstateViewModel clickedProperty = row.getItem();
                    openEditWindow(clickedProperty);
                }
            });

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem editItem = new MenuItem("Edit");
                    editItem.setOnAction(e -> openEditWindow(row.getItem()));
                    MenuItem deleteItem = new MenuItem("Delete");
                    deleteItem.setOnAction(e -> deleteProperty(row.getItem()));
                    contextMenu.getItems().addAll(editItem, deleteItem);
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });

        // Setup Pagination
        setupPagination();
    }

    // --- Data Loading Methods ---

    /**
     * Loads all properties from the database into the property list as ViewModels.
     */
    private void loadProperties() {
        propertyList.clear();
        try {
            List<RealEstate> properties = realEstateDao.getAllRealEstates();
            if (properties != null) {
                properties.stream()
                        .map(RealEstateViewModel::new)
                        .forEach(propertyList::add);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load properties: " + e.getMessage());
        }
    }

    // --- Filter Methods ---

    /**
     * Adds listeners to the filter input fields to update the FilteredList predicate.
     */
    private void addFilterListeners() {
        generalFilterField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        filterOwnerField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        filterAddressField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        filterMinPriceField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        filterMaxPriceField.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
    }

    /**
     * Updates the predicate of the FilteredList based on filter input fields.
     */
    private void updateFilters() {
        filteredData.setPredicate(property -> {
            if (property == null) {
                return false;
            }

            // General Filter
            String generalFilter = generalFilterField.getText().toLowerCase().trim();
            if (!generalFilter.isEmpty()) {
                boolean matches = property.ownerProperty().get().toLowerCase().contains(generalFilter) ||
                        property.addressProperty().get().toLowerCase().contains(generalFilter) ||
                        property.realEstateNameProperty().get().toLowerCase().contains(generalFilter) ||
                        String.valueOf(property.priceProperty().get()).contains(generalFilter) ||
                        String.valueOf(property.numberOfRoomsProperty().get()).contains(generalFilter);
                if (!matches) {
                    return false;
                }
            }

            // Owner Filter
            String ownerFilter = filterOwnerField.getText().toLowerCase().trim();
            if (!ownerFilter.isEmpty()) {
                if (!property.ownerProperty().get().toLowerCase().contains(ownerFilter)) {
                    return false;
                }
            }

            // Address Filter
            String addressFilter = filterAddressField.getText().toLowerCase().trim();
            if (!addressFilter.isEmpty()) {
                if (!property.addressProperty().get().toLowerCase().contains(addressFilter)) {
                    return false;
                }
            }

            // Min Price Filter
            String minPriceText = filterMinPriceField.getText().trim();
            double minPrice = 0.0;
            if (!minPriceText.isEmpty()) {
                try {
                    minPrice = Double.parseDouble(minPriceText);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (property.priceProperty().get() < minPrice) {
                    return false;
                }
            }

            // Max Price Filter
            String maxPriceText = filterMaxPriceField.getText().trim();
            double maxPrice = Double.MAX_VALUE;
            if (!maxPriceText.isEmpty()) {
                try {
                    maxPrice = Double.parseDouble(maxPriceText);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (property.priceProperty().get() > maxPrice) {
                    return false;
                }
            }

            return true;
        });

        setupPagination();
    }

    // --- Pagination Setup ---

    /**
     * Sets up the pagination control for the property table based on the filtered and sorted data.
     */
    private void setupPagination() {
        int totalProperties = filteredData.size();
        int pageCount = (int) Math.ceil((double) totalProperties / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    /**
     * Creates a page for the pagination control.
     *
     * @param pageIndex the index of the page to create
     * @return a VBox containing the table of properties
     */
    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, sortedData.size());
        propertyTable.setItems(FXCollections.observableArrayList(sortedData.subList(fromIndex, toIndex)));
        return new VBox(propertyTable);
    }

    // --- Event Handlers ---

    /**
     * Handles the action of adding a new property.
     *
     * @param event the action event
     */
    @FXML
    private void handleAddProperty(ActionEvent event) {
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

        // Create new RealEstateViewModel
        RealEstateViewModel newPropertyVM = new RealEstateViewModel(new RealEstate());
        newPropertyVM.realEstateNameProperty().set(realEstateName);
        newPropertyVM.addressProperty().set(address);
        newPropertyVM.numberOfRoomsProperty().set(numberOfRooms);
        newPropertyVM.priceProperty().set(price);
        newPropertyVM.ownerProperty().set(owner);

        // Convert ViewModel to Entity
        RealEstate newPropertyEntity = newPropertyVM.toEntity();

        // Save to database
        try {
            boolean success = realEstateDao.saveRealEstate(newPropertyEntity);
            if (success) {
                // Reload properties to include the new entry
                loadProperties();
                updateFilters();
                clearPropertyInputFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Property added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Add Error", "Failed to add property.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Add Error", "Failed to add property: " + e.getMessage());
        }
    }

    /**
     * Clears the input fields in the property form.
     */
    private void clearPropertyInputFields() {
        ownerField.clear();
        realEstateNameField.clear();
        addressField.clear();
        numberOfRoomsField.clear();
        priceField.clear();
    }

    /**
     * Opens the edit window for a selected property.
     *
     * @param propertyVM the property ViewModel to edit
     */
    private void openEditWindow(RealEstateViewModel propertyVM) {
        try {
            FXMLLoader loader = new FXMLLoader(RealEstateApplication.class.getResource("real-estate-edit.fxml"));
            Parent root = loader.load();

            RealEstateEditController controller = loader.getController();
            controller.setPropertyViewModel(propertyVM);
            controller.setRealEstateController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Property");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(propertyTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // After editing, refresh data
            loadProperties();
            updateFilters();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load Edit Property window: " + e.getMessage());
        }
    }

    /**
     * Deletes a selected property.
     *
     * @param propertyVM the property ViewModel to delete
     */
    private void deleteProperty(RealEstateViewModel propertyVM) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this property?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Convert ViewModel to Entity
                RealEstate propertyEntity = propertyVM.toEntity();
                boolean success = realEstateDao.deleteRealEstate(propertyEntity);
                if (success) {
                    loadProperties();
                    updateFilters();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Property deleted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Error", "Failed to delete property.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Delete Error", "Failed to delete property: " + e.getMessage());
            }
        }
    }

    /**
     * Handles the exit action from the menu.
     *
     * @param event the action event
     */
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    // --- Helper Methods ---

    /**
     * Shows an alert dialog.
     *
     * @param type    the type of alert
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refreshes the property table data.
     */
    public void refreshData() {
        loadProperties();
        updateFilters();
    }
}
