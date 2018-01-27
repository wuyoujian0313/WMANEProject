package air.com.weimeitc.bqwh.wxapi;

/**
 * Created by wuyoujian on 17/4/6.
 */

public class WXUserInfo extends Object {
    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String province;
    private String city;
    private String country;
    private String headimagurl;
    private String unionid;

    public WXUserInfo(){}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadimagurl() {
        return headimagurl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProvince() {
        return province;
    }

    public int getSex() {
        return sex;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setHeadimagurl(String headimagurl) {
        this.headimagurl = headimagurl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
