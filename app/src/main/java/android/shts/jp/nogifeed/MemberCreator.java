package android.shts.jp.nogifeed;

import android.shts.jp.nogifeed.listener.MemberCreateListener;
import android.shts.jp.nogifeed.models.Member;
import android.shts.jp.nogifeed.utils.MemberUtils;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saitoushouta on 2014/09/23.
 *
 * 未使用
 */
public class MemberCreator {

    private final ExecutorService mThread = Executors.newSingleThreadExecutor();

    private MemberCreateListener mListener;
    private Set<Member> mMembers;

    private boolean isExist(String url) {
    // TODO: check already have profile data
//        String[] name = MemberUtils.getNameFrom(url);
//        String fullname = name[0] + name[1];
//
//        for (Member m : mMembers) {
//            if (fullname != m.getFullname()) {
//                return true;
//            }
//        }
        return false;
    }

    public void create(final String url, final MemberCreateListener listener) {

        if (isExist(url)) {
            return;
        }

        mThread.submit(new Runnable() {
            @Override
            public void run() {
                Member member = MemberUtils.createMemberFrom(url);
                if (member == null) {
                    listener.onFailure();
                } else {
                    listener.onSuccess(member);
                }
            }
        });

    }

}
