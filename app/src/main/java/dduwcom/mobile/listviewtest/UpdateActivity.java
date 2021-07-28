package dduwcom.mobile.listviewtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {
    PostItem postItem;
    EditText updateTitle;
    EditText updateContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);

        postItem = (PostItem) getIntent().getSerializableExtra("item");

        updateTitle = findViewById(R.id.updateTitle);
        updateContent = findViewById(R.id.updateContent);

        updateTitle.setText(postItem.getTitle());
        updateContent.setText(postItem.getText());
    }

    public void updateOnClick(View v) {
        switch(v.getId()) {
            case R.id.updateButton:
                if (updateTitle.getText().toString().equals("")) {
                    Toast.makeText(this, "필수 항목이 입력되지 않았습니다(제목)", Toast.LENGTH_SHORT).show();
                } else {
                    Intent resultIntent = new Intent();

                    PostItem editPost = new PostItem();
                    editPost.setId(postItem.getId());
                    editPost.setTitle(updateTitle.getText().toString());
                    editPost.setText(updateContent.getText().toString());

                    resultIntent.putExtra("updateData", editPost);

                    setResult(RESULT_OK, resultIntent);
                }
                break;
        }
        finish();
    }
}
