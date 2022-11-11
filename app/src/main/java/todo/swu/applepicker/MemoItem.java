package todo.swu.applepicker;

import com.google.firebase.Timestamp;

public class MemoItem {
    Timestamp timestamp;
    String memo;
    boolean achievement;

    //빈 생성자 추가
    public MemoItem(){}

    public MemoItem(Timestamp timestamp, String memo,boolean achievement) {
        this.timestamp = timestamp;
        this.memo = memo;
        this.achievement = achievement;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    };

    public String getMemo() {
        return memo;
    }

    public boolean getAchievement() {
        return achievement;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void switchAchievement() {
        this.achievement = !achievement;
    }

}
