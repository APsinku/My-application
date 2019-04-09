package abhi.osmchat;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class RequestsFragment extends Fragment {
    private RecyclerView myRequestList;
    private  View myMainView;
    private DatabaseReference FriendsRequestsReference;
    private FirebaseAuth mAuth;
    String online_user_id;

    private DatabaseReference UsersReference;

    private  DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList = (RecyclerView) myMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsRequestsReference  = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(online_user_id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");


        Toasty.Config.getInstance()

                .setErrorColor(ContextCompat.getColor(myMainView.getContext(),R.color.Tcolor1) )
                .setSuccessColor(ContextCompat.getColor(myMainView.getContext(),R.color.TcolorGreen))
                .setTextColor(ContextCompat.getColor(myMainView.getContext(),R.color.Tcolor3))
                .tintIcon(true)
                .apply();


        myRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestList.setLayoutManager(linearLayoutManager);






        return myMainView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
                (
                        Requests.class,
                        R.layout.friend_request_all_users_layout,
                        RequestsFragment.RequestViewHolder.class,
                        FriendsRequestsReference

                )
        {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, int position)
            {
                final  String list_Users_id  = getRef(position).getKey();
                DatabaseReference get_type_ref  = getRef(position).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {
                                UsersReference.child(list_Users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        final   String userName = dataSnapshot.child("name").getValue().toString();
                                        final String  thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                                        final String  userStatus =  dataSnapshot.child("status").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumb_user_image(thumbImage,getContext());
                                        viewHolder.setUser_Status(userStatus);


                                        Button req_accept_btn =  viewHolder.mView.findViewById(R.id.request_accept_btn);
                                        Button request_decline_btn =  viewHolder.mView.findViewById(R.id.request_decline_btn);


                                        req_accept_btn.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view) {


                                                SimpleDateFormat currentDate =  new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String saveCurrentDate = currentDate.format(new Date());

                                                FriendsDatabaseRef.child(online_user_id).child(list_Users_id).child("date").setValue(saveCurrentDate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                FriendsDatabaseRef.child(list_Users_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                FriendsReqDatabaseRef.child(online_user_id).child(list_Users_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    FriendsReqDatabaseRef.child(list_Users_id).child(online_user_id).removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)

                                                                                                                {
                                                                                                                    if (task.isSuccessful())
                                                                                                                    {

                                                                                                                        //Toast.makeText(getContext(),"Friend Request Accepted",Toast.LENGTH_SHORT).show();
                                                                                                                        Toasty.success(getContext(), "Friend Request Accepted", Toast.LENGTH_SHORT, true).show();
                                                                                                                    }

                                                                                                                }
                                                                                                            });

                                                                                                }

                                                                                            }
                                                                                        });



                                                                            }
                                                                        });

                                                            }
                                                        });

                                            }
                                        });

                                        request_decline_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                FriendsReqDatabaseRef.child(online_user_id).child(list_Users_id).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful())
                                                                {
                                                                    FriendsReqDatabaseRef.child(list_Users_id).child(online_user_id).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)

                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {

                                                                                        // Toast.makeText(getContext(), "Friends Request Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                                                                        Toasty.error(getContext(), "Friends Request Cancelled Successfully", Toast.LENGTH_SHORT, true).show();

                                                                                    }

                                                                                }
                                                                            });

                                                                }

                                                            }
                                                        });
                                            }
                                        });




                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            }
        };
        myRequestList.setAdapter(firebaseRecyclerAdapter);



    }




    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RequestViewHolder(View itemView)
        {
            super(itemView);

            mView = itemView;
        }


        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);

        }

        public void setThumb_user_image(final String thumbImage, final Context ctx)
        {

            final CircleImageView thumb_image = (CircleImageView)  mView.findViewById(R.id.request_profile_image);

            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_avatar).into(thumb_image);

                        }
                    });


        }

        public void setUser_Status(String userStatus)
        {
            TextView status = (TextView) mView.findViewById(R.id.request_profile_status);
            status.setText(userStatus);
        }
    }


}


