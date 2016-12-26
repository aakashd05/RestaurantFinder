package beitproject.com.restrofinder.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import beitproject.com.restrofinder.R;
import beitproject.com.restrofinder.models.RestaurantModel;

/**
 * Created by AkaashD on 12/23/2016.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.SingleLookHolder> {
    Activity activity;
    ArrayList<RestaurantModel> allData = new ArrayList<>();
    boolean toggle;

    public RestaurantListAdapter(ArrayList<RestaurantModel> listData, Activity activity) {
        this.allData = listData;
        this.activity = activity;
    }

    public void showAll() {
        toggle = true;
        notifyDataSetChanged();
    }

    public boolean isShowingAll() {
        return toggle;
    }

    public void showTopThree() {
        toggle = false;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<RestaurantModel> listData) {
        this.allData = listData;
        notifyDataSetChanged();
    }

    @Override
    public SingleLookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_restaurant_list, parent, false);
        SingleLookHolder singleLookHolder = new SingleLookHolder(view);
        return singleLookHolder;
    }

    @Override
    public void onBindViewHolder(SingleLookHolder holder, int position) {
        RestaurantModel RestaurantModel = allData.get(position);
        holder.restaurantName.setText(RestaurantModel.name);
        holder.restaurantAddress.setText(RestaurantModel.address);
        Picasso
                .with(activity)
                .load(RestaurantModel.iconUrl)
                .into(holder.restaurantIcon);
        holder.ratingBar.setRating(Float.parseFloat(RestaurantModel.rating));
    }

    @Override
    public int getItemCount() {
            return toggle ? allData.size() : (allData.size() > 3 ? 3 : allData.size());
    }

    public class SingleLookHolder extends RecyclerView.ViewHolder {
        TextView restaurantName, restaurantAddress;
        ImageView restaurantIcon;
        RatingBar ratingBar;

        public SingleLookHolder(View itemView) {
            super(itemView);
            View view = itemView;
            restaurantAddress = (TextView) view.findViewById(R.id.restaurant_address);
            restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
            restaurantIcon = (ImageView) view.findViewById(R.id.restaurant_image);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        }
    }
}
