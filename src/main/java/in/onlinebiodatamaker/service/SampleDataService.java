package in.onlinebiodatamaker.service;

import in.onlinebiodatamaker.model.BiodataSample;
import org.springframework.stereotype.Service;

/**
 *
 * @author admin
 */
@Service
public class SampleDataService {

    public BiodataSample getMarathiSample() {
        BiodataSample b = new BiodataSample();

        b.setName("वैभव कल्याणकर");
        b.setBirthDate("१२ मे १९९८");
        b.setBirthTime("सकाळी १०:३०");
        b.setBirthPlace("पुणे");

        b.setReligion("हिंदू");
        b.setCaste("कुंभार");
        b.setRashi("मेष");
        b.setNakshatra("अश्विनी");
        b.setGan("देव गण");
        b.setNadi("आद्य नाडी");

        b.setHeight("५ फूट ८ इंच");
        b.setComplexion("गोरा");
        b.setBloodGroup("O+");

        b.setEducation("बी.ई. कॉम्प्युटर इंजिनिअरिंग");
        b.setProfession("सॉफ्टवेअर इंजिनिअर");
        b.setIncome("₹८ लाख वार्षिक");

        b.setFatherName("श्री. अनिल कल्याणकर");
        b.setFatherOccupation("शासकीय नोकरी");
        b.setMotherName("श्रीमती सुनीता कल्याणकर");
        b.setMotherOccupation("गृहिणी");

        b.setBrothers("१ भाऊ (लहान)");
        b.setSisters("१ बहीण (मोठी)");

        b.setMama("श्री. सुरेश पाटील (पुणे)");
        b.setRelatives("पुणे, सातारा आणि कोल्हापूर येथे नातेवाईक");

        b.setAddress("साई निवास, गणेशनगर, कात्रज, पुणे - ४११०४६");
        b.setMobile("९८७६५४३२१०");
        return b;
    }
}
