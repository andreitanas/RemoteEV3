package ca.tanas;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import tanas.ca.ev3controller.R;

public class RatingHelper {

    private final static String RATE_DONTSHOW = "rate_dontshowagain";
    private final static String RATE_REMIND = "rate_remindlater";
    private final static String RATE_CLICKEDRATE = "rate_clickedrated";
    private final static String COUNT_APPLAUNCH = "app_launch_count";
    private final static String COUNT_REMINDLAUNCH = "remind_launch_count";
    private final static String COUNT_RATEDLAUNCH = "rated_launch_count";
    private final static String DATE_FIRSTLAUNCH = "app_first_launch";
    private final static String DATE_REMINDSTART = "remind_start_date";
    private final static String DATE_RATEDSTART = "rated_start_date";

    private final static int DAYS_FIRSTPROMPT = 3;
    private final static int DAYS_REMINDPROMPT = 7;
    private final static int DAYS_RATEDPROMPT = 30;

    private final static int LAUNCHES_FIRSTPROMPT = 5;
    private final static int LAUNCHES_REMIND = 15;
    private final static int LAUNCHES_RATED = 25;

    private static void clearFirstLaunchPrefs(SharedPreferences.Editor editor) {
        editor.remove(COUNT_APPLAUNCH);
        editor.remove(DATE_FIRSTLAUNCH);
        editor.commit();
    }

    private static void clearRemindPrefs(SharedPreferences.Editor editor) {
        editor.remove(RATE_REMIND);
        editor.remove(COUNT_REMINDLAUNCH);
        editor.remove(DATE_REMINDSTART);
        editor.commit();
    }

    private static void clearRatedPrefs(SharedPreferences.Editor editor) {
        editor.remove(RATE_CLICKEDRATE);
        editor.remove(COUNT_RATEDLAUNCH);
        editor.remove(DATE_RATEDSTART);
        editor.commit();
    }

    private static void addRemindPrefs(SharedPreferences.Editor editor) {
        editor.putBoolean(RATE_REMIND, true);
        editor.putLong(DATE_REMINDSTART, System.currentTimeMillis());
        editor.commit();
    }

    private static void addRatedPrefs(SharedPreferences.Editor editor) {
        editor.putBoolean(RATE_CLICKEDRATE, true);
        editor.putLong(DATE_RATEDSTART, System.currentTimeMillis());
        editor.commit();
    }

    private static void addDontShowPref(SharedPreferences.Editor editor) {
        clearRemindPrefs(editor);
        clearRatedPrefs(editor);
        editor.putBoolean(RATE_DONTSHOW, true);
        editor.commit();
    }

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean dontShow = prefs.getBoolean(RATE_DONTSHOW, false);
        if (dontShow)
            return;

        long appLaunchCount = prefs.getLong(COUNT_APPLAUNCH, 0);
        long remindLaunchCount = prefs.getLong(COUNT_REMINDLAUNCH, 0);
        long ratedLaunchCount = prefs.getLong(COUNT_RATEDLAUNCH, 0);
        long appFirstLaunchDate = prefs.getLong(DATE_FIRSTLAUNCH, 0);
        long remindStartDate = prefs.getLong(DATE_REMINDSTART, 0);
        long ratedStartDate = prefs.getLong(DATE_RATEDSTART, 0);

        SharedPreferences.Editor editor = prefs.edit();

        boolean ratedClicked = prefs.getBoolean(RATE_CLICKEDRATE, false);
        if (ratedClicked) {
            long launches = ratedLaunchCount + 1;
            editor.putLong(COUNT_RATEDLAUNCH, launches);

            if (ratedStartDate == 0) {
                ratedStartDate = System.currentTimeMillis();
                editor.putLong(DATE_RATEDSTART, ratedStartDate);
            }

            if (launches >= LAUNCHES_RATED) {
                if (System.currentTimeMillis() >= ratedStartDate +
                        (DAYS_RATEDPROMPT * 24 * 60 * 60 * 1000)) {
                    clearRatedPrefs(editor);
                    showRateDialog(mContext, editor);
                }
                else
                    editor.commit();
            }
            else
                editor.commit();

            return;
        }

        boolean remindLater = prefs.getBoolean(RATE_REMIND, false);
        if (remindLater) {
            long launches = remindLaunchCount + 1;
            editor.putLong(COUNT_REMINDLAUNCH, launches);

            if (remindStartDate == 0) {
                remindStartDate = System.currentTimeMillis();
                editor.putLong(DATE_REMINDSTART, remindStartDate);
            }

            if (launches >= LAUNCHES_REMIND) {
                if (System.currentTimeMillis() >= remindStartDate +
                        (DAYS_REMINDPROMPT * 24 * 60 * 60 * 1000)) {
                    clearRemindPrefs(editor);
                    showRateDialog(mContext, editor);
                }
                else
                    editor.commit();
            }
            else
                editor.commit();

            return;
        }

        long launches = appLaunchCount + 1;
        editor.putLong(COUNT_APPLAUNCH, launches);

        if (appFirstLaunchDate == 0) {
            appFirstLaunchDate = System.currentTimeMillis();
            editor.putLong(DATE_FIRSTLAUNCH, appFirstLaunchDate);
        }

        if (launches >= LAUNCHES_FIRSTPROMPT) {
            if (System.currentTimeMillis() >= appFirstLaunchDate +
                    (DAYS_FIRSTPROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
            else
                editor.commit();
        }
        else
            editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        String appTitle = mContext.getString(R.string.app_name);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout rateView = (LinearLayout)inflater.inflate(R.layout.rate_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.rating_rate_this_app);
        builder.setView(rateView);

        TextView rateDesc = rateView.findViewById(R.id.rating_message);
        rateDesc.setText(String.format(mContext.getString(R.string.rating_message), appTitle));

        Button b1 = rateView.findViewById(R.id.rating_rate);
        b1.setText(String.format(mContext.getString(R.string.rating_rate), appTitle));

        final AlertDialog dialog = builder.create();

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null)
                {
                    clearFirstLaunchPrefs(editor);
                    clearRemindPrefs(editor);
                    addRatedPrefs(editor);
                }
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                }
                catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });

        Button b2 = rateView.findViewById(R.id.rating_remind);
        b2.setText(R.string.rating_remind_later);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    clearFirstLaunchPrefs(editor);
                    clearRatedPrefs(editor);
                    addRemindPrefs(editor);
                }

                dialog.dismiss();
            }
        });

        Button b3 = rateView.findViewById(R.id.rating_already_rated);
        b3.setText(R.string.rating_already_rated);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    clearFirstLaunchPrefs(editor);
                    clearRemindPrefs(editor);
                    clearRatedPrefs(editor);
                    addDontShowPref(editor);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
