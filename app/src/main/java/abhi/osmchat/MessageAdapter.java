package abhi.osmchat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    CircleImageView ProfileImage;





    public  MessageAdapter (List<Messages>userMessagesList)
    {
        this.userMessagesList = userMessagesList;

    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View  V = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position)
    {
        String message_sender_id = mAuth.getCurrentUser().getUid();

       final Messages messages = userMessagesList.get(position);

        String  fromUserId  = messages.getFrom();
        String  message_type = messages.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String  thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(holder.userProfileImage.getContext()).load(thumb_image)
                        .placeholder(R.drawable.default_avatar).into(holder.userProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       // lp2.weight = 1.0f;

        if (fromUserId.equals(message_sender_id))
        {
           // holder.messageText.setBackgroundResource(R.drawable.in_message_bg);
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            holder.txtMessage.setTextColor(Color.BLACK);
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

           // layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
          //  layoutParams.gravity = Gravity.RIGHT;
         //   holder.txtInfo.setLayoutParams(layoutParams);







          holder.txtMessage.setGravity(Gravity.CENTER);
         //   lp2.gravity = Gravity.RIGHT;

           holder.ProfileImage.setVisibility(View.INVISIBLE);


        }
        else
        {

            holder.contentWithBG.setBackgroundResource(R.drawable.bubble_in);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            holder.txtMessage.setTextColor(Color.BLACK);
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);











//            holder.txtMessage.setBackgroundResource(R.drawable.bubble_out);
//            holder.txtMessage.setTextColor(Color.BLACK);
            holder.txtMessage.setGravity(Gravity.CENTER);

          //  lp2.gravity = Gravity.LEFT;

        }
//        holder.messageText.setLayoutParams(lp2);
//        holder.messageText2.setLayoutParams(lp2);
//        layout.addView(holder.messageText);


        holder.txtMessage.setText(messages.getMessage());

      //  holder.messageText2.setText(messages.getMessage());



        if (message_type.equals("text"))
        {
            holder.txtMessage.setText(messages.getMessage());
          //  holder.messageText2.setText(messages.getMessage());

          //  holder.messageImage.setVisibility(View.INVISIBLE);
        }
        else

        {
            holder.txtMessage.setVisibility(View.INVISIBLE);
         //   holder.messageText2.setVisibility(View.INVISIBLE);
            Picasso.with(holder.userProfileImage.getContext()).load(messages.getMessage())
                    .placeholder(R.drawable.psb).into(holder.messageImage);
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtMessage;
       // public TextView messageText2;
        public CircleImageView userProfileImage;
        public ImageView messageImage;
        public   CircleImageView   ProfileImage;


        public LinearLayout contentWithBG;
        public LinearLayout content;


        public MessageViewHolder (View view)
        {

            super(view);
            txtMessage =  (TextView)  view.findViewById(R.id.txtMessage);
         //  messageText2 = (TextView) view.findViewById(R.id.message_text2);

            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

            ProfileImage =(CircleImageView) view.findViewById(R.id.message_profile_layout) ;
           userProfileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);

          contentWithBG = (LinearLayout) view.findViewById(R.id.contentWithBackground);
            content = (LinearLayout) view.findViewById(R.id.content);



        }

    }
}
