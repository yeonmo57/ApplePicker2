package todo.swu.applepicker;

public class MemoItem {
    String memo;
    boolean achievement;

    //빈 생성자 추가
    public MemoItem(){}

    public MemoItem(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public boolean getAchievement() {
        return achievement;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void switchAchievement() {
        this.achievement = !achievement;
    }

}
