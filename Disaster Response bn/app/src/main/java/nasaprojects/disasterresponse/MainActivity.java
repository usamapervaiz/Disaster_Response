package nasaprojects.disasterresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //TextView alertsignup;


    static boolean statepatientinfolayout = true, statepatientdescriptionlayout = true, statepatientaddresslayout = true, statepatientmedicalhistorylayout = true;
    static boolean statescenegpsormanuallayout = true;
    GPSTracker gps;

   // String urldomain="http://192.168.20.24/nasa/disaster_response/";
   // String urldomain = "http://10.153.211.188/nasa/disaster_response/";
     //String urldomain="http://192.168.1.8/nasa/disaster_response/";
     String urldomain="https://www.colorask.com/nasa/disaster_response/";

    static ImageButton imageViewer;
    static String patienthistorytimp="";

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

       pagemain();
    }

    TextView bodyfronttextview, bodybacktextview;

    public void pagemain() {


        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {

            checkyourinternet();
        }

        if (getdata("login").equals("Notfound")) {
            counter = -100;
            final TextView alertlogin = (TextView) findViewById(R.id.alertmain);

            final EditText email = (EditText) findViewById(R.id.emaillogin);
            final EditText pass = (EditText) findViewById(R.id.passlogin);

            final String urltoreq = urldomain + "login.php";

            final String tologinid[] = new String[2];
            final String tologinvalue[] = new String[2];


            tologinid[0] = "email";
            tologinid[1] = "pass";


            Button login = (Button) findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tologinvalue[0] = email.getText().toString();
                    tologinvalue[1] = pass.getText().toString();

                    if (tologinvalue[0].length() > 0 && tologinvalue[1].length() > 0) {
                        new Fetchsignup(tologinid, tologinvalue, alertlogin, urltoreq, "login").execute();
                    } else {
                        alertlogin.setText("Make sure to enter the email and the password");
                    }

                }
            });


            Button signup = (Button) findViewById(R.id.signup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pagesignup();

                }
            });

        } else {

            pageafterlogin();

        }

    }

    public String patientidandname()
    {
        String datatext="patinet name: "+getdata("lastpatientname")+"\npatient id: "+getdata("lastpatientid");
        datatext=datatext.replace("Notfound","");
        datatext=datatext.replace(",","");
        return datatext;
    }

    public void pageafterlogin() {
        setContentView(R.layout.mainmenu);
        gps = new GPSTracker(MainActivity.this);

        counter++;


        TextView lastscenenumber=(TextView) findViewById(R.id.disasternameandnumber);
        String datatext="Patinet name: "+getdata("lastpatientname")+" : Tag : "+getdata("lasttag")+"\nLast patient # "+getdata("lastpatientid")+"\nLast Scene # "+getdata("lastscenenumber");
        datatext=datatext.replace("Notfound","");
        datatext=datatext.replace(",","");

        lastscenenumber.setText(datatext);

        ImageButton addscene = (ImageButton) findViewById(R.id.addscene);
        addscene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addscenepage();

            }
        });

        ImageButton addpatient = (ImageButton) findViewById(R.id.addpatient);
        addpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addpatientpage();

            }
        });

        ImageButton addvital = (ImageButton) findViewById(R.id.addvital);
        addvital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                treatmentvitalspage();

            }
        });

        ImageButton addtreat = (ImageButton) findViewById(R.id.addtreatment);
        addtreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagetreatmentmenu();

            }
        });

        ImageButton addtriage = (ImageButton) findViewById(R.id.triagedetailsbutton);
        addtriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialpage();

            }
        });


        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutapp();

            }
        });

        ImageButton history = (ImageButton) findViewById(R.id.historybutton);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyl();

            }
        });

        ImageButton injurydetailsbutton = (ImageButton) findViewById(R.id.injurydetailsbutton);
        injurydetailsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontbodypage();

            }
        });

    }

//history

    public String parrgetid(int i)
    {
        String patientsids=getdata("patientshistoryid");
        String[] pidarr = patientsids.split("#");

        return pidarr[i];
    }

    public String idnosp(String id)
    {
        String[] idnosp=id.split(",");
        String idnospv=idnosp[1];
        return idnospv;
    }
    public String parrgettag(String id)
    {
        id=idnosp(id);
        String patienttag=getdata("patienthistorytag"+id);

        return patienttag;
    }


    // Defined Array values to show in ListView

    public void historyl()
    {
        setContentView(R.layout.listh);
        counter++;

        // Get ListView object from xml
        ListView listV = (ListView) findViewById(R.id.listtable);


        //values
        String patientsnames=getdata("patientshistory");

        final String[] parr = patientsnames.split(",");
        String valuess = "";
        String notagnames="";String lasttagv="";

        valuess="Patients for Scene : " + getdata("lastscenenumber")+",";
        for (int cop = 0; cop < parr.length; cop++) {
            if(!parr[cop].equals("Notfound") && !parr[cop].equals("") ) {
                valuess = valuess + parr[cop] + "  [" + parrgettag(parrgetid(cop)) + " TAG]" + ",";
                notagnames=notagnames+","+parr[cop];
                lasttagv=lasttagv+","+parrgettag(parrgetid(cop));

            }
        }

String[] values=valuess.split(",");
final String[] namesvaluearr=notagnames.split(",");
final String[] lasttagarr=lasttagv.split(",");

      ///Toast.makeText(getApplicationContext(),notagnames , Toast.LENGTH_LONG).show();

          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        listV.setAdapter(adapter);


        ImageButton back = (ImageButton) findViewById(R.id.backh);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageafterlogin();
            }
        });



        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(position>0)
                {
                    savedata("lastpatientid", parrgetid(position - 1));
                    savedata("lastpatientname", namesvaluearr[position]);
                    savedata("lasttag", lasttagarr[position]);

                    Toast.makeText(getApplicationContext(), " Successfully now you editing the patient " +parr[position - 1]+" id: "+idnosp(parrgetid(position - 1)), Toast.LENGTH_LONG).show();
                }
            }

        });


        /*

        listItems.add("aaa");
        listItems.add("bbb");


        listItems.add("Patients for Scene : " + getdata("lastscenenumber"));

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);


        ImageButton back = (ImageButton) findViewById(R.id.backh);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageafterlogin();
            }
        });

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(position>0)
                {
                    savedata("lastpatientid", parrgetid(position - 1));
                    Toast.makeText(getApplicationContext(), " Successfully now you editing the patient " +parr[position - 1]+" id: "+parrgetid(position - 1), Toast.LENGTH_LONG).show();
                }
            }

        });

*/
    }





    //end list funs
    public void historypage()
    {
        setContentView(R.layout.historyl);
        counter++;



        TextView t=(TextView) findViewById(R.id.historylist);

        String historylist="Last scene # "+getdata("lastscenenumber")+"\n"+getdata("patientshistory");
        historylist= historylist.replace("Notfound","");
        t.setText(historylist);


    }


    public void logoutapp() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        savedata("login", "Notfound");
                        counter = -100;
                        pagemain();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void checkyourinternet() {

        new AlertDialog.Builder(this)
                .setTitle("Not internet available")
                .setMessage("Make sure to connect to internet ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void addscenepage() {
        setContentView(R.layout.sceneinfo);
        counter++;

        TextView timedateserver = (TextView) findViewById(R.id.datetimesceneinfo);
        timebyserver(timedateserver);

        imageViewer = (ImageButton) findViewById(R.id.addscenepic);
        imageViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePicture();

            }
        });

        final TextView alertsceneinfo = (TextView) findViewById(R.id.alertsceneinfo);
        final TextView gpstext = (TextView) findViewById(R.id.gpstext);

        final LinearLayout layoutgpsormanual = (LinearLayout) findViewById(R.id.layout_gpsormanual_addsceneinfopage);

        final RadioGroup gpsormanual = (RadioGroup) findViewById(R.id.gpsormanual);

        final String urltoreqtoaddscene = urldomain + "addscene.php";

        //final  EditText firstname=(EditText) findViewById(R.id.firstname);

        gpsormanual.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                statescenegpsormanuallayout = showhide(layoutgpsormanual, statescenegpsormanuallayout);

            }
        });

        //String gpstosend="";
        // Check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            String gpsres = "GPS:- \nLat: " + latitude + " \nLong: " + longitude;
            String gpstosend = "," + latitude + "," + longitude;
            savedata("gps", gpstosend);
            gpstext.setText(gpsres);
            // Toast.makeText(getApplicationContext(),gpsres , Toast.LENGTH_LONG).show();

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

        final String tosendsceneid[] = new String[7];
        final String tosendscenevalue[] = new String[7];

        final String tokenvalue = getdata("login");
        final String gpslocationtosend = getdata("gps");
        final EditText scenenumber = (EditText) findViewById(R.id.scenenumber);
        final EditText countryaddress = (EditText) findViewById(R.id.countryaddress);
        final EditText cityaddress = (EditText) findViewById(R.id.cityaddress);
        final EditText streetaddress = (EditText) findViewById(R.id.streetnameaddress);
        final EditText postalcode = (EditText) findViewById(R.id.postalcodeaddress);


        final Spinner disastertype = (Spinner) findViewById(R.id.disastertype);


        tosendsceneid[0] = "token";
        tosendsceneid[1] = "scenenumber";
        tosendsceneid[2] = "locationgps";
        tosendsceneid[3] = "manuallocation";
        tosendsceneid[4] = "manualpostalcode";
        tosendsceneid[5] = "disastertype";
        tosendsceneid[6] = "img";

        Button saveandstartaddpatient = (Button) findViewById(R.id.saveandstart);
        saveandstartaddpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String manuallocationtosend = "," + countryaddress.getText().toString() + "," + cityaddress.getText().toString() + "," + streetaddress.getText().toString();

                tosendscenevalue[0] = tokenvalue;
                tosendscenevalue[1] = scenenumber.getText().toString();
                tosendscenevalue[2] = gpslocationtosend;
                tosendscenevalue[3] = manuallocationtosend;
                tosendscenevalue[4] = postalcode.getText().toString();
                tosendscenevalue[5] = disastertype.getSelectedItem().toString();
                tosendscenevalue[6] = getdata("lastimg");

                savedata("lastscenenumber", tosendscenevalue[1]);
                savedata("patientshistory","");savedata("patientshistoryid","");
                new Fetchsignup(tosendsceneid, tosendscenevalue, alertsceneinfo, urltoreqtoaddscene, "addscene").execute();


                //addpatientpage();

            }
        });


        Button canceladdscene = (Button) findViewById(R.id.canceladdscene);
        canceladdscene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageafterlogin();

            }
        });

    }

    public void addpatientpage() {
        setContentView(R.layout.addpatientvalues);
        counter++;

        imageViewer = (ImageButton) findViewById(R.id.addpatientpic);
        imageViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              takePicture();
            }
        });

        alertaddpatient = (TextView) findViewById(R.id.alertaddpatient);
        final TextView scenenumbertxtpatientpage = (TextView) findViewById(R.id.lastscenenumberpatient);
        final TextView gpstextpatient = (TextView) findViewById(R.id.gpstextpatient);

        /*click buttons hide and show*/
        Button addpatientinfobutton = (Button) findViewById(R.id.addpatientinfo);
        Button addpatientdescriptionbutton = (Button) findViewById(R.id.addpatientdescription);
        Button addpatientaddressbutton = (Button) findViewById(R.id.addpatientaddress);
        Button addpatientmedicalhistorybutton = (Button) findViewById(R.id.addpatientmedical);


        final LinearLayout patientinfolayout = (LinearLayout) findViewById(R.id.layout_addpatientinfo_addpatientvaluespage);
        final LinearLayout patientdescriptionlayout = (LinearLayout) findViewById(R.id.layout_addpatientdescription_addpatientvaluespage);
        final LinearLayout patientaddresslayout = (LinearLayout) findViewById(R.id.layout_addpatientaddress_addpatientvaluespage);
        final LinearLayout patientmedicalinfolayout = (LinearLayout) findViewById(R.id.layout_addpatientmedicalhistory_addpatientvaluespage);


        patientinfolayout.setVisibility(View.GONE);
        patientdescriptionlayout.setVisibility(View.GONE);
        patientaddresslayout.setVisibility(View.GONE);
        patientmedicalinfolayout.setVisibility(View.GONE);


        addpatientinfobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statepatientinfolayout = showhide(patientinfolayout, statepatientinfolayout);

            }
        });


        addpatientdescriptionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statepatientdescriptionlayout = showhide(patientdescriptionlayout, statepatientdescriptionlayout);

            }
        });


        addpatientaddressbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statepatientaddresslayout = showhide(patientaddresslayout, statepatientaddresslayout);

            }
        });


        addpatientmedicalhistorybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statepatientmedicalhistorylayout = showhide(patientmedicalinfolayout, statepatientmedicalhistorylayout);

            }
        });

         /*end click buttons hide and show*/


        final String urltoreqtoaddpatient = urldomain + "addpatient.php";


        // Check if GPS enabled for pateint location
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();


            String gpsres = "GPS:- Lat: " + latitude + " ,Long: " + longitude;
            String gpstosend = "," + latitude + "," + longitude;
            savedata("lastgpspatient", gpstosend);
            gpstextpatient.setText(gpsres);
            // Toast.makeText(getApplicationContext(),gpsres , Toast.LENGTH_LONG).show();

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

        final String tosendaddpatientid[] = new String[21];
        final String tosendaddpatientvalue[] = new String[21];

        final String tokenvalue = getdata("login");
        final String gpslocationtosend = getdata("lastgpspatient");
        final String scenenumberrelated = getdata("lastscenenumber");

        scenenumbertxtpatientpage.setText("Scene number: " + scenenumberrelated);

        if (scenenumberrelated.equals("Notfound")) {
            alertaddpatient.setText("Fist add scene of the disaster before add patients");
        }

        final EditText lastname = (EditText) findViewById(R.id.addpatientvaluespagelastname);
        final EditText fistaname = (EditText) findViewById(R.id.addpatientvaluespagefirstname);
        final EditText middlename = (EditText) findViewById(R.id.addpatientvaluespagemiddlename);
        final EditText birthday = (EditText) findViewById(R.id.addpatientvaluespagedateofbirth);
        final EditText agerange = (EditText) findViewById(R.id.addpatientvaluespageagerange);
        final Spinner race = (Spinner) findViewById(R.id.spinnerraceaddpatientvaluespage);
        final Spinner gender = (Spinner) findViewById(R.id.spinnergenderaddpatientvaluespage);
        final EditText hieghtpatient = (EditText) findViewById(R.id.hieght_addpatientvaluespage);
        final EditText weightpatient = (EditText) findViewById(R.id.weight_addpatientvaluespage);
        final EditText streetaddresspatient = (EditText) findViewById(R.id.streetaddress_addpatientvaluespage);
        final EditText cityaddresspatient = (EditText) findViewById(R.id.City_addpatientvaluespage);
        final EditText state = (EditText) findViewById(R.id.State_addpatientvaluespage);
        final EditText zipcode = (EditText) findViewById(R.id.Zipcode_addpatientvaluespage);
        final EditText phonenumber = (EditText) findViewById(R.id.Phone_addpatientvaluespage);
        final EditText problems = (EditText) findViewById(R.id.Problems_addpatientvaluespage);
        final EditText medications = (EditText) findViewById(R.id.medications_addpatientvaluespage);
        final EditText alergies = (EditText) findViewById(R.id.Alergies_addpatientvaluespage);


/*
*
*
*
* $token=$data['token'];
$scenenumberrelated=$data['scenenumberrelated'];
$patientlocationgps=$data['patientlocationgps'];
$lastname=$data['lastname'];
$firstname=$data['fistname'];
$middlename=$data['middlename'];
$birthday=$data['birthday'];
$agerange=$data['agerange'];
$race=$data['race'];
$gender=$data['gender'];
$patienthieght=$data['patienthieght'];
$patientweight=$data['patientweight'];
$streetaddresspatient=$data['streetaddresspatient'];
$citypatinetlive=$data['citypatinetlive'];
$statepatientlive=$data['statepatientlive'];
$zipcode=$data['zipcode'];
$phonenumber=$data['phonenumber'];
$patientproblem=$data['patientproblem'];
$patientmedications=$data['patientmedications'];
$alergies=$data['alergies'];*/


        tosendaddpatientid[0] = "token";
        tosendaddpatientid[1] = "scenenumberrelated";
        tosendaddpatientid[2] = "patientlocationgps";
        tosendaddpatientid[3] = "lastname";
        tosendaddpatientid[4] = "fistname";
        tosendaddpatientid[5] = "middlename";
        tosendaddpatientid[6] = "birthday";
        tosendaddpatientid[7] = "agerange";
        tosendaddpatientid[8] = "race";
        tosendaddpatientid[9] = "gender";
        tosendaddpatientid[10] = "patienthieght";
        tosendaddpatientid[11] = "patientweight";
        tosendaddpatientid[12] = "streetaddresspatient";
        tosendaddpatientid[13] = "citypatinetlive";
        tosendaddpatientid[14] = "statepatientlive";
        tosendaddpatientid[15] = "zipcode";
        tosendaddpatientid[16] = "phonenumber";
        tosendaddpatientid[17] = "patientproblem";
        tosendaddpatientid[18] = "patientmedications";
        tosendaddpatientid[19] = "alergies";
        tosendaddpatientid[20] = "img";

        Button savepatientandaddother = (Button) findViewById(R.id.savepatientandaddother);
        savepatientandaddother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tosendaddpatientvalue[0] = tokenvalue;
                tosendaddpatientvalue[1] = scenenumberrelated;
                tosendaddpatientvalue[2] = gpslocationtosend;
                tosendaddpatientvalue[3] = lastname.getText().toString();
                tosendaddpatientvalue[4] = fistaname.getText().toString();
                tosendaddpatientvalue[5] = middlename.getText().toString();
                tosendaddpatientvalue[6] = birthday.getText().toString();
                tosendaddpatientvalue[7] = agerange.getText().toString();
                tosendaddpatientvalue[8] = race.getSelectedItem().toString();
                tosendaddpatientvalue[9] = gender.getSelectedItem().toString();
                tosendaddpatientvalue[10] = hieghtpatient.getText().toString();
                tosendaddpatientvalue[11] = weightpatient.getText().toString();
                tosendaddpatientvalue[12] = streetaddresspatient.getText().toString();
                tosendaddpatientvalue[13] = cityaddresspatient.getText().toString();
                tosendaddpatientvalue[14] = state.getText().toString();
                tosendaddpatientvalue[15] = zipcode.getText().toString();
                tosendaddpatientvalue[16] = phonenumber.getText().toString();
                tosendaddpatientvalue[17] = problems.getText().toString();
                tosendaddpatientvalue[18] = medications.getText().toString();
                tosendaddpatientvalue[19] = alergies.getText().toString();
                tosendaddpatientvalue[20] = getdata("lastimg");


                savedata("lastpatientname",tosendaddpatientvalue[4]+" "+tosendaddpatientvalue[3]);

                patienthistorytimp=tosendaddpatientvalue[4]+" "+tosendaddpatientvalue[3]+","+getdata("patientshistory");


                new Fetchsignup(tosendaddpatientid, tosendaddpatientvalue, alertaddpatient, urltoreqtoaddpatient, "addpatient").execute();


                //addpatientpage();

            }
        });


        Button canceladdpatient = (Button) findViewById(R.id.canceladdpatient);
        canceladdpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageafterlogin();

            }
        });


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            counter--;
            if (counter < -1) {
                exitapp();
            } else {
                counter--;
                pageafterlogin();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitapp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    RadioButton selectedgender;

    public void pagesignup() {

        setContentView(R.layout.signup);

        final TextView alertsignup = (TextView) findViewById(R.id.alertsignup);

        final String urltoreq = urldomain + "signup.php";

        final EditText firstname = (EditText) findViewById(R.id.firstname);
        final EditText lastname = (EditText) findViewById(R.id.lastname);
        final EditText emailaddress = (EditText) findViewById(R.id.emaillogin);
        final EditText pass = (EditText) findViewById(R.id.passlogin);
        final EditText confirmpass = (EditText) findViewById(R.id.Passconfirm);
        final EditText hospitalname = (EditText) findViewById(R.id.hospitalname);
        final EditText hospitalid = (EditText) findViewById(R.id.hospitalid);
        final EditText birthday = (EditText) findViewById(R.id.birthdaydaysignup);
        final EditText phonenumber = (EditText) findViewById(R.id.phonenumber);


        final RadioGroup gender = (RadioGroup) findViewById(R.id.gendersignup);

        final String tosendsignupid[] = new String[9];
        final String tosendsignupvalue[] = new String[9];


        tosendsignupid[0] = "firstname";
        tosendsignupid[1] = "lastname";
        tosendsignupid[2] = "email";
        tosendsignupid[3] = "pass";
        tosendsignupid[4] = "hospitalname";
        tosendsignupid[5] = "hospitalid";
        tosendsignupid[6] = "birthday";
        tosendsignupid[7] = "gender";
        tosendsignupid[8] = "phonenumber";


        Button signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (confirmpass.getText().toString().equals(pass.getText().toString()) && pass.getText().toString().length() > 0) {
                    selectedgender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());

                    tosendsignupvalue[0] = firstname.getText().toString();
                    tosendsignupvalue[1] = lastname.getText().toString();
                    tosendsignupvalue[2] = emailaddress.getText().toString();
                    tosendsignupvalue[3] = pass.getText().toString();
                    tosendsignupvalue[4] = hospitalname.getText().toString();
                    tosendsignupvalue[5] = hospitalid.getText().toString();
                    tosendsignupvalue[6] = birthday.getText().toString();
                    tosendsignupvalue[7] = selectedgender.getText().toString();
                    tosendsignupvalue[8] = phonenumber.getText().toString();


                    new Fetchsignup(tosendsignupid, tosendsignupvalue, alertsignup, urltoreq, "signup").execute();


                } else {
                    alertsignup.setText("Check pass confirmation to be the same");
                }

            }
        });


        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagemain();

            }
        });


    }


    private boolean showhide(LinearLayout layout, boolean state) {
        if (state) {
            layout.setVisibility(View.VISIBLE);
            return false;

        } else {
            layout.setVisibility(View.GONE);
            return true;
        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onInit(int status) {

    }

    private class Fetchsignup extends AsyncTask<Void, Void, String> {

        String arr1[], arr2[];
        TextView t;
        String urltoreq;
        String afterrequestpattern;

        private Fetchsignup(String[] arr1rec, String[] arr2rec, TextView trec, String urltoreqrec, String afterrequestfunction) {
            arr1 = arr1rec;
            arr2 = arr2rec;
            t = trec;
            urltoreq = urltoreqrec;
            afterrequestpattern = afterrequestfunction;
            t.setText("Sending .. ");

        }

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(urltoreq);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");


                //for the post
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject obj = new JSONObject();
                    for (int i = 0; i < arr1.length; i++) {
                        obj.put(arr1[i], arr2[i]);
                    }


                    wr.writeBytes(obj.toString());
                    // Log.e("JSON Input", obj.toString());
                    wr.flush();
                    wr.close();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //end for the post


                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                // Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        //  Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.contains("Error:")) {
                t.setText(s);

            } else {
                //t.setText(s);
                afterrequest(afterrequestpattern, s);
            }
            // alertsignup.setText(s);

        }


        private void afterrequest(String st, String r) {
            if (st.equals("signup")) {
                pagemain();
                TextView alertlogin = (TextView) findViewById(R.id.alertmain);
                alertlogin.setText("Successfully Registered , now login");
            } else if (st.equals("login")) {

                savedata("login", r);
                pageafterlogin();

            } else if (st.equals("addscene")) {

                Toast.makeText(MainActivity.this, "Scene added successfully", Toast.LENGTH_LONG).show();
                pageafterlogin();


            } else if (st.equals("addpatient")) {

                savedata("lastpatientid", r);
                savedata("patientshistoryid",r+"#"+getdata("patientshistoryid"));
                savedata("patientshistory",patienthistorytimp);
                Toast.makeText(MainActivity.this, "Successfully added Patient", Toast.LENGTH_LONG).show();
                //addpatientpage();
                //ventilationmainpage();
                pageafterlogin();
                //t.setText(r);

            } else if (st.equals("addtag")) {
               String idtouse=idnosp(getdata("lastpatientid"));
                savedata("patienthistorytag"+idtouse,getdata("lasttag"));
                pageafterlogin();

               /// t.setText(r);

            } else if (st.equals("addfrontbody")) {

                backbodypage();
                ///   t.setText("Successfully added the front body");

            } else if (st.equals("addbackbody")) {
                injuryinfopage();

            } else if (st.equals("getdatetime")) {

                t.setText(r);
                ///   t.setText("Successfully added the front body");

            } else if (st.equals("addvital")) {

                treatmentvitalspage();
                ///   t.setText("Successfully added the front body");

            }else if (st.equals("addtreatment")) {

               pagetreatmentmenu();
                ///   t.setText("Successfully added the front body");

            }else if (st.equals("addinjury")) {

               pageafterlogin();
            }

        }
    }

    static Bitmap bitmapimagbodyfront;
    static ImageView frontbodyimg;

    //Front body page
    public void frontbodypage() {
        setContentView(R.layout.bodyfront);
        bodyfronttextview = (TextView) findViewById(R.id.bodyfronttextview);
        bodyfronttextview.setText(patientidandname());
        frontbodyimg = (ImageView) findViewById(R.id.bodyfrontimageview);
        ImageView checkwhite = (ImageView) findViewById(R.id.checkwhitebodyfront);


        Button cleanfront=(Button) findViewById(R.id.cleanfront);
        cleanfront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                frontbodypage();
            }
        });

        checkwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendbodyfront(bodyfronttextview);

            }
        });


        frontbodyimg.setOnTouchListener(handleTouch);


        bitmapimagbodyfront = BitmapFactory.decodeResource(getResources(), R.drawable.bodyfrontimgsm);
        bitmapimagbodyfront = bitmapimagbodyfront.copy(Bitmap.Config.ARGB_8888, true);
        frontbodyimg.setImageBitmap(bitmapimagbodyfront);

    }

    static Bitmap bitmapimagbodyback;
    static ImageView backbodyimg;

    //Front body page
    public void backbodypage() {
        setContentView(R.layout.bodyback);
counter++;
        bodybacktextview = (TextView) findViewById(R.id.bodybacktextview);
        bodybacktextview.setText(patientidandname());
        backbodyimg = (ImageView) findViewById(R.id.bodybackimageview);
        ImageView checkwhite = (ImageView) findViewById(R.id.checkwhitebodyback);

        checkwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendbodyback(bodybacktextview);

            }
        });


        Button cleanback=(Button) findViewById(R.id.cleanback);
        cleanback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                backbodypage();
            }
        });

        backbodyimg.setOnTouchListener(handleTouchback);


        bitmapimagbodyback = BitmapFactory.decodeResource(getResources(), R.drawable.bodybackimg);
        bitmapimagbodyback = bitmapimagbodyback.copy(Bitmap.Config.ARGB_8888, true);
        backbodyimg.setImageBitmap(bitmapimagbodyback);

    }


    //Ventilation main page
    public void ventilationmainpage() {

        setContentView(R.layout.ventilations);
        counter++;
        RelativeLayout ventilationyes = (RelativeLayout) findViewById(R.id.radialpresent);
        RelativeLayout ventilationno = (RelativeLayout) findViewById(R.id.radialnotpresent);

        TextView  ventpatientinfo = (TextView) findViewById(R.id.ventpatientinfo);
        ventpatientinfo.setText(patientidandname());

        ttst("ispatientbreathing");

        ventilationyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breathspeedpage();

            }
        });

        ventilationno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repositionpage();

            }
        });


    }

    //Ventilation
    public void repositionpage() {

        setContentView(R.layout.reposition);
        counter++;
        RelativeLayout yesbreathingnow = (RelativeLayout) findViewById(R.id.yesbreathingnow);
        RelativeLayout nobreathingnow = (RelativeLayout) findViewById(R.id.nobreathingnow);

       final TextView  reposipatientinfo = (TextView) findViewById(R.id.reposipatientinfo);
        reposipatientinfo.setText(patientidandname());

        ttst("reposition");
        ImageButton back = (ImageButton) findViewById(R.id.back_reposition);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationmainpage();
            }
        });


        yesbreathingnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Red",reposipatientinfo);

            }
        });

        nobreathingnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Black",reposipatientinfo);

            }
        });


    }

    //ventilation
    public void breathspeedpage() {

        setContentView(R.layout.breathspeed);

        RelativeLayout fastbreathspeed = (RelativeLayout) findViewById(R.id.fastbreath);
        RelativeLayout slowbreathspeed = (RelativeLayout) findViewById(R.id.slowbreath);

        final TextView  ventilationpatinetinfo = (TextView) findViewById(R.id.ventilationpatinetinfo);
        ventilationpatinetinfo.setText(patientidandname());

        ttst("whatthespeedofbreath");

        ImageButton backbreath = (ImageButton) findViewById(R.id.back_breath);

        backbreath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationmainpage();
            }
        });


        fastbreathspeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Red",ventilationpatinetinfo);
            }
        });

        slowbreathspeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circulationpage();
            }
        });


    }

    //ventilation
    public void circulationpage() {

        setContentView(R.layout.circulation);
        RelativeLayout radialpresent = (RelativeLayout) findViewById(R.id.radialpresent);
        RelativeLayout radialnotpresent = (RelativeLayout) findViewById(R.id.radialnotpresent);

       final TextView  circupatientinfo = (TextView) findViewById(R.id.circupatientinfo);
        circupatientinfo.setText(patientidandname());

        ttst("circulation");

        ImageButton back = (ImageButton) findViewById(R.id.back_circulation);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationmainpage();
            }
        });


        radialpresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mentalpage();
            }
        });

        radialnotpresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Red",circupatientinfo);
            }
        });


    }

    //ventilation
    public void mentalpage() {

        setContentView(R.layout.mentalstatus);
        RelativeLayout yescanfollowsimplecommands = (RelativeLayout) findViewById(R.id.yescanfollowsimplecommands);
        RelativeLayout nocanfollowsimplecommands = (RelativeLayout) findViewById(R.id.nocanfollowsimplecommands);


       final TextView  mentalpatientinfo = (TextView) findViewById(R.id.mentalpatientinfo);
        mentalpatientinfo.setText(patientidandname());

        ttst("mentalstatus");


        ImageButton back = (ImageButton) findViewById(R.id.back_mental);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circulationpage();
            }
        });


        yescanfollowsimplecommands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseredoryellowtag();
            }
        });

        nocanfollowsimplecommands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Red",mentalpatientinfo);
            }
        });


    }


    //ventilation
    public void chooseredoryellowtag() {

        setContentView(R.layout.redoryellow);
        RelativeLayout redtag = (RelativeLayout) findViewById(R.id.redtag);
        RelativeLayout yellowtag = (RelativeLayout) findViewById(R.id.yellowtag);

        ImageButton back = (ImageButton) findViewById(R.id.back_redoryellow);

       final TextView  redoryellowpatientinfo = (TextView) findViewById(R.id.redoryellowpatientinfo);
        redoryellowpatientinfo.setText(patientidandname());

        ttst("redoryellow");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mentalpage();
            }
        });


        redtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Red",redoryellowpatientinfo);
            }
        });

        yellowtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventilationres("Yellow",redoryellowpatientinfo);
            }
        });


    }

    public void sendbodyfront(TextView t) {
        String base64String = ImageUtil.convert(bitmapimagbodyfront);


        final String tosendtagid[] = new String[3];
        final String tosendtagvalue[] = new String[3];
        final String urltoreq = urldomain + "addfrontimage.php";


        tosendtagid[0] = "token";
        tosendtagid[1] = "patientid";
        tosendtagid[2] = "bodyfront";

        tosendtagvalue[0] = getdata("login");
        tosendtagvalue[1] = getdata("lastpatientid");
        tosendtagvalue[2] = base64String;

        new Fetchsignup(tosendtagid, tosendtagvalue, t, urltoreq, "addfrontbody").execute();


    }

    public void sendbodyback(TextView t) {
        String base64String = ImageUtil.convert(bitmapimagbodyback);


        final String tosendtagid[] = new String[3];
        final String tosendtagvalue[] = new String[3];
        final String urltoreq = urldomain + "addbackimage.php";


        tosendtagid[0] = "token";
        tosendtagid[1] = "patientid";
        tosendtagid[2] = "bodyback";

        tosendtagvalue[0] = getdata("login");
        tosendtagvalue[1] = getdata("lastpatientid");
        tosendtagvalue[2] = base64String;

        new Fetchsignup(tosendtagid, tosendtagvalue, t, urltoreq, "addbackbody").execute();


    }

    static String headinjuryinfosum="";
public void injuryinfopage()
{
    setContentView(R.layout.injuryinfo);
    counter++;

    final TextView  alertinjuryinfo = (TextView) findViewById(R.id.alertinjuryinfo);

    final EditText othertypeinjury = (EditText) findViewById(R.id.othertypeinjury);
    final EditText typeinjury = (EditText) findViewById(R.id.typeinjury);

    TextView  triagecodepatientinfo = (TextView) findViewById(R.id.injuryinfopatientinfo);
    triagecodepatientinfo.setText(patientidandname());


    final TextView timedateserver = (TextView) findViewById(R.id.timebyserverinjuryinfo);
    timebyserver(timedateserver);

    final CheckBox headinjurycheckbox=(CheckBox) findViewById(R.id.headinjurycheckbox);
    final CheckBox blunttraumacheckbox=(CheckBox) findViewById(R.id.blunttraumacheckbox);
    final CheckBox burncheckbox=(CheckBox) findViewById(R.id.burncheckbox);
    final CheckBox lacerationcheckbox=(CheckBox) findViewById(R.id.lacerationcheckbox);
    final CheckBox cardiaccheckbox=(CheckBox) findViewById(R.id.cardiaccheckbox);
    final CheckBox diabeticcheckbox=(CheckBox) findViewById(R.id.diabeticcheckbox);
    final CheckBox hazmatexposurecheckbox=(CheckBox) findViewById(R.id.hazmatexposurecheckbox);
    final CheckBox cspinecheckbox=(CheckBox) findViewById(R.id.cspinecheckbox);
    final CheckBox penetratinginjurycheckbox=(CheckBox) findViewById(R.id.penetratinginjurycheckbox);
    final CheckBox fracturecheckbox=(CheckBox) findViewById(R.id.fracturecheckbox);
    final CheckBox amputationcheckbox=(CheckBox) findViewById(R.id.amputationcheckbox);
    final CheckBox respiratorycheckbox=(CheckBox) findViewById(R.id.respiratorycheckbox);
    final CheckBox obgyncheckbox=(CheckBox) findViewById(R.id.obgyncheckbox);

    final RadioGroup radioGroupinjury = (RadioGroup) findViewById(R.id.radiogroupinjuryinfo);


    headinjuryinfosum="";
    Button saveinjuryinfo=(Button) findViewById(R.id.saveinjuryinfo);
    saveinjuryinfo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            RadioButton  radioGroupinjurybutton = (RadioButton) findViewById(radioGroupinjury.getCheckedRadioButtonId());

            if(headinjurycheckbox.isChecked())
                headinjuryinfosum=","+headinjuryinfosum+headinjurycheckbox.getText()+",";

            if(blunttraumacheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+blunttraumacheckbox.getText()+",";

            if(burncheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+burncheckbox.getText()+",";

            if(lacerationcheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+lacerationcheckbox.getText()+",";

            if(cardiaccheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+cardiaccheckbox.getText()+",";

            if(diabeticcheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+diabeticcheckbox.getText()+",";

             if(hazmatexposurecheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+hazmatexposurecheckbox.getText()+",";

            if(cspinecheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+cspinecheckbox.getText()+",";

            if(penetratinginjurycheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+penetratinginjurycheckbox.getText()+",";

            if(fracturecheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+fracturecheckbox.getText()+",";

            if(amputationcheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+amputationcheckbox.getText()+",";

            if(respiratorycheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+respiratorycheckbox.getText()+",";

            if(obgyncheckbox.isChecked())
                headinjuryinfosum=headinjuryinfosum+obgyncheckbox.getText()+",";

            headinjuryinfosum=headinjuryinfosum+othertypeinjury.getText()+",";
            headinjuryinfosum=headinjuryinfosum+radioGroupinjurybutton.getText()+",";
            headinjuryinfosum=headinjuryinfosum+typeinjury.getText()+",";


            addinjuryinfo(headinjuryinfosum,alertinjuryinfo);





        }
    });



}
    public void initialpage()
    {
        setContentView(R.layout.initial);
        counter++;
        Button savetriagecode=(Button) findViewById(R.id.savetriagecode);

        final TextView  alerttextini = (TextView) findViewById(R.id.alertini);


        final RadioGroup radioGrouptriage = (RadioGroup) findViewById(R.id.radioGrouptriage);


        TextView  triagecodepatientinfo = (TextView) findViewById(R.id.triagecodepatientinfo);
        triagecodepatientinfo.setText(patientidandname());


        final TextView timedateserver = (TextView) findViewById(R.id.timebyservertriagecode);
        timebyserver(timedateserver);

        savetriagecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton  radioGrouptriagebutton = (RadioButton) findViewById(radioGrouptriage.getCheckedRadioButtonId());
                addini(radioGrouptriagebutton.getText().toString(), alerttextini);

            }
        });


    }


    public void addini(String res,TextView alertini) {

        final String tosendtagid[] = new String[3];
        final String tosendtagvalue[] = new String[3];
        final String urltoreq = urldomain + "addinitial.php";


        tosendtagid[0] = "token";
        tosendtagid[1] = "patientid";
        tosendtagid[2] = "ini";

        tosendtagvalue[0] = getdata("login");
        tosendtagvalue[1] = getdata("lastpatientid");
        tosendtagvalue[2] = res;

        new Fetchsignup(tosendtagid, tosendtagvalue, alertini, urltoreq, "addini").execute();
        ventilationmainpage();

    }

    public void addinjuryinfo(String res,TextView alerttext)
    {

        final String tosendtagid[] = new String[3];
        final String tosendtagvalue[] = new String[3];
        final String urltoreq = urldomain + "addinjury.php";


        tosendtagid[0] = "token";
        tosendtagid[1] = "patientid";
        tosendtagid[2] = "injuryinfo";

        tosendtagvalue[0] = getdata("login");
        tosendtagvalue[1] = getdata("lastpatientid");
        tosendtagvalue[2] = res;

        new Fetchsignup(tosendtagid, tosendtagvalue, alerttext, urltoreq, "addinjury").execute();


    }

    //ventilation res
    TextView alertaddpatient;

    public void ventilationres(String res,TextView alertt) {

        ///addpatientpage();
        ttst(res);
        final String tosendtagid[] = new String[3];
        final String tosendtagvalue[] = new String[3];
        final String urltoreq = urldomain + "addtag.php";


        tosendtagid[0] = "token";
        tosendtagid[1] = "patientid";
        tosendtagid[2] = "tag";

        tosendtagvalue[0] = getdata("login");
        tosendtagvalue[1] = getdata("lastpatientid");
        tosendtagvalue[2] = res;


        savedata("lasttag",res);
        new Fetchsignup(tosendtagid, tosendtagvalue, alertt, urltoreq, "addtag").execute();
        // alertaddpatient.setText("Last patient Tag : "+res);
    }

    String filename = "disasterresponse";

    public void savedata(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(filename, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }

    public void saveintdata(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(filename, MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public int getintdata(String key) {
        int ret = 0;
        SharedPreferences prefs = getSharedPreferences(filename, MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if (restoredText != null) {

            ret = prefs.getInt(key, 0);

        }

        return ret;

    }

    public String getdata(String key) {
        String ret = "Notfound";
        SharedPreferences prefs = getSharedPreferences(filename, MODE_PRIVATE);
        String restoredText = prefs.getString("login", null);
        if (restoredText != null) {

            ret = prefs.getString(key, "Notfound");

        }

        return ret;

    }


    private static final int CAMERA_REQUEST = 1888; // field

    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap picture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this

            String base64String = ImageUtil.convert(picture);
            savedata("lastimg", base64String);
            imageViewer.setImageBitmap(picture); //for example I put bmp in an ImageView
        }
    }

    static MediaPlayer mPlayer = null;

    //tts text to speach
    public void ttst(String txt) {
        if (mPlayer != null)
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }



/*
       if(mPlayer.isPlaying())
       {
           mPlayer.pause();
       }
*/

        if (txt.equals("ispatientbreathing"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.ispatientbreathing);

        if (txt.equals("whatthespeedofbreath"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.whatthespeedofbreath);

        if (txt.equals("circulation"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.circulation);

        if (txt.equals("mentalstatus"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.mentalstatus);

        if (txt.equals("redoryellow"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.redoryellow);

        if (txt.equals("reposition"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.reposition);

        if (txt.equals("Red"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.redtag);


        if (txt.equals("Yellow"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.yellowtag);

        if (txt.equals("Black"))
            mPlayer = MediaPlayer.create(MainActivity.this, R.raw.blacktag);



        mPlayer.start();
    }


    private View.OnTouchListener handleTouchback = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Log.i("TAG", "touched down");
                    ///Toast.makeText(getApplicationContext(),  "touched down "  , Toast.LENGTH_LONG).show();

                    break;
                case MotionEvent.ACTION_MOVE:
                    // Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    // Toast.makeText(getApplicationContext(), x + ", " + y , Toast.LENGTH_LONG).show();

                    drawbit(x, y, bitmapimagbodyback, backbodyimg);

                    //bodybacktextview.setText(x + ", " + y);
                    break;
                case MotionEvent.ACTION_UP:
                    ///Log.i("TAG", "touched up");
                    ///Toast.makeText(getApplicationContext(),  "touched up "  , Toast.LENGTH_LONG).show();
                    break;

                //return false;
            }


            return true;
        }
    };


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Log.i("TAG", "touched down");
                    ///Toast.makeText(getApplicationContext(),  "touched down "  , Toast.LENGTH_LONG).show();

                    break;
                case MotionEvent.ACTION_MOVE:
                    // Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    // Toast.makeText(getApplicationContext(), x + ", " + y , Toast.LENGTH_LONG).show();

                    drawbit(x, y, bitmapimagbodyfront, frontbodyimg);

                    //bodyfronttextview.setText(x + ", " + y);
                    break;
                case MotionEvent.ACTION_UP:
                    ///Log.i("TAG", "touched up");
                    ///Toast.makeText(getApplicationContext(),  "touched up "  , Toast.LENGTH_LONG).show();
                    break;

                //return false;
            }


            return true;
        }
    };


    public void drawbitback(int x, int y) {

        if (x > 1 && y > 1 && x < bitmapimagbodyback.getWidth() && y < bitmapimagbodyback.getHeight())

            for (int c = 0; c < 6; c++) {
                if ((x + c) < bitmapimagbodyback.getWidth()) {

                    bitmapimagbodyback.setPixel(x + c, y, Color.RED);

                }
                if ((y + c) < bitmapimagbodyback.getHeight()) {

                    bitmapimagbodyback.setPixel(x, y + c, Color.RED);

                }

                if ((y - c) > 1) {

                    bitmapimagbodyback.setPixel(x, y - c, Color.RED);

                }

                if ((x - c) > 1) {

                    bitmapimagbodyback.setPixel(x - c, y, Color.RED);

                }


            }

        backbodyimg.setImageBitmap(bitmapimagbodyback);
    }


    public void drawbit(int x, int y,Bitmap bitmapimagbodyfront,ImageView frontbodyimg)
{

     if(x>1 && y>1 && x<bitmapimagbodyfront.getWidth() && y<bitmapimagbodyfront.getHeight())

    for(int c=0;c<5;c++)
    {
        if((x+c)<bitmapimagbodyfront.getWidth() )
        {

            bitmapimagbodyfront.setPixel(x+c, y, Color.RED);

        }
        if( (y+c)<bitmapimagbodyfront.getHeight())
        {

            bitmapimagbodyfront.setPixel(x, y+c, Color.RED);

        }

        if( (y-c)>1)
        {

            bitmapimagbodyfront.setPixel(x, y-c, Color.RED);

        }

        if( (x-c)>1 )
        {

            bitmapimagbodyfront.setPixel(x-c, y, Color.RED);

        }

        if((x+c)<bitmapimagbodyfront.getWidth() && (y+c)<bitmapimagbodyfront.getHeight() )
        {

            bitmapimagbodyfront.setPixel(x+c, y+c, Color.RED);

        }


        if((x-c)>0 && (y-c)>0 )
        {

            bitmapimagbodyfront.setPixel(x-c, y-c, Color.RED);

        }

    }

   frontbodyimg.setImageBitmap(bitmapimagbodyfront);
}

    static String oldhis;
    public void treatmentvitalspage()
    {

        String history=getdata(getdata("lastpatientid")+"vitals");
         oldhis=history;
        if(history.equals("Notfound"))
        {
            history="No vital added yet";
            oldhis="";
        }



        setContentView(R.layout.treatment);
        counter++;
        final TextView alerttreatmentvital = (TextView) findViewById(R.id.alerttreatmentvital);
        final TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmentvital);

        TextView historytext = (TextView) findViewById(R.id.historyvitals);
        historytext.setText(history);

        TextView  vitalpatientinfo = (TextView) findViewById(R.id.vitalpatientinfo);
        vitalpatientinfo.setText(patientidandname());

        timebyserver(timedateserver);


        final String urltoreq = urldomain + "addvital.php";

        final EditText pulse = (EditText) findViewById(R.id.bvmedit);
        final EditText bloodpressure = (EditText) findViewById(R.id.bloodpressure);
        final EditText respiration = (EditText) findViewById(R.id.respiration);
        final EditText levelofconsiciousness = (EditText) findViewById(R.id.levelofconsiciousness);


        final String tosendid[] = new String[6];
        final String tosendvalue[] = new String[6];


        tosendid[0] = "token";
        tosendid[1] = "patientid";
        tosendid[2] = "pulse";
        tosendid[3] = "bloodpressure";
        tosendid[4] = "respiration";
        tosendid[5] = "levelofconsiciousness";

        tosendvalue[0] = getdata("login");
        tosendvalue[1] = getdata("lastpatientid");



        ImageButton addvital = (ImageButton) findViewById(R.id.addvital);
        addvital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pulse.getText().toString().length() > 0)
                {

                    tosendvalue[2] = pulse.getText().toString();
                    tosendvalue[3] = bloodpressure.getText().toString();
                    tosendvalue[4] = respiration.getText().toString();
                    tosendvalue[5] = levelofconsiciousness.getText().toString();

                    String datetime=timedateserver.getText().toString();
                    savedata(getdata("lastpatientid")+"vitals",oldhis+" Date and time : "+datetime+"\n Pulse : "+ tosendvalue[2]+"\n Blood Pressure : "
                    +tosendvalue[3]+"\n Respiration : "+tosendvalue[4]+"\n Consiciousness : "+tosendvalue[5]+
                                    "\n *********************************** \n"
                    );

                    new Fetchsignup(tosendid, tosendvalue, alerttreatmentvital, urltoreq, "addvital").execute();


                } else {
                    alerttreatmentvital.setText("Check you added the pulse value");
                }

            }
        });


        //ImageView checkwhite = (ImageView) findViewById(R.id.checkwhitevital);
  /*     checkwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagetreatmentmenu();

            }
        });
*/



    }

    private void timebyserver(TextView timedateserver) {

        final String tosendidt[] = new String[1];
        final String tosendvaluet[] = new String[1];

        final String urltoreq2 = urldomain + "getdatetime.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");
        new Fetchsignup(tosendidt, tosendvaluet, timedateserver, urltoreq2, "getdatetime").execute();


    }

    public void pagetreatmentmenu()
    {
        setContentView(R.layout.treatment_menu);
        counter++;

        Button bvmbutton=(Button) findViewById(R.id.bvmbutton);

        TextView  treatmenupatientinfo = (TextView) findViewById(R.id.treatmenupatientinfo);
        treatmenupatientinfo.setText(patientidandname());


        bvmbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmentbvm();

            }
        });

        Button etbutton=(Button) findViewById(R.id.etbutton);
        etbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmentet();

            }
        });

        Button eoabutton=(Button) findViewById(R.id.eoabutton);
        eoabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmenteoa();

            }
        });

        Button ptlbutton=(Button) findViewById(R.id.ptlbutton);
        ptlbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmentptl();

            }
        });


        Button o=(Button) findViewById(R.id.o2button);
        o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmento();

            }
        });

        Button bleed=(Button) findViewById(R.id.bleedingbutton);
        bleed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pagetreatmentbleed();

            }
        });
    }


    public void pagetreatmentbvm()
    {
        setContentView(R.layout.treatment_bvm);
        counter++;
        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmentbvm);
         timebyserver(timedateserver);


        TextView  bvmtreatpatientinfo = (TextView) findViewById(R.id.bvmtreatpatientinfo);
        bvmtreatpatientinfo.setText(patientidandname());

        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmentbvm);

        final EditText valueedit=(EditText) findViewById(R.id.bvmedit);


        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "bvm";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmentbvm);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tosendvaluet[3] = valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();


            }
        });


    }

    public void pagetreatmentet()
    {
        setContentView(R.layout.treatment_et);
        counter++;
        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmentet);
        timebyserver(timedateserver);

        TextView  etpatientinfo = (TextView) findViewById(R.id.etpatientinfo);
        etpatientinfo.setText(patientidandname());

        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmentet);

        final EditText valueedit=(EditText) findViewById(R.id.etedit);


        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "et";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmentet);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tosendvaluet[3] = valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();



            }
        });


    }


    public void pagetreatmenteoa()
    {
        setContentView(R.layout.treatment_eoa);

        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmenteoa);
        timebyserver(timedateserver);


        TextView  eoapatientinfo = (TextView) findViewById(R.id.eoapatientinfo);
        eoapatientinfo.setText(patientidandname());

        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmenteoa);

        final EditText valueedit=(EditText) findViewById(R.id.eoaedit);


        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "eoa";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmenteoa);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tosendvaluet[3] = valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();



            }
        });


    }


    public void pagetreatmentptl()
    {
        setContentView(R.layout.treatment_ptl);
        counter++;
        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmentptl);
        timebyserver(timedateserver);

        TextView  ptlpatientinfo = (TextView) findViewById(R.id.ptlpatientinfo);
        ptlpatientinfo.setText(patientidandname());

        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmentptl);

        final EditText valueedit=(EditText) findViewById(R.id.ptledit);


        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "ptl";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmentptl);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tosendvaluet[3] = valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();

            }
        });


    }

    public void pagetreatmento()
    {
        setContentView(R.layout.treatment_o);
        counter++;
        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmento);
        timebyserver(timedateserver);

        TextView  opatientinfo = (TextView) findViewById(R.id.opatientinfo);
        opatientinfo.setText(patientidandname());


        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmento);

        final EditText byo=(EditText) findViewById(R.id.obyedit);
        final EditText lmino=(EditText) findViewById(R.id.lminoedit);
        final EditText valueedit=(EditText) findViewById(R.id.initialsoedit);


        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "oxygen";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmento);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tosendvaluet[3] = "By:"+byo.getText().toString()+",Lmin:"+lmino.getText().toString()+",initial:"+valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();

            }
        });


    }

    public void pagetreatmentbleed()
    {
        setContentView(R.layout.treatment_bleeding);
        counter++;
        TextView timedateserver = (TextView) findViewById(R.id.timebyservertreatmentbleed);
        timebyserver(timedateserver);


        TextView  bleedtreatpatientinfo = (TextView) findViewById(R.id.bleedtreatpatientinfo);
        bleedtreatpatientinfo.setText(patientidandname());

        final TextView alerttext = (TextView) findViewById(R.id.alerttreatmentbleed);

        final EditText valueedit=(EditText) findViewById(R.id.bleededit);



        final String tosendidt[] = new String[4];
        final String tosendvaluet[] = new String[4];

        final String urltoreq2 = urldomain + "addtreatment.php";
        tosendidt[0] = "token";
        tosendvaluet[0] = getdata("login");


        tosendidt[1] = "patientid";
        tosendvaluet[1] = getdata("lastpatientid");


        tosendidt[2] = "type";
        tosendvaluet[2] = "bleed";



        tosendidt[3] = "value";

        ImageView check = (ImageView) findViewById(R.id.checkwhitetreatmentbleed);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tosendvaluet[3] = valueedit.getText().toString();
                new Fetchsignup(tosendidt, tosendvaluet, alerttext, urltoreq2, "addtreatment").execute();



            }
        });


    }

}
