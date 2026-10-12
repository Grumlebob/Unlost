package bfst22.model.address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * AddressBook holds three TSTs. One for the full addresses, one with the postcodes and a hashset for each,
 * and one for the cities and a hashset for each. It also holds a list of PointsOfInterest.
 */
public class AddressBook implements Serializable {
    public static final long serialVersionUID = 12316449L;
    private final List<Address> pointOfInterestList;
    private final TST<Address> addressTST;
    private final TST<HashSet<Address>> postcodeTST;
    private final TST<HashSet<Address>> cityTST;
    private int size = 0;

    public AddressBook() {
        this.pointOfInterestList = new ArrayList<>();
        this.addressTST = new TST<>();
        this.postcodeTST = new TST<>();
        this.cityTST = new TST<>();
    }
    public List<Address> getPointOfInterestList() {
        return pointOfInterestList;
    }

    /**
     * Searches the TSTs for the prefix.
     * If the full address TST has no matches, the postcode TST is searched.
     * If it has no matches the city TST is searched.
     * If the addressList has more than one address it is sorted.
     * @param prefix The search query from AddressMenu
     * @return A sorted addressList of all the addresses that match the prefix query.
     */
    public List<Address> searchTSTByPrefix(String prefix) {
        List<Address> addressList = new ArrayList<>();
        for(String addressString : addressTST.keysWithPrefix(prefix.toLowerCase())) {
            addressList.add(addressTST.get(addressString));
        }
        if(addressList.isEmpty()) {
            for(String addressString : postcodeTST.keysWithPrefix(prefix)) {
                addressList.addAll(postcodeTST.get(addressString));
            }
            if(addressList.isEmpty()) {
                for (String addressString : cityTST.keysWithPrefix(prefix.toLowerCase())) {
                    addressList.addAll(cityTST.get(addressString));
                }
            }
        }
        if(addressList.size() > 1) addressList = addressList.stream().sorted().toList();

        return addressList;
    }

    /**
     * Adds the full address to the AddressTST.
     * If the address has a postcode and/or city, the address is also added to that TST.
     * @param address The Address to be added to the TST
     */
    public void addAddress(Address address) {
        size++;
        addressTST.put(address.toString().toLowerCase(), address);
        if(address.postcode != null) {
            if(!postcodeTST.contains(address.postcode)) postcodeTST.put(address.postcode, new HashSet<>());
            postcodeTST.get(address.postcode).add(address);
        }
        if(address.city != null) {
            if(!cityTST.contains(address.city.toLowerCase())) cityTST.put(address.city.toLowerCase(), new HashSet<>());
            cityTST.get(address.city.toLowerCase()).add(address);
        }
    }

    /**
     * Adds a point of interest both to the full address TST and to the list of points of interest.
     */
    public void addPointOfInterest(Address poi) {
        addAddress(poi);
        pointOfInterestList.add(poi);
    }

    /**
     * Sums the sizes of the three TSTs in the AddressBook
     * @return returns the summed size of the three TSTs
     */
    public int size() {
        return size;
    }
}
