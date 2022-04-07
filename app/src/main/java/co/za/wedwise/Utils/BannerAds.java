//package co.za.wedwise.Utils;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.widget.LinearLayout;
//
//import com.google.ads.mediation.admob.AdMobAdapter;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import co.za.wedwise.R;
//
//public class BannerAds {
//    public static void ShowBannerAds(Context context, LinearLayout mAdViewLayout) {
//
//        AdView mAdView = new AdView(context);
//        mAdView.setAdSize(AdSize.SMART_BANNER);
//        mAdView.setAdUnitId(context.getResources().getString(R.string.admob_bannerID));
//        AdRequest.Builder builder = new AdRequest.Builder();
//        // load non Personalized ads
//        Bundle extras = new Bundle();
//        extras.putString("npa", "1");
//        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
//
//        mAdView.loadAd(builder.build());
//        mAdViewLayout.addView(mAdView);
//    }
//
//    public static void ShowInterstitialAds(Context context) {
//        MobileAds.initialize(context, (context.getResources().getString(R.string.admob_publiserID)));
//                final InterstitialAd mInterstitial = new InterstitialAd(context);
//                mInterstitial.setAdUnitId(context.getResources().getString(R.string.admob_interstitial));
//
//                mInterstitial.loadAd(new AdRequest.Builder().build());
//
//                mInterstitial.show();
//                if (!mInterstitial.isLoaded()) {
//                    AdRequest.Builder builder1 = new AdRequest.Builder();
//
//                    mInterstitial.loadAd(builder1.build());
//                }
//                mInterstitial.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdLoaded() {
//                        super.onAdLoaded();
//                        mInterstitial.show();
//                    }
//                });
//
//    }
//
//}