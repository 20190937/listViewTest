package dduwcom.mobile.listviewtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "my listView Test";
    private final String BASE_URL = "https://fc6d2a9a3824.ngrok.io";

    final int UPDATE_CODE = 200;

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<PostItem> postList;
    private dduwcom.mobile.test.MyAPI mMyAPI;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        postList = new ArrayList<PostItem>();

        Log.d(TAG,"initMyAPI : " + BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mMyAPI = retrofit.create(dduwcom.mobile.test.MyAPI.class);

        Call<List<PostItem>> getCall = mMyAPI.get_posts();
        getCall.enqueue(new Callback<List<PostItem>>() {
            @Override
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if( response.isSuccessful()){
                    List<PostItem> mList = response.body();
                    String result ="";
                    for( PostItem item : mList) {
                        postList.add(item);
                    }
                    Log.d(TAG,result);


                    adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, postList);
                    listView.setAdapter(adapter);;

                }else {
                    Log.d(TAG,"Status Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                Log.d(TAG,"Fail msg : " + t.getMessage());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostItem item = postList.get(position);
                intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("item", item); // serializable ??? ????????????????????? ??? ??? ??????
                startActivityForResult(intent, UPDATE_CODE);
            }
        });


        // listView ????????? ????????? ??????
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position; // ??????????????? ?????????????????? ????????? ?????????. ???????????? ????????? ????????????.
                String msg = postList.get(position).getTitle() + "???(???) ?????????????????????????";

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); // ?????? this ?????? ??????????????? ?????? ?????????
                builder.setTitle("????????? ??????");
                builder.setMessage(msg);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pk = postList.get(position).getId();

                        Log.d(TAG,"DELETE");
                        // pk ?????? ????????? ????????????
                        Call<PostItem> deleteCall = mMyAPI.delete_posts(pk);
                        deleteCall.enqueue(new Callback<PostItem>() {
                            @Override
                            public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                                if(response.isSuccessful()){
                                    Log.d(TAG,"?????? ??????");

                                    Call<List<PostItem>> getCall = mMyAPI.get_posts();
                                    getCall.enqueue(new Callback<List<PostItem>>() {
                                        @Override
                                        public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                                            if( response.isSuccessful()){
                                                postList.clear();

                                                List<PostItem> mList = response.body();
                                                String result ="";
                                                for( PostItem item : mList) {
                                                    postList.add(item);
                                                    Log.d(TAG,item.toString());
                                                }
                                                Log.d(TAG,result);
                                                adapter.notifyDataSetChanged();
                                            }else {
                                                Log.d(TAG,"Status Code : " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<PostItem>> call, Throwable t) {
                                            Log.d(TAG,"Fail msg : " + t.getMessage());
                                        }
                                    });

                                }else {
                                    Log.d(TAG,"Status Code : " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<PostItem> call, Throwable t) {
                                Log.d(TAG,"Fail msg : " + t.getMessage());
                            }
                        });

                        //?????? ??????
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("??????", null);
                builder.setCancelable(false);
                builder.show();

                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {    // onresume??? ?????? ???????????????
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this,  "?????? ??????", Toast.LENGTH_SHORT).show();

                    PostItem editPost = (PostItem) data.getSerializableExtra("updateData");

                    Log.d(TAG,"PATCH");
                    PostItem item = new PostItem();
                    item.setTitle(editPost.getTitle());
                    item.setText(editPost.getText());
                    //pk ?????? ????????? ???????????????????????? ???????????? setting ?????? ????????????
                    Call<PostItem> patchCall = mMyAPI.patch_posts(editPost.getId(),item);
                    patchCall.enqueue(new Callback<PostItem>() {
                        @Override
                        public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                            if(response.isSuccessful()){
                                Log.d(TAG,"patch ??????");

                                Call<List<PostItem>> getCall = mMyAPI.get_posts();
                                getCall.enqueue(new Callback<List<PostItem>>() {
                                    @Override
                                    public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                                        if( response.isSuccessful()){
                                            postList.clear();

                                            List<PostItem> mList = response.body();
                                            String result ="";
                                            for( PostItem item : mList) {
                                                postList.add(item);
                                            }
                                            Log.d(TAG,result);


                                            adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, postList);
                                            listView.setAdapter(adapter);;

                                        }else {
                                            Log.d(TAG,"Status Code : " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<PostItem>> call, Throwable t) {
                                        Log.d(TAG,"Fail msg : " + t.getMessage());
                                    }
                                });

                            }else{
                                Log.d(TAG,"Status Code : " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<PostItem> call, Throwable t) {
                            Log.d(TAG,"Fail msg : " + t.getMessage());
                        }
                    });

                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "?????? ??????", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    public void onClick(View v) {
        final ConstraintLayout addLayout = (ConstraintLayout) View.inflate(this, R.layout.add_layout, null);

        switch (v.getId()) {
            case R.id.button :
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("????????? ??????")
                        .setView(addLayout)
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText title = (EditText) addLayout.findViewById(R.id.txtTitle);
                                EditText content = (EditText) addLayout.findViewById(R.id.txtContent);

                                Log.d(TAG,"POST");
                                PostItem item = new PostItem();
                                item.setTitle(title.getText().toString());
                                item.setText(content.getText().toString());

                                Call<PostItem> postCall = mMyAPI.post_posts(item);
                                postCall.enqueue(new Callback<PostItem>() {
                                    @Override
                                    public void onResponse(Call<PostItem> call, Response<PostItem> response) {
                                        if(response.isSuccessful()){
                                            Log.d(TAG,"?????? ??????");

                                            Log.d(TAG,"GET");
                                            Call<List<PostItem>> getCall = mMyAPI.get_posts();
                                            getCall.enqueue(new Callback<List<PostItem>>() {
                                                @Override
                                                public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                                                    if( response.isSuccessful()){
                                                        postList.clear();

                                                        List<PostItem> mList = response.body();
                                                        String result ="";
                                                        for(PostItem item : mList) {
                                                            postList.add(item);
                                                            Log.d(TAG,item.toString());
                                                        }
                                                        Log.d(TAG,result);
                                                        adapter.notifyDataSetChanged();
                                                    }else {
                                                        Log.d(TAG,"Status Code : " + response.code());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<PostItem>> call, Throwable t) {
                                                    Log.d(TAG,"Fail msg : " + t.getMessage());
                                                }
                                            });

                                        }else {
                                            Log.d(TAG,"Status Code : " + response.code());
                                            Log.d(TAG,response.errorBody().toString());
                                            Log.d(TAG,call.request().body().toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PostItem> call, Throwable t) {
                                        Log.d(TAG,"Fail msg : " + t.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
        }
    }
}