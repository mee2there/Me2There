package com.innovation.me2there.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.model.PreferenceItem;
import com.innovation.me2there.model.UserVO;
import com.innovation.me2there.others.Mee2ThereApplication;
import com.innovation.me2there.util.DateUtil;
import com.innovation.me2there.util.LocationUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by ashley on 1/25/15.
 */
public class DataStore {

    static private Set<EventDetailVO> _allEvents = new HashSet<>();
    static private MongoDB _db;
    static private UserVO _user;
    static final List<PreferenceItem> _preferenceItems = fetchPreferences();
    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";
    private static int runningEventNo = 0;
    private static Integer discoverEventsPageNo = 0;
    private static Integer goingEventsPageNo = 0;
    private static Integer myEventsPageNo = 0;


    public static Integer nextDiscoverEventsPageNo() {
        discoverEventsPageNo = discoverEventsPageNo + 1;
        return discoverEventsPageNo;
    }

    public static Integer nextGoingEventsPageNo() {
        goingEventsPageNo = goingEventsPageNo + 1;
        return goingEventsPageNo;
    }

    public static Integer nextMyEventsPageNo() {
        myEventsPageNo = myEventsPageNo + 1;
        return myEventsPageNo;
    }

    public static UserVO getUser() {
        return _user;
    }

    public static void setUser(UserVO inUser) {
        _user = inUser;
    }

    public static void addToEvents(EventDetailVO _newEvent) {
        _allEvents.add(_newEvent);
    }

    public static void addToEvents(Set<EventDetailVO> _newEvents) {
        if (_allEvents != null) {
            _allEvents.addAll(_newEvents);
        } else {
            setEvents(_newEvents);
        }
    }

    public static void removeFromEvents(EventDetailVO _oldEvent) {
        _allEvents.remove(_oldEvent);
    }

    private static void setEvents(Set<EventDetailVO> inEvents) {
        _allEvents = inEvents;
    }

    public static Set<EventDetailVO> getEvents() {
        return _allEvents;
    }

    /**
     *
     * @return
     */
    public static Integer getNextIndex() {
        if (_allEvents != null) {
            return _allEvents.size() + 1;
        }
        return 0;
    }

    static public List<PreferenceItem> getUserPreferences() {
        return _preferenceItems;
    }

    static public EventDetailVO getEventById(Integer parmID) {
        Log.i("DataStore", "getEventById : " + parmID);
        EventDetailVO returnEvent = null;
        Iterator<EventDetailVO> iterator = _allEvents.iterator();
        while (iterator.hasNext()) {
            EventDetailVO nextEvent = iterator.next();
            if (nextEvent.getEventIndex() == parmID) {
                returnEvent = nextEvent;
                break;
            }
        }
        return returnEvent;
    }

    public static Set<EventDetailVO> getAroundYouEvents(Double latitude, Double longitude, int pageNo, Context theContext) {

        Set<EventDetailVO> eventVOList = new HashSet<>();
        try {
            String userId = getUser().get_objID();
            URL url = new URL(Mee2ThereApplication.EVENTS_URL + "/" + latitude + "/" + longitude + "/" + pageNo + "/" + 8 + "/" + 6);
            return getEvents(url, theContext);
        } catch (MalformedURLException m) {
            m.printStackTrace();
        }        return eventVOList;
    }

    static public Bitmap getImage(String eventName, String filename) {
        Bitmap imageBitMap;
        try {
            String urlBaseString = Mee2ThereApplication.EVENTS_URL + "/fs/download/";
            String query = URLEncoder.encode(filename, "utf-8");
            URL url = new URL(urlBaseString + query);
            if (eventName.equals(filename)) {
                urlBaseString = Mee2ThereApplication.EVENTS_URL + "/fs/downloadbyname/";
                query = URLEncoder.encode(filename, "utf-8");
                url = new URL(urlBaseString + query);

            }

            Log.d("DataStore", "Downloading Image : " + url);
            imageBitMap = BitmapFactory.decodeStream((InputStream) url.getContent());
            return imageBitMap;
        } catch (EOFException eof) {
            //return imageBitMap;
            Log.e("DataStore", "EOFException Error in getImage : " + eof.getMessage());

        } catch (MalformedURLException m) {
            Log.e("DataStore", "MalformedURLException Error in getImage : " + m.getMessage());

            m.printStackTrace();
        } catch (IOException i) {
            Log.e("DataStore", "IOException Error in getImage : " + i.getMessage());

            i.printStackTrace();
        } catch (Exception e) {
            Log.e("DataStore", "Exception in getImage : " + e.getMessage());

            e.printStackTrace();
        }
        return null;
    }

    public static Set<EventDetailVO> getRSVPedEvents(int pageNo, Context theContext) {

        Set<EventDetailVO> eventVOList = new HashSet<>();
        try {
            String userId = getUser().get_objID();
            URL url = new URL(Mee2ThereApplication.GOING_EVENTS_URL + "/" + userId + "/" + pageNo + "/" + 6);
            Log.i("getRSVPedEvents", url.toString());
            return getEvents(url, theContext);
        } catch (MalformedURLException m) {
            m.printStackTrace();
        }
        return eventVOList;
    }


    public static Set<EventDetailVO> getMyEvents(int pageNo, Context theContext) {
        Set<EventDetailVO> eventVOList = new HashSet<>();
        try {
            String userId = getUser().get_objID();
            URL url = new URL(Mee2ThereApplication.MY_EVENTS_URL + "/" + userId + "/" + pageNo + "/" + 6);
            Log.i("getMyEvents", url.toString());
            return getEvents(url, theContext);
        } catch (MalformedURLException m) {
            m.printStackTrace();
        }
        return eventVOList;
    }

    private static Set<EventDetailVO> getEvents(URL url, Context theContext) {
        Set<EventDetailVO> eventVOList = new HashSet<>();
        try {
            String userId = getUser().get_objID();
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonArray eventsArray = root.getAsJsonArray();

            for (JsonElement eventJson : eventsArray) {
                runningEventNo++;
                EventDetailVO newEvent = new EventDetailVO();
                newEvent.pojoFromJson(eventJson.getAsJsonObject());
                newEvent.setEventIndx(runningEventNo);
                newEvent.setShortLocText(LocationUtil.getVeryShortAddressFromLocation(newEvent.get_latitude(), newEvent.get_longitude(), theContext));

                eventVOList.add(newEvent);

            }
        } catch (MalformedURLException m) {
            m.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }

        addToEvents(eventVOList);

        return eventVOList;
    }


    public static Set<UserVO> getParticipants(EventDetailVO event, Context theContext) {
        Set<UserVO> userList = new HashSet<>();
        try {

            String eventID = event.get_objID();
            URL url = new URL(Mee2ThereApplication.EVENTS_PARTICIPANTS_URL + "/" + eventID );
            Log.d("EventDetail", "getParticipants : " + url.toString());

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            int status = request.getResponseCode();
            Log.d("EventDetail", "getParticipants : " + status);

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    // Convert String to json object
                    JSONArray participantsArray = new JSONArray(sb.toString());
//                    JSONArray participantsArray = null;
//                    responseJson.toJSONArray(participantsArray);
                    for (int i = 0; i < participantsArray.length(); i++) {
                        JSONObject participantObj = participantsArray.getJSONObject(i);
                        //runningEventNo++;
                        UserVO eventUser = new UserVO(participantObj.getString("name"),participantObj.getString("email") );
                        //newEvent.setEventIndx(runningEventNo);
                        //newEvent.setShortLocText(LocationUtil.getVeryShortAddressFromLocation(newEvent.get_latitude(), newEvent.get_longitude(), theContext));

                        userList.add(eventUser);

                    }
            }

        }
        catch (JSONException j) {
            j.printStackTrace();
        }
        catch (MalformedURLException m) {
            m.printStackTrace();
        }
        catch (IOException i) {
            i.printStackTrace();
        }


        return userList;
    }

    public static void insertEvent(EventDetailVO toCreate) {
        AsyncTask<EventDetailVO, Void, Set<EventDetailVO>> task = new AsyncTask<EventDetailVO, Void, Set<EventDetailVO>>() {

            @Override
            protected Set<EventDetailVO> doInBackground(EventDetailVO... events) {
                EventDetailVO eventToCreate = events[0];
                Log.i("DataStore", "insertEvent" + eventToCreate.toString());
                createEvent(eventToCreate);
                addToEvents(eventToCreate);
                return null;

            }

        };
        task.execute(toCreate);
        try {
           task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore", "insertEvent" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "insertEvent" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "insertEvent" + e0.getMessage());
        }
    }

    public static void rsvpEvent(EventDetailVO eventToUpdate, final UserVO user) {
        AsyncTask<EventDetailVO, Void, Void> task = new AsyncTask<EventDetailVO, Void, Void>() {

            @Override
            protected Void doInBackground(EventDetailVO... events) {
                EventDetailVO event = events[0];
                rsvpEvent(event.get_objID(), true);
                return null;
            }

        };
        task.execute(eventToUpdate);
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore", "rsvpEvent" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "rsvpEvent" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "rsvpEvent" + e0.getMessage());
        }
    }

    public static void withdrawRSVP(EventDetailVO eventToUpdate, final UserVO user) {
        AsyncTask<EventDetailVO, Void, Void> task = new AsyncTask<EventDetailVO, Void, Void>() {

            @Override
            protected Void doInBackground(EventDetailVO... events) {
                EventDetailVO event = events[0];
                rsvpEvent(event.get_objID(), false);
                return null;
            }

        };
        task.execute(eventToUpdate);
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore", "rsvpEvent" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "rsvpEvent" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "rsvpEvent" + e0.getMessage());
        }
    }

    public static String uploadImage(String imageUri, final String infileName) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... images) {
                String imageToUpload = images[0];
                String imageFileName = images[1];

                return postImage(Mee2ThereApplication.IMAGE_UPLOAD_URL, imageToUpload, imageFileName);

            }

        };
        task.execute(imageUri, infileName);
        try {
            return task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore ", "uploadImage" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "uploadImage" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "uploadImage" + e0.getMessage());
        }
        return null;
    }

    static List<PreferenceItem> fetchPreferences() {
        List<PreferenceItem> childTexts = new ArrayList<PreferenceItem>();

        childTexts.add(new PreferenceItem("Cricket"));
        childTexts.add(new PreferenceItem("Football"));
        childTexts.add(new PreferenceItem("Baseball"));
        childTexts.add(new PreferenceItem("Soccer"));
        childTexts.add(new PreferenceItem("Hockey"));
        childTexts.add(new PreferenceItem("Tennis"));
        childTexts.add(new PreferenceItem("Movie"));
        childTexts.add(new PreferenceItem("Dance"));
        childTexts.add(new PreferenceItem("Party"));
        childTexts.add(new PreferenceItem("Salsa"));
        childTexts.add(new PreferenceItem("Prayer"));
        childTexts.add(new PreferenceItem("Skating"));
        childTexts.add(new PreferenceItem("Skiing"));
        childTexts.add(new PreferenceItem("BBQ"));
        childTexts.add(new PreferenceItem("Biking"));
        return childTexts;

    }

    public static String postImage(String url,
                                   String imagePath,
                                   String fileName) {

        Log.i("postImage", imagePath);
        if (imagePath != null) {
            try {
                File imageFile = new File(imagePath);
                Log.d("postImage", "File " + imageFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("query", fileName, ContentType.TEXT_PLAIN);
                builder.addBinaryBody("source", imageFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
                HttpEntity multipart = builder.build();

                httpPost.setEntity(multipart);


                HttpResponse response = httpClient.execute(httpPost, localContext);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    //If everything goes ok, we can get the response
                    String fullRes = EntityUtils.toString(response.getEntity());
                    Log.d("DEBUG", fullRes);
                    return fullRes;

                } else {
                    Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                    return null;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String createEvent(EventDetailVO newEvent) {
        Log.i("createEvent", "CreateEvent");

        String imagePath = newEvent.getImagePath();
        String fileName = newEvent.getEventName();
        newEvent.setCreateUser(DataStore.getUser().get_objID());

        String json = newEvent.jsonFromPojo();

        Log.i("createEvent", "New Event Pojo" + json);

        if (imagePath != null) {
            try {
                File imageFile = new File(imagePath);
                Log.d("postImage", "File " + imageFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Mee2ThereApplication.CREATE_EVENT_URL);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("query", new StringBody(json, ContentType.TEXT_PLAIN));
                builder.addBinaryBody("source", imageFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
                HttpEntity multipart = builder.build();

                httpPost.setEntity(multipart);

                HttpResponse response = httpClient.execute(httpPost, localContext);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    String responseText = null;
                    try {
                        responseText = EntityUtils.toString(response.getEntity());
                        // Convert String to json object
                        JSONObject responseJson = new JSONObject(responseText);

                        if (responseJson.get("imageId") != null) {
                            newEvent.set_imageID(responseJson.getString("imageId"));
                        }
                        if (responseJson.get("thumbnailId") != null) {
                            newEvent.set_imageTumbNailID(responseJson.getString("thumbnailId"));
                        }

                        newEvent.set_objID(responseJson.getString("_id"));
                        Log.d("DEBUG", newEvent.get_objID());
                        return newEvent.get_objID();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("Parse Exception", e + "");

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("IO Exception 2", e + "");

                    }

                    Log.i("responseText", responseText);



                } else {
                    Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                    return null;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static boolean rsvpEvent(String eventId, boolean rsvpFlag) {
        String userId = getUser().get_objID();
        Log.i("RSVP user", "Event ID: " + eventId + " UserId: " + userId + " Rsvp?:" + rsvpFlag);
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("_eventId", eventId);
            jsonParam.put("_userId", userId);
            jsonParam.put("_rsvp", rsvpFlag);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Mee2ThereApplication.RSVP_EVENT_URL);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("query", new StringBody(jsonParam.toString(), ContentType.TEXT_PLAIN));
            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);


            HttpResponse response = httpClient.execute(httpPost, localContext);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                Log.d("DEBUG", "RSVP Success, Response Code: " + statusCode);

                return true;

            } else {
                Log.d("DEBUG", "RSVP HTTP Fail, Response Code: " + statusCode);
                return false;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("Parse Exception", e + "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        return false;
    }


    public static UserVO verifyUser(String userToken) {
        Log.i("Verify user", "token" + userToken);
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", userToken);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Mee2ThereApplication.VERIFY_USER_URL);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("query", new StringBody(jsonParam.toString(), ContentType.TEXT_PLAIN));
            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);
            HttpResponse response = httpClient.execute(httpPost, localContext);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String responseText = null;
                try {
                    responseText = EntityUtils.toString(response.getEntity());
                    // Convert String to json object
                    JSONObject responseJson = new JSONObject(responseText);
                    Boolean authSuccess = responseJson.getBoolean("success");
                    Log.i("Verify user", " sucess: " + authSuccess);

                    if (authSuccess) {
                        UserVO user = parseUserAuthResponse(responseJson);
                        user.setUserToken(userToken);
                        return user;
                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("Parse Exception", e + "");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("IO Exception 2", e + "");

                }

                Log.i("responseText", responseText);


            } else {
                Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                return null;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("Parse Exception", e + "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        return null;
    }
    //}


    public static UserVO loginUser(String userEmail, String password) {
        Log.i("Login user", "logon " + userEmail + " pwd " + password);
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("name", userEmail);
            jsonParam.put("password", password);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Mee2ThereApplication.AUTHENTICATE_USER_URL);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //builder.addTextBody("query", fileName, ContentType.TEXT_PLAIN);
            builder.addPart("query", new StringBody(jsonParam.toString(), ContentType.TEXT_PLAIN));
            //builder.addBinaryBody("source", imageFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);


            HttpResponse response = httpClient.execute(httpPost, localContext);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String responseText = null;
                try {
                    responseText = EntityUtils.toString(response.getEntity());
                    // Convert String to json object
                    JSONObject responseJson = new JSONObject(responseText);

                    Boolean authSuccess = responseJson.getBoolean("success");
                    Log.i("Login user", userEmail + " sucess: " + authSuccess);

                    if (authSuccess) {
                        String userToken = responseJson.getString("token");
                        UserVO user = parseUserAuthResponse(responseJson);
                        user.setUserToken(userToken);
                        return user;
                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("Parse Exception", e + "");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("IO Exception 2", e + "");

                }

                Log.i("responseText", responseText);


            } else {
                Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                return null;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("Parse Exception", e + "");

        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        return null;
    }
    //}

    private static UserVO parseUserAuthResponse(JSONObject responseJson) throws JSONException {
        String idValue = null;
        String fullName = null;
        String userEmail = null;
        String aboutYourself = null;
        Integer distanceWillingToTravel = 100;

        if (responseJson.has("id")) {
            idValue = responseJson.getString("id");
        }
        if (responseJson.has("fullname")) {
             fullName = responseJson.getString("fullname");
        }
        if (responseJson.has("email")) {
            userEmail = responseJson.getString("email");
        }
        if (responseJson.has("aboutyourself")) {
            aboutYourself = responseJson.getString("aboutyourself");
        }

        if(responseJson.has("distanceWillingToTravel")) {
            distanceWillingToTravel = responseJson.getInt("distanceWillingToTravel");
        }
        UserVO user = new UserVO(idValue,
                fullName,
                null,
                userEmail,
                null,//userObject.getString("fbid"),
                null,//userObject.get("location"),
                null//userObject.get("preference")

        );
        user.setAboutYourself(aboutYourself);
        user.setDistanceWillingToTravel(distanceWillingToTravel);
        Log.i("Verify user", " about : " + aboutYourself);

        if(responseJson.has("preferences")) {
            JSONArray prefArray = responseJson.getJSONArray("preferences");
            Log.i("Verify user", " prefArray: " + prefArray.length());

            for (int i = 0; i < prefArray.length(); i++) {
                JSONObject prefObj = prefArray.getJSONObject(i);
                String subCategory = prefObj.getString("subcategory");
                user.addPreference(subCategory);
            }
        }
        return user;

    }


    public static String createUser(UserVO newUser) {
        Log.i("createUser", "createUser");

        String json = newUser.jsonFromPojo();

        Log.i("createUser", "New User Pojo" + json);

//        if (imagePath != null) {
        try {
//                File imageFile = new File(imagePath);
//                Log.d("postImage", "File " + imageFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Mee2ThereApplication.CREATE_USER_URL);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //builder.addTextBody("query", fileName, ContentType.TEXT_PLAIN);
            builder.addPart("query", new StringBody(json, ContentType.TEXT_PLAIN));
            //builder.addBinaryBody("source", imageFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);


            HttpResponse response = httpClient.execute(httpPost, localContext);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String responseText = null;
                try {
                    responseText = EntityUtils.toString(response.getEntity());
                    // Convert String to json object
                    JSONObject responseJson = new JSONObject(responseText);


                    Boolean authSuccess = responseJson.getBoolean("success");
                    Log.i("Create user", " success: " + authSuccess);

                    if (authSuccess) {
                        String token_value = responseJson.getString("token");
                        String id_value = responseJson.getString("id");

                        newUser.setUserToken(token_value);
                        newUser.set_objID(id_value);

                        return token_value;
                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("Parse Exception", e + "");

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("IO Exception 2", e + "");

                }

                Log.i("responseText", responseText);


            } else {
                Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                return null;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        return null;
    }
    //}


    public static String updateUser(UserVO newUser) {
        Log.i("createUser", "createUser");

        String json = newUser.jsonFromPojo();

        Log.i("createUser", "New User Pojo" + json);

//        if (imagePath != null) {
        try {
//                File imageFile = new File(imagePath);
//                Log.d("postImage", "File " + imageFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(Mee2ThereApplication.UPDATE_USER_URL);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //builder.addTextBody("query", fileName, ContentType.TEXT_PLAIN);
            builder.addPart("query", new StringBody(json, ContentType.TEXT_PLAIN));
            //builder.addBinaryBody("source", imageFile, ContentType.APPLICATION_OCTET_STREAM, fileName);
            HttpEntity multipart = builder.build();

            httpPost.setEntity(multipart);


            HttpResponse response = httpClient.execute(httpPost, localContext);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                //If everything goes ok, we can get the response
                String fullRes = EntityUtils.toString(response.getEntity());
                Log.d("DEBUG", fullRes);
                return fullRes;

            } else {
                Log.d("DEBUG", "HTTP Fail, Response Code: " + statusCode);
                return null;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}



