package android.shts.jp.nogifeed.listener;

import android.shts.jp.nogifeed.models.Member;

public interface MemberCreateListener {
    public void onSuccess(Member member);
    public void onFailure();
}
