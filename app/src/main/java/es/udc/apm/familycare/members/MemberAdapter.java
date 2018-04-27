package es.udc.apm.familycare.members;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.text.Normalizer;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.R;

/**
 * Created by Gonzalo on 14/04/2018.
 * Member adapter to use with member list
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    public interface OnMemberSelectedListener {
        void onMemberSelected(Member member);
    }

    private List<Member> values;
    private OnMemberSelectedListener mListener;

    MemberAdapter(List<Member> values, OnMemberSelectedListener listener) {
        this.values = values;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.lv_item_member, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(values.get(position), this.mListener);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextDrawable.IShapeBuilder builder = TextDrawable.builder()
                .beginConfig().toUpperCase().endConfig();

        @BindView(R.id.tv_item_name)
        TextView itemName;

        @BindView(R.id.img_item)
        ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Member member, final OnMemberSelectedListener listener) {
            //Name
            this.itemName.setText(member.getName());

            //Image
            // TODO: In memory letter cache
            String letter = Normalizer.normalize(String.valueOf(member.getName().charAt(0)),
                    Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(letter);
            TextDrawable drawable = builder.buildRound(letter, color);
            itemImage.setImageDrawable(drawable);

            // Click listener
            this.itemView.setOnClickListener(v -> {
                if(listener != null) {
                    // Set selected background
                    this.itemView.setBackground(background);

                    // Call listener
                    listener.onMemberSelected(member);
                }
            });
        }
    }
}
