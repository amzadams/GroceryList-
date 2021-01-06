package wapmass.grocerylist.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import wapmass.grocerylist.Adapter.RecyclerviewAdapter;
import wapmass.grocerylist.Data.Database;
import wapmass.grocerylist.Model.Grocery;
import wapmass.grocerylist.R;

public class ListActivity extends AppCompatActivity {
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog();
            }
        });

        setData();

    }

    private void setData(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        ArrayList<Grocery> groceries = db.getAllGroceries();
        RecyclerviewAdapter adapter = new RecyclerviewAdapter(this, groceries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void AddDialog(){
        //get builder, inflate view, set view, create dialog and show it
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View addView = getLayoutInflater().inflate(R.layout.add_item, null);
        final Button save = addView.findViewById(R.id.saveButton);
        builder.setView(addView);
        final Dialog dialog = builder.create();
        dialog.show();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                EditText name = addView.findViewById(R.id.name);
                EditText qty = addView.findViewById(R.id.qty);
                String groceryName = name.getText().toString();
                String groceryQty = qty.getText().toString();
                if (groceryName.isEmpty() && groceryQty.isEmpty()) {
                    Snackbar.make(view, "Fill the Form", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    saveGroceryToDB(groceryName, groceryQty);
                    dialog.dismiss();
                }
            }
        });
    }

    private void saveGroceryToDB(String name, String quantity) {
        Grocery grocery = new Grocery();
        grocery.setName(name);
        grocery.setQuantity(quantity);
        try{
            if (db.addData(grocery)){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Date Saved", Toast.LENGTH_LONG).show();
                        //start a new activity
                        startActivity(new Intent(ListActivity.this, ListActivity.class));
                        finish();
                    }
                }, 1200);
            }else{
                Toast.makeText(getApplicationContext(), "Date Not Saved", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
