package android.shts.jp.nogifeed.listener;

import android.shts.jp.nogifeed.models.Member;

/**
 * Created by saitoushouta on 2014/09/23.
 */
public interface MemberCreateListener {
    public void onSuccess(Member member);
    public void onFailure();
}
