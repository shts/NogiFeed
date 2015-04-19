package shts.jp.android.nogifeed.listener;

import shts.jp.android.nogifeed.models.Member;

public interface MemberCreateListener {
    public void onSuccess(Member member);
    public void onFailure();
}
