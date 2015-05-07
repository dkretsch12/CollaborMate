package com.DPAC.collabormate;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ProjectsActivity extends ListActivity {
    private ProjectsDataSource datasource;
    private String projectName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        datasource = new ProjectsDataSource(this);
        datasource.open();

        List<Project> values = datasource.getAllProjects();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        ListView lv = (ListView)findViewById(android.R.id.list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ProjectsActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_projects, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_projects, menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_project).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(this, Preferences.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Will be called via the onClick attribute
    // of the buttons in projects.xml
    public void deleteProject(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Project> adapter = (ArrayAdapter<Project>) getListAdapter();
        Project project = null;
        if (getListAdapter().getCount() > 0) {
            project = (Project) getListAdapter().getItem(0);
            datasource.deleteProject(project);
            adapter.remove(project);
        }
        adapter.notifyDataSetChanged();
    }

    public void newProject(View view){
        @SuppressWarnings("unchecked")
        final AlertDialog.Builder[] builder = {new AlertDialog.Builder(this)};
        builder[0].setTitle("New Project Name:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder[0].setView(input);

        // Add new course
        builder[0].setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                projectName = input.getText().toString();
                ArrayAdapter<Project> adapter = (ArrayAdapter<Project>) getListAdapter();
                Project project = null;
                project = datasource.createProject(projectName);
                adapter.add(project);
                adapter.notifyDataSetChanged();
            }
        });
        //Cancel
        builder[0].setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder[0].show();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}
