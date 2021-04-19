package pollub.ism.lab06;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;


public class DatabaseManager extends SQLiteOpenHelper {

    private static final String TAG = "DBManager";

    private static final String DB_NAME = "Stoisko z warzywami";
    static final int VERSION = 1;
    public static final String TABLE_NAME = "Warzywniak";
    public static final String COLUMN_ONE_NAME = "NAME";
    public static final String COLUMN_TWO_NAME = "QUANTITY";

    private final Context context;

    DatabaseManager(Context context){
        super(context,DB_NAME,null,VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ONE_NAME+ " TEXT, " + COLUMN_TWO_NAME +" INTEGER)";
        sqLiteDatabase.execSQL(sqlQuery);

        ContentValues tuple = new ContentValues();
        String[] products = context.getResources().getStringArray(R.array.Products);

        for(String product : products){
            tuple.clear();
            tuple.put(COLUMN_ONE_NAME,product);
            tuple.put(COLUMN_TWO_NAME,0);
            sqLiteDatabase.insert(TABLE_NAME,null,tuple);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public int getProductQuantity(String selectedProduct){

        int amount = 0;
        SQLiteDatabase readDB = null;
        Cursor cursor = null;

        try{
            readDB = getReadableDatabase();

            cursor = readDB.query(
                    DatabaseManager.TABLE_NAME,
                    new String[]{DatabaseManager.COLUMN_TWO_NAME},
                    DatabaseManager.COLUMN_TWO_NAME + "=?", new String[]{selectedProduct},
                    null,null, null);
            cursor.moveToFirst();
            Log.i(TAG,"Reading from database completed");
        }catch (SQLException exception){
            Log.w(TAG,"Error reading from database");
        }
         finally {
            if(cursor!=null) cursor.close();
            if(readDB!=null) readDB.close();
        }
        return amount;
    }

    public void updateStorage(String chosenProduct, int newAmount){

        SQLiteDatabase writeDB = null;

        try{
            writeDB = getWritableDatabase();

            ContentValues tuple = new ContentValues();
            tuple.put(COLUMN_TWO_NAME, Integer.toString(newAmount));

            writeDB.update(TABLE_NAME, tuple, COLUMN_ONE_NAME + "=?", new String[]{chosenProduct});
            Log.i(TAG,"Writing to database completed");
        }catch (SQLException ex){
            Log.w(TAG,"Error saving to database");
        }finally {
            if(writeDB != null) writeDB.close();
        }
    }
}