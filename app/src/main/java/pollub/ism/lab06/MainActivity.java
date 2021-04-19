package pollub.ism.lab06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import pollub.ism.lab06.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding mainBinding;
    private ArrayAdapter<CharSequence> adapter;

    DatabaseManager DB;
    String chosenProductName = null;
    Integer chosenProductAmount = null;

    public enum DBOperation {STORE, GIVEAWAY};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding =  ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        adapter = ArrayAdapter.createFromResource(this,R.array.Products, android.R.layout.simple_dropdown_item_1line);
        mainBinding.spinner.setAdapter(adapter);

        DB = new DatabaseManager(this);

        mainBinding.buttonStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState(DBOperation.STORE);
            }
        });

        mainBinding.buttonGiveaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState(DBOperation.GIVEAWAY);
            }
        });

        mainBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                chosenProductName = adapter.getItem(position).toString();
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void update(){
        chosenProductAmount = DB.getProductQuantity(chosenProductName);
        mainBinding.currentStorageState.setText("Magazyn ma "+chosenProductAmount+" sztuk towaru "+chosenProductName);
    }

    private void changeState(DBOperation operation){
        Integer amountChange = null;
        Integer newAmount = null;

        try {
            amountChange = Integer.parseInt(mainBinding.editAmount.getText().toString());
        }catch(NumberFormatException ex){
            Log.w(TAG,"Incorrect amount supplied");
            return;
        }finally {
            mainBinding.editAmount.setText("");
        }

        switch (operation){
            case STORE:
                newAmount  = chosenProductAmount + amountChange;
                break;
            case GIVEAWAY:
                newAmount = chosenProductAmount - amountChange;
                break;
        }

        DB.updateStorage(chosenProductName, newAmount);

        update();
    }
}