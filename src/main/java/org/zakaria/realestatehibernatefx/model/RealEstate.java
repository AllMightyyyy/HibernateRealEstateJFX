package org.zakaria.realestatehibernatefx.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a Real Estate property.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "real_estate")
public class RealEstate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "RealEstateName", unique = true, nullable = false)
    @NotBlank(message = "Real estate name cannot be blank")
    private String realEstateName;

    @Column(name = "zipCode")
    @Min(value = 10000, message = "Zip code must be at least 5 digits")
    @Max(value = 99999, message = "Zip code must be at most 5 digits")
    private long zipCode;

    @Column(name = "address")
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Column(name = "price")
    @Positive(message = "Price must be a positive number")
    private double price;

    @Column(name = "number_of_rooms")
    @Positive(message = "Number of rooms must be a positive number")
    private int numberOfRooms;

    @Column(name = "owner")
    @NotBlank(message = "Owner name cannot be blank")
    private String owner;

    @Override
    public String toString() {
        return "RealEstate{" +
                "id=" + id +
                ", realEstateName='" + realEstateName + '\'' +
                ", zipCode=" + zipCode +
                ", address='" + address + '\'' +
                ", price=" + price +
                ", numberOfRooms=" + numberOfRooms +
                ", owner='" + owner + '\'' +
                '}';
    }
}
