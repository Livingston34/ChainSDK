package xybank.com.chainsdk;

import android.app.Application;

import com.xybank.chain.android.sdk.ChainAPI;


/**
 * Created by Livingston on 2018/2/22.
 */

public class ChainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ChainAPI.init(this, "PAMFT1OBBMVHBHYPYGJBPGARZ1GR5YUK", "http://blockchain.xyebank.com/api/v1/");
    }
}
