package wapmass.grocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import wapmass.grocerylist.Model.Grocery;
import wapmass.grocerylist.Util.Constants;

public class Database  extends SQLiteOpenHelper{
    private Context context;
    public Database(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String creat_table = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.GROCERY_ID + " INTEGER PRIMARY KEY,"
                + Constants.GROCERY_NAME + " TEXT,"
                + Constants.GROCERY_QTY + " TEXT,"
                + Constants.GROCERY_ADDED_DATE + " LONG);";
        sqLiteDatabase.execSQL(creat_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(Grocery grocery){
        SQLiteDatabase db = this.getWritableDatabase();
        //HashMap
        ContentValues values = new ContentValues();
        values.put(Constants.GROCERY_QTY, grocery.getQuantity());
        values.put(Constants.GROCERY_NAME, grocery.getName());
        values.put(Constants.GROCERY_ADDED_DATE,System.currentTimeMillis());//get system time
        return (db.insert(Constants.TABLE_NAME, null, values) != -1);
    }

    public Grocery getGrocery(int id){
        //get readable db, get cursor, set values and return object
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                Constants.GROCERY_ID, Constants.GROCERY_NAME, Constants.GROCERY_QTY, Constants.GROCERY_ADDED_DATE
        }, Constants.GROCERY_ID + "=?",new String[]{String.valueOf(id)}, null, null, null, null);
        Grocery grocery = new Grocery();
        if(cursor != null){
            cursor.moveToFirst();
            grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_ID))));
            grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_NAME)));
            //convert time to readable format
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.GROCERY_ADDED_DATE))).getTime());
            grocery.setDateItemAdded(formatedDate);
        }
        return grocery;
    }

    public ArrayList<Grocery> getAllGroceries(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Grocery> groceryList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.GROCERY_ID, Constants.GROCERY_NAME, Constants.GROCERY_QTY, Constants.GROCERY_ADDED_DATE}, null, null, null, null, Constants.GROCERY_ADDED_DATE + " DESC");
        if(cursor.moveToFirst()){
            do{
                Grocery grocery = new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_NAME)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.GROCERY_QTY)));
                //convert time to readable format
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.GROCERY_ADDED_DATE))).getTime());
                grocery.setDateItemAdded(formatedDate);

                groceryList.add(grocery);
            }while(cursor.moveToNext());
        }
        return groceryList;
    }

    public int updateGrocery(Grocery grocery){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.GROCERY_NAME, grocery.getName());
        values.put(Constants.GROCERY_QTY, grocery.getQuantity());
        values.put(Constants.GROCERY_ADDED_DATE, System.currentTimeMillis());

        return db.update(Constants.TABLE_NAME, values, Constants.GROCERY_ID + "=?", new String[]{String.valueOf(grocery.getId())});
    }

    public void deleteGrocery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.GROCERY_ID+ "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    //Get count
    public int getGroceriesCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

}
