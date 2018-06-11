package es.udc.apm.familycare.members;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.List;

import es.udc.apm.familycare.R;
import es.udc.apm.familycare.model.Member;

/**
 * Created by Gonzalo on 14/04/2018.
 * Member adapter to use with member list
 */

public class MemberAdapter extends ArrayAdapter<Member>  {

    private List<Member> values;

    private TextDrawable.IShapeBuilder builder = TextDrawable.builder()
            .beginConfig().toUpperCase().endConfig();

    static class ViewHolder {
        TextView itemName;
        ImageView itemImg;
    }

    MemberAdapter(Context context, List<Member> values) {
        super(context, -1, values);
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            convertView = inflater.inflate(R.layout.lv_item_member, parent, false);
            holder = new ViewHolder();
            holder.itemName = convertView.findViewById(R.id.tv_item_name);
            holder.itemImg = convertView.findViewById(R.id.img_item);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        // Background of selected item
        Drawable background = getContext().getResources().getDrawable(R.drawable.list_selector);
        background.setColorFilter(getContext().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        convertView.setBackground(background);

        Member member = values.get(position);

        //Name
        holder.itemName.setText(member.getName());

        //Image
        String letter = Normalizer.normalize(String.valueOf(member.getName().charAt(0)),
                Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(letter);
        TextDrawable drawable = builder.buildRound(letter, color);
        if(member.getPhoto() != null && !member.getPhoto().isEmpty()) {
            Picasso.get().load(member.getPhoto()).error(drawable).into(holder.itemImg);
        } else {
            holder.itemImg.setImageDrawable(drawable);
        }

        return convertView;
    }

}
