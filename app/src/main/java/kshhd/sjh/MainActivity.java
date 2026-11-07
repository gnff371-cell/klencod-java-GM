
package kshhd.sjh;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;

public class MainActivity extends Activity {

    private final int BG = 0xFF080B12;
    private final int PANEL = 0xFF141A26;
    private final int RED = 0xFFE32636;
    private final int WHITE = 0xFFF5F6FA;
    private final int MUTED = 0xFF929BAA;

    private VideoView videoView;
    private TextView playerMessage;
    private TextView statusText;

    private int dp(float value) {
        return (int)(value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setBackgroundColor(BG);

        // HEADER
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(15), dp(18), dp(15));
        header.setBackgroundColor(PANEL);

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("SPORT LIVE");
        logo.setTextColor(RED);
        logo.setTextSize(23);
        logo.setTypeface(null, Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("عالم الرياضة بين يديك");
        subtitle.setTextColor(MUTED);
        subtitle.setTextSize(11);

        brand.addView(logo);
        brand.addView(subtitle);

        header.addView(brand,
                new LinearLayout.LayoutParams(0, -2, 1));

        Button search = button("بحث");
        header.addView(search);

        search.setOnClickListener(v -> showSearch());

        root.addView(header);

        // MAIN SCROLL
        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setPadding(dp(15), dp(15), dp(15), dp(25));
        content.setOrientation(LinearLayout.VERTICAL);

        // LIVE TITLE
        TextView liveTitle = text("البث المباشر", 20, WHITE, true);
        content.addView(liveTitle);

        TextView liveSubtitle =
                text("شاهد قنواتك الرياضية المفضلة", 12, MUTED, false);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);
        subParams.topMargin = dp(5);
        subParams.bottomMargin = dp(14);
        content.addView(liveSubtitle, subParams);

        // VIDEO PLAYER
        FrameLayout player = new FrameLayout(this);
        player.setBackgroundColor(Color.BLACK);

        videoView = new VideoView(this);

        player.addView(videoView,
                new FrameLayout.LayoutParams(-1, -1));

        playerMessage = text(
                "SPORT LIVE\n\nاختر قناة لبدء المشاهدة",
                16, WHITE, true);

        playerMessage.setGravity(Gravity.CENTER);

        player.addView(playerMessage,
                new FrameLayout.LayoutParams(-1, -1));

        LinearLayout.LayoutParams playerParams =
                new LinearLayout.LayoutParams(-1, dp(215));

        playerParams.bottomMargin = dp(12);

        content.addView(player, playerParams);

        statusText = text("المشغل جاهز", 12, MUTED, false);
        content.addView(statusText);

        // CATEGORIES
        TextView categoryTitle =
                text("التصنيفات الرياضية", 18, WHITE, true);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);

        titleParams.topMargin = dp(25);
        titleParams.bottomMargin = dp(12);

        content.addView(categoryTitle, titleParams);

        LinearLayout categories = new LinearLayout(this);
        categories.setOrientation(LinearLayout.HORIZONTAL);

        String[] categoryNames = {
                "الكل", "كرة القدم", "كرة السلة", "التنس"
        };

        for (String name : categoryNames) {
            Button b = button(name);

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(-2, dp(42));

            p.setMargins(dp(3), 0, dp(3), 0);
            categories.addView(b, p);

            b.setOnClickListener(v ->
                    Toast.makeText(this,
                            name, Toast.LENGTH_SHORT).show());
        }

        content.addView(categories);

        // CHANNELS
        TextView channelsTitle =
                text("القنوات الرياضية", 19, WHITE, true);

        LinearLayout.LayoutParams channelTitleParams =
                new LinearLayout.LayoutParams(-1, -2);

        channelTitleParams.topMargin = dp(25);
        channelTitleParams.bottomMargin = dp(12);

        content.addView(channelsTitle, channelTitleParams);

        addChannel(content,
                "قناة رياضية 01",
                "قناة تجريبية - أضف رابط البث الخاص بك");

        addChannel(content,
                "قناة رياضية 02",
                "قناة تجريبية - أضف رابط البث الخاص بك");

        addChannel(content,
                "قناة رياضية 03",
                "قناة تجريبية - أضف رابط البث الخاص بك");

        // FOOTER
        TextView footer = text(
                "SPORT LIVE  |  الإصدار 1.0",
                11, MUTED, false);

        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, dp(45));

        footerParams.topMargin = dp(15);

        content.addView(footer, footerParams);

        scroll.addView(content);
        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);

        videoView.setOnPreparedListener(mp -> {
            playerMessage.setVisibility(View.GONE);
            statusText.setText("يتم تشغيل البث");
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            playerMessage.setVisibility(View.VISIBLE);
            playerMessage.setText("تعذر تشغيل البث\nتحقق من الرابط");
            statusText.setText("حدث خطأ في تشغيل الفيديو");
            return true;
        });
    }

    // CHANNEL CARD
    private void addChannel(LinearLayout parent,
                            String name, String description) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(13), dp(13), dp(13), dp(13));
        card.setBackground(background(PANEL, 15));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView channelName = text(name, 15, WHITE, true);
        TextView channelDescription =
                text(description, 11, MUTED, false);

        info.addView(channelName);
        info.addView(channelDescription);

        card.addView(info,
                new LinearLayout.LayoutParams(0, -2, 1));

        Button play = button("مشاهدة");
        card.addView(play);

        View.OnClickListener listener =
                v -> showStreamDialog(name);

        card.setOnClickListener(listener);
        play.setOnClickListener(listener);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(10);

        parent.addView(card, params);
    }

    // STREAM URL DIALOG
    private void showStreamDialog(String channelName) {

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextColor(WHITE);
        input.setHintTextColor(MUTED);
        input.setHint("https://example.com/live.m3u8");
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_URI);

        LinearLayout box = new LinearLayout(this);
        box.setPadding(dp(20), dp(10), dp(20), dp(5));
        box.addView(input);

        new AlertDialog.Builder(this)
                .setTitle(channelName)
                .setMessage("أدخل رابط البث المباشر:")
                .setView(box)
                .setPositiveButton("تشغيل", (d, w) -> {

                    String url = input.getText().toString().trim();

                    if (!url.startsWith("https://") &&
                            !url.startsWith("http://")) {
                        Toast.makeText(this,
                                "الرابط غير صحيح",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    playStream(url);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // PLAY STREAM
    private void playStream(String url) {

        try {
            playerMessage.setVisibility(View.VISIBLE);
            playerMessage.setText("جارٍ الاتصال بالبث...");

            statusText.setText("جارٍ تحميل الفيديو");

            videoView.stopPlayback();
            videoView.setVideoURI(Uri.parse(url));

            MediaController controller =
                    new MediaController(this);

            controller.setAnchorView(videoView);
            videoView.setMediaController(controller);

            videoView.start();

        } catch (Exception e) {
            playerMessage.setVisibility(View.VISIBLE);
            playerMessage.setText("تعذر تشغيل الرابط");
        }
    }

    // SEARCH
    private void showSearch() {

        EditText input = new EditText(this);
        input.setHint("اسم القناة");

        new AlertDialog.Builder(this)
                .setTitle("البحث عن قناة")
                .setView(input)
                .setPositiveButton("بحث", (d, w) -> {

                    String query =
                            input.getText().toString().trim();

                    Toast.makeText(this,
                            "البحث عن: " + query,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {

        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private Button button(String value) {

        Button b = new Button(this);
        b.setText(value);
        b.setTextSize(12);
        b.setAllCaps(false);
        b.setTextColor(WHITE);
        b.setPadding(dp(10), 0, dp(10), 0);
        b.setBackground(background(RED, 11));

        return b;
    }

    private GradientDrawable background(int color, int radius) {

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(radius));

        return bg;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (videoView != null) {
            videoView.stopPlayback();
        }

        super.onDestroy();
    }
}
