package com.gohool.sqiteexample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<ToDoItem> mToDoItems; // m:전역변수
    private Context mcontext;
    private DBHelper mDBhelper;

    public CustomAdapter(ArrayList<ToDoItem> mToDoItems, Context mcontext) {
        this.mToDoItems = mToDoItems;
        this.mcontext = mcontext;
        mDBhelper = new DBHelper(mcontext); //

    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);

        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        holder.tv_title.setText(mToDoItems.get(position).getTitle());
        holder.tv_content.setText(mToDoItems.get(position).getContent());
        holder.tv_date.setText(mToDoItems.get(position).getWriteDate());

    }

    @Override
    public int getItemCount() {
        return mToDoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //item_list.xml
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);


            itemView.setOnClickListener(new View.OnClickListener() { //객체 하나
                @Override
                public void onClick(View v) {
                    int curPos = getAdapterPosition(); //클릭한  위치값 가져오기
                    ToDoItem toDoItem = mToDoItems.get(curPos);


                    String[] strChoiceItems = {"수정하기", "삭제하기"}; //0, 1
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setTitle("원하는 작업을 선택해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            if(position == 0) { //수정하기 / 팝업창
                                Dialog dialog = new Dialog(mcontext, android.R.style.Theme_Material_Light_Dialog); //팝업창
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText edit_title = dialog.findViewById(R.id.edit_title);
                                EditText edit_content = dialog.findViewById(R.id.edit_content);
                                Button dialog_btn = dialog.findViewById(R.id.dialog_btn);

                                edit_title.setText(toDoItem.getTitle()); //수정시 내용 가져오기
                                edit_content.setText(toDoItem.getContent());

                                edit_title.setSelection(edit_title.getText().length()); //수정시 커서 마지막에 놓기


                                dialog_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //db 업데이트
                                        String title = edit_title.getText().toString();
                                        String content = edit_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); //현재시간 받아오기
                                        String beforeTime = toDoItem.getWriteDate(); //등록되었던 시간

                                        mDBhelper.UpdateToDo(title, content,currentTime,beforeTime); //db에 넣기

                                        //UI 업데이트
                                        toDoItem.setTitle(title);;
                                        toDoItem.setContent(content);
                                        toDoItem.setWriteDate(currentTime);
                                        notifyItemChanged(curPos, toDoItem); //현재 객체, 갱신
                                        dialog.dismiss(); //끔
                                        Toast.makeText(mcontext, "수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show(); //필수

                            } else if(position == 1){ //삭제하기
                                // db 테이블 삭제
                                String beforeTime = toDoItem.getWriteDate(); //등록되었던 시간
                                mDBhelper.DeleteToDo(beforeTime);
                                //UI 삭제
                                mToDoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mcontext, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
    // 액티비티에서 호출되는 함수이며, 현재 어댑터에 새로운 게시글 아이템을 전달받아 추가하는 목적
    public void addItem(ToDoItem _item){
        mToDoItems.add(0, _item); // 0번째에 추가됨
        notifyItemInserted(0); //새로고침
    }

}
