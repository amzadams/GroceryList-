package wapmass.grocerylist.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import wapmass.grocerylist.Data.Database;
import wapmass.grocerylist.Model.Grocery;
import wapmass.grocerylist.R;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.RowClass> {
    private Context context;
    private ArrayList<Grocery> groceries;
    public RecyclerviewAdapter(Context context, ArrayList<Grocery> groceries){
        this.context = context;
        this.groceries = groceries;
    }
    @Override
    public RecyclerviewAdapter.RowClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_row, parent, false);
        return new RowClass(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerviewAdapter.RowClass holder, int position) {
        Grocery grocery = groceries.get(position);
        holder.name.setText(grocery.getName());
        holder.qty.setText("Qty: "+ grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDateItemAdded());

    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

    public class RowClass  extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, qty, dateAdded;
        public Button edit, delete;
        public RowClass(View view, Context context) {
            super(view);
            name = view.findViewById(R.id.groceryName);
            qty = view.findViewById(R.id.quantity);
            dateAdded = view.findViewById(R.id.dateAdded);

            edit = view.findViewById(R.id.editButton);
            delete = view.findViewById(R.id.deleteButton);

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.editButton:
                    int position = getAdapterPosition();
                    Grocery grocery = groceries.get(position);
                    editItem(grocery);
                    break;

                case R.id.deleteButton:
                    position = getAdapterPosition();
                    grocery = groceries.get(position);
                    deleteItem(grocery.getId());

                    break;
            }

        }

        public void editItem(final Grocery grocery) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            final View view = LayoutInflater.from(context).inflate(R.layout.add_item, null);

            final EditText groceryItem = view.findViewById(R.id.name);
            final EditText quantity =  view.findViewById(R.id.qty);
            final TextView title = view.findViewById(R.id.title);

            groceryItem.setText(grocery.getName());
            quantity.setText(grocery.getQuantity());

            title.setText("Edit Grocery");
            Button saveButton = view.findViewById(R.id.saveButton);


            alertDialogBuilder.setView(view);
            final Dialog dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Database db = new Database(context);
                    //Update item
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(quantity.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty()
                            && !quantity.getText().toString().isEmpty()) {
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);
                    }else {
                        Snackbar.make(view, "Add Grocery and Quantity", Snackbar.LENGTH_LONG).show();
                    }

                    dialog.dismiss();

                }
            });

        }

        public void deleteItem(final int id) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            final View view = LayoutInflater.from(context).inflate(R.layout.confirmation_dialog, null);
            Button noButton =  view.findViewById(R.id.noButton);
            Button yesButton =  view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            final Dialog dialog = alertDialogBuilder.create();
            dialog.show();


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the item.
                    Database db = new Database(context);
                    //delete item
                    db.deleteGrocery(id);
                    groceries.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

        }

    }

}

