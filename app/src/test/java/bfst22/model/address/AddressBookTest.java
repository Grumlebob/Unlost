package bfst22.model.address;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressBookTest {
    AddressBook addressBook;

    @BeforeEach
    void setUp() {
        addressBook = new AddressBook();
    }

    @AfterEach
    void tearDown() {
        addressBook = null;
    }

    @Test
    void searchTSTByPrefixRoadName() {
        Address address = new Address(0, 0, "Road House", "1", "1234", "Testing City");
        addressBook.addAddress(address);

        Address actual = addressBook.searchTSTByPrefix("Road").get(0);

        assertEquals(address, actual);
    }

    @Test
    void searchTSTByPrefixPostCode() {
        Address address = new Address(0, 0, "Road House", "1", "1234", "Testing City");
        addressBook.addAddress(address);

        Address actual = addressBook.searchTSTByPrefix("123").get(0);

        assertEquals(address, actual);
    }

    @Test
    void searchTSTByPrefixCity() {
        Address address = new Address(0, 0, "Road House", "1", "1234", "Testing City");
        addressBook.addAddress(address);

        Address actual = addressBook.searchTSTByPrefix("Testing").get(0);

        assertEquals(address, actual);
    }

    @Test
    void searchTSTByPrefixNoResult() {
        Address address = new Address(0, 0, "Road House", "1", "1234", "Testing City");
        addressBook.addAddress(address);

        List<Address> actual = addressBook.searchTSTByPrefix("Alley");

        assertTrue(actual.isEmpty());
    }

    @Test
    void addAddress() {

    }

    @Test
    void addPointOfInterest() {
        var poi = new PointOfInterest(0,0, "Interesting Point", "Very Interesting");
        addressBook.addPointOfInterest(poi);

        assertTrue(addressBook.getPointOfInterestList().contains(poi));
    }

    @Test
    void size() {
    }
}