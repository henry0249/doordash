package sample.doordash.com.doordash.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sample.doordash.com.doordash.domain.MenuItem;

/**
 * Created by Hakeem on 1/22/17.
 */

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.MenuItemViewHolder> {
    private List<MenuItem> mItems;
    private Context mContext;

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mMenuItemName;

        public MenuItemViewHolder(View view) {
            super(view);
            mMenuItemName = (TextView) view;
        }

        public void updateView(MenuItem item) {
            mMenuItemName.setText(item.mName);
        }

        public View getView(){
            return mMenuItemName;
        }
    }

    public MenuItemsAdapter(Context context, List<MenuItem> items) {
        mContext = context;
        mItems = items;
    }

    public void update(List<MenuItem> items){
        this.mItems.clear();
        this.mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        holder.updateView(mItems.get(position));

        final MenuItem item = mItems.get(position);
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Item touched: " + item.mName, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
