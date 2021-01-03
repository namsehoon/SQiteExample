package com.gohool.sqiteexample;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

//데이터베이스
public class DBHelper extends SQLiteOpenHelper {


    //버전
    private static final int DB_VERSION = 1;
    //db이름
    private static final String DB_NAME = "dw.db";

    //생성자
    public DBHelper(@Nullable Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) { //데이터베이스가 생성됐을때 호출/ AUTOINCREMENT: 하나하나씩 값이 올라감 id++
        // 데이터베이스 -> 테이블 -> 컬럼 -> 값
        db.execSQL("CREATE TABLE IF NOT EXISTS ToDoList (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, writeDate TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);
    }

    //SELECT문 (조회)
    public ArrayList<ToDoItem> getToDoList() { //ArrayList<ToDoItem>데이터형식의 메소드
      ArrayList<ToDoItem> toDoItems = new ArrayList<>();

      SQLiteDatabase db = getReadableDatabase();
      Cursor cursor = db.rawQuery("SELECT * FROM ToDoList ORDER BY writeDate DESC", null); //cursor : 가르키다
      if(cursor.getCount() != 0) { //데이터가 있으면
          while (cursor.moveToNext()){ //다음 데이터가 없을때까지
              int id = cursor.getInt(cursor.getColumnIndex("id")); //cursor가 가르켜 가져옴 / 데이터를 변수에 넣어줄거임
              String title = cursor.getString(cursor.getColumnIndex("title"));
              String content = cursor.getString(cursor.getColumnIndex("content"));
              String writeDate = cursor.getString(cursor.getColumnIndex("writeDate"));


              ToDoItem toDoItem = new ToDoItem();
              toDoItem.setId(id);
              toDoItem.setTitle(title);
              toDoItem.setContent(content);
              toDoItem.setWriteDate(writeDate);
              toDoItems.add(toDoItem); // 리스트에 추가해줌


          }
      }
      cursor.close(); //종료
      return  toDoItems;
    }


    // INSERT문 (삽입) / id값 넣지않음 알아서 입력됨 /
    public void InsertTodo(String _title, String _content, String _writeDate){
        SQLiteDatabase db = getWritableDatabase(); // 쓰기가능
        db.execSQL("INSERT INTO ToDoList (title, content, writeDate) VALUES('" + _title + "', '" + _content + "' , '" + _writeDate + "');"); // sql문
    }

    //UPDATE 문(수정)
    public void UpdateToDo(String _title, String _content, String _writeDate, String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE ToDoList SET title= '" + _title + "', content = '" + _content + "',  writeDate = '" + _writeDate + "' WHERE writeDate= '" + _beforeDate + "'");
    }

    //DELETE 문(삭제)
    public void DeleteToDo(String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM ToDoList WHERE writeDate = '" + _beforeDate + "' "); //id만 지우면 됨
    }

}
