package com.example.android.bookStore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bookStore.R;
import com.example.android.bookStore.pojo.CatalogItemDetail;

import java.util.List;

/**
 * This class would act as an adapter for the list of products displayed on catalog screen
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final ICatalogListContract mICatalogListContract;
    private List<CatalogItemDetail> mListItems;

    public CatalogAdapter(Context mContext, ICatalogListContract iCatalogListContract,
                          List<CatalogItemDetail> listCatalogItems) {
        this.mContext = mContext;
        this.mICatalogListContract = iCatalogListContract;
        this.mListItems = listCatalogItems;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_product, parent, false);
        return new CatalogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CatalogViewHolder holder, int position) {
        holder.position = position;
        CatalogItemDetail catalogItemDetail = mListItems.get(position);
        holder.textProductName.setText(mContext.getString(
                R.string.label_product_name,
                position + 1 + "",
                catalogItemDetail.getItemName())
        );
        holder.textProductPrice.setText(mContext.getString(
                R.string.label_product_price,
                catalogItemDetail.getItemPrice() + ""));
        holder.textProductQuantity.setText(mContext.getString(
                R.string.label_product_quantity,
                catalogItemDetail.getItemQuantity() + ""));
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }


    public class CatalogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textProductPrice;
        TextView textProductQuantity;
        TextView textProductName;
        TextView textSale;
        TextView textEdit;
        int position;

        CatalogViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            itemView.setOnClickListener(this);
            textProductPrice = itemView.findViewById(R.id.text_itemPrice);
            textProductQuantity = itemView.findViewById(R.id.text_itemQuantity);
            textProductName = itemView.findViewById(R.id.text_itemName);
            textSale = itemView.findViewById(R.id.text_sale);
            textSale.setOnClickListener(this);
            textEdit = itemView.findViewById(R.id.text_edit);
            textEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_sale:
                    if (mICatalogListContract != null)
                        mICatalogListContract.onProductSale(position);
                    break;
                case R.id.text_edit:
                    if (mICatalogListContract != null)
                        mICatalogListContract.onProductEdit(position);
                    break;
                default:
                    if (mICatalogListContract != null)
                        mICatalogListContract.onProductSelected(position);

            }
        }
    }

    public interface ICatalogListContract {
        void onProductSale(int position);

        void onProductEdit(int position);

        void onProductSelected(int position);
    }
}