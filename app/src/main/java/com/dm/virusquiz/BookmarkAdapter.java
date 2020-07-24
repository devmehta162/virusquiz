package com.dm.virusquiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.viewholder> {

    private List<QuestionModel> list;

    public BookmarkAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_layout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.setData(list.get(position).getQuestion(),list.get(position).getCorrectAns(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

        private ImageButton deletebtn;
        private TextView answer,question;
        public viewholder(@NonNull View itemView)
        {
            super(itemView);
          answer=  itemView.findViewById(R.id.answer);
          question=itemView.findViewById(R.id.question);
          deletebtn=itemView.findViewById(R.id.delete_btn);
        }
        private void setData(String question , String answer, final int postion){
            this.question.setText(question);
            this.answer.setText(answer);

            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(postion);
                    notifyItemRemoved(postion);
                }
            });
        }
    }
}
