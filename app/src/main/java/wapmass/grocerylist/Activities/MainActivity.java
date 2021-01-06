package wapmass.grocerylist.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import wapmass.grocerylist.Data.Database;
import wapmass.grocerylist.Model.Grocery;
import wapmass.grocerylist.R;

public class MainActivity extends AppCompatActivity {
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new Database(this);
        byPassActivity();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        startActivity(new Intent(MainActivity.this, ListActivity.class));
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
    public void byPassActivity() {
        if (db.getGroceriesCount() > 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
            finish();
        }
    }
}
