package example.ngondo.dbexample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase contactDB = null;

    Button btncreateDB, btnAddContact, btnDeleteContact, btnGetContact, btnDeleteDB;
    EditText nameEditText, emailEditText, idEditText, contactListEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the view elements
        btncreateDB = (Button) findViewById(R.id.btnCreateDB);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnDeleteContact = (Button) findViewById(R.id.btnDeleteContact);
        btnGetContact = (Button) findViewById(R.id.btngetContact);
        btnDeleteDB = (Button) findViewById(R.id.btnDeleteDB);
        nameEditText = (EditText) findViewById(R.id.nameText);
        emailEditText = (EditText) findViewById(R.id.emailText);
        idEditText = (EditText) findViewById(R.id.idText);
        contactListEditText = (EditText) findViewById(R.id.contactList);
    }

    public void createDatabase(View view) {
        try{
            contactDB = this.openOrCreateDatabase("MyContacts", MODE_PRIVATE, null);
            contactDB.execSQL("CREATE TABLE IF NOT EXISTS contacts " +
                    "(id integer primary key, name VARCHAR, email VARCHAR);");
            File database = getApplicationContext().getDatabasePath("MyContacts.db");

            if(!database.exists()){
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Database Missing! Sorry", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Log.e("CONTACTS ERROR", "Error creating database");
        }
        //After this make the rest of the buttons clickable
        btnAddContact.setClickable(true);
        btnDeleteContact.setClickable(true);
        btnGetContact.setClickable(true);
        btnDeleteDB.setClickable(true);
    }

    public void addContact(View view) {
        //capture data from the edit Text Fields
        String contactName = nameEditText.getText().toString();
        String contactEmail = emailEditText.getText().toString();
        //Insert into the DB
        contactDB.execSQL("INSERT INTO contacts (name, email) VALUES('" +
                contactName + "', '" + contactEmail + "');");
    }

    public void getContact(View view) {
        //Ping DB with a query and store the queried data into a cursor object.
        Cursor cursor = contactDB.rawQuery("SELECT * FROM contacts", null);

        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int emailColumn = cursor.getColumnIndex("email");

        cursor.moveToFirst();
        String contactList = "";

        //check if cursor has data and put that data in the string as it moves to the next record
        //on the DB
        if(cursor != null && (cursor.getCount() > 0)){
            do{
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String email = cursor.getString(emailColumn);

                //push them to the empty string for display
                contactList = contactList + id + ": " + name + ": " + email + "\n";
            }while (cursor.moveToNext());

            //Now show the results to the view
            contactListEditText.setText(contactList);
        }else{
            Toast.makeText(this, "No record found!!", Toast.LENGTH_SHORT).show();
            contactListEditText.setText("");
        }
    }

    public void deleteContact(View view) {
        String id = idEditText.getText().toString();
        contactDB.execSQL("DELETE FROM contacts WHERE id = " + id + ";");
    }

    public void deleteDatabase(View view) {
        this.deleteDatabase("MyContacts");
    }

    //Close DB when activity is destroyed
    @Override
    protected void onDestroy() {
        contactDB.close();
        super.onDestroy();
    }
}
