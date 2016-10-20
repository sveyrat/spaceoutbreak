package com.github.sveyrat.spaceoutbreak.display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.List;

public class RoleNightAdapter extends BaseAdapter {

    private final Context context;
    private List<Role> roles;

    public RoleNightAdapter(Context context, List<Role> roles) {
        super();
        this.context = context;
        this.roles = roles;
    }

    @Override
    public int getCount() {
        return roles.size();
    }

    @Override
    public Role getItem(int position) {
        return roles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Role role = roles.get(position);
        View roleView = convertView;
        if (roleView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            roleView = inflater.inflate(R.layout.role_item, parent, false);
        }
        TextView roleName = (TextView) roleView.findViewById(R.id.role_name);
        roleName.setText(context.getResources().getString(role.getLabelResourceId()));

        ImageView selectedIndicatorImageView = (ImageView) roleView.findViewById(R.id.selected_image);
        selectedIndicatorImageView.setVisibility(View.GONE);

        ImageView playerRolePicto = (ImageView) roleView.findViewById(R.id.player_role_picto);
        playerRolePicto.setImageResource(role.getImageResourceId());

        return roleView;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
