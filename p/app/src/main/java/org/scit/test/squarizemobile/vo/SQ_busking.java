package org.scit.test.squarizemobile.vo;

/**
 * Created by user on 2017-01-06.
 */

public class SQ_busking {
    private int sq_busking_id;
    private String id;
    private String title;
    private String location;
    private String latitude;
    private String longitude;
    private String url;
    private String genre;
    private int rating;
    private String teamname;
    private String gallery = "";
    private String gallery2 = "";
    private String gallery3 = "";
    private String gallery4 = "";
    private String gallery5 = "";
    private String buskingdate;
    private int runningtime;
    private String description;
    private String end;

    public SQ_busking() {
        // TODO Auto-generated constructor stub
    }

    public SQ_busking(int sq_busking_id, String id, String title, String location, String latitude, String longitude,
                      String url, String genre, int rating, String teamname, String gallery, String gallery2, String gallery3,
                      String gallery4, String gallery5, String buskingdate, int runningtime, String description) {
        super();
        this.sq_busking_id = sq_busking_id;
        this.id = id;
        this.title = title;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.genre = genre;
        this.rating = rating;
        this.teamname = teamname;
        this.gallery = gallery;
        this.gallery2 = gallery2;
        this.gallery3 = gallery3;
        this.gallery4 = gallery4;
        this.gallery5 = gallery5;
        this.buskingdate = buskingdate;
        this.runningtime = runningtime;
        this.description = description;
    }

    public int getSq_busking_id() {
        return sq_busking_id;
    }

    public void setSq_busking_id(int sq_busking_id) {
        this.sq_busking_id = sq_busking_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getGallery2() {
        return gallery2;
    }

    public void setGallery2(String gallery2) {
        this.gallery2 = gallery2;
    }

    public String getGallery3() {
        return gallery3;
    }

    public void setGallery3(String gallery3) {
        this.gallery3 = gallery3;
    }

    public String getGallery4() {
        return gallery4;
    }

    public void setGallery4(String gallery4) {
        this.gallery4 = gallery4;
    }

    public String getGallery5() {
        return gallery5;
    }

    public void setGallery5(String gallery5) {
        this.gallery5 = gallery5;
    }

    public String getBuskingdate() {
        return buskingdate;
    }

    public void setBuskingdate(String buskingdate) {
        this.buskingdate = buskingdate;
    }

    public int getRunningtime() {
        return runningtime;
    }

    public void setRunningtime(int runningtime) {
        this.runningtime = runningtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "SQ_busking [sq_busking_id=" + sq_busking_id + ", id=" + id + ", title=" + title + ", location="
                + location + ", latitude=" + latitude + ", longitude=" + longitude + ", url=" + url + ", genre=" + genre
                + ", rating=" + rating + ", teamname=" + teamname + ", gallery=" + gallery + ", gallery2=" + gallery2
                + ", gallery3=" + gallery3 + ", gallery4=" + gallery4 + ", gallery5=" + gallery5 + ", buskingdate="
                + buskingdate + ", runningtime=" + runningtime + ", description=" + description + ", end=" + end + "]";
    }
}