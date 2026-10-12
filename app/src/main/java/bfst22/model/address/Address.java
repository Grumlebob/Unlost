package bfst22.model.address;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Address utilizes the builder pattern. It can hold an lonX and latY coordinate, a street name,
 * a house number and/or letter, a postcode and a city.
 */
public class Address implements Serializable, Comparable<Address> {
    public static final long serialVersionUID = 741698745L;
    //Necessary
    public final float lonX, latY;
    public final String street, house;
    //additional
    public int houseNumber;
    public String houseLetter;
    public final String postcode, city;

    /**
     * The constructor for the address. This is called by the builder.
     * @param lonX The x coordinate for the address.
     * @param latY The y coordinate for the address.
     * @param street The street name (required).
     * @param house The house number and/or letter as a string (required).
     * @param postcode The postcode (optional).
     * @param city The city name (optional).
     */
    public Address(float lonX, float latY, String street, String house, String postcode, String city) {
        this.lonX = lonX;
        this.latY = latY;
        this.street = street;
        this.house = house;
        if(house != null) splitHouseNumberAndLetter();
        this.postcode = postcode;
        this.city = city;
    }

    /**
     * Uses a regular expression (RegEx) to split the house variable into both a number and letter(s) if present.
     */
    private void splitHouseNumberAndLetter() {
        final String regex = "^ *(?<houseNumber>[0-9]+) *(?<houseLetter>[\\p{L}]+)?";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(house);
        if(matcher.matches()) {
            this.houseNumber = Integer.parseInt(matcher.group("houseNumber"));
            if(matcher.group("houseLetter") != null) {
                this.houseLetter = matcher.group("houseLetter");
            }
        }
    }

    /**
     * Uses a StringBuilder to build the address as a string, to show the user, when they search.
     * @return the address as a String.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street).append(" ");
        if (house != null) sb.append(house);
        if (postcode != null) sb.append(", ").append(postcode).append(" ");
        if (city != null) sb.append(city);
        return sb.toString().intern();
    }

    /**
     * Overrides the default compareTo method.
     * Makes sure that "Test Road 2" is between "Test Road 1" and "Test Road 100".
     * @param that The road to be compared to.
     * @return -1 if this address is less than that address. If that address is less than this returns 1.
     * If they are equal, returns 0.
     */
    @Override
    public int compareTo(Address that) {
        if(this.street.compareTo(that.street) < 0) return -1;
        else if(this.street.compareTo(that.street) > 0) return +1;

        if(houseNumber != 0) {
            if(this.houseNumber < that.houseNumber) return -1;
            else if(this.houseNumber > that.houseNumber) return +1;
        }

        if(this.houseLetter != null && that.houseLetter != null) {
            if(this.houseLetter.compareTo(that.houseLetter) < 0) return -1;
            else if(this.houseLetter.compareTo(that.houseLetter) > 0) return +1;
        }
        return 0;
    }

    /**
     * The builder design pattern. "Collects" all the variables from the MapDataParser. Uses isValid to decide
     * whether it should be added to the AddressBook.
     */
    public static class Builder {
        private String street, house, postcode, city;
        private float lonX, latY;

        public void street(String street) {
            this.street = street.intern();
        }

        public void house(String house) {
            this.house = house.intern();
        }

        public void postcode(String postCode) {
            this.postcode = postCode.intern();
        }

        public void city(String city) {
            this.city = city.intern();
        }

        public void coords(float lonX, float latY) {
            this.lonX = lonX;
            this.latY = latY;
        }

        public Address build() {
            return new Address(lonX, latY, street, house, postcode, city);
        }

        public boolean isValid() {
            return street != null && house != null;
        }
    }
}
