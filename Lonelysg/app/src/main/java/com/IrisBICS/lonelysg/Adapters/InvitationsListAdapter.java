package com.IrisBICS.lonelysg.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.IrisBICS.lonelysg.Models.Invitation;
import com.IrisBICS.lonelysg.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class InvitationsListAdapter extends ArrayAdapter<Invitation> implements Filterable {

        private ArrayList<Invitation> invitationsList;
        private ArrayList<Invitation> displayedInvitationsList;
        private Activity context;

        public ArrayList<Invitation> getDisplayedList(){
            return displayedInvitationsList;
        }

        public InvitationsListAdapter(Activity context, ArrayList<Invitation> invitations) {
            super(context, R.layout.invitation_list_layout,invitations);
            this.context = context;
            this.invitationsList = invitations;
            this.displayedInvitationsList = invitations;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View r = convertView;
            ViewHolder viewHolder;
            if(r==null) {
                LayoutInflater layoutInflater = context.getLayoutInflater();
                r = layoutInflater.inflate(R.layout.invitation_list_layout,null,true);
                viewHolder = new ViewHolder(r);
                r.setTag(viewHolder);
            }
            else viewHolder = (ViewHolder) r.getTag();
            viewHolder.invitationTitle.setText(displayedInvitationsList.get(position).getTitle());
            viewHolder.invitationDateTime.setText(displayedInvitationsList.get(position).getDate()+" " + displayedInvitationsList.get(position).getStartTime()+" - " + displayedInvitationsList.get(position).getEndTime());
            viewHolder.invitationLocation.setText(displayedInvitationsList.get(position).getLocationName());
            if (displayedInvitationsList.get(position).getInvPic()!=null) {
                Picasso.get().load(displayedInvitationsList.get(position).getInvPic()).into(viewHolder.invitationImage);
            }
            return r;
        }

        class ViewHolder {
            TextView invitationTitle;
            TextView invitationDateTime;
            TextView invitationLocation;
            CircleImageView invitationImage;

            ViewHolder(View v) {
                invitationTitle = v.findViewById(R.id.invitationTitle);
                invitationDateTime = v.findViewById(R.id.invitationDateTime);
                invitationLocation = v.findViewById(R.id.invitationLocation);
                invitationImage = v.findViewById(R.id.invListImage);
            }
        }

        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults oReturn = new FilterResults();
                    final ArrayList<Invitation> results = new ArrayList<>();
                    if (invitationsList == null)
                        invitationsList = displayedInvitationsList;
                    if (constraint != null) {
                        if (invitationsList != null && invitationsList.size() > 0) {
                            for (final Invitation i : invitationsList) {
                                if ((i.getTitle().toLowerCase().contains(constraint.toString()))|(i.getLocationName().toLowerCase().contains(constraint.toString())))
                                    results.add(i);
                            }
                        }
                        oReturn.values = results;
                    }
                    return oReturn;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    displayedInvitationsList = (ArrayList<Invitation>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return displayedInvitationsList.size();
        }

        @Override
        public Invitation getItem(int position) {
            return displayedInvitationsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


}




