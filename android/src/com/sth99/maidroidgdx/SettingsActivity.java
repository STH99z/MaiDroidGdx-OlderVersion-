package com.sth99.maidroidgdx;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single listGameMechanism. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the listGameMechanism of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    public static HashMap<String, Object> universalPrefs;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        //// FIXME: 2016/11/17 找个优雅的方式处理吧，StackOverflow上并没有
        if (preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        sBindPreferenceSummaryToValueListener = new MyOPCListener(getApplicationContext());
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GameMechanismFragment.class.getName().equals(fragmentName)
                || SoundPreferenceFragment.class.getName().equals(fragmentName)
                || VisualPreferenceFragment.class.getName().equals(fragmentName);
    }

    static final String[] listGameMechanism = {
            "judge_timing_offset",
            "note_timing_offset",
            "use_accurate_judgement",
            "optimize_star_dash03",
            "debug_show",
    };
    static final String[] listVisual = {
            "disp_duration",
            "scaling_duration",
            "appear_position",
            "viewport_scale",
            "eff_text_offset",
            "disp_frame_eff"
    };
    static final String[] listSound = {
            "use_hitsound",
            "use_starsound",
    };

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GameMechanismFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_gamemechanism);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            for (String s : listGameMechanism) {
                bindPreferenceSummaryToValue(findPreference(s));
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VisualPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_visual);
            setHasOptionsMenu(true);

            for (String s : listVisual) {
                bindPreferenceSummaryToValue(findPreference(s));
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SoundPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sound);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            for (String s : listSound) {
                bindPreferenceSummaryToValue(findPreference(s));
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

class MyOPCListener implements Preference.OnPreferenceChangeListener {
    Context appContext;
    ArrayList<String> floatKeys = new ArrayList<String>() {{
        add("disp_duration");
        add("scaling_duration");
        add("appear_position");
        add("viewport_scale");
        add("note_timing_offset");
        add("judge_timing_offset");
        add("eff_text_offset");
    }};

    private static boolean isFloat(String str) {
        try {
            float f = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static float parseFloat(String str) {
        return Float.parseFloat(str);
    }

    public MyOPCListener(Context appContext) {
        this.appContext = appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For listGameMechanism preferences, look up the correct display value in
            // the preference's 'entries' listGameMechanism.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
            return true;

        } else if (preference instanceof SwitchPreference) {
            SettingsActivity.universalPrefs.remove(preference.getKey());
            SettingsActivity.universalPrefs.put(preference.getKey(), value);
            return true;

        } else if (preference instanceof EditTextPreference) {
            if (!(value instanceof String))
                return false;
            if (floatKeys.contains(preference.getKey())) {
                if (!isFloat((String) value)) {
                    Toast.makeText(appContext, "请输入一个有效的小数\n本次输入作废", Toast.LENGTH_SHORT).show();
                    return false;
                }
                float valueF = parseFloat((String) value);
                if (preference.getKey().equals("appear_position")) {
                    if (valueF < 0f || valueF > 1f) {
                        Toast.makeText(appContext, "数字要在0.0和1.0之间的\n本次输入作废", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
            SettingsActivity.universalPrefs.remove(preference.getKey());
            SettingsActivity.universalPrefs.put(preference.getKey(), value);
            return true;

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            return true;
        }
    }
}

class P2GSetter implements Runnable {
    public P2GSetter() {
        super();
    }

    @Override
    public void run() {
        try {
            if (contain("debug_show"))
                Engine.showDebugInfo = (boolean) get("debug_show");
            if (contain("use_accurate_judgement"))
                Engine.useAccuratejudgementMethod = (boolean) get("use_accurate_judgement");
            if (contain("use_hitsound"))
                Engine.isPlayHitSE = (boolean) get("use_hitsound");
            if (contain("use_starsound"))
                Engine.isPlayStarSE = (boolean) get("use_starsound");
            if (contain("disp_duration"))
                BoardManager.noteDispTime = Float.parseFloat((String) get("disp_duration"));
            if (contain("scaling_duration"))
                BoardManager.noteScalingTime = Float.parseFloat((String) get("scaling_duration"));
            if (contain("appear_position"))
                MaiNote.note_appear_pos = Float.parseFloat((String) get("appear_position"));
            if (contain("viewport_scale"))
                Engine.viewportScale = Float.parseFloat((String) get("viewport_scale"));
            if (contain("note_timing_offset"))
                Engine.noteTimingOffset = Float.parseFloat((String) get("note_timing_offset"));
            if (contain("judge_timing_offset"))
                Engine.judgeTimingOffset = Float.parseFloat((String) get("judge_timing_offset"));
        } catch (Exception e) {
            System.out.println("Fail");
        }
    }

    boolean contain(String key) {
        return SettingsActivity.universalPrefs.containsKey(key);
    }

    Object get(String key) {
        return SettingsActivity.universalPrefs.get(key);
    }
}