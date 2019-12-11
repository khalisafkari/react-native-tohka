package com.tohka.ads;


import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.ReactPropGroup;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.smaato.soma.*;
import com.smaato.soma.bannerutilities.constant.BannerStatus;
import com.smaato.soma.debug.Debugger;

public class TKTohkaManager extends SimpleViewManager<ReactViewGroup> {

    public static final String TAG = "SMAATOBANNER";

    public static final String PROP_AD_DIMENSION = "adDimension";
    public static final String PROP_PUBLISHER_ID = "publisherID";
    public static final String PROP_AD_SPACE_ID = "adSpaceID";
    public static final String PROP_TEST_DEVICE_ID = "testDeviceID";

    public static final String PROP_LOCATION_SETLAT = "setLat";
    public static final String PROP_LOCATION_SETLONG = "setLong";

    private RCTEventEmitter mEventEmiiter;
    private  ThemedReactContext mThemedReactContext;
    private AdSettings adSettings = null;
    private String testDeviceID = null;




    @ReactProp(name = PROP_AD_SPACE_ID)
    public void setPropAdSpaceId(final ReactViewGroup view,String adSpaceID){
        Log.d(TAG,"setPropAdSpaceId");

        attachNewAdView(view);
        BannerView newAdView = (BannerView) view.getChildAt(0);
        if (newAdView != null){
            if (adSpaceID != null && !adSpaceID.equals("")){
                adSettings.setAdspaceId(Long.parseLong(adSpaceID));
            }
            loadAd(newAdView);
        }
    }

    @ReactProp(name = PROP_PUBLISHER_ID)
    public void setPublisherID(final ReactViewGroup view,String publisherID){
        Log.d(TAG,"setPublisherID");

        attachNewAdView(view);
        BannerView newAdView  = (BannerView) view.getChildAt(0);
        if (newAdView != null){
            if (publisherID != null && !publisherID.equals("")){
                adSettings.setPublisherId(Long.parseLong(publisherID));
            }
            loadAd(newAdView);
        }
    }

    @ReactProp(name = PROP_AD_DIMENSION)
    public void setPropAdDimension(final ReactViewGroup view, String adDimension){
        Log.d(TAG,"setPropAdDimension");
        attachNewAdView(view);
        BannerView newAdView = (BannerView) view.getChildAt(0);
        if (newAdView != null){
            if (adDimension != null && !adDimension.equals("")){
                adSettings.setAdDimension(getAdDimensionForString(adDimension));
            }
            loadAd(newAdView);
        }
    }

    @ReactPropGroup(names = {
            PROP_LOCATION_SETLAT,
            PROP_LOCATION_SETLONG
    })
    public void setPropLocationSetlat(final ReactViewGroup view,double lat, double llong){
        Log.d(TAG,"setPropLocationSetlat");
        attachNewAdView(view);
        BannerView newAdView = (BannerView) view.getChildAt(0);
        newAdView.getUserSettings().setLatitude(lat);
        newAdView.getUserSettings().setLongitude(llong);
        loadAd(newAdView);
    }

    @ReactProp(name = PROP_TEST_DEVICE_ID)
    public void setTestDeviceID(final ReactViewGroup view, final String testDeviceID) {
        Log.d(TAG, "setTestDeviceID");

        this.testDeviceID = testDeviceID;
    }

    @Override
    public String getName() {
        return "TKTohka";
    }

    @Override
    protected ReactViewGroup createViewInstance(ThemedReactContext themedReactContext) {
        Debugger.DEBUG_LEVEL = 3;
        mThemedReactContext = themedReactContext;
        mEventEmiiter = themedReactContext.getJSModule(RCTEventEmitter.class);
        ReactViewGroup view = new ReactViewGroup(themedReactContext);
        adSettings = new AdSettings();
        adSettings.setPublisherId(-0);
        adSettings.setAdspaceId(-0);
        adSettings.setAdDimension(AdDimension.NOT_SET);
        attachNewAdView(view);
        return view;
    }

    protected void attachNewAdView(final ReactViewGroup view){
        Log.d(TAG,"attachNewAdView");
        final BannerView adView = new BannerView(mThemedReactContext);
        BannerView oldView = (BannerView) view.getChildAt(0);
        view.removeAllViews();
        if (oldView != null){
            oldView.destroy();
        }
        view.addView(adView);
    }

    private int convertDpToPx(int dp) {
        return Math.round(
                dp * (mThemedReactContext.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    private void updateSize(View adView){
        Resources r = mThemedReactContext.getResources();
        int width = 0;
        int height = 0;

        int left = adView.getLeft();
        int top = adView.getTop();

        switch (adSettings.getAdDimension()){
            case MEDIUMRECTANGLE:
                width = convertDpToPx(300);
                height = convertDpToPx(250);
                break;
            case SKYSCRAPER:
                width = convertDpToPx(120);
                height = convertDpToPx(600);
                break;
            case LEADERBOARD:
                width = convertDpToPx(728);
                height = convertDpToPx(90);
                break;
            case DEFAULT:
            default:
                width = convertDpToPx(320);
                height = convertDpToPx(50);
        }

        adView.measure(width,height);
        adView.layout(left, top, left + width, top + height);
        adView.setLayoutParams(new ViewGroup.LayoutParams(width,height));
    }


    private void loadAd(final BannerView view){
        Log.d(TAG,"loadAd");

        if (adSettings != null){
            view.setAdSettings(adSettings);
            switch (adSettings.getAdDimension()){
                case SKYSCRAPER:
                    view.getAdSettings().setBannerWidth(120);
                    view.getAdSettings().setBannerHeight(600);
                    break;
                case MEDIUMRECTANGLE:
                    view.getAdSettings().setBannerWidth(300);
                    view.getAdSettings().setBannerHeight(250);
                    break;
                case LEADERBOARD:
                    view.getAdSettings().setBannerWidth(728);
                    view.getAdSettings().setBannerHeight(90);
                    break;
                case DEFAULT:
                default:
                    view.getAdSettings().setBannerWidth(320);
                    view.getAdSettings().setBannerHeight(50);
            }

            if (adSettings.getAdspaceId() != -1 && adSettings.getPublisherId() != -1 &&
            adSettings.getAdDimension() != AdDimension.NOT_SET){
                Log.d(TAG,"Load OK");

                view.addAdListener(new AdListenerInterface() {
                    @Override
                    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBannerInterface) {
                        if (receivedBannerInterface.getStatus() == BannerStatus.ERROR){
                            Log.d(TAG, receivedBannerInterface.getErrorCode() + ":"
                                    + receivedBannerInterface.getErrorMessage());
                        }else {
                            Log.d(TAG,"OK SUPUESTAMENTE");
                        }
                        updateSize(view);
                    }
                });
                view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                        Log.d(TAG, "onLayoutChange");
                    }
                });
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View view) {
                        Log.d(TAG, "onViewAttachedToWindow");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View view) {
                        Log.d(TAG, "onViewDetachedFromWindow");
                    }
                });
                view.setBannerStateListener(new BannerStateListener() {
                    @Override
                    public void onWillOpenLandingPage(BaseView baseView) {
                        Log.d(TAG, "onWillOpenLandingPage");
                    }

                    @Override
                    public void onWillCloseLandingPage(BaseView baseView) {
                        Log.d(TAG, "onWillCloseLandingPage");
                    }
                });
                view.asyncLoadNewBanner();
            }
        }
    }


    private AdDimension getAdDimensionForString(String adSize){
        switch (adSize){
            case "MEDIUMRECTANGLE":
                return AdDimension.MEDIUMRECTANGLE;
            case "SKYSCRAPER":
                return AdDimension.SKYSCRAPER;
            case "LEADERBOARD":
                return AdDimension.LEADERBOARD;
            case "DEFAULT":
            default:
                return AdDimension.DEFAULT;
        }
    }

    public enum Events {
        EVENT_SIZE_CHANGE("onSizeChange"), EVENT_RECEIVE_AD("onAdViewDidReceiveAd"),
        EVENT_ERROR("onDidFailToReceiveAdWithError"), EVENT_WILL_PRESENT("onAdViewWillPresentScreen"),
        EVENT_WILL_DISMISS("onAdViewWillDismissScreen"), EVENT_DID_DISMISS("onAdViewDidDismissScreen"),
        EVENT_WILL_LEAVE_APP("onAdViewWillLeaveApplication");

        private final String mName;
        Events(final String name){
            mName = name;
        }

        @Override
        public String toString() {
            return "Events{" +
                    "mName='" + mName + '\'' +
                    '}';
        }
    }

}
