
//        -----------------------------------com.baeble.www.baebleapp.Svcmodel.java-----------------------------------

package com.baeble.www.baebleapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeaturedVideo implements Parcelable{

    @SerializedName("videoID")
    @Expose
    private String videoID;
    @SerializedName("videoType")
    @Expose
    private String videoType;
    @SerializedName("videoName")
    @Expose
    private String videoName;
    @SerializedName("videoStatus")
    @Expose
    private String videoStatus;
    @SerializedName("videoImage")
    @Expose
    private String videoImage;
    @SerializedName("videoShortDescription")
    @Expose
    private String videoShortDescription;
    @SerializedName("videoDescription")
    @Expose
    private String videoDescription;
    @SerializedName("albumName")
    @Expose
    private String albumName;
    @SerializedName("videoDate")
    @Expose
    private String videoDate;
    @SerializedName("videoDuration")
    @Expose
    private String videoDuration;
    @SerializedName("videoLength")
    @Expose
    private String videoLength;
    @SerializedName("downloadPath")
    @Expose
    private String downloadPath;
    @SerializedName("previewPath")
    @Expose
    private String previewPath;
    @SerializedName("introVideoPath")
    @Expose
    private String introVideoPath;
    @SerializedName("singleVideoPath")
    @Expose
    private String singleVideoPath;
    @SerializedName("singleVideoPath169")
    @Expose
    private String singleVideoPath169;
    @SerializedName("m3u8Path")
    @Expose
    private String m3u8Path;
    @SerializedName("m3u8Path_No_4K")
    @Expose
    private String m3u8PathNo4K;
    @SerializedName("embedVideoLength")
    @Expose
    private String embedVideoLength;
    @SerializedName("mp4Path")
    @Expose
    private String mp4Path;
    @SerializedName("webm")
    @Expose
    private String webmPath;
    @SerializedName("teaserVideoPath")
    @Expose
    private String teaserVideoPath;
    @SerializedName("pageURL")
    @Expose
    private String pageURL;
    @SerializedName("homepageTeaser")
    @Expose
    private String homepageTeaser;
    @SerializedName("viewCount")
    @Expose
    private String viewCount;
    @SerializedName("datePosted")
    @Expose
    private String datePosted;
    @SerializedName("fps")
    @Expose
    private String fps;
    @SerializedName("customTitle")
    @Expose
    private String customTitle;
    @SerializedName("bandID")
    @Expose
    private String bandID;
    @SerializedName("bandName")
    @Expose
    private String bandName;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("bandImage")
    @Expose
    private String bandImage;
    @SerializedName("iTunesArtistID")
    @Expose
    private String iTunesArtistID;
    @SerializedName("venueID")
    @Expose
    private String venueID;
    @SerializedName("venueName")
    @Expose
    private String venueName;
    @SerializedName("setlist")
    @Expose
    private SetlistModel[] setlist;
    @SerializedName("captions")
    @Expose
    private String captions;
    @SerializedName("ads")
    @Expose
    private AdsModel[] adsList;

    protected FeaturedVideo(Parcel in) {
        videoID = in.readString();
        videoType = in.readString();
        videoName = in.readString();
        videoStatus = in.readString();
        videoImage = in.readString();
        videoShortDescription = in.readString();
        videoDescription = in.readString();
        albumName = in.readString();
        videoDate = in.readString();
        videoDuration = in.readString();
        videoLength = in.readString();
        downloadPath = in.readString();
        previewPath = in.readString();
        introVideoPath = in.readString();
        singleVideoPath = in.readString();
        singleVideoPath169 = in.readString();
        m3u8Path = in.readString();
        m3u8PathNo4K = in.readString();
        embedVideoLength = in.readString();
        mp4Path = in.readString();
        webmPath = in.readString();
        teaserVideoPath = in.readString();
        pageURL = in.readString();
        homepageTeaser = in.readString();
        viewCount = in.readString();
        datePosted = in.readString();
        fps = in.readString();
        customTitle = in.readString();
        bandID = in.readString();
        bandName = in.readString();
        bio = in.readString();
        link = in.readString();
        bandImage = in.readString();
        iTunesArtistID = in.readString();
        venueID = in.readString();
        venueName = in.readString();
        captions = in.readString();
    }

    public static final Creator<FeaturedVideo> CREATOR = new Creator<FeaturedVideo>() {
        @Override
        public FeaturedVideo createFromParcel(Parcel in) {
            return new FeaturedVideo(in);
        }

        @Override
        public FeaturedVideo[] newArray(int size) {
            return new FeaturedVideo[size];
        }
    };

    /**
     *
     * @return
     * The videoID
     */
    public String getVideoID() {
        return videoID;
    }

    /**
     *
     * @param videoID
     * The videoID
     */
    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    /**
     *
     * @return
     * The videoType
     */
    public String getVideoType() {
        return videoType;
    }

    /**
     *
     * @param videoType
     * The videoType
     */
    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    /**
     *
     * @return
     * The videoName
     */
    public String getVideoName() {
        return videoName;
    }

    /**
     *
     * @param videoName
     * The videoName
     */
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    /**
     *
     * @return
     * The videoStatus
     */
    public String getVideoStatus() {
        return videoStatus;
    }

    /**
     *
     * @param videoStatus
     * The videoStatus
     */
    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    /**
     *
     * @return
     * The videoImage
     */
    public String getVideoImage() {
        return videoImage;
    }

    /**
     *
     * @param videoImage
     * The videoImage
     */
    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    /**
     *
     * @return
     * The videoShortDescription
     */
    public String getVideoShortDescription() {
        return videoShortDescription;
    }

    /**
     *
     * @param videoShortDescription
     * The videoShortDescription
     */
    public void setVideoShortDescription(String videoShortDescription) {
        this.videoShortDescription = videoShortDescription;
    }

    /**
     *
     * @return
     * The videoDescription
     */
    public String getVideoDescription() {
        return videoDescription;
    }

    /**
     *
     * @param videoDescription
     * The videoDescription
     */
    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    /**
     *
     * @return
     * The albumName
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     *
     * @param albumName
     * The albumName
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     *
     * @return
     * The videoDate
     */
    public String getVideoDate() {
        return videoDate;
    }

    /**
     *
     * @param videoDate
     * The videoDate
     */
    public void setVideoDate(String videoDate) {
        this.videoDate = videoDate;
    }

    /**
     *
     * @return
     * The videoDuration
     */
    public String getVideoDuration() {
        return videoDuration;
    }

    /**
     *
     * @param videoDuration
     * The videoDuration
     */
    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    /**
     *
     * @return
     * The videoLength
     */
    public String getVideoLength() {
        return videoLength;
    }

    /**
     *
     * @param videoLength
     * The videoLength
     */
    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    /**
     *
     * @return
     * The downloadPath
     */
    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     *
     * @param downloadPath
     * The downloadPath
     */
    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    /**
     *
     * @return
     * The previewPath
     */
    public String getPreviewPath() {
        return previewPath;
    }

    /**
     *
     * @param previewPath
     * The previewPath
     */
    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    /**
     *
     * @return
     * The introVideoPath
     */
    public String getIntroVideoPath() {
        return introVideoPath;
    }

    /**
     *
     * @param introVideoPath
     * The introVideoPath
     */
    public void setIntroVideoPath(String introVideoPath) {
        this.introVideoPath = introVideoPath;
    }

    /**
     *
     * @return
     * The singleVideoPath
     */
    public String getSingleVideoPath() {
        return singleVideoPath;
    }

    /**
     *
     * @param singleVideoPath
     * The singleVideoPath
     */
    public void setSingleVideoPath(String singleVideoPath) {
        this.singleVideoPath = singleVideoPath;
    }

    /**
     *
     * @return
     * The singleVideoPath169
     */
    public String getSingleVideoPath169() {
        return singleVideoPath169;
    }

    /**
     *
     * @param singleVideoPath169
     * The singleVideoPath169
     */
    public void setSingleVideoPath169(String singleVideoPath169) {
        this.singleVideoPath169 = singleVideoPath169;
    }

    /**
     *
     * @return
     * The m3u8Path
     */
    public String getM3u8Path() {
        return m3u8Path;
    }

    /**
     *
     * @param m3u8PathNo4K
     * The m3u8Path
     */
    public void setM3u8PathNo4K(String m3u8PathNo4K) {
        this.m3u8PathNo4K = m3u8PathNo4K;
    }

    /**
     *
     * @return
     * The m3u8Path
     */
    public String getM3u8PathNo4K() {
        return m3u8PathNo4K;
    }

    /**
     *
     * @param m3u8Path
     * The m3u8Path
     */
    public void setM3u8Path(String m3u8Path) {
        this.m3u8Path = m3u8Path;
    }

    /**
     *
     * @return
     * The embedVideoLength
     */
    public String getEmbedVideoLength() {
        return embedVideoLength;
    }

    /**
     *
     * @param embedVideoLength
     * The embedVideoLength
     */
    public void setEmbedVideoLength(String embedVideoLength) {
        this.embedVideoLength = embedVideoLength;
    }

    /**
     *
     * @return
     * The mp4Path
     */
    public String getMp4Path() {
        return mp4Path;
    }

    /**
     *
     * @param mp4Path
     * The mp4Path
     */
    public void setMp4Path(String mp4Path) {
        this.mp4Path = mp4Path;
    }
    /**
     *
     * @return
     * The webmPath
     */
    public String getWebmPath() {
        return webmPath;
    }

    /**
     *
     * @param webmPath
     * The webmPath
     */
    public void setWebmPath(String webmPath) {
        this.webmPath = webmPath;
    }
    /**
     *
     * @return
     * The teaserVideoPath
     */
    public String getTeaserVideoPath() {
        return teaserVideoPath;
    }

    /**
     *
     * @param teaserVideoPath
     * The teaserVideoPath
     */
    public void setTeaserVideoPath(String teaserVideoPath) {
        this.teaserVideoPath = teaserVideoPath;
    }

    /**
     *
     * @return
     * The pageURL
     */
    public String getPageURL() {
        return pageURL;
    }

    /**
     *
     * @param pageURL
     * The pageURL
     */
    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }


    /**
     *
     * @return
     * The homepageTeaser
     */
    public String getHomepageTeaser() {
        return homepageTeaser;
    }

    /**
     *
     * @param homepageTeaser
     * The homepageTeaser
     */
    public void setHomepageTeaser(String homepageTeaser) {
        this.homepageTeaser = homepageTeaser;
    }

    /**
     *
     * @return
     * The viewCount
     */
    public String getViewCount() {
        return viewCount;
    }

    /**
     *
     * @param viewCount
     * The viewCount
     */
    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    /**
     *
     * @return
     * The datePosted
     */
    public String getDatePosted() {
        return datePosted;
    }

    /**
     *
     * @param datePosted
     * The datePosted
     */
    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    /**
     *
     * @return
     * The fps
     */
    public String getFps() {
        return fps;
    }

    /**
     *
     * @param fps
     * The fps
     */
    public void setFps(String fps) {
        this.fps = fps;
    }

    /**
     *
     * @return
     * The customTitle
     */
    public String getCustomTitle() {
        return customTitle;
    }

    /**
     *
     * @param customTitle
     * The customTitle
     */
    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    /**
     *
     * @return
     * The bandID
     */
    public String getBandID() {
        return bandID;
    }

    /**
     *
     * @param bandID
     * The bandID
     */
    public void setBandID(String bandID) {
        this.bandID = bandID;
    }

    /**
     *
     * @return
     * The bandName
     */
    public String getBandName() {
        return bandName;
    }

    /**
     *
     * @param bandName
     * The bandName
     */
    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    /**
     *
     * @return
     * The bio
     */
    public String getBio() {
        return bio;
    }

    /**
     *
     * @param bio
     * The bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The bandImage
     */
    public String getBandImage() {
        return bandImage;
    }

    /**
     *
     * @param bandImage
     * The bandImage
     */
    public void setBandImage(String bandImage) {
        this.bandImage = bandImage;
    }

    /**
     *
     * @return
     * The iTunesArtistID
     */
    public String getITunesArtistID() {
        return iTunesArtistID;
    }

    /**
     *
     * @param iTunesArtistID
     * The iTunesArtistID
     */
    public void setITunesArtistID(String iTunesArtistID) {
        this.iTunesArtistID = iTunesArtistID;
    }

    /**
     *
     * @return
     * The venueID
     */
    public String getVenueID() {
        return venueID;
    }

    /**
     *
     * @param venueID
     * The venueID
     */
    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    /**
     *
     * @return
     * The venueName
     */
    public String getVenueName() {
        return venueName;
    }

    /**
     *
     * @param venueName
     * The venueName
     */
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }


    /**
     *
     * @return
     * The captions
     */
    public String getCaptions() {
        return captions;
    }

    /**
     *
     * @param captions
     * The captions
     */
    public void setCaptions(String captions) {
        this.captions = captions;
    }

    /**
     *
     * @return
     * The setlist
     */
    public SetlistModel[] getSetList() {
        return setlist;
    }

    /**
     *
     * @param setlist
     * The setlist
     */
    public void setSetList(SetlistModel[] setlist) {
        this.setlist = setlist;
    }
    /**
     *
     * @return
     * The adsList
     */
    public AdsModel[] getAds() {
        return adsList;
    }

    /**
     *
     * @param adsList
     * The adsList
     */
    public void setAds(AdsModel[] adsList) {
        this.adsList = adsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoID);
        dest.writeString(videoType);
        dest.writeString(videoName);
        dest.writeString(videoStatus);
        dest.writeString(videoImage);
        dest.writeString(videoShortDescription);
        dest.writeString(videoDescription);
        dest.writeString(albumName);
        dest.writeString(videoDate);
        dest.writeString(videoDuration);
        dest.writeString(videoLength);
        dest.writeString(downloadPath);
        dest.writeString(previewPath);
        dest.writeString(introVideoPath);
        dest.writeString(singleVideoPath);
        dest.writeString(singleVideoPath169);
        dest.writeString(m3u8Path);
        dest.writeString(m3u8PathNo4K);
        dest.writeString(embedVideoLength);
        dest.writeString(mp4Path);
        dest.writeString(webmPath);
        dest.writeString(teaserVideoPath);
        dest.writeString(pageURL);
        dest.writeString(homepageTeaser);
        dest.writeString(viewCount);
        dest.writeString(datePosted);
        dest.writeString(fps);
        dest.writeString(customTitle);
        dest.writeString(bandID);
        dest.writeString(bandName);
        dest.writeString(bio);
        dest.writeString(link);
        dest.writeString(bandImage);
        dest.writeString(iTunesArtistID);
        dest.writeString(venueID);
        dest.writeString(venueName);
        dest.writeString(captions);
    }

    public class SetlistModel{
        @SerializedName("setlistitemid")
        @Expose
        private String setlistitemid;

        @SerializedName("songname")
        @Expose
        private String songname;

        @SerializedName("fps")
        @Expose
        private String fps;

        @SerializedName("starttimeindex")
        @Expose
        private String starttimeindex;

        @SerializedName("endtimeindex")
        @Expose
        private String endtimeindex;

        public String getSetListItemId() {
            return setlistitemid;
        }
        public void setSetListItemId(String setlistitemid) {
            this.setlistitemid = setlistitemid;
        }

        public String getSongName() {
            return songname;
        }
        public void setSongName(String songname) {
            this.songname = songname;
        }

        public String getFps() {
            return fps;
        }
        public void setFps(String fps) {
            this.fps = fps;
        }

        public String getStartTimeIdx() {
            return starttimeindex;
        }
        public void setStartTimeIdx(String starttimeindex) {
            this.starttimeindex = starttimeindex;
        }

        public String getEndTimeIdx() {
            return endtimeindex;
        }
        public void setEndTimeIdx(String endtimeindex) {
            this.endtimeindex = endtimeindex;
        }
    }
    public class AdsModel{
        @SerializedName("preroll_mobile_android_tv")
        @Expose
        private String prerollMobileAndroidTv;

        public String getPrerollMobileAndroidTv() {
            return prerollMobileAndroidTv;
        }

        public void setPrerollMobileAndroidTv(String prerollMobileAndroidTv) {
            this.prerollMobileAndroidTv = prerollMobileAndroidTv;
        }
    }
}

