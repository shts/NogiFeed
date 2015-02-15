package shts.jp.android.nogifeed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class FromParseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Parse", "onReceive");
        try {
            //jsonから値を取り出し
            Bundle extra        = intent.getExtras();
            String data         = extra.getString("com.parse.Data");
            JSONObject json     = new JSONObject(data);
            Log.i("Parse", data);

            //取り出したデータを変数へ
            String msg          = json.getString("msg");

            Log.i("parse push test!", "msg : " + msg);

        } catch (JSONException e) {
            Log.e("failed to Parse : ", e.toString());
        }
    }
}
