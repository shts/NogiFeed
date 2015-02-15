package shts.jp.android.nogifeed.listener;

public interface MemberCreateListener {
    public void onSuccess(shts.jp.android.nogifeed.models.Member member);
    public void onFailure();
}
