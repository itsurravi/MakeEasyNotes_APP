package ravi_sharma.makemynotes.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ravi_sharma.makemynotes.R;
import ravi_sharma.makemynotes.Model.notesfile;

public class Listadap extends RecyclerView.Adapter<Listadap.ViewHolder> {

    private Activity context;
    List<notesfile> noteobj;

    private OnItemLongClicked onLongClick;
    private OnItemClicked onClick;

    public Listadap(Activity context, List<notesfile> noteobj) {
        this.context = context;
        this.noteobj = noteobj;
    }

    public interface OnItemLongClicked {
        void onItemLongClick(int position);
    }

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public void setOnLongClick(OnItemLongClicked onLongClick) {
        this.onLongClick=onLongClick;
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick=onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.list_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        notesfile file = noteobj.get(position);

        long date = Long.parseLong(file.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy - hh:mm:ss a");
        Date resultdate = new Date(date);

        holder.texttitle.setText(file.getTitle());
        holder.textdata.setText(file.getData());
        holder.textDate.setText(sdf.format(resultdate).toString());

        holder.cardLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClick.onItemLongClick(position);
                return false;
            }
        });

        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteobj.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView texttitle;
        TextView textdata;
        TextView textDate;
        LinearLayout cardLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            texttitle = (TextView) itemView.findViewById(R.id.textViewtitle);
            textdata = (TextView) itemView.findViewById(R.id.textViewdata);
            textDate = (TextView) itemView.findViewById(R.id.date);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_layout);
        }
    }
}
