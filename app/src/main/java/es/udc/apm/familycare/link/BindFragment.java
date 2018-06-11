package es.udc.apm.familycare.link;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.utils.Constants;

public class BindFragment extends Fragment {

    private RouterActivity routerActivity = null;

    @BindView(R.id.bind_toolbar) Toolbar toolbar;
    @BindView(R.id.et_bind_key) EditText etBind;
    @BindView(R.id.btn_bind_bind) Button btnBind;
    @BindView(R.id.btn_bind_unbind) Button btnUnbind;

    private UserRepository mRepo;
    private User user = null;

    public static BindFragment newInstance() {
        return new BindFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bind, container, false);
        ButterKnife.bind(this, v);

        if(this.routerActivity != null) {
            this.routerActivity.setActionBar(this.toolbar);
        }

        this.mRepo = new UserRepository();

        FamilyCare.getUser().observe(this, user -> {
            this.user = user;
            if (user == null || user.getVip() == null || user.getVip().isEmpty()) {
                setUIUnbinded();
                return;
            }

            setUIBinded(user.getVipName());
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

    @OnClick(R.id.btn_bind_bind) void onClickBind() {
        String link = etBind.getText().toString();
        if(link.trim().isEmpty()) {
            Toast.makeText(getActivity(), R.string.caption_no_key, Toast.LENGTH_SHORT).show();
            return;
        }

        // Update UI
        this.btnBind.setVisibility(View.GONE);
        this.etBind.setEnabled(false);
        this.mRepo.getUserByLink(link).observe(this, users -> {
            if(users != null && users.size() == 1) {
                // Save in db
                if (this.user != null) {
                    User vipUser = users.get(0);
                    Map<String, Object> values = new HashMap<>();
                    values.put(Constants.Properties.VIP, vipUser.getUid());
                    values.put(Constants.Properties.VIP_NAME, vipUser.getName());
                    this.mRepo.editUser(this.user.getUid(), values);
                }
            } else {
                // Redo UI
                this.setUIUnbinded();
                Toast.makeText(getActivity(), R.string.caption_invalid_key, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_bind_unbind) void onClickUnbind() {
        this.btnUnbind.setVisibility(View.GONE);
        if (this.user != null) {
            Map<String, Object> values = new HashMap<>();
            values.put(Constants.Properties.VIP, null);
            values.put(Constants.Properties.VIP_NAME, null);
            this.mRepo.editUser(this.user.getUid(), values);
        }
        this.btnBind.setVisibility(View.VISIBLE);
        this.etBind.setEnabled(true);
    }

    private void setUIBinded(String name) {
        this.etBind.setEnabled(false);
        this.etBind.setText(name);
        this.btnBind.setVisibility(View.GONE);
        this.btnUnbind.setVisibility(View.VISIBLE);
    }

    private void setUIUnbinded() {
        this.etBind.setText("");
        this.etBind.setEnabled(true);
        this.btnBind.setVisibility(View.VISIBLE);
        this.btnUnbind.setVisibility(View.GONE);
    }
}
