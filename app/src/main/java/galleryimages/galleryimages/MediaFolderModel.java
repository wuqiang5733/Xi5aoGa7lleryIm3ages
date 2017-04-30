package galleryimages.galleryimages;

/**
 * Created by WuQiang on 2017/4/30.
 */

public class MediaFolderModel {
    String folderName;       // 文件夹的名称
    int mediaItemSum;        // 文件夹下有多少 Item
    String firstItemImage;   // 这个文件夹下的第一个 Item 的图标

    public MediaFolderModel(String folderName, int mediaItemSum, String firstItemImage) {
        this.folderName = folderName;
        this.mediaItemSum = mediaItemSum;
        this.firstItemImage = firstItemImage;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getMediaItemSum() {
        return mediaItemSum;
    }

    public void setMediaItemSum(int mediaItemSum) {
        this.mediaItemSum = mediaItemSum;
    }

    public String getFirstItemImage() {
        return firstItemImage;
    }

    public void setFirstItemImage(String firstItemImage) {
        this.firstItemImage = firstItemImage;
    }
}
