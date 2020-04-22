package org.pepppt.sample.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.events.CoreEvent;
import org.pepppt.core.events.MessagesEventArgs;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.core.messages.models.Message;
import org.pepppt.sample.R;
import org.pepppt.sample.ui.utils.AlertDialogBuilder;
import org.pepppt.sample.ui.utils.CallbackInfos;

import java.util.Locale;

public class MessagesActivity extends AppCompatActivity {

    LinearLayout messages_linear_layout_list;
    String lang_iso2;
    private static final String TEL_PREFIX = "tel:";
    private static final String BROWSER_PREFIX = "http";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        EventBus.getDefault().register(this);
        //TODO: only for Testing
        lang_iso2 = "de";//Locale.getDefault().getLanguage();
        ProximityTracingService.getInstance().requestAllMessages(lang_iso2);
//        ProximityTracingService.getInstance().loadAllFakeMessages();
        //TODO: show spinner
        messages_linear_layout_list = (LinearLayout) findViewById(R.id.messages_linear_layout_list);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CallbackInfos callbackInfos) {
        CoreEvent event = callbackInfos.getEvent();

        DialogInterface.OnClickListener reCheckMessages = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProximityTracingService.getInstance().requestAllMessages(lang_iso2);
            }
        };

        switch (event) {
            case LOAD_ALL_MESSAGES_SUCCESSFULLY: {
                //TODO: hide spinner
                MessagesEventArgs messagesEventArgs = (MessagesEventArgs) callbackInfos.getArgs();
                messages_linear_layout_list.removeAllViews();
                for (Message message : messagesEventArgs.getMessages()) {

                    String html = "<div style='margin:0 0 8 0px;background-color:white;padding:0 5 5 5px;'>";
                    html = html + "<h1>" + message.getMobileHeadline() + "</h1>";
                    html = html + "<h2>" + message.getMobileSubline() + "</h2>";
                    html = html + "<div>" + message.getMobileMessage() + "</div>";
                    if (message.getMobileCtaTarget() != null && message.getMobileCtaTarget().length() > 0) {
                        html = html + "<div style='padding: 5 0 0 0px;'><a href='" + message.getMobileCtaTarget() + "'>" + message.getMobileCtaTarget() + "</a></div>";
                    }
                    if (message.getMobileCta() != null && message.getMobileCta().length() > 0) {
                        html = html + "<div style='padding: 5 0 0 0px;'><a href='tel:" + message.getMobileCta() + "'>" + message.getMobileCta() + "</a></div>";
                    }

                    html = html + "</div>";

                    LinearLayout linearLayoutInner = new LinearLayout(MessagesActivity.this);
                    linearLayoutInner.setOrientation(LinearLayout.VERTICAL);
                    messages_linear_layout_list.addView(linearLayoutInner);

                    LinearLayout linearLayoutHeader = new LinearLayout(MessagesActivity.this);
                    linearLayoutHeader.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayoutHeader.setBackgroundColor(Color.TRANSPARENT);

                    linearLayoutHeader.setPadding(0, 18, 0, 0);
                    linearLayoutInner.addView(linearLayoutHeader);

                    TextView textViewDate = new TextView(MessagesActivity.this);
                    linearLayoutHeader.addView(textViewDate);
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    textViewDate.setLayoutParams(params);
                    textViewDate.setTextColor(Color.BLACK);
                    textViewDate.setTextSize(18);
                    textViewDate.setPadding(18, 5, 0, 0);
                    textViewDate.setText(message.getMobileEnctime());
                    textViewDate.setBackgroundColor(Color.WHITE);


                    TextView textViewClose = new TextView(MessagesActivity.this);
                    linearLayoutHeader.addView(textViewClose);
                    textViewClose.setText("x");
                    textViewClose.setTextColor(Color.RED);
                    textViewClose.setTextSize(18);
                    textViewClose.setTypeface(null, Typeface.BOLD);
                    textViewClose.setPadding(0, 5, 18, 0);
                    textViewClose.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    textViewClose.setBackgroundColor(Color.WHITE);
                    textViewClose.setGravity(Gravity.RIGHT);
                    textViewClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProximityTracingService.getInstance().confirmMessage(message.getId());
                        }
                    });


                    WebView webView = new WebView(MessagesActivity.this);
                    webView.setPadding(0, 0, 0, 8);
                    webView.setWebViewClient(new CustomWebViewClient());
                    webView.setBackgroundColor(Color.WHITE);
                    webView.loadData(html, "text/html; charset=UTF-8", null);
                    linearLayoutInner.addView(webView);
                }
                break;
            }
            case LOAD_ALL_MESSAGES_FAILED:
            case NO_NEW_MESSAGES: {
                //TODO: hide spinner
                messages_linear_layout_list.removeAllViews();
                TextView textView = new TextView(MessagesActivity.this);
                textView.setText(getString(R.string.messages_no_messages));
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                messages_linear_layout_list.addView(textView);
            }
            break;


            case MESSAGE_CONFIRMED:
                AlertDialogBuilder.showSimpleWithOk(MessagesActivity.this, "Messages", "MESSAGE_CONFIRMED", null);
                ProximityTracingService.getInstance().requestAllMessages(lang_iso2);
//                ProximityTracingService.getInstance().loadAllFakeMessages();
                break;
            case CONFIRM_MESSAGES_FAILED:
                AlertDialogBuilder.showSimpleWithOk(MessagesActivity.this, "Messages", "CONFIRM_MESSAGES_FAILED", null);
                ProximityTracingService.getInstance().requestAllMessages(lang_iso2);
//                ProximityTracingService.getInstance().loadAllFakeMessages();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void dataButtonOnClick(View view) {
        Intent intent = new Intent(MessagesActivity.this, MainDataActivity.class);
        startActivity(intent);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            if (url != null) {
                if (url.startsWith(TEL_PREFIX)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else if (url.startsWith(BROWSER_PREFIX)) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (ProximityTracingService.getInstance().hasBeenDecommissioned()) {
                Intent nextScreen = new Intent(this, UploadFinishedActivity.class);
                startActivity(nextScreen);
            }
        } catch (PackageNotFoundException e) {
            e.printStackTrace();
        }
    }
}

