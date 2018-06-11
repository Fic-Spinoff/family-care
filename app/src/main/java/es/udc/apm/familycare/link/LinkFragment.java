package es.udc.apm.familycare.link;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.utils.Constants;

public class LinkFragment extends Fragment {

    private RouterActivity routerActivity = null;

    @BindView(R.id.link_toolbar) Toolbar toolbar;
    @BindView(R.id.et_link_key)
    TextView etLink;

    private LinkViewModel viewModel;

    public static LinkFragment newInstance() {
        return new LinkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_link, container, false);
        ButterKnife.bind(this, v);

        if(this.routerActivity != null) {
            this.routerActivity.setActionBar(this.toolbar);
        }

        //etLink.setKeyListener(null);

        String uid = "";
        if (getActivity() != null) {
            uid = getActivity().getSharedPreferences(Constants.PREFS_USER, Context.MODE_PRIVATE)
                    .getString(Constants.PREFS_USER_UID, null);
        }
        this.viewModel = new LinkViewModel(new UserRepository(), uid);

        FamilyCare.getUser().observe(this, user -> {
            if (user != null && user.getLink() != null) {
                this.etLink.setText(user.getLink());
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.routerActivity = (RouterActivity) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " must implement RouterActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.routerActivity = null;
    }

    @OnClick(R.id.et_link_key) void onClickLink() {
        if(this.etLink.getText() == null) {
            return;
        }

        // Get clipboard manager
        ClipboardManager clipboardManager = (ClipboardManager) FamilyCare.app
                .getSystemService(Context.CLIPBOARD_SERVICE);
        String link = this.etLink.getText().toString();

        // If link and manager then copy
        if (clipboardManager != null && !link.isEmpty()) {
            ClipData clip = ClipData.newPlainText(Constants.Properties.LINK, link);
            clipboardManager.setPrimaryClip(clip);
            // Show message copied
            Toast.makeText(FamilyCare.app,
                    getString(R.string.caption_link_copied), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_link_refresh) void onClickRefresh(View v) {
        v.setVisibility(View.INVISIBLE);
        this.viewModel.refreshLink().observe(this, s -> {
            v.setVisibility(View.VISIBLE);
            if (s != null) {
                this.etLink.setText(s);
            }
        });
    }

    @OnClick(R.id.btn_link_share) void onClickShare() {
        if(this.etLink.getText() == null) {
            return;
        }

        String link = this.etLink.getText().toString();
        if (!link.isEmpty()) {
            this.shareLink(getString(R.string.text_share_link, link));
        }
    }

    @OnClick(R.id.btn_link_share_link) void onClickShareLink() {
        // TODO
    }

    private void shareLink(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType(Constants.MimeType.TEXT);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.label_share)));
    }
}
