package es.udc.tfg.pruebafinalfirebase.Messages;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.DBManager;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils.Utils;

/**
 * Created by Usuario on 18/02/2017.
 */
public class msgRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_LEFT = 1;
    private static final int VIEW_RIGHT = 2;
    private static final int VIEW_FIRST = 3;

    private ArrayList<Message> mDataset;
    private DBManager dbManager;
    private String TAG = "msgRecyclerViewAdapter";
    private String myId,groupId;
    private Context context;
    private OnMsgAdapterInteractionListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
    public msgRecyclerViewAdapter(ArrayList<Message> messages, String groupId) {
        mDataset = messages;
        dbManager = DBManager.getInstance();
        myId = dbManager.getId();
        this.groupId = groupId;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_FIRST;

        String id = mDataset.get(position-1).getSender().getMemberId();
        if(id.equals(myId))
            return VIEW_RIGHT;
        else
            return VIEW_LEFT;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.context = parent.getContext();
        if (context instanceof OnMsgAdapterInteractionListener) {
            mListener = (OnMsgAdapterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMsgAdapterInteractionListener");
        }

        if(viewType == VIEW_LEFT){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_row_left, parent, false);

            RecyclerView.ViewHolder vh = new msgRecyclerViewAdapter.ViewHolderLeft(v);
            return vh;
        }else if (viewType == VIEW_RIGHT){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_row_right, parent, false);

            RecyclerView.ViewHolder vh = new msgRecyclerViewAdapter.ViewHolderRight(v);


            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_first_row, parent, false);

            RecyclerView.ViewHolder vh = new msgRecyclerViewAdapter.ViewHolderFirst(v);


            return vh;
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderRight vhRight;
        final ViewHolderLeft vhLeft;
        final ViewHolderFirst vhFirst;

        final int i = getItemViewType(position);


        if (i == VIEW_FIRST){
            vhFirst = (ViewHolderFirst) holder;
            vhFirst.first_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vhFirst.tv.setVisibility(View.GONE);
                    vhFirst.pb.setVisibility(View.VISIBLE);
                    dbManager.initMsgList(groupId,mDataset.size());
                }
            });
        } else {
            final Message msg = mDataset.get(position-1);
            final String stringDate = Utils.longToShortDate(msg.getTime());
            final String m = msg.getMsg();

            if(i == VIEW_LEFT){
                vhLeft = (ViewHolderLeft) holder;
                vhLeft.name.setText(msg.getSender().getNick());
                vhLeft.name.setTextColor(Color.HSVToColor(Utils.stringToHSVColor(msg.getSender().getMemberId())));
                vhLeft.msg.setText(m);
                vhLeft.date_right.setText(stringDate);
                vhLeft.date_down.setText(stringDate);

                if(msg.getType()== Message.TYPE_IMG){
                    vhLeft.img.setVisibility(View.VISIBLE);
                    vhLeft.img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference ref = FirebaseStorage.getInstance().getReference().child(DBManager.STORAGE_MESSAGES).child(msg.getMsgId());
                            ref.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    vhLeft.img.setImageBitmap(bitmap);
                                }
                            });
                        }
                    });
                }

                if(m.length()>20){
                    vhLeft.date_right.setVisibility(View.GONE);
                    vhLeft.date_down.setVisibility(View.VISIBLE);
                }else{
                    vhLeft.date_right.setVisibility(View.VISIBLE);
                    vhLeft.date_down.setVisibility(View.GONE);
                }
                if(msg.getType()==Message.TYPE_IP){
                    vhLeft.content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.ipClicked(msg);
                        }
                    });
                }

            } else if (i == VIEW_RIGHT){
                vhRight = (ViewHolderRight) holder;
                vhRight.name.setText("Me");
                vhRight.msg.setText(m);
                vhRight.date_right.setText(stringDate);
                vhRight.date_down.setText(stringDate);

                if(m.length()>20){
                    vhRight.date_right.setVisibility(View.GONE);
                    vhRight.date_down.setVisibility(View.VISIBLE);
                }else{
                    vhRight.date_right.setVisibility(View.VISIBLE);
                    vhRight.date_down.setVisibility(View.GONE);
                }

                if(msg.getType()==Message.TYPE_IP){
                    vhRight.content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.ipClicked(msg);
                        }
                    });
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return mDataset.size()+1;
    }

    public ArrayList<Message> getmDataset(){
        return mDataset;
    }





    public static class ViewHolderLeft extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,msg,date_right,date_down;
        public ImageView img;
        public RelativeLayout content;

        public ViewHolderLeft(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.sender_name_tv);
            msg = (TextView)v.findViewById(R.id.msg_tv);
            date_right = (TextView)v.findViewById(R.id.msg_date_tv_right);
            date_down = (TextView)v.findViewById(R.id.msg_date_tv_down);
            content = (RelativeLayout) v.findViewById(R.id.msg_content);
            img = (ImageView) v.findViewById(R.id.msg_img);
        }
    }

    public static class ViewHolderRight extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name,msg,date_right,date_down;
        public RelativeLayout content;

        public ViewHolderRight(View v) {
            super(v);
            name = (TextView)v.findViewById(R.id.sender_name_tv_right);
            msg = (TextView)v.findViewById(R.id.msg_tv_right);
            date_right = (TextView)v.findViewById(R.id.msg_date_tv_right_right);
            date_down = (TextView)v.findViewById(R.id.msg_date_tv_down_right);
            content = (RelativeLayout) v.findViewById(R.id.msg_content_right);
        }
    }

    public static class ViewHolderFirst extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout first_row;
        public TextView tv;
        public ProgressBar pb;

        public ViewHolderFirst(View v) {
            super(v);
            first_row = (RelativeLayout) v.findViewById(R.id.first_row_msg);
            tv = (TextView) v.findViewById(R.id.first_row_tv);
            pb = (ProgressBar) v.findViewById(R.id.first_row_pb);
        }
    }

    public interface OnMsgAdapterInteractionListener{
        void ipClicked(Message msg);
    }
}