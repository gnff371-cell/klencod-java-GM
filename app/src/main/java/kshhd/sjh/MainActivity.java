
package kshhd.sjh;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView status;
    private LinearLayout channelsLayout;
    private EditText urlInput;

    private final ArrayList<String[]> channels = new ArrayList<>();

    private int red = Color.rgb(235, 35, 55);
    private int background = Color.rgb(12, 14, 19);
    private int cardColor = Color.rgb(25, 28, 36);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        channels.add(new String[]{"SPORT LIVE 01", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"});
        channels.add(new String[]{"SPORT LIVE 02", ""});
        channels.add(new String[]{"SPORT LIVE 03", ""});
        channels.add(new String[]{"SPORT LIVE 04", ""});

        createPlayer();
        createInterface();
    }

    private void createPlayer() {
        player = new ExoPlayer.Builder(this).build();
    }

    private void createInterface() {

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(background);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(16), dp(18), dp(16), dp(25));

        scroll.addView(main);
        setContentView(scroll);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = new TextView(this);
        logo.setText("SPORT ");
        logo.setTextColor(Color.WHITE);
        logo.setTextSize(25);
        logo.setTypeface(null, Typeface.BOLD);

        TextView live = new TextView(this);
        live.setText("LIVE");
        live.setTextColor(red);
        live.setTextSize(25);
        live.setTypeface(null, Typeface.BOLD);

        header.addView(logo);
        header.addView(live);

        TextView subtitle = new TextView(this);
        subtitle.setText("منصتك الرياضية للبث المباشر");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(13);
        subtitle.setPadding(0, dp(5), 0, dp(15));

        main.addView(header);
        main.addView(subtitle);

        TextView section = new TextView(this);
        section.setText("المباراة الآن");
        section.setTextColor(Color.WHITE);
        section.setTextSize(18);
        section.setTypeface(null, Typeface.BOLD);
        section.setPadding(0, dp(8), 0, dp(12));
        main.addView(section);

        playerView = new PlayerView(this);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        GradientDrawable playerBg = new GradientDrawable();
        playerBg.setColor(Color.BLACK);
        playerBg.setCornerRadius(dp(14));
        playerView.setBackground(playerBg);
        playerView.setClipToOutline(true);

        main.addView(playerView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(215)));

        status = new TextView(this);
        status.setText("اختر قناة لبدء المشاهدة");
        status.setTextColor(Color.LTGRAY);
        status.setTextSize(12);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, dp(10), 0, dp(10));
        main.addView(status);

        Button addButton = makeButton("+ إضافة رابط بث");
        addButton.setOnClickListener(v -> showUrlDialog());
        main.addView(addButton);

        TextView channelTitle = new TextView(this);
        channelTitle.setText("القنوات الرياضية");
        channelTitle.setTextColor(Color.WHITE);
        channelTitle.setTextSize(19);
        channelTitle.setTypeface(null, Typeface.BOLD);
        channelTitle.setPadding(0, dp(22), 0, dp(12));
        main.addView(channelTitle);

        channelsLayout = new LinearLayout(this);
        channelsLayout.setOrientation(LinearLayout.VERTICAL);
        main.addView(channelsLayout);

        refreshChannels();
    }

    private void refreshChannels() {
        channelsLayout.removeAllViews();

        for (int i = 0; i < channels.size(); i++) {
            final int index = i;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(12), dp(14), dp(12));

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(cardColor);
            bg.setCornerRadius(dp(12));
            row.setBackground(bg);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(65));
            params.bottomMargin = dp(9);

            TextView name = new TextView(this);
            name.setText(channels.get(i)[0]);
            name.setTextColor(Color.WHITE);
            name.setTextSize(15);
            name.setTypeface(null, Typeface.BOLD);
            name.setGravity(Gravity.CENTER_VERTICAL);

            row.addView(name, new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

            TextView play = new TextView(this);
            play.setText("▶ مشاهدة");
            play.setTextColor(Color.WHITE);
            play.setTextSize(13);
            play.setGravity(Gravity.CENTER);
            play.setPadding(dp(13), dp(9), dp(13), dp(9));

            GradientDrawable playBg = new GradientDrawable();
            playBg.setColor(red);
            playBg.setCornerRadius(dp(8));
            play.setBackground(playBg);

            row.addView(play);

            row.setOnClickListener(v -> {
                String url = channels.get(index)[1];

                if (url == null || url.trim().isEmpty()) {
                    showUrlDialogForChannel(index);
                } else {
                    playChannel(url);
                }
            });

            channelsLayout.addView(row, params);
        }
    }

    private void playChannel(String url) {
        try {
            if (url == null || url.trim().isEmpty()) {
                status.setText("الرابط فارغ");
                return;
            }

            String cleanUrl = url.trim();

            MediaItem.Builder itemBuilder = new MediaItem.Builder()
                    .setUri(cleanUrl);

            if (cleanUrl.toLowerCase().contains(".m3u8")) {
                itemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8);
            }

            player.setMediaItem(itemBuilder.build());
            player.prepare();
            player.play();

            status.setText("جاري تشغيل البث...");
        } catch (Exception e) {
            status.setText("تعذر تشغيل الرابط");
        }
    }

    private void showUrlDialog() {
        showUrlDialogForChannel(-1);
    }

    private void showUrlDialogForChannel(int channelIndex) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18), dp(10), dp(18), dp(5));

        EditText nameInput = new EditText(this);
        nameInput.setHint("اسم القناة");
        nameInput.setSingleLine(true);
        box.addView(nameInput);

        EditText linkInput = new EditText(this);
        linkInput.setHint("رابط البث M3U8 أو MP4");
        linkInput.setSingleLine(true);
        linkInput.setInputType( android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_URI);
        box.addView(linkInput);

        new AlertDialog.Builder(this)
                .setTitle("إضافة قناة")
                .setView(box)
                .setPositiveButton("حفظ وتشغيل", (dialog, which) -> {

                    String name = nameInput.getText().toString().trim();
                    String url = linkInput.getText().toString().trim();

                    if (url.isEmpty()) {
                        status.setText("أدخل رابط البث أولاً");
                        return;
                    }

                    if (name.isEmpty()) {
                        name = "قناة جديدة";
                    }

                    if (channelIndex >= 0) {
                        channels.set(channelIndex, new String[]{name, url});
                    } else {
                        channels.add(new String[]{name, url});
                    }

                    refreshChannels();
                    playChannel(url);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14);
        button.setAllCaps(false);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(red);
        bg.setCornerRadius(dp(12));
        button.setBackground(bg);

        return button;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
