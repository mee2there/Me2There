package com.innovation.me2there.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.innovation.me2there.adapters.MessageAdapter;
import com.innovation.me2there.R;
import com.innovation.me2there.activities.ChatActivity;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.Message;
import com.innovation.me2there.others.Mee2ThereApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class ChatActivityFragment extends Fragment {

    private static final int REQUEST_LOGIN = 0;

    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername ;
    private String mUserId ;
    private ChatActivity chatActivity;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Mee2ThereApplication.SERVER_ADDRESS);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        chatActivity = (ChatActivity)activity;
        mAdapter = new MessageAdapter(activity, mMessages);
    }

    /**

     */
    @Override
    public void onResume() {
        super.onResume();
        mInputMessageView.clearFocus();
       // mInputMessageView.requestFocus();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsername = DataStore.getUser().get_userFullName();
        mUserId = DataStore.getUser().get_objID().toString();

        setHasOptionsMenu(true);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("login", onLogin);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();
        // perform the user login attempt.
        mSocket.emit("add user", mUsername,mUserId,chatActivity.getEvent().get_objID());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("login", onLogin);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button sendButton = (Button) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }

        mUsername = data.getStringExtra("username");
        int numUsers = data.getIntExtra("numUsers", 1);

        addLog(getResources().getString(R.string.message_welcome));
        addParticipantsLog(numUsers);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void addMessage(String username, String message,Date time) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .userid(mUserId)
                .time(time)
                .username(username).message(message).build());
        //Collections.sort(mMessages);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                      .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");
        addMessage(mUsername, message, new Date());

        // perform the sending message attempt.
        mSocket.emit("new message", message);
    }

//    private void startSignIn() {
//        mUsername = null;
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivityForResult(intent, REQUEST_LOGIN);
//    }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        // startSignIn();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Integer noOfUsers;
                    JSONArray prevChats;
                    try {
                        noOfUsers = data.getInt("numUsers");
                        prevChats = data.getJSONArray("chats");
                        for (int i = 0; i < prevChats.length(); i++) {
                            JSONObject aChat = prevChats.getJSONObject(i);
                            Message newMessage = new Message();
                            newMessage.pojoFromJson(aChat);
                            addMessage(newMessage.getUsername(), newMessage.getMessage(), newMessage.getmTime());
                        }
                    } catch (JSONException e) {
                        Log.i("ChatActivity", "On Login Exception" + e);

                        return;
                    }

                    Log.i("ChatActivity", "On Login Prev Chats" + prevChats);


                    //addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);
                    addMessage(username, message,new Date());
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.i("ChatActivity", "On User Logged " + numUsers);

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    removeTyping(username);
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };
/**
 @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
 Bundle savedInstanceState) {
 rootView = inflater.inflate(R.layout.fragment_main_discover, container, false);
 activity = (MainActivity) getActivity();
 msgView = (ListView) rootView.findViewById(R.id.listView);

 msgList = new ArrayAdapter<String>(activity,
 android.R.layout.simple_list_item_1);
 msgView.setAdapter(msgList);

 //		msgView.smoothScrollToPosition(msgList.getCount() - 1);

 Button btnSend = (Button) rootView.findViewById(R.id.btn_Send);

 receiveMsg();
 btnSend.setOnClickListener(new View.OnClickListener() {

 @Override public void onClick(View v) {
 // TODO Auto-generated method stub

 final EditText txtEdit = (EditText) rootView.findViewById(R.id.txt_inputText);
 //msgList.add(txtEdit.getText().toString());
 sendMessageToServer(txtEdit.getText().toString());
 msgView.smoothScrollToPosition(msgList.getCount() - 1);

 }
 });
 return rootView;
 }


 //End Receive msg from server//

 public void sendMessageToServer(String str) {

 final String str1=str;
 new Thread(new Runnable() {

 @Override public void run() {
 // TODO Auto-generated method stub
 //String host = "opuntia.cs.utep.edu";
 String host="10.0.2.2";
 //String host2 = "127.0.0.1";
 PrintWriter out;
 try {
 Socket socket = new Socket(host, 8008);
 out = new PrintWriter(socket.getOutputStream());

 // out.println("hello");
 out.println(str1);
 Log.d("", "hello");
 out.flush();
 } catch (UnknownHostException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 Log.d("", "hello222");
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 Log.d("", "hello4333");
 }

 }
 }).start();
 }



 public void receiveMsg()
 {
 new Thread(new Runnable()
 {
 @Override public void run() {
 // TODO Auto-generated method stub

 //final  String host="opuntia.cs.utep.edu";
 final String host="10.0.2.2";
 //final String host="localhost";
 Socket socket = null ;
 BufferedReader in = null;
 try {
 socket = new Socket(host,8008);
 } catch (UnknownHostException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }

 try {
 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }

 while(true)
 {
 String msg = null;
 try {
 msg = in.readLine();
 Log.d("","MSGGG:  "+ msg);

 //msgList.add(msg);
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }
 if(msg == null)
 {
 break;
 }
 else
 {
 displayMsg(msg);
 }
 }

 }
 }).start();


 }

 public void displayMsg(String msg)
 {
 final String mssg=msg;
 handler.post(new Runnable() {

 @Override public void run() {
 // TODO Auto-generated method stub
 msgList.add(mssg);
 msgView.setAdapter(msgList);
 msgView.smoothScrollToPosition(msgList.getCount() - 1);
 Log.d("","hi");
 }
 });

 }*/
}
