package in.hcdc.demo.model;

/**
 *
 * @author Vaibhav
 */
public class SampleBiodataFactory {
    public static BiodataRequest create() {
        BiodataRequest b = new BiodataRequest();

        b.setName("शुभम देशमुख");
        b.setBirthDate("12 मार्च 1996");
        b.setBirthTime("सकाळी 7:30");
        b.setBirthPlace("पुणे");
        b.setReligion("हिंदू");
        b.setCaste("मराठा");
        b.setHeight("5 फूट 8 इंच");
        b.setEducation("बी.ई. (संगणक)");

        b.setFatherName("श्री. रामचंद्र देशमुख");
        b.setMotherName("सौ. सुनीता देशमुख");
        b.setBrothers("1");
        b.setSisters("1");

        b.setAddress("पुणे, महाराष्ट्र");
        b.setMobile("9XXXXXXXXX");

        return b;
    }
}
