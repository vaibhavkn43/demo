package in.onlinebiodatamaker.video;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Vaibhav
 */

public class WeddingVideoRequest {

    private String introTitle;
    private String familyTitle;
    private String brideName;
    private String groomName;
    private String weddingDate;
    private String weddingTime;
    private String venue;

    private String photo1;
    private String photo2;
    private String photo3;

    private String inviteMessageLine1;
    private String inviteMessageLine2;

    private String contactNumber;
    private String musicTrack;

    public String getIntroTitle() {
        return introTitle;
    }

    public void setIntroTitle(String introTitle) {
        this.introTitle = introTitle;
    }

    public String getFamilyTitle() {
        return familyTitle;
    }

    public void setFamilyTitle(String familyTitle) {
        this.familyTitle = familyTitle;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public String getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(String weddingDate) {
        this.weddingDate = weddingDate;
    }

    public String getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(String weddingTime) {
        this.weddingTime = weddingTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getPhoto3() {
        return photo3;
    }

    public void setPhoto3(String photo3) {
        this.photo3 = photo3;
    }

    public String getInviteMessageLine1() {
        return inviteMessageLine1;
    }

    public void setInviteMessageLine1(String inviteMessageLine1) {
        this.inviteMessageLine1 = inviteMessageLine1;
    }

    public String getInviteMessageLine2() {
        return inviteMessageLine2;
    }

    public void setInviteMessageLine2(String inviteMessageLine2) {
        this.inviteMessageLine2 = inviteMessageLine2;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMusicTrack() {
        return musicTrack;
    }

    public void setMusicTrack(String musicTrack) {
        this.musicTrack = musicTrack;
    }

}
