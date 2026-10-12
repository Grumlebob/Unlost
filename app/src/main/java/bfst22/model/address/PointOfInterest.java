package bfst22.model.address;

/**
 * This class currently does not have any other functionality than the Address class.
 * The reason why they are split up is to make it easy to implement extra functionality to the class.
 */
public class PointOfInterest extends Address {

    public PointOfInterest(float lonX, float latY, String street, String house) {
        super(lonX, latY, street, house, null, null);
    }
}
