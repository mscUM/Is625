package de.unima.is625;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;



public class MainActivity extends AppCompatActivity
{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ActivityEntryDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new ActivityEntryDataSource(this);
        activateAddButton();
        initializeCAB();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        dataSource.open();
        showAllActivityListEntries();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        dataSource.close();
    }

    private void showAllActivityListEntries()
    {
        List<ActivityEntry> activityEntryList = dataSource.getAllActivityEntries();
        ArrayAdapter<ActivityEntry> activityEntryArrayAdapter =
                new ArrayAdapter<>
                        (
                                this,
                                //android.R.layout.select_dialog_item,
                                android.R.layout.simple_list_item_multiple_choice,
                                activityEntryList
                        );
        ListView activityEntriesListView = (ListView) findViewById(R.id.listView_activity_entries);
        activityEntriesListView.setAdapter(activityEntryArrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.
        */
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void activateAddButton()
    {
        Button btn_addActivity = (Button) findViewById(R.id.btn_add_activity);
        final EditText editTextActName = (EditText) findViewById(R.id.editText_act_name);
        final EditText editTextActLocation = (EditText) findViewById(R.id.editText_act_location);
        final EditText editTextActRange = (EditText) findViewById(R.id.editText_act_range);

        btn_addActivity.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v)
                    {
                        String act_name = editTextActName.getText().toString();
                        String act_location = editTextActLocation.getText().toString();
                        String act_rangeString = editTextActRange.getText().toString();

                        if (TextUtils.isEmpty(act_name))
                        {
                            editTextActName.setError(getString(R.string.editText_errorMessage));
                            return;
                        }
                        if (TextUtils.isEmpty(act_location))
                        {
                            editTextActLocation.setError(getString(R.string.editText_errorMessage));
                            return;
                        }
                        if (TextUtils.isEmpty(act_rangeString))
                        {
                            editTextActRange.setError(getString(R.string.editText_errorMessage));
                            return;
                        }
                        int act_range = Integer.parseInt(act_rangeString);
                        dataSource.createActivityEntry(act_name, act_location, act_range);

                        //reset EditText (name, location, range)
                        editTextActName.setText("");
                        editTextActLocation.setText("");
                        editTextActRange.setText("");

                        //hide soft keyboard after clicking outside the EditText
                        InputMethodManager inputMethodManager;
                        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (getCurrentFocus() != null)
                        {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        showAllActivityListEntries();
                    }
                }
        ); //btn_addActivity.setOnClickListener

    }


    private void initializeCAB() {

        final ListView activityEntriesListView = (ListView) findViewById(R.id.listView_activity_entries);
        //allow multiple choice -> x>1 entries can be selected
        activityEntriesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        activityEntriesListView.setMultiChoiceModeListener
                (
                        new AbsListView.MultiChoiceModeListener() {
                            int selectCount = 0;

                            // count selected listView entries
                            // refresh contextual action bar with invalidate()
                            @Override
                            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                                if (checked) {
                                    selectCount++;
                                } else {
                                    selectCount--;
                                }
                                String cabTitle = selectCount + " " + getString(R.string.cab_checked_string);
                                mode.setTitle(cabTitle);
                                mode.invalidate();

                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                getMenuInflater().inflate(R.menu.menu_cab, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                MenuItem item = menu.findItem(R.id.cab_change);
                                if (selectCount == 1) {
                                    //hide edit symbol if x>1 items are selected
                                    item.setVisible(true);
                                } else {
                                    item.setVisible(false);
                                }
                                return true;

                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                boolean returnValue = true;
                                //SparseBooleanArrays map integers to booleans
                                SparseBooleanArray touchedActivityEntriesPositions = activityEntriesListView.getCheckedItemPositions();

                                switch (item.getItemId()) {
                                    case R.id.cab_delete:
                                        for (int i = 0; i < touchedActivityEntriesPositions.size(); i++) {
                                            boolean isChecked = touchedActivityEntriesPositions.valueAt(i);
                                            if (isChecked) {
                                                int posInListView = touchedActivityEntriesPositions.keyAt(i);
                                                ActivityEntry activityEntry = (ActivityEntry) activityEntriesListView.getItemAtPosition(posInListView);
                                                dataSource.deleteActivityEntry(activityEntry);
                                            }
                                        }
                                        showAllActivityListEntries();
                                        mode.finish();
                                        break;

                                    case R.id.cab_change:
                                        for (int i = 0; i < touchedActivityEntriesPositions.size(); i++)
                                        {
                                            boolean isChecked = touchedActivityEntriesPositions.valueAt(i);
                                            if (isChecked)
                                            {
                                                int posInListView = touchedActivityEntriesPositions.keyAt(i);
                                                ActivityEntry activityEntry = (ActivityEntry) activityEntriesListView.getItemAtPosition(posInListView);
                                                AlertDialog editDialog = createEditDialog(activityEntry);
                                                editDialog.show();
                                            }
                                        }
                                        mode.finish();
                                        break;


                                    default:
                                        returnValue = false;
                                        break;
                                }
                                return returnValue;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode)
                            {
                                selectCount = 0; //reset count
                            }
                        }
                );
    }



    private AlertDialog createEditDialog(final ActivityEntry a) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_edit, null);

        final EditText etNewActName = (EditText) dialogView.findViewById(R.id.editText_new_act_name);
        etNewActName.setText(a.getAct_name());

        final EditText etNewActLocation = (EditText) dialogView.findViewById(R.id.editText_new_act_location);
        etNewActLocation.setText(a.getAct_location());

        final EditText etNewActRange = (EditText) dialogView.findViewById(R.id.editText_new_act_range);
        etNewActRange.setText(String.valueOf(a.getAct_range()));

        builder
                .setView(dialogView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton
                        (
                                R.string.dialog_button_positive,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        String act_name = etNewActName.getText().toString();
                                        String act_location = etNewActLocation.getText().toString();
                                        String act_rangeString = etNewActRange.getText().toString();

                                        if ((TextUtils.isEmpty(act_name))
                                                || (TextUtils.isEmpty(act_location))
                                                || (TextUtils.isEmpty(act_rangeString)))
                                        {
                                            //if empty return
                                            return;
                                        }

                                        int act_range = Integer.parseInt(act_rangeString);

                                        // update database with changed values
                                        ActivityEntry updatedActivityEntry = dataSource.updateActivityEntry(a.getId(), act_name, act_location, act_range);

                                        Log.d(LOG_TAG, "Old Entry - ID: " + a.getId()
                                                + " content: " + a.toString());
                                        Log.d(LOG_TAG, "New Entry - ID: " + updatedActivityEntry.getId()
                                                + " content: " + updatedActivityEntry.toString());

                                        showAllActivityListEntries();
                                        //close dialog
                                        dialog.dismiss();
                                    }
                                }
                        )
                .setNegativeButton
                        (
                                R.string.dialog_button_negative,
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

        return builder.create();
    }
}
