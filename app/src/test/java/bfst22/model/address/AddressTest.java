package bfst22.model.address;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    Address.Builder address;

    @BeforeEach
    void setUp() {
        address = new Address.Builder();
    }

    @Test
    void testOnlyHouseAndNumber(){
        //Arrange
        Address expected = new Address(0,0,"Testing Road", "10",null,null);

        //Act
        address.coords(0,0);
        address.street("Testing Road");
        address.house("10");
        Address actual = address.build();

        //Assert
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void testFullAddress() {
        //Arrange
        Address expected = new Address(0,0,"Testing Road", "10","1234","Testopolis");

        //Act
        address.coords(0,0);
        address.street("Testing Road");
        address.house("10");
        address.postcode("1234");
        address.city("Testopolis");
        Address actual = address.build();

        //Assert
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void testInvalidAddress() {
        address.coords(0,0);
        address.street("Testing Road");

        assertFalse(address.isValid());
    }

    @Test
    void testValidAddress() {
        address.coords(0,0);
        address.street("Testing Road");
        address.house("10");

        assertTrue(address.isValid());
    }

    @Test
    void testSplitAddress() {
        int expectedNumber = 10;
        String expectedLetter = "A";

        address.coords(0,0);
        address.street("Testing Road");
        address.house("10A");
        var actual = address.build();

        assertEquals(expectedNumber, actual.houseNumber);
        assertEquals(expectedLetter, actual.houseLetter);
    }

    @Test
    void testCompareToByStreet() {
        Address address1 = new Address(0,0,"A Road","1", null, null);
        Address address2 = new Address(0,0,"B Road", "1", null, null);

        int expected = -1;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToByStreet2() {
        Address address1 = new Address(0,0,"A Road","1", null, null);
        Address address2 = new Address(0,0,"B Road", "1", null, null);

        int expected = 1;

        int actual = address2.compareTo(address1);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToByHouseNumber() {
        Address address1 = new Address(0,0,"A Road","1", null, null);
        Address address2 = new Address(0,0,"A Road", "2", null, null);

        int expected = -1;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToByHouseNumber2() {
        Address address1 = new Address(0,0,"A Road","2", null, null);
        Address address2 = new Address(0,0,"A Road", "1", null, null);

        int expected = 1;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToByHouseLetter() {
        Address address1 = new Address(0,0,"A Road","1A", null, null);
        Address address2 = new Address(0,0,"A Road", "1B", null, null);

        int expected = -1;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToByHouseLetter2() {
        Address address1 = new Address(0,0,"A Road","1B", null, null);
        Address address2 = new Address(0,0,"A Road", "1A", null, null);

        int expected = 1;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @Test
    void testCompareToEqualAddresses() {
        Address address1 = new Address(0,0,"A Road","1", null, null);
        Address address2 = new Address(0,0,"A Road", "1", null, null);

        int expected = 0;

        int actual = address1.compareTo(address2);

        assertEquals(expected, actual);
    }

    @AfterEach
    void tearDown() {
        address = null;
    }
}