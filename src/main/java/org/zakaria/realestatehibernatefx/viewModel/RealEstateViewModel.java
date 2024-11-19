package org.zakaria.realestatehibernatefx.viewmodel;

import javafx.beans.property.*;

import org.zakaria.realestatehibernatefx.model.RealEstate;

/**
 * ViewModel class for RealEstate entities, encapsulating JavaFX properties for UI binding.
 */
public class RealEstateViewModel {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty realEstateName = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final IntegerProperty numberOfRooms = new SimpleIntegerProperty();
    private final StringProperty owner = new SimpleStringProperty();

    /**
     * Constructs a ViewModel from a RealEstate entity.
     *
     * @param realEstate the RealEstate entity
     */
    public RealEstateViewModel(RealEstate realEstate) {
        this.id.set(realEstate.getId());
        this.realEstateName.set(realEstate.getRealEstateName());
        this.address.set(realEstate.getAddress());
        this.price.set(realEstate.getPrice());
        this.numberOfRooms.set(realEstate.getNumberOfRooms());
        this.owner.set(realEstate.getOwner());
    }

    // Getters for JavaFX properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty realEstateNameProperty() {
        return realEstateName;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty numberOfRoomsProperty() {
        return numberOfRooms;
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    /**
     * Converts the ViewModel back to a RealEstate entity.
     *
     * @return a RealEstate entity with updated values
     */
    public RealEstate toEntity() {
        RealEstate realEstate = new RealEstate();
        realEstate.setId(this.id.get());
        realEstate.setRealEstateName(this.realEstateName.get());
        realEstate.setAddress(this.address.get());
        realEstate.setPrice(this.price.get());
        realEstate.setNumberOfRooms(this.numberOfRooms.get());
        realEstate.setOwner(this.owner.get());
        return realEstate;
    }

    /**
     * Updates the ViewModel's properties based on a RealEstate entity.
     *
     * @param realEstate the RealEstate entity
     */
    public void updateFromEntity(RealEstate realEstate) {
        this.id.set(realEstate.getId());
        this.realEstateName.set(realEstate.getRealEstateName());
        this.address.set(realEstate.getAddress());
        this.price.set(realEstate.getPrice());
        this.numberOfRooms.set(realEstate.getNumberOfRooms());
        this.owner.set(realEstate.getOwner());
    }
}
