package kristijandelivuk.com.nadjiprijevoz.model.navigation;

/**
 * Created by kdelivuk on 30/07/15.
 */
public class NavigationDrawerItem {

    private String title;
    private int resId;

    public NavigationDrawerItem() {}

    public NavigationDrawerItem (String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
