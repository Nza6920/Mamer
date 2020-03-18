package com.example.my.mamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;

public class TopicParticularsTestActivity extends AppCompatActivity {

    private IARE_Toolbar mToolbar;

    private AREditText mEditText;

    private boolean scrollerAtEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_particulars_test);

        initToolbar();
    }


    private void initToolbar() {
        mToolbar = this.findViewById(R.id.areToolbar);
//        加粗
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
//        斜体
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
//        下划线
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
//        “
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
//        列表123
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
//        列表abc
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
//        分割线
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
//        连接
        IARE_ToolItem link = new ARE_ToolItem_Link();
//        下标
//        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
//        上标
//        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
//        左对齐
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
//        居中
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
//        右对齐
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
//        图片
        IARE_ToolItem image = new ARE_ToolItem_Image();
//        @
        IARE_ToolItem at = new ARE_ToolItem_At();
        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(image);
        mToolbar.addToolbarItem(at);

        mEditText = this.findViewById(R.id.arEditText);
        mEditText.setToolbar(mToolbar);

//        setHtml();
//
//        initToolbarArrow();
    }

    private void setHtml() {
        String html = "<p style=\"text-align: center;\"><strong>New Feature in 0.1.2</strong></p>\n" +
                "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">In this release, you have a new usage with ARE.</span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">AREditText + ARE_Toolbar, you are now able to control the position of the input area and where to put the toolbar at and, what ToolItems you'd like to have in the toolbar. </span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #3366ff;\">You can not only define the Toolbar (and it's style), you can also add your own ARE_ToolItem with your style into ARE.</span></p>\n" +
                "<p style=\"text-align: left;\">&nbsp;</p>\n" +
                "<p style=\"text-align: left;\"><span style=\"color: #ff00ff;\"><em><strong>Why not give it a try now?</strong></em></span></p>";
        mEditText.fromHtml(html);
    }

//    private void initToolbarArrow() {
//
//        if (this.mToolbar instanceof ARE_ToolbarDefault) {
//            ((ARE_ToolbarDefault) mToolbar).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//                @Override
//                public void onScrollChanged() {
//                    int scrollX = ((ARE_ToolbarDefault) mToolbar).getScrollX();
//                    int scrollWidth = ((ARE_ToolbarDefault) mToolbar).getWidth();
//                    int fullWidth = ((ARE_ToolbarDefault) mToolbar).getChildAt(0).getWidth();
//
//                    if (scrollX + scrollWidth < fullWidth) {
//                        imageView.setImageResource(R.drawable.alignright);
//                        scrollerAtEnd = false;
//                    } else {
//                        imageView.setImageResource(R.drawable.alignleft);
//                        scrollerAtEnd = true;
//                    }
//                }
//            });
//        }
//
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (scrollerAtEnd) {
//                    ((ARE_ToolbarDefault) mToolbar).smoothScrollBy(-Integer.MAX_VALUE, 0);
//                    scrollerAtEnd = false;
//                } else {
//                    int hsWidth = ((ARE_ToolbarDefault) mToolbar).getChildAt(0).getWidth();
//                    ((ARE_ToolbarDefault) mToolbar).smoothScrollBy(hsWidth, 0);
//                    scrollerAtEnd = true;
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int menuId = item.getItemId();
//        if (menuId == com.chinalwb.are.R.id.action_save) {
//            String html = this.mEditText.getHtml();
//            Util.saveHtml(this, html);
//            return true;
//        }
//        if (menuId == R.id.action_show_tv) {
//            String html = this.mEditText.getHtml();
//            Intent intent = new Intent(this, TextViewActivity.class);
//            intent.putExtra(HTML_TEXT, html);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mToolbar.onActivityResult(requestCode, resultCode, data);
    }

}
